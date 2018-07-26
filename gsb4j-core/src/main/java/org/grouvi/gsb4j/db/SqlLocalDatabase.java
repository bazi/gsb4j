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


import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.sql.DataSource;

import org.grouvi.gsb4j.SafeBrowsingAppModule;
import org.grouvi.gsb4j.data.ThreatListDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;


/**
 * SQL database backed implementation of {@link LocalDatabase}.
 *
 * @author azilet
 */
class SqlLocalDatabase implements LocalDatabase
{
    private static final Logger LOGGER = LoggerFactory.getLogger( SqlLocalDatabase.class );

    private static final Set<ThreatListDescriptor> CREATED_TABLES = new HashSet<>();
    private static final Lock LOCK = new ReentrantLock();

    final int BATCH_SIZE = 50 * 1000;

    @Inject
    @Named( SafeBrowsingAppModule.TAG )
    DataSource dataSource;


    @Override
    public List<String> load( ThreatListDescriptor descriptor ) throws IOException
    {
        checkTable( descriptor );

        String s = "SELECT prefix FROM " + descriptor + " ORDER BY prefix";
        try ( Connection conn = dataSource.getConnection();
              PreparedStatement ps = conn.prepareStatement( s ) )
        {
            List<String> result = new LinkedList<>();
            ResultSet rs = ps.executeQuery();
            while ( rs.next() )
            {
                result.add( rs.getString( 1 ) );
            }
            LOGGER.info( "Loaded {} items", result.size() );
            return result;
        }
        catch ( SQLException ex )
        {
            throw new IOException( ex );
        }
    }


    @Override
    public BufferedReader loadReader( ThreatListDescriptor descriptor ) throws IOException
    {
        throw new UnsupportedOperationException( "Buffered reader can not be created for SQL database." );
    }


    @Override
    public void persist( ThreatListDescriptor descriptor, List<String> hashes ) throws IOException
    {
        checkTable( descriptor );

        String s = "INSERT INTO " + descriptor + " VALUES (?)";
        try ( Connection conn = dataSource.getConnection();
              PreparedStatement ps = conn.prepareStatement( s ) )
        {
            int offset = 0;
            int inserted = 0;
            for ( String hash : hashes )
            {
                ps.setString( 1, hash );
                inserted += ps.executeUpdate();
                if ( ++offset == BATCH_SIZE )
                {
                    conn.commit();
                    LOGGER.info( "Inserted {} item(s)", inserted );
                    offset = 0;
                    inserted = 0;
                }
            }
            if ( offset > 0 )
            {
                conn.commit();
                LOGGER.info( "Inserted {} item(s)", inserted );
            }
        }
        catch ( SQLException ex )
        {
            throw new IOException( ex );
        }
    }


    @Override
    public boolean contains( String hash, ThreatListDescriptor descriptor ) throws IOException
    {
        checkTable( descriptor );

        String s = "SELECT prefix FROM " + descriptor + " WHERE prefix=?";
        try ( Connection conn = dataSource.getConnection();
              PreparedStatement ps = conn.prepareStatement( s ) )
        {
            ps.setString( 1, hash );
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
        catch ( SQLException ex )
        {
            throw new IOException( ex );
        }
    }


    @Override
    public void clear( ThreatListDescriptor descriptor ) throws IOException
    {
        try ( Connection conn = dataSource.getConnection();
              Statement st = conn.createStatement() )
        {
            st.execute( "DROP TABLE IF EXISTS " + descriptor );
            conn.commit();
            CREATED_TABLES.remove( descriptor );
        }
        catch ( SQLException ex )
        {
            throw new IOException( ex );
        }
        LOGGER.info( "Table {} dropped", descriptor );
    }


    private void checkTable( ThreatListDescriptor descriptor ) throws IOException
    {
        if ( CREATED_TABLES.contains( descriptor ) )
        {
            return;
        }
        Lock ref = LOCK;
        ref.lock();
        try
        {
            if ( !CREATED_TABLES.contains( descriptor ) )
            {
                _createTable( descriptor );
                CREATED_TABLES.add( descriptor );
            }
        }
        catch ( SQLException ex )
        {
            throw new IOException( ex );
        }
        finally
        {
            ref.unlock();
        }
    }


    private void _createTable( ThreatListDescriptor descriptor ) throws SQLException
    {
        String sql = "CREATE TABLE IF NOT EXISTS " + descriptor
                + " (prefix TEXT CONSTRAINT pk PRIMARY KEY ASC ON CONFLICT REPLACE)";
        try ( Connection conn = dataSource.getConnection();
              PreparedStatement ps = conn.prepareStatement( sql ) )
        {
            ps.execute();
            conn.commit();
        }
    }

}

