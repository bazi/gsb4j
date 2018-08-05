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


import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.grouvi.gsb4j.api.SafeBrowsingApiModule;
import org.grouvi.gsb4j.db.LocalDatabaseModule;
import org.grouvi.gsb4j.properties.Gsb4jPropertiesModule;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;


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
     * Base URL for Google Safe Browsing API. Note that API requests have their own paths to be resolved on this base
     * URL and so this URL has a trailing slash.
     */
    public static final String API_BASE_URL = "https://safebrowsing.googleapis.com/v4/";


    private Gsb4j()
    {
        // not to be constructed
    }


    /**
     * Bootstraps Gsb4j. Modules returned by {@link #getModules()} are used.
     *
     * @return injector for the supplied set of modules
     * @see #bootstrap(java.util.Properties)
     */
    public static Injector bootstrap()
    {
        List<Module> modules = getModules();
        return Guice.createInjector( Stage.PRODUCTION, modules );
    }


    /**
     * Bootstraps Gsb4j. Modules returned by {@link #getModules(java.util.Properties)} are used.
     *
     * @param properties properties to be used as a source of configuration
     * @return injector for the supplied set of modules
     * @see #bootstrap()
     */
    public static Injector bootstrap( Properties properties )
    {
        List<Module> modules = getModules( properties );
        return Guice.createInjector( Stage.PRODUCTION, modules );
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
}

