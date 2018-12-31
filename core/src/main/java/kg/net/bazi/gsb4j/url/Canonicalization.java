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

package kg.net.bazi.gsb4j.url;


import java.net.IDN;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.DecoderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.CharMatcher;
import com.google.inject.Inject;

import kg.net.bazi.gsb4j.util.IpUtils;
import kg.net.bazi.gsb4j.util.PercentEncoder;
import kg.net.bazi.gsb4j.util.UrlSplitter;


/**
 * Class to canonicalize URLs according to Google Safe Browsing API requirements.
 *
 * @author azilet
 */
public class Canonicalization
{
    private static final Logger LOGGER = LoggerFactory.getLogger( Canonicalization.class );

    /**
     * Pattern to match HTTP schemes in URLs.
     */
    private static final Pattern HTTP_SCHEME_PATTERN = Pattern.compile( "^https?://" );
    /**
     * Pattern for percent encoded symbols. Used to find percent encoded symbols.
     */
    private static final Pattern PERCENT_SYMBOL_PATTERN = Pattern.compile( "%[0-9a-f]{2}", Pattern.CASE_INSENSITIVE );
    /**
     * Pattern for hex encoded symbols.
     */
    private static final Pattern HEX_SYMBOL_PATTERN = Pattern.compile( "\\\\x([0-9a-f]+)", Pattern.CASE_INSENSITIVE );

    @Inject
    PercentEncoder percentEncoder;

    @Inject
    UrlSplitter urlSplitter;

    @Inject
    IpUtils ipUtils;


    /**
     * Canonicalizes the supplied URL string.
     *
     * @param url URL string to canonicalize
     * @return canonicalized URL string
     * @throws MalformedURLException when supplied URL string is not in a well-formed URL format
     */
    public String canonicalize( String url ) throws MalformedURLException
    {
        NormalizedUrl normalized = normalize( url );

        String host = normalized.host;
        // remove all leading and trailing dots and replace multiplce occurrences of dots to single ones
        host = host.replaceFirst( "^\\.{1,}", "" );
        host = host.replaceFirst( "\\.{1,}$", "" );
        host = host.replaceAll( "\\.{2,}", "." ).toLowerCase();

        // if host part can be parsed as IP address in other formats but in decimal notation,
        // normalize it as 4 dot-seperated decimals
        if ( ipUtils.isIpAddress( host ) && !ipUtils.isDecimalIpAddress( host ) )
        {
            InetAddress inetAddr = ipUtils.toInetAddress( host );
            if ( inetAddr != null )
            {
                host = inetAddr.getHostAddress();
            }
        }

        // TODO: http://\\x01\\x80.com/
        //host = decodeHexSymbols( host );

        String path = normalized.path;
        // (1) replce: "/./" to "/"; remove "/../" and "/.." along with the preceding path component
        // (2) replace consecutive slashes
        path = path.replaceAll( "/\\./", "/" ).replaceAll( "/\\w+/\\.\\./?", "/" );
        path = path.replaceAll( "/{2,}", "/" );


        String full = normalized.url
                .replace( normalized.host, host )
                .replace( normalized.path, path );

        // percent escape query string
        StringBuilder sb = new StringBuilder();
        for ( char ch : full.toCharArray() )
        {
            if ( ch <= 32 || ch >= 127 || ch == '#' || ch == '%' )
            {
                sb.append( percentEncoder.encode( Character.toString( ch ) ).toUpperCase() );
            }
            else
            {
                sb.append( ch );
            }
        }
        return sb.toString();
    }


    private NormalizedUrl normalize( String url ) throws MalformedURLException
    {
        String str = url.trim();
        if ( !HTTP_SCHEME_PATTERN.matcher( str ).find() )
        {
            str = "http://" + str;
        }

        // convert host part to ASCII Punycode if needed
        boolean isAscii = CharMatcher.ascii().matchesAllOf( url );
        if ( !isAscii )
        {
            URL tmp = new URL( url );
            String hostPunyCode = IDN.toASCII( tmp.getHost() );
            str = url.replace( tmp.getHost(), hostPunyCode );
        }

        // remove white space chars
        str = str.replaceAll( "[\t\r\n]", "" );

        // remove fragment
        int fragmentIndex = str.indexOf( "#" );
        if ( fragmentIndex != -1 )
        {
            str = str.substring( 0, fragmentIndex );
        }

        // percent-unescape
        while ( PERCENT_SYMBOL_PATTERN.matcher( str ).find() )
        {
            StringBuffer sb = new StringBuffer();
            Matcher matcher = PERCENT_SYMBOL_PATTERN.matcher( str );
            while ( matcher.find() )
            {
                String decoded = percentEncoder.decode( matcher.group() );
                if ( decoded.equals( "$" ) || decoded.equals( "\\" ) )
                {
                    decoded = "\\" + decoded;
                }
                matcher.appendReplacement( sb, decoded );
            }
            matcher.appendTail( sb );
            str = sb.toString();
        }

        UrlSplitter.UrlParts parts = urlSplitter.split( str );
        if ( parts == null )
        {
            throw new MalformedURLException( "Can not divide URL into host, path, and query parts: " + str );
        }

        NormalizedUrl normalizedUrl = new NormalizedUrl();
        normalizedUrl.host = parts.getHost();
        // we disregard port numbers
        if ( parts.getPort() != null )
        {
            normalizedUrl.url = str.replaceFirst( parts.getPort(), "" );
        }
        else
        {
            normalizedUrl.url = str;
        }
        // ensure that path ends with slash
        if ( parts.getPath() == null )
        {
            normalizedUrl.path = "/";
            if ( parts.getQuery() == null )
            {
                normalizedUrl.url = str + "/";
            }
        }
        else
        {
            normalizedUrl.path = parts.getPath();
        }
        return normalizedUrl;
    }


    private String decodeHexSymbols( String str ) throws DecoderException
    {
        StringBuffer sb = new StringBuffer();
        Matcher matcher = HEX_SYMBOL_PATTERN.matcher( str );
        while ( matcher.find() )
        {
            matcher.appendReplacement( sb, "%" + matcher.group( 1 ) );
        }
        matcher.appendTail( sb );
        return sb.toString();
    }


    /**
     * Helper POJO to be pass data between methods.
     */
    private static class NormalizedUrl
    {
        private String url;
        private String host;
        private String path;
    }


}

