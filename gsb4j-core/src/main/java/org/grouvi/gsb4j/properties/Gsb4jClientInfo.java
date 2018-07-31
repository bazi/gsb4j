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

package org.grouvi.gsb4j.properties;


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.grouvi.gsb4j.data.ClientInfo;

import com.google.inject.ProvisionException;
import com.google.inject.Singleton;


/**
 * Information about Google Safe Browsing API client implementation. Client info should uniquely identify a client
 * implementation, not an individual user.
 *
 * @author <a href="https://github.com/bazi">bazi</a>
 */
@Singleton
public class Gsb4jClientInfo
{
    /**
     * A client ID that uniquely identifies the client implementation of the Safe Browsing API.
     */
    public final String CLIENT_ID;

    /**
     * The version of the client implementation.
     */
    public final String CLIENT_VERSION;


    private Gsb4jClientInfo()
    {
        Properties prop = new Properties();
        try ( InputStream is = ClassLoader.getSystemResourceAsStream( "client-info.properties" ) )
        {
            prop.load( is );
        }
        catch ( IOException ex )
        {
            throw new ProvisionException( "Failed to load client implementation info", ex );
        }
        CLIENT_ID = prop.getProperty( "client.id" );
        CLIENT_VERSION = prop.getProperty( "client.version" );
    }


    /**
     * Makes a client info for this implementation.
     *
     * @return client info instance
     */
    public ClientInfo make()
    {
        ClientInfo ci = new ClientInfo();
        ci.setClientId( CLIENT_ID );
        ci.setClientVersion( CLIENT_VERSION );
        return ci;
    }
}

