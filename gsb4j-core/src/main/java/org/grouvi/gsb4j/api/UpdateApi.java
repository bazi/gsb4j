/*
 * Copyright 2018 Azilet B.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.grouvi.gsb4j.api;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;

import org.grouvi.gsb4j.Gsb4jConst;
import org.grouvi.gsb4j.cache.ThreatListDescriptorsCache;
import org.grouvi.gsb4j.data.ThreatEntry;
import org.grouvi.gsb4j.data.ThreatInfo;
import org.grouvi.gsb4j.data.ThreatListDescriptor;
import org.grouvi.gsb4j.data.ThreatMatch;
import org.grouvi.gsb4j.db.LocalDatabase;
import org.grouvi.gsb4j.url.Canonicalization;
import org.grouvi.gsb4j.url.Hashing;
import org.grouvi.gsb4j.url.SuffixPrefixExpressions;
import org.grouvi.gsb4j.util.Gsb4jUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;

import com.google.inject.Inject;
import com.google.inject.name.Named;


/**
 * Interface to Update API.
 *
 * @author azilet
 */
class UpdateApi extends SafeBrowsingApiBase implements SafeBrowsingApi
{
    private static final Logger LOGGER = LoggerFactory.getLogger( UpdateApi.class );

    @Inject
    private Canonicalization canonicalizer;

    @Inject
    private SuffixPrefixExpressions expressionGenerator;

    @Inject
    private Hashing hashing;

    @Inject
    private LocalDatabase localDatabase;

    @Inject
    private ThreatListDescriptorsCache descriptorsCache;

    @Inject
    private UpdateApiCache cache;

    @Inject
    private Gsb4jUtils gsb4jUtils;

    @Inject
    private StateHolder stateHolder;

    @Inject
    @Named( Gsb4jConst.GSB4J )
    private ScheduledExecutorService executor;


    @Override
    public boolean isLookupApi()
    {
        return false;
    }


    @Override
    public ThreatMatch check( String url )
    {
        try
        {
            List<UrlHashCollision> collisions = findHashPrefixCollisions( url );
            if ( !collisions.isEmpty() )
            {
                List<ThreatMatch> threats = checkCollisions( collisions );
                if ( !threats.isEmpty() )
                {
                    return selectMoreGenericThreat( threats );
                }
            }
            else
            {
                LOGGER.info( "URL hash not in local database: {}", url );
            }
        }
        catch ( IOException | DecoderException ex )
        {
            LOGGER.error( "Failed to check in Safe Browsing lists", ex );
        }
        return null;
    }


    @Override
    Logger getLogger()
    {
        return LOGGER;
    }


    private List<ThreatMatch> checkCollisions( List<UrlHashCollision> collisions ) throws IOException, DecoderException
    {
        List<ThreatMatch> threats = new ArrayList<>();

        // first, check for unexpired positive cache hits
        for ( UrlHashCollision collision : collisions )
        {
            ThreatMatch match = cache.getPositive( collision.fullHash, false );
            if ( match != null )
            {
                threats.add( match );
            }
        }
        // if there are unexpired positive cache entries we unsafe URL
        if ( !threats.isEmpty() )
        {
            LOGGER.info( "Unexpired positive cache hit found" );
            return threats;
        }

        // check if we have expired positive cache entries
        List<UrlHashCollision> expiredCollisions = new ArrayList<>();
        for ( UrlHashCollision collision : collisions )
        {
            ThreatMatch match = cache.getPositive( collision.fullHash, true );
            if ( match != null )
            {
                threats.add( match );
                expiredCollisions.add( collision );
            }
        }
        if ( !expiredCollisions.isEmpty() )
        {
            LOGGER.info( "Expired positive cache hit found. Sending full hash request in background." );
            // small deviation from the API protocol - we consider URL as unsafe due to expired positive cache hits
            // we do send full hash request as per protocol but in the background
            forkFullHashRequest( expiredCollisions );
            return threats;
        }

        Iterator<UrlHashCollision> it = collisions.iterator();
        while ( it.hasNext() )
        {
            if ( cache.hasNegative( it.next().fullHash ) )
            {
                it.remove();
            }
        }
        if ( collisions.isEmpty() )
        {
            return Collections.emptyList();
        }
        return requestFullHashes( collisions );
    }


