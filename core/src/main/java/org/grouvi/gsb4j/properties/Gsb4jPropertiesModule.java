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


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Properties;

import org.grouvi.gsb4j.Gsb4jConst;

import com.google.inject.AbstractModule;
import com.google.inject.ProvisionException;
import com.google.inject.name.Names;


/**
 * Guice module to initialize bindings related to gsb4j properties.
 *
 * @author <a href="https://github.com/bazi">bazi</a>
 */
public class Gsb4jPropertiesModule extends AbstractModule
{

    private Path propertiesFile;


    /**
     * Sets path to properties file.
     *
     * @param propertiesFile path to properties file; may be {@code null}
     * @return this class for chained setup
     */
    public Gsb4jPropertiesModule setPropertiesFile( Path propertiesFile )
    {
        this.propertiesFile = propertiesFile;
        return this;
    }


    @Override
    protected void configure()
    {
        if ( propertiesFile != null )
        {
            Properties prop = readProperties( propertiesFile );
            bind( Properties.class ).annotatedWith( Names.named( Gsb4jConst.GSB4J ) ).toInstance( prop );
            bind( Gsb4jProperties.class ).to( Gsb4jFileProperties.class );
        }
        else
        {
            bind( Gsb4jProperties.class ).to( Gsb4jSystemProperties.class );
        }
    }


    private Properties readProperties( Path propertiesFile )
    {
        try ( InputStream is = new FileInputStream( propertiesFile.toFile() ) )
        {
            Properties prop = new Properties();
            prop.load( is );
            return prop;
        }
        catch ( IOException ex )
        {
            throw new ProvisionException( "Failed to read properties file", ex );
        }
    }

}

