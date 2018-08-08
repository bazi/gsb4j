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

package kg.net.bazi.gsb4j.db;


import javax.sql.DataSource;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import kg.net.bazi.gsb4j.Gsb4j;


/**
 * Guice module to initialize database related bindings.
 * <p>
 * This module is not supposed to be used directly! To bootstrap, consider bootstrap methods in {@link Gsb4j} or methods
 * that return list of all necessary modules.
 *
 * @author azilet
 */
public class LocalDatabaseModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind( LocalDatabase.class ).to( SqlLocalDatabase.class );

        bind( DataSource.class ).annotatedWith( Names.named( Gsb4j.GSB4J ) )
                .toProvider( DbConnectionProvider.class ).asEagerSingleton();
    }
}

