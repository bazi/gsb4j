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


import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.grouvi.gsb4j.api.ThreatListGetter;
import org.grouvi.gsb4j.data.ThreatListDescriptor;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;


/**
 * Holds currently available threat list descriptors. Updated on every update request to API.
 *
 * @author azilet
 */
@Singleton
public class ThreatListDescriptorsCache
{
    @Inject
    private Provider<ThreatListGetter> threatListGetterProvider;

    private final Set<ThreatListDescriptor> cache = new HashSet<>();


    /**
     * Gets cached threat list descriptors.
     *
     * @return descriptors collection
     */
    public Collection<ThreatListDescriptor> get()
    {
        if ( cache.isEmpty() )
        {
            return getRefreshed();
        }
        return Collections.unmodifiableCollection( cache );
    }


    /**
     * Gets threat list descriptors first refreshing the cache.
     *
     * @return descriptors
     */
    public Collection<ThreatListDescriptor> getRefreshed()
    {
        List<ThreatListDescriptor> ls = threatListGetterProvider.get().getLists();
        put( ls );
        return Collections.unmodifiableCollection( cache );
    }


    /**
     * Updates threat list descriptors.
     *
     * @param descriptors descriptors to put into cache
     */
    public synchronized void put( List<ThreatListDescriptor> descriptors )
    {
        cache.addAll( descriptors );
    }
}

