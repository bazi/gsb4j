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

package org.grouvi.gsb4j;


/**
 * Constant values used in Gsb4j.
 *
 * @author <a href="https://github.com/bazi">bazi</a>
 */
public class Gsb4jConst
{
    /**
     * Name to distinguish things that belong to Gsb4j. Specifically, it is used to name Guice bindings for types not
     * specific to Gsb4j. For example, Gsb4j has its own binding to {@link ScheduledExecutorService} instance which is
     * annotated with this name. This avoids possible conflicts when Gsb4j module is bootstrapped with other modules
     * that have bindings to {@link ScheduledExecutorService} as well.
     */
    public static final String GSB4J = "gsb4j";

    /**
     * Base URL for Google Safe Browsing API. Note that API requests have their own paths to be resolved on this base
     * URL and so this URL has a trailing slash.
     */
    public static final String API_BASE_URL = "https://safebrowsing.googleapis.com/v4/";


    private Gsb4jConst()
    {
        // not to be constructed
    }
}

