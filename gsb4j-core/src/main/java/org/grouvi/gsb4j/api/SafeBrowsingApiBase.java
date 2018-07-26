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


import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.grouvi.gsb4j.SafeBrowsingAppModule;
import org.grouvi.gsb4j.data.PlatformType;
import org.grouvi.gsb4j.data.ThreatMatch;
import org.grouvi.gsb4j.util.HttpHelper;
import org.grouvi.gsb4j.util.SbProperties;
import org.slf4j.Logger;

import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.name.Named;


/**
 * Abstract base class for Safe Browsing API implementation classes.
 *
 * @author azilet
 */
abstract class SafeBrowsingApiBase
{
    @Inject
    @Named( value = SafeBrowsingAppModule.TAG )
    CloseableHttpClient httpClient;

    @Inject
    HttpHelper httpHelper;

    @Inject
    SbProperties sbProperties;

    @Inject
    @Named( value = SafeBrowsingAppModule.TAG )
    Gson gson;


    abstract Logger getLogger();


    /**
     * Refer to {@link SafeBrowsingApiBase#selectMoreGenericThreat(org.grouvi.sb.data.ThreatMatch[])} docs.
     *
     * @param matches
     * @return
     */
    protected ThreatMatch selectMoreGenericThreat( List<ThreatMatch> matches )
    {
        return selectMoreGenericThreat( matches.toArray( new ThreatMatch[matches.size()] ) );
    }


    /**
     * Selects threat among supplied matches that has an impact to all or any of platforms.
     *
     * @param matches matches to select from
     * @return a match that has more generic impact; or first one if there is no such a threat
     */
    protected ThreatMatch selectMoreGenericThreat( ThreatMatch[] matches )
    {
        if ( matches.length > 1 )
        {
            StringBuilder sb = new StringBuilder();
            for ( ThreatMatch match : matches )
            {
                sb.append( System.lineSeparator() ).append( gson.toJson( match ) );
            }
            getLogger().info( "Multiple threat matches found: {}", sb.toString() );
        }
        for ( ThreatMatch match : matches )
        {
            if ( match.getPlatformType() == PlatformType.ALL_PLATFORMS )
            {
                return match;
            }
            if ( match.getPlatformType() == PlatformType.ANY_PLATFORM )
            {
                return match;
            }
        }
        return matches[0];
    }


    /**
     * Makes up an HTTP request based on passed arguments.
     *
     * @param httpMethod HTTP method like GET, POST, etc.
     * @param endpoint endpoint to build URL
     * @param payload payload to include in request; maybe {@code null}
     * @return HTTP request
     */
    HttpUriRequest makeRequest( String httpMethod, String endpoint, Object payload )
    {
        RequestBuilder builder = RequestBuilder.create( httpMethod )
                .setUri( sbProperties.getBaseUrl() + endpoint )
                .addParameter( "key", sbProperties.getApiKey() )
                .setHeader( HttpHeaders.CONTENT_TYPE, "application/json" );

        if ( payload != null )
        {
            builder.setEntity( new StringEntity( gson.toJson( payload ), StandardCharsets.UTF_8 ) );
        }
        return builder.build();
    }


    /**
     * Wraps payload into a map together with client info. Client info is necessary for all API requests.
     *
     * @param name name of the payload data
     * @param payload payload
     * @return map of payload and client info data
     */
    Map<String, Object> wrapPayload( String name, Object payload )
    {
        Map<String, Object> map = new HashMap<>();
        map.put( "client", sbProperties.makeClientInfo() );
        map.put( name, payload );
        return map;
    }

}

