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

package kg.net.bazi.gsb4j.http;


import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import javax.servlet.DispatcherType;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;
import com.google.inject.servlet.GuiceFilter;

import kg.net.bazi.gsb4j.Gsb4j;


/**
 * Main class to start Gsb4j HTTP endpoint.
 *
 * @author bazi
 */
public class Gsb4jHttpServer
{
    static final Logger LOGGER = LoggerFactory.getLogger( Logger.ROOT_LOGGER_NAME );


    /**
     * Main method of Gsb4j HTTP module. Bootstraps DI and starts Jetty server. The port for the server can be supplied
     * by system property "http.port" (defaults to 8080).
     *
     * @param args arguments not used
     * @throws InterruptedException
     */
    public static void main( String[] args ) throws InterruptedException
    {
        int port = Integer.parseInt( System.getProperty( "http.port", "8080" ) );
        Server server;
        try
        {
            Injector injector = bootstrapDI();
            server = initServer( port, injector );
            server.start();
        }
        catch ( Exception ex )
        {
            LOGGER.error( "Failed to start Gsb4j HTTP endpoint", ex );
            System.exit( 1 );
            return;
        }
        server.join();
    }


    private static Injector bootstrapDI()
    {
        List<Module> modules = new ArrayList<>();
        modules.addAll( Gsb4j.getModules() );
        modules.add( new Gsb4jServletModule() );
        return Guice.createInjector( Stage.PRODUCTION, modules );
    }


    private static Server initServer( int port, Injector injector )
    {
        Server server = new Server();
        server.setStopAtShutdown( true );

        ServerConnector http = new ServerConnector( server );
        http.setPort( port );
        http.setIdleTimeout( 15000 );

        server.addConnector( http );

        GzipHandler gzipHandler = new GzipHandler();
        server.setHandler( gzipHandler );

        ServletContextHandler handler = new ServletContextHandler();
        handler.setContextPath( "/gsb4j" );

        FilterHolder guiceFilter = new FilterHolder( injector.getInstance( GuiceFilter.class ) );
        handler.addFilter( guiceFilter, "/*", EnumSet.allOf( DispatcherType.class ) );

        gzipHandler.setHandler( handler );

        // servlets can be added here but they will not be filtered by Guice

        return server;
    }


}

