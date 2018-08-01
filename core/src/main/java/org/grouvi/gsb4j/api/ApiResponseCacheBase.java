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

package org.grouvi.gsb4j.api;


import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.grouvi.gsb4j.data.ThreatMatch;
import org.grouvi.gsb4j.util.Gsb4jUtils;

import com.google.inject.Inject;


/**
 * Base class for threats cache implementations.
 *
 * @author azilet
 */
abstract class ApiResponseCacheBase implements Runnable
{
    @Inject
    Gsb4jUtils gsb4jUtils;


    final void startMe( ScheduledExecutorService scheduler, long initialDelay, long delay, TimeUnit unit )
    {
        scheduler.scheduleWithFixedDelay( this, initialDelay, delay, unit );
    }


    /**
     * Checks if threat match has expired.
     *
     * @param match threat match to check
     * @return {@code true} if match has expired; {@code false} otherwise
     */
    boolean isExpired( ThreatMatch match )
    {
        String duration = match.getCacheDuration();
        return match.getTimestamp() + gsb4jUtils.durationToMillis( duration ) < System.currentTimeMillis();
    }
}
