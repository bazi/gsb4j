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
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.grouvi.gsb4j.data.ThreatListDescriptor;
import org.grouvi.gsb4j.util.SbProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.ProvisionException;


/**
 * File system backed implementation of {@link LocalDatabase}.
 *
 * @author azilet
 */
class FileLocalDatabase implements LocalDatabase
{
    private static final Logger LOGGER = LoggerFactory.getLogger( FileLocalDatabase.class );

    private final Path parent;


    @Inject
    FileLocalDatabase( SbProperties sbProperties )
    {
        Path dbDirectory = sbProperties.getDbDirectory();
        if ( !Files.exists( dbDirectory ) )
        {
            try
            {
                Files.createDirectories( dbDirectory );
            }
            catch ( IOException ex )
            {
                throw new ProvisionException( "Failed to create parent directory", ex );
            }
        }
        this.parent = dbDirectory;
    }


    @Override
    public List<String> load( ThreatListDescriptor descriptor ) throws IOException
    {
        Path path = makePathToDataFile( descriptor );
        if ( Files.exists( path ) )
        {
            List<String> result = new LinkedList<>();
            try ( BufferedReader br = new BufferedReader( new FileReader( path.toFile() ) ) )
            {
                String hash;
                while ( ( hash = br.readLine() ) != null )
                {
                    result.add( hash );
                }
            }
            return result;
        }
        else
        {
            LOGGER.info( "No local file for {}", descriptor );
        }
        return Collections.emptyList();
    }


    @Override
    public BufferedReader loadReader( ThreatListDescriptor descriptor ) throws IOException
    {
        Path path = makePathToDataFile( descriptor );
        if ( Files.exists( path ) )
        {
            return new BufferedReader( new FileReader( path.toFile() ) );
        }
        LOGGER.info( "No local file for {}", descriptor );
        return new BufferedReader( new StringReader( "" ) );
    }


    @Override
    public void persist( ThreatListDescriptor descriptor, List<String> hashes ) throws IOException
    {
        Path path = makePathToDataFile( descriptor );
        try ( BufferedWriter bw = new BufferedWriter( new FileWriter( path.toFile() ) ) )
        {
            for ( String hash : hashes )
            {
                bw.write( hash );
                bw.newLine();
            }
        }
    }


    @Override
    public boolean contains( String hash, ThreatListDescriptor descriptor ) throws IOException
    {
        // very very inefficient implementation!!! local database cache shall be used instead
        try ( BufferedReader br = loadReader( descriptor ) )
        {
            String line;
            while ( ( line = br.readLine() ) != null )
            {
                if ( line.equals( hash ) )
                {
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    public void clear( ThreatListDescriptor descriptor )
    {
        Path path = makePathToDataFile( descriptor );
        try
        {
            Files.deleteIfExists( path );
        }
        catch ( IOException ex )
        {
            LOGGER.error( "Failed to delete database file for {}", descriptor, ex );
        }
    }


    private Path makePathToDataFile( ThreatListDescriptor descriptor )
    {
        return parent.resolve( descriptor.toString() );
    }


}

