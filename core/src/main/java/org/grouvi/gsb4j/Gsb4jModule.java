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


import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

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
 * Guice module to initialize common bindings used Gsb4j.
 * <p>
 * This module is not supposed to be used directly! To bootstrap, consider bootstrap methods in {@link Gsb4j} or methods
 * that return list of all necessary modules.
 *
 * @author azilet
 */
public class Gsb4jModule extends AbstractModule
{

    @Override
    protected void configure()
    {
        bind( CloseableHttpClient.class )
                .annotatedWith( Names.named( Gsb4j.GSB4J ) )
                .toProvider( HttpClientProvider.class )
                .asEagerSingleton();
    }


    @Provides
    @Named( Gsb4j.GSB4J )
    @Singleton
    Gson makeGson( EnumTypeAdapterFactory factory )
    {
        return new GsonBuilder()
                .setFieldNamingPolicy( FieldNamingPolicy.IDENTITY )
                .registerTypeAdapterFactory( factory )
                .create();
    }


    @Provides
    @Named( Gsb4j.GSB4J )
    @Singleton
    ScheduledExecutorService makeScheduler()
    {
        return Executors.newScheduledThreadPool( 4 );
    }

}

