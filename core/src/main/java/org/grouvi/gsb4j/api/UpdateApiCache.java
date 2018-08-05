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


import java.util.Base64;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.grouvi.gsb4j.Gsb4j;
import org.grouvi.gsb4j.data.ThreatMatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.codec.binary.Hex;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;


/**
 * Cache for threat matches from Update API.
 *
 * @author azilet
 */
@Singleton
class UpdateApiCache extends ApiResponseCacheBase
{
    private static final Logger LOGGER = LoggerFactory.getLogger( UpdateApiCache.class );

    private ConcurrentMap<String, ThreatMatch> positiveCache = new ConcurrentHashMap<>();
    private ConcurrentMap<String, NegativeCacheEntry> negativeCache = new ConcurrentHashMap<>();


    @Inject
    UpdateApiCache( @Named( Gsb4j.GSB4J ) ScheduledExecutorService scheduler )
    {
        startMe( scheduler, 1, 3, TimeUnit.MINUTES );
    }


    @Override
    public void run()
    {
        int removedNegative = 0;
        Iterator<NegativeCacheEntry> negativeIt = negativeCache.values().iterator();
        while ( negativeIt.hasNext() )
        {
            NegativeCacheEntry e = negativeIt.next();
            if ( e.timestamp + e.duration < System.currentTimeMillis() )
            {
                negativeIt.remove();
                removedNegative++;
            }
        }
        if ( removedNegative > 0 )
        {
            LOGGER.info( "Removed {} negative cache entries", removedNegative );
        }

        int removedPositive = 0;
        Iterator<ThreatMatch> positiveIt = positiveCache.values().iterator();
        while ( positiveIt.hasNext() )
        {
            ThreatMatch match = positiveIt.next();
            if ( isExpired( match ) )
            {
                // expired positive cache entries are not immediately removed; we clear after 24 hours 
                if ( System.currentTimeMillis() - match.getTimestamp() > TimeUnit.HOURS.toMillis( 24 ) )
                {
                    positiveIt.remove();
                    removedPositive++;
                }
            }
        }
        if ( removedPositive > 0 )
        {
            LOGGER.info( "Removed {} positive cache entries", removedPositive );
        }
    }


    /**
     * Gets positive cache entry for the full hash.
     *
     * @param fullHash full hash to get positive threat match for
     * @param expired indicates if selected cache entry shall be unexpired or expired
     * @return threat match if there is a positive cache entry for the full hash that matches {@code expired} parameter;
     * {@code null} otherwise
     */
    public ThreatMatch getPositive( String fullHash, boolean expired )
    {
        ThreatMatch match = positiveCache.get( fullHash );
        if ( match != null )
        {
            if ( expired && isExpired( match ) )
            {
                return match;
            }
            if ( !expired && !isExpired( match ) )
            {
                return match;
            }
        }
        return null;
    }


    /**
     * Puts threat match as a positive cache entry.
     *
     * @param match match to put cache entry for
     */
    public void putPositive( ThreatMatch match )
    {
        byte[] bytes = Base64.getDecoder().decode( match.getThreat().getHash() );
        String hexFullHash = Hex.encodeHexString( bytes );
        match.setTimestamp( System.currentTimeMillis() );
        positiveCache.put( hexFullHash, match );
    }


    /**
     * Checks if there is an unexpired negative cache entry for the full hash.
     *
     * @param fullHash full hash to check for negative cache entry
     * @return {@code true} if there is an unexpired negative cache entry; {@code falses} otherwise
     */
    public boolean hasNegative( String fullHash )
    {
        NegativeCacheEntry e = negativeCache.get( fullHash );
        return e != null && e.timestamp + e.duration > System.currentTimeMillis();
    }


    /**
     * Puts a negative cache entry for the full hash.
     *
     * @param fullHash full hash to put negative cache entry for
     * @param duration negative duration to cache threat match
     */
    public void putNegative( String fullHash, long duration )
    {
        negativeCache.put( fullHash, new NegativeCacheEntry( duration ) );
    }


    private class NegativeCacheEntry
    {
        long duration;
        long timestamp = System.currentTimeMillis();


        public NegativeCacheEntry( long duration )
        {
            this.duration = duration;
        }
    }


}

