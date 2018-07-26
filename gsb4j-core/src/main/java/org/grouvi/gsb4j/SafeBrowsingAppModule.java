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
package org.grouvi.gsb4j;


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.grouvi.gsb4j.api.SafeBrowsingApiModule;
import org.grouvi.gsb4j.db.LocalDatabaseModule;

import org.apache.http.impl.client.CloseableHttpClient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.ProvisionException;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;


/**
 * Guice module for Safe Browsing API client implementation.
 *
 * @author azilet
 */
public class SafeBrowsingAppModule extends AbstractModule
{
    public static final String TAG = "safe-browsing";


    @Override
    protected void configure()
    {
        bind( CloseableHttpClient.class )
                .annotatedWith( Names.named( TAG ) )
                .toProvider( HttpClientProvider.class )
                .asEagerSingleton();

        install( new LocalDatabaseModule() );
        install( new SafeBrowsingApiModule() );
    }


    @Provides
    @Named( TAG )
    @Singleton
    Gson makeGson( EnumTypeAdapterFactory factory )
    {
        return new GsonBuilder()
                .registerTypeAdapterFactory( factory )
                .create();
    }


    @Provides
    @Named( TAG )
    @Singleton
    Properties getProperties()
    {
        Properties properties = new Properties();
        try ( InputStream is = ClassLoader.getSystemResourceAsStream( "app.properties" ) )
        {
            properties.load( is );
            return properties;
        }
        catch ( IOException ex )
        {
            throw new ProvisionException( "Failed to read properties file: " + ex.getMessage() );
        }
    }


    @Provides
    @Named( TAG )
    @Singleton
    ScheduledExecutorService makeScheduler()
    {
        return Executors.newScheduledThreadPool( 4 );
    }

}

