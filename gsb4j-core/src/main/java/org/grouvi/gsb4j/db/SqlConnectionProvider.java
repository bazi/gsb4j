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
package org.grouvi.gsb4j.db;


import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import javax.sql.DataSource;

import org.sqlite.JDBC;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.ProvisionException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;


/**
 * Data source provider for SQL local database.
 *
 * @author azilet
 */
class SqlConnectionProvider implements Provider<DataSource>
{
    private DataSource dataSource;


    @Inject
    SqlConnectionProvider()
    {
        Properties properties = new Properties();
        try ( InputStream is = ClassLoader.getSystemResourceAsStream( "db.sb.properties" ) )
        {
            properties.load( is );
        }
        catch ( IOException ex )
        {
            throw new ProvisionException( "Failed to load local database properties", ex );
        }

        String jdbcUrl = properties.getProperty( "jdbcUrl" );
        if ( jdbcUrl != null && jdbcUrl.startsWith( JDBC.PREFIX ) )
        {
            Path path = Paths.get( jdbcUrl.substring( JDBC.PREFIX.length() ) );
            if ( !Files.exists( path.getParent() ) )
            {
                try
                {
                    Files.createDirectories( path.getParent() );
                }
                catch ( IOException ex )
                {
                    throw new ProvisionException( "Failed to create data directory", ex );
                }
            }
        }
        else
        {
            throw new ProvisionException( "Invalid JDBC URL" );
        }

        HikariConfig config = new HikariConfig( properties );
        this.dataSource = new HikariDataSource( config );
    }


    @Override
    public DataSource get()
    {
        return dataSource;
    }
}

