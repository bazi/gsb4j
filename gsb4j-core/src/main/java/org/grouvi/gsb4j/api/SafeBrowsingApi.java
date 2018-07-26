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


import org.grouvi.gsb4j.data.ThreatMatch;


/**
 * Sage Browsing API interface.
 *
 * @author azilet
 */
public interface SafeBrowsingApi
{

    /**
     * Tag name to identify Lookup API client implementation.
     */
    String LOOKUP_API = "lookup";

    /**
     * Tag name to identify Update API client implementation.
     */
    String UPDATE_API = "update";


    /**
     * Identifies if this Safe Browsing API implementation is a Lookup API or an Update API.
     *
     * @return {@code true} if this is a Lookup API implementation; {@code false} if it is not, i.e. it is an Update API
     * implementation
     */
    boolean isLookupApi();


    /**
     * Checks the supplied URL if it is in the threat lists of the Google Safe Browsing API.
     *
     * @param url URL to check
     * @return threat match if URL is found in one of threat lists; {@code null} otherwise which means URL is safe
     */
    ThreatMatch check( String url );


}

