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

package org.grouvi.gsb4j.util;


/**
 * Utility methods used throughout application.
 *
 * @author azilet
 */
public class Gsb4jUtils
{

    /**
     * Parses and converts duration strings from API to milliseconds. API returns durations in seconds with up to nine
     * fractional digits, terminated by 's' like "593.44s".
     *
     * @param duration duration string to parse
     * @return duration in milliseconds
     */
    public long durationToMillis( String duration )
    {
        double seconds = !duration.isEmpty()
                ? Double.parseDouble( duration.substring( 0, duration.length() - 1 ) )
                : 0;
        return Math.round( seconds * 1000 );
    }

}

