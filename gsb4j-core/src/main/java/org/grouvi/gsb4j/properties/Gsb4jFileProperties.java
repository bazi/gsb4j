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

package org.grouvi.gsb4j.properties;


import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.grouvi.gsb4j.Gsb4jConst;

import com.google.inject.Inject;
import com.google.inject.name.Named;


/**
 * Implementation of {@link Gsb4jProperties} which uses supplied properties file as the source for values.
 *
 * @author <a href="https://github.com/bazi">bazi</a>
 */
class Gsb4jFileProperties implements Gsb4jProperties
{
    @Inject
    @Named( Gsb4jConst.GSB4J )
    Properties properties;


    @Override
    public String getApiKey()
    {
        return properties.getProperty( API_KEY );
    }


    @Override
    public String getApiHttpReferrer()
    {
        return properties.getProperty( API_HTTP_REFERRER );
    }


    @Override
    public Path getDataDirectory()
    {
        String dataDir = properties.getProperty( DATA_DIRECTORY );
        if ( dataDir != null && !dataDir.isEmpty() )
        {
            return Paths.get( dataDir );
        }
        return Gsb4jProperties.getDefaultDataDirectory();
    }

}

