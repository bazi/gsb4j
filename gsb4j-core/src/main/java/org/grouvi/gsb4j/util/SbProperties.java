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
package org.grouvi.gsb4j.util;


import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.grouvi.gsb4j.SafeBrowsingAppModule;
import org.grouvi.gsb4j.data.ClientInfo;

import com.google.inject.Inject;
import com.google.inject.name.Named;


/**
 * Safe Browsing app specific helper methods that depend on values from properties file.
 *
 * @author azilet
 */
public class SbProperties
{
    @Inject
    @Named( SafeBrowsingAppModule.TAG )
    private Properties properties;


    /**
     * Gest API key from properties file.
     *
     * @return API key
     */
    public String getApiKey()
    {
        return properties.getProperty( "api.key" );
    }


    /**
     * Gets base URL of the API. URL is expected to include trailing slash.
     *
     * @return base URL of the API
     */
    public String getBaseUrl()
    {
        String url = properties.getProperty( "base.url" );
        return url.endsWith( "/" ) ? url : url + "/";
    }


    /**
     * Gets parent directory for local database files.
     *
     * @return path to parent directory
     */
    public Path getDbDirectory()
    {
        String dir = properties.getProperty( "data.dir", "." );
        return Paths.get( dir );
    }


    /**
     * Makes a client info for this implementation. Client ID and version values are read from properties file.
     *
     * @return client info instance
     */
    public ClientInfo makeClientInfo()
    {
        ClientInfo ci = new ClientInfo();
        ci.setClientId( properties.getProperty( "client.id" ) );
        ci.setClientVersion( properties.getProperty( "client.version" ) );
        return ci;
    }


}

