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


import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


/**
 * Simple percent encoding class. Percent encoding is basically a URL encoding that is performed by {@link URLEncoder}
 * with the exception of space character which is encoded as "+" according to "application/x-www-form-urlencoded" specs.
 * This class encodes spaces to "%20". TODO:
 *
 * @author azilet
 */
public class PercentEncoder
{


    /**
     * Encodes selected characters in a supplied string to percent encoding.
     * <p>
     * NOTE: hexadecimal digits are in upper case
     *
     * @param s string to encode
     * @return encoded string
     */
    public String encode( String s )
    {
        try
        {
            String urlEncoded = URLEncoder.encode( s, StandardCharsets.UTF_8.name() );
            return urlEncoded.replace( "+", "%20" );
        }
        catch ( UnsupportedEncodingException ex )
        {
            throw new IllegalStateException( "Shall not happen", ex );
        }
    }


    /**
     * Decodes percent encoded string.
     *
     * @param s string to decode
     * @return decoded string
     */
    public String decode( String s )
    {
        try
        {
            return URLDecoder.decode( s, StandardCharsets.UTF_8.name() );
        }
        catch ( UnsupportedEncodingException ex )
        {
            throw new IllegalStateException( "Shall not happen", ex );
        }
    }

}
