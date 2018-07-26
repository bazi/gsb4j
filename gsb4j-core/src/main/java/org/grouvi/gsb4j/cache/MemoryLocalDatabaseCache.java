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
package org.grouvi.gsb4j.cache;


import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;

import org.grouvi.gsb4j.SafeBrowsingAppModule;
import org.grouvi.gsb4j.data.ThreatListDescriptor;
import org.grouvi.gsb4j.db.LocalDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;


/**
 * In-memory implementation of {@link LocalDatabaseCache}. This implementation provides the best performance in the cost
 * of memory size used. Use this implementation if memory size is not a concern.
 *
 * @author azilet
 */
class MemoryLocalDatabaseCache implements LocalDatabaseCache
{
    private static final Logger LOGGER = LoggerFactory.getLogger( MemoryLocalDatabaseCache.class );

    private ConcurrentMap<ThreatListDescriptor, Set<String>> map = new ConcurrentHashMap<>();
    private boolean ready = false;


    @Inject
    MemoryLocalDatabaseCache( LocalDatabase localDatabase, ThreatListDescriptorsCache descriptorsCache,
                              @Named( SafeBrowsingAppModule.TAG ) ScheduledExecutorService executor )
    {
        startLoading( localDatabase, descriptorsCache, executor );
    }


    @Override
    public boolean contains( String hash, ThreatListDescriptor descriptor )
    {
        Set<String> set = map.get( descriptor );
        return set != null && set.contains( hash );
    }


    @Override
    public void put( ThreatListDescriptor descriptor, Collection<String> hashes )
    {
        if ( hashes instanceof Set )
        {
            map.put( descriptor, ( Set ) hashes );
        }
        else
        {
            map.put( descriptor, new HashSet<>( hashes ) );
        }
    }


    @Override
    public void clear()
    {
        map.clear();
    }


    @Override
    public void clear( ThreatListDescriptor descriptor )
    {
        map.remove( descriptor );
    }


    @Override
    public boolean isReady()
    {
        return ready;
    }


    private void startLoading( LocalDatabase localDatabase, ThreatListDescriptorsCache descriptorsCache,
                               ScheduledExecutorService executor )
    {
        executor.execute( new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    for ( ThreatListDescriptor descriptor : descriptorsCache.get() )
                    {
                        List<String> hashes = localDatabase.load( descriptor );
                        map.put( descriptor, new HashSet<>( hashes ) );
                        LOGGER.info( "Loaded {} hash prefixes for {}", hashes.size(), descriptor );
                    }
                }
                catch ( IOException | RuntimeException ex )
                {
                    LOGGER.error( "Failed to init cache", ex );
                }
                finally
                {
                    ready = true;
                }
            }
        } );
    }


}

