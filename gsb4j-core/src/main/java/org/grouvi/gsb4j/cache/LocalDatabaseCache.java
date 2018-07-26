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

import org.grouvi.gsb4j.data.ThreatListDescriptor;


/**
 * Local database cache interface for Safe Browsing API. Primarily intended to check if a hash exists in a hashes list
 * of a threat list.
 *
 * @author azilet
 */
public interface LocalDatabaseCache
{

    /**
     * Checks if the hash exists in the descriptors hashes set.
     *
     * @param hash hash to check
     * @param descriptor hashes set descriptor to look in
     * @return {@code true} if hash exists in the cache; {@code false} otherwise
     */
    boolean contains( String hash, ThreatListDescriptor descriptor );


    /**
     * Puts hashes for the descriptor.
     *
     * @param descriptor descriptor to put hashes for
     * @param hashes hashes to cache
     */
    void put( ThreatListDescriptor descriptor, Collection<String> hashes );


    /**
     * Clears all the cache contents.
     */
    void clear();


    /**
     * Clears cache contents for the supplied descriptor.
     *
     * @param descriptor descriptor to clear content for
     */
    void clear( ThreatListDescriptor descriptor );


    /**
     * Gets state of the cache. This method returns {@code true} when the case has loaded all necessary data and isReady
     * to serve requests. This is useful for caches whose initial loading takes up a long time.
     *
     * @return {@code true} if cache is isReady for use; {@code false} otherwise
     */
    boolean isReady();

}

