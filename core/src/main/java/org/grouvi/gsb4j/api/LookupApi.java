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
import java.util.List;
import java.util.Map;

import org.grouvi.gsb4j.data.ThreatEntry;
import org.grouvi.gsb4j.data.ThreatInfo;
import org.grouvi.gsb4j.data.ThreatMatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;

import com.google.inject.Inject;


/**
 * Lookup API interface.
 *
 * @author azilet
 */
class LookupApi extends SafeBrowsingApiBase implements SafeBrowsingApi
{
    private static final Logger LOGGER = LoggerFactory.getLogger( LookupApi.class );

    @Inject
    private LookupApiCache cache;


    @Override
    public ThreatMatch check( String url )
    {
        ThreatMatch cached = cache.get( url );
        if ( cached != null )
        {
            LOGGER.info( "Cached URL found: {}", url );
            return cached;
        }
        return requestApi( url );
    }


    @Override
    Logger getLogger()
    {
        return LOGGER;
    }


    private ThreatMatch requestApi( String url )
    {
        ThreatInfo threatInfo = new ThreatInfo();
        threatInfo.getThreatEntries().add( makeThreatEntry( url ) );

        Map<String, Object> body = wrapPayload( "threatInfo", threatInfo );
        HttpUriRequest req = makeRequest( HttpPost.METHOD_NAME, "threatMatches:find", body );

        try ( CloseableHttpResponse resp = httpClient.execute( req );
              InputStream is = getInputStream( resp ) )
        {
            ApiResponse apiResponse = gson.fromJson( new InputStreamReader( is ), ApiResponse.class );
            if ( apiResponse.matches != null && !apiResponse.matches.isEmpty() )
            {
                ThreatMatch match = selectMoreGenericThreat( apiResponse.matches );
                cache.put( match );
                return match;
            }
        }
        catch ( IOException ex )
        {
            LOGGER.error( "Failed to query Lookup API", ex );
        }
        return null;
    }


    private ThreatEntry makeThreatEntry( String url )
    {
        ThreatEntry e = new ThreatEntry();
        e.setUrl( url );
        return e;
    }


    private static class ApiResponse
    {
        List<ThreatMatch> matches;
    }


}

