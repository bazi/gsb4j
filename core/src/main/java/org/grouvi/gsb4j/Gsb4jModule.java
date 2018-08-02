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


import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.grouvi.gsb4j.api.SafeBrowsingApiModule;
import org.grouvi.gsb4j.db.LocalDatabaseModule;
import org.grouvi.gsb4j.properties.Gsb4jPropertiesModule;

import org.apache.http.impl.client.CloseableHttpClient;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;


/**
 * Guice module for Gsb4j - Google Safe Browsing API client implementation. This is the main module to bootstrap gsb4j
 * bindings.
 *
 * @author azilet
 */
public class Gsb4jModule extends AbstractModule
{

    private final Path propertiesFile;


    /**
     * Default constructor. Using this constructor implies that external configurations are performed through system
     * properties.
     *
     * @see #Gsb4jModule(java.nio.file.Path)
     */
    public Gsb4jModule()
    {
        this.propertiesFile = null;
    }


    /**
     * Constructor with properties file.
     *
     * @param propertiesFile path to properties file
     */
    public Gsb4jModule( Path propertiesFile )
    {
        this.propertiesFile = propertiesFile;
    }


    @Override
    protected void configure()
    {
        bind( CloseableHttpClient.class )
                .annotatedWith( Names.named( Gsb4jConst.GSB4J ) )
                .toProvider( HttpClientProvider.class )
                .asEagerSingleton();

        install( new Gsb4jPropertiesModule().setPropertiesFile( propertiesFile ) );
        install( new LocalDatabaseModule() );
        install( new SafeBrowsingApiModule() );
    }


    @Provides
    @Named( Gsb4jConst.GSB4J )
    @Singleton
    Gson makeGson( EnumTypeAdapterFactory factory )
    {
        return new GsonBuilder()
                .setFieldNamingPolicy( FieldNamingPolicy.IDENTITY )
                .registerTypeAdapterFactory( factory )
                .create();
    }


    @Provides
    @Named( Gsb4jConst.GSB4J )
    @Singleton
    ScheduledExecutorService makeScheduler()
    {
        return Executors.newScheduledThreadPool( 4 );
    }

}

