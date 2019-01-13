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

import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;

import java.util.Arrays;
import java.util.EnumSet;

import javax.servlet.DispatcherType;

import kg.net.bazi.gsb4j.Gsb4j;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.util.component.LifeCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class to start Gsb4j HTTP endpoint.
 *
 * @author bazi
 */
public class Gsb4jHttpServer {

    static final Logger LOGGER = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

    /**
     * Main method of Gsb4j HTTP module. Bootstraps DI and starts Jetty server. The port for the server can be supplied
     * by system property "http.port" (defaults to 8080).
     *
     * @param args arguments not used
     * @throws InterruptedException when web server thread is interrupted
     */
    public static void main(String[] args) throws InterruptedException {
        int port = Integer.parseInt(System.getProperty("http.port", "8080"));
        Server server;
        try {
            Gsb4j gsb4j = Gsb4j.bootstrap();
            Injector injector = bootstrapDependencyInjector(gsb4j);

            server = initServer(port, injector);
            server.addLifeCycleListener(new Gsb4jHttpLifeCycleListener(gsb4j));
            server.setStopAtShutdown(true);
            server.start();
        } catch (Exception ex) {
            LOGGER.error("Failed to start Gsb4j HTTP endpoint", ex);
            System.exit(1);
            return;
        }
        server.join();
    }

    private static Injector bootstrapDependencyInjector(Gsb4j gsb4j) {
        Injector injector = gsb4j.getInjector();
        return injector.createChildInjector(Arrays.asList(new Gsb4jServletModule()));
    }

    private static Server initServer(int port, Injector injector) {
        Server server = new Server();
        server.setStopAtShutdown(true);

        ServerConnector http = new ServerConnector(server);
        http.setPort(port);
        http.setIdleTimeout(15000);

        server.addConnector(http);

        GzipHandler gzipHandler = new GzipHandler();
        server.setHandler(gzipHandler);

        ServletContextHandler handler = new ServletContextHandler();
        handler.setContextPath("/gsb4j");

        FilterHolder guiceFilter = new FilterHolder(injector.getInstance(GuiceFilter.class));
        handler.addFilter(guiceFilter, "/*", EnumSet.allOf(DispatcherType.class));

        gzipHandler.setHandler(handler);

        // servlets can be added here but they will not be filtered by Guice
        return server;
    }

    static class Gsb4jHttpLifeCycleListener extends AbstractLifeCycle.AbstractLifeCycleListener {

        private final Gsb4j gsb4j;

        private Gsb4jHttpLifeCycleListener(Gsb4j gsb4j) {
            this.gsb4j = gsb4j;
        }

        @Override
        public void lifeCycleStopped(LifeCycle event) {
            gsb4j.shutdown();
        }
    }

}