    /**
     * This method does everything to check if supplied URL is listed in the local database.
     *
     * @param url
     * @return list of hash prefix collisions in local database; never {@code null}
     * @throws IOException
     */
    private List<UrlHashCollision> findHashPrefixCollisions( String url ) throws IOException
    {
        String canonicalized = canonicalizer.canonicalize( url );
        Set<String> expressions = expressionGenerator.makeExpressions( canonicalized );

        List<UrlHashCollision> collisions = new ArrayList<>();
        for ( int n = Hashing.MIN_SIGNIFICANT_BYTES; n < Hashing.MAX_SIGNIFICANT_BYTES; n++ )
        {
            for ( String expression : expressions )
            {
                String prefix = hashing.computeHashPrefix( expression, n );
                ThreatListDescriptor descriptor = presentInThreatList( prefix );
                if ( descriptor != null )
                {
                    UrlHashCollision collision = new UrlHashCollision();
                    collision.hashPrefix = prefix;
                    collision.fullHash = hashing.computeFullHash( expression );
                    collision.descriptor = descriptor;
                    collisions.add( collision );
                }
            }
        }
        return collisions;
    }


    private ThreatListDescriptor presentInThreatList( String prefix ) throws IOException
    {
        for ( ThreatListDescriptor descriptor : descriptorsCache.get() )
        {
            if ( localDatabase.contains( prefix, descriptor ) )
            {
                return descriptor;
            }
        }
        return null;
    }


    private void forkFullHashRequest( List<UrlHashCollision> collisions )
    {
        executor.execute( new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    requestFullHashes( collisions );
                }
                catch ( DecoderException | IOException | RuntimeException ex )
                {
                    LOGGER.error( "Failed to make full hash request in background", ex );
                }
            }
        } );
    }


    private List<ThreatMatch> requestFullHashes( List<UrlHashCollision> collisions ) throws DecoderException, IOException
    {
        if ( !stateHolder.isFindAllowed() )
        {
            LOGGER.info( "Skipping full hash find requests to API due to wait duration" );
            return Collections.emptyList();
        }

        List<String> clientStates = new ArrayList<>();
        for ( ThreatListDescriptor descriptor : descriptorsCache.get() )
        {
            clientStates.add( stateHolder.getState( descriptor ) );
        }

        ThreatInfo threatInfo = new ThreatInfo();
        threatInfo.getThreatTypes().clear();
        threatInfo.getPlatformTypes().clear();
        for ( UrlHashCollision collision : collisions )
        {
            threatInfo.getThreatTypes().add( collision.descriptor.getThreatType() );
            threatInfo.getPlatformTypes().add( collision.descriptor.getPlatformType() );
            threatInfo.getThreatEntries().add( makeThreatEntry( collision.hashPrefix ) );
        }

        Map<String, Object> payload = wrapPayload( "clientStates", clientStates );
        payload.put( "threatInfo", threatInfo );

        ApiResponse apiResponse;
        HttpUriRequest req = makeRequest( HttpPost.METHOD_NAME, "fullHashes:find", payload );
        try ( CloseableHttpResponse resp = httpClient.execute( req );
              InputStream is = getInputStream( resp ) )
        {
            // TODO: back-off on status codes other than 200
            apiResponse = gson.fromJson( new InputStreamReader( is ), ApiResponse.class );
        }
        try
        {
            if ( apiResponse.matches == null )
            {
                return Collections.emptyList();
            }
            List<ThreatMatch> matches = new ArrayList<>();
            for ( ThreatMatch match : apiResponse.matches )
            {
                byte[] bytes = Base64.getDecoder().decode( match.getThreat().getHash() );
                String hexFull = Hex.encodeHexString( bytes );
                if ( collisions.stream().filter( c -> c.fullHash.equalsIgnoreCase( hexFull ) ).findFirst().isPresent() )
                {
                    matches.add( match );
                    cache.putPositive( match );
                }
                else if ( apiResponse.negativeCacheDuration != null )
                {
                    long duration = gsb4jUtils.durationToMillis( apiResponse.negativeCacheDuration );
                    cache.putNegative( hexFull, duration );
                }
            }

            LOGGER.info( "Response to full hash request:{}", gson.toJson( matches ) );
            return matches;
        }
        finally
        {
            if ( apiResponse.minimumWaitDuration != null )
            {
                long duration = gsb4jUtils.durationToMillis( apiResponse.minimumWaitDuration );
                stateHolder.setMinWaitDurationForFinds( duration );
            }
        }
    }


    private ThreatEntry makeThreatEntry( String prefix ) throws DecoderException
    {
        byte[] bytes = Hex.decodeHex( prefix.toCharArray() );
        String base64encoded = Base64.getEncoder().encodeToString( bytes );

        ThreatEntry e = new ThreatEntry();
        e.setHash( base64encoded );
        return e;
    }


    /**
     * Class to represent a hit in the local database for a hash prefix.
     */
    private static class UrlHashCollision
    {
        String hashPrefix;
        String fullHash;
        ThreatListDescriptor descriptor;
    }


    private static class ApiResponse
    {
        List<ThreatMatch> matches;
        String minimumWaitDuration;
        String negativeCacheDuration;
    }


}

