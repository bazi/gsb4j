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


import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;

import javax.sql.DataSource;

import org.grouvi.gsb4j.api.SafeBrowsingApi;
import org.grouvi.gsb4j.api.SafeBrowsingApiModule;
import org.grouvi.gsb4j.db.LocalDatabaseModule;
import org.grouvi.gsb4j.properties.Gsb4jPropertiesModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.http.impl.client.CloseableHttpClient;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Stage;
import com.google.inject.name.Names;


/**
 * Main entry point to GSB4J application. Besides bootstrapping, this class is a place for application wide constant
 * values and utility methods.
 *
 * @author <a href="https://github.com/bazi">bazi</a>
 */
public class Gsb4j
{
    /**
     * Name to distinguish things that belong to Gsb4j. Specifically, it is used to name Guice bindings for types not
     * specific to Gsb4j. For example, Gsb4j has its own binding of
     * {@link java.util.concurrent.ScheduledExecutorService} instance which is annotated with this name. This avoids
     * possible conflicts when Gsb4j module is bootstrapped together with other modules that have bindings to the same
     * standard classes.
     */
    public static final String GSB4J = "gsb4j";

    /**
     * Base URL for Google Safe Browsing API. Note that API requests have their own paths to be resolved relative to
     * this base URL. This URL has a trailing slash.
     */
    public static final String API_BASE_URL = "https://safebrowsing.googleapis.com/v4/";

    private static final Logger LOGGER = LoggerFactory.getLogger( Gsb4j.class );
    private static Injector injector;


    Gsb4j()
    {
        // not to be constructed by users
    }


    /**
     * Gets an instance of {@link Gsb4j} class. Instances of {@link Gsb4j} are used to get API implementations.
     *
     * @return {@link Gsb4j} instance
     */
    public static Gsb4j instance()
    {
        if ( injector == null )
        {
            throw new IllegalStateException( "Gsb4j shall be bootstrapped before asking instances." );
        }
        return injector.getInstance( Gsb4j.class );
    }


    /**
     * Bootstraps Gsb4j. Modules returned by {@link #getModules()} are used.
     *
     * @return {@link Gsb4j} instance
     * @see #bootstrap(java.util.Properties)
     */
    public static Gsb4j bootstrap()
    {
        List<Module> modules = getModules();
        injector = Guice.createInjector( Stage.PRODUCTION, modules );
        return injector.getInstance( Gsb4j.class );
    }


    /**
     * Bootstraps Gsb4j. Modules returned by {@link #getModules(java.util.Properties)} are used.
     *
     * @param properties properties to be used as a source of configuration
     * @return {@link Gsb4j} instance
     * @see #bootstrap()
     */
    public static Gsb4j bootstrap( Properties properties )
    {
        List<Module> modules = getModules( properties );
        injector = Guice.createInjector( Stage.PRODUCTION, modules );
        return injector.getInstance( Gsb4j.class );
    }


    /**
     * Gets a list of modules necessary to bootstrap Gsb4j. Bootstrapping with these modules expect configuration values
     * be provided through system properties.
     *
     * @return modules
     * @see #getModules(java.util.Properties)
     * @see #bootstrap()
     */
    public static List<Module> getModules()
    {
        return Arrays.asList(
                new Gsb4jModule(),
                new Gsb4jPropertiesModule(),
                new LocalDatabaseModule(),
                new SafeBrowsingApiModule()
        );
    }


    /**
     * Gets a list of modules necessary to bootstrap Gsb4j. Supplied properties will be used as a source of
     * configuration properties.
     *
     * @param properties properties to be used as a source of configuration
     * @return modules
     * @see #getModules()
     * @see #bootstrap(java.util.Properties)
     */
    public static List<Module> getModules( Properties properties )
    {
        return Arrays.asList(
                new Gsb4jModule(),
                new Gsb4jPropertiesModule().setPropertiesFile( properties ),
                new LocalDatabaseModule(),
                new SafeBrowsingApiModule()
        );
    }


    /**
     * Parses and converts duration strings from API to milliseconds. API returns durations in seconds with up to nine
     * fractional digits, terminated by 's' like "593.44s".
     *
     * @param duration duration string to parse
     * @return duration in milliseconds
     */
    public static long durationToMillis( String duration )
    {
        double seconds = !duration.isEmpty()
                ? Double.parseDouble( duration.substring( 0, duration.length() - 1 ) )
                : 0;
        return Math.round( seconds * 1000 );
    }


    /**
     * Gets Safe Browsing API client implementation instance.
     *
     * @param name name of the API implementation type, implementation names are defined as constants in
     * {@link SafeBrowsingApi}
     * @return API implementation instance
     */
    public SafeBrowsingApi getApiClient( String name )
    {
        Set<String> validNames = SafeBrowsingApi.getImplementationNames();
        if ( !validNames.contains( name ) )
        {
            String names = String.join( ", ", validNames );
            throw new IllegalArgumentException( "Invalid name for API impl: " + name + ". Valid names: " + names );
        }
        Key<SafeBrowsingApi> key = Key.get( SafeBrowsingApi.class, Names.named( name ) );
        return injector.getInstance( key );
    }


    /**
     * Shuts down Gsb4j. This method releases all resources related to Gsb4j.
     */
    public void shutdown()
    {
        // stop all scheduled tasks
        Key<ScheduledExecutorService> scedulerKey = Key.get( ScheduledExecutorService.class, Names.named( GSB4J ) );
        ScheduledExecutorService scheduler = injector.getInstance( scedulerKey );
        scheduler.shutdown();

        // cleanup HTTP client resources
        Key<CloseableHttpClient> httpClientKey = Key.get( CloseableHttpClient.class, Names.named( GSB4J ) );
        CloseableHttpClient httpClient = injector.getInstance( httpClientKey );
        close( httpClient, "HTTP client" );

        // close db connections
        Key<DataSource> dataSourceKey = Key.get( DataSource.class, Names.named( GSB4J ) );
        DataSource dataSource = injector.getInstance( dataSourceKey );
        if ( dataSource instanceof Closeable )
        {
            close( ( Closeable ) dataSource, "DB pool" );
        }
    }


    private void close( Closeable closeable, String objectType )
    {
        try
        {
            closeable.close();
        }
        catch ( IOException ex )
        {
            LOGGER.error( "Failed to close {}: {}", objectType, ex.getMessage() );
        }
    }

}

