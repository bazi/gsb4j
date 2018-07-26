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


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;


/**
 * Utility class that contains methods to ease working with HTTP entities.
 *
 * @author azilet
 */
public class HttpHelper
{
    private static final int BUFFER_SIZE = 1024 * 4;
    /**
     * Pattern to retrieve charset from content type string.
     */
    private static final Pattern CHARSET_PATTERN = Pattern.compile( "charset=([\\w-]+)", Pattern.CASE_INSENSITIVE );


    HttpHelper()
    {
    }


    /**
     * Reads whole response data as a string.
     *
     * @param response http response to read data from
     * @return string representation of the response
     * @throws IOException when could not read response stream
     */
    public String readAsString( HttpResponse response ) throws IOException
    {
        StringBuilder sb = new StringBuilder();
        try ( InputStreamReader isr = new InputStreamReader( getInputStream( response ) ) )
        {
            int n;
            char[] buf = new char[BUFFER_SIZE];
            while ( ( n = isr.read( buf ) ) != -1 )
            {
                sb.append( buf, 0, n );
            }
        }
        return sb.toString();
    }


    /**
     * Gets input stream of the http response. Stream is returned only if the response is OK and a valid content stream
     * exists. Exception is thrown otherwise.
     *
     * @param response http response to get input stream from
     * @return input stream if valid stream exists; never {@code null}
     * @throws IOException when no valid response stream was found; for example there is no response stream if the
     * response status is not HTTP 200, or there is not response entity at all
     */
    public InputStream getInputStream( HttpResponse response ) throws IOException
    {
        if ( response.getStatusLine().getStatusCode() != HttpStatus.SC_OK )
        {
            throw new IOException( "Response status code is not OK: " + response.getStatusLine() );
        }
        if ( response.getEntity() == null )
        {
            throw new IOException( "No message entity found in response" );
        }
        return response.getEntity().getContent();
    }


    /**
     * Gets content type of the HTTP response.
     *
     * @param response response whose content type is returned
     * @return content type if corresponding headers exist; {@code null} otherwise
     */
    public final String getContentType( HttpResponse response )
    {
        Header contentTypeHeader;
        HttpEntity entity = response.getEntity();
        if ( entity != null )
        {
            contentTypeHeader = response.getEntity().getContentType();
        }
        else
        {
            contentTypeHeader = response.getFirstHeader( HttpHeaders.CONTENT_TYPE );
        }
        if ( contentTypeHeader != null )
        {
            return contentTypeHeader.getValue();
        }
        return null;
    }


    /**
     * Gets charset of the response.
     *
     * @param response HTTP response to get charset from
     * @return charset specified in the response; {@code null} if not specified
     */
    public final String getCharset( HttpResponse response )
    {
        String contentType = getContentType( response );
        if ( contentType != null )
        {
            Matcher matcher = CHARSET_PATTERN.matcher( contentType );
            if ( matcher.find() )
            {
                return matcher.group( 1 );
            }
        }
        return null;
    }
}

