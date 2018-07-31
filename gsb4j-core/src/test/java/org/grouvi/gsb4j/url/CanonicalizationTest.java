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
package org.grouvi.gsb4j.url;


import java.net.MalformedURLException;
import java.net.URISyntaxException;

import org.grouvi.gsb4j.util.IpUtils;
import org.grouvi.gsb4j.util.PercentEncoder;
import org.grouvi.gsb4j.util.UrlSplitter;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.apache.commons.codec.DecoderException;


/**
 *
 * @author azilet
 */
public class CanonicalizationTest
{

    private Canonicalization canon = new Canonicalization();


    @Before
    public void setUp()
    {
        canon.percentEncoder = new PercentEncoder();
        canon.urlSplitter = new UrlSplitter();
        canon.ipUtils = new IpUtils();
    }


    @After
    public void tearDown()
    {
    }


    @Test
    public void testCompute()
    {
    }


    @Test
    public void testCanonicalize() throws MalformedURLException, URISyntaxException, DecoderException
    {
        Assert.assertEquals( "http://host/%25", canon.canonicalize( "http://host/%25%32%35" ) );
        Assert.assertEquals( "http://host/%25%25", canon.canonicalize( "http://host/%25%32%35%25%32%35" ) );
        Assert.assertEquals( "http://host/%25", canon.canonicalize( "http://host/%2525252525252525" ) );
        Assert.assertEquals( "http://host/asdf%25asd", canon.canonicalize( "http://host/asdf%25%32%35asd" ) );
        Assert.assertEquals( "http://host/%25%25%25asd%25%25", canon.canonicalize( "http://host/%%%25%32%35asd%%" ) );
        Assert.assertEquals( "http://www.google.com/", canon.canonicalize( "http://www.google.com/" ) );
        Assert.assertEquals( "http://168.188.99.26/.secure/www.ebay.com/", canon.canonicalize(
                             "http://%31%36%38%2e%31%38%38%2e%39%39%2e%32%36/%2E%73%65%63%75%72%65/%77%77%77%2E%65%62%61%79%2E%63%6F%6D/" ) );
        Assert.assertEquals(
                "http://195.127.0.11/uploads/%20%20%20%20/.verify/.eBaysecure=updateuserdataxplimnbqmn-xplmvalidateinfoswqpcmlx=hgplmcx/",
                canon.canonicalize(
                        "http://195.127.0.11/uploads/%20%20%20%20/.verify/.eBaysecure=updateuserdataxplimnbqmn-xplmvalidateinfoswqpcmlx=hgplmcx/" ) );
        Assert.assertEquals( "http://host%23.com/~a!b@c%23d$e%25f^00&11*22(33)44_55+", canon.canonicalize(
                             "http://host%23.com/%257Ea%2521b%2540c%2523d%2524e%25f%255E00%252611%252A22%252833%252944_55%252B" ) );
        Assert.assertEquals( "http://195.127.0.11/blah", canon.canonicalize( "http://3279880203/blah" ) );
        Assert.assertEquals( "http://www.google.com/", canon.canonicalize( "http://www.google.com/blah/.." ) );
        Assert.assertEquals( "http://www.google.com/", canon.canonicalize( "www.google.com/" ) );
        Assert.assertEquals( "http://www.google.com/", canon.canonicalize( "www.google.com" ) );
        Assert.assertEquals( "http://www.evil.com/blah", canon.canonicalize( "http://www.evil.com/blah#frag" ) );
        Assert.assertEquals( "http://www.google.com/", canon.canonicalize( "http://www.GOOgle.com/" ) );
        Assert.assertEquals( "http://www.google.com/", canon.canonicalize( "http://www.google.com.../" ) );
        Assert.assertEquals( "http://www.google.com/foobarbaz2", canon.canonicalize(
                             "http://www.google.com/foo\tbar\rbaz\n2" ) );
        Assert.assertEquals( "http://www.google.com/q?", canon.canonicalize( "http://www.google.com/q?" ) );
        Assert.assertEquals( "http://www.google.com/q?r?", canon.canonicalize( "http://www.google.com/q?r?" ) );
        Assert.assertEquals( "http://www.google.com/q?r?s", canon.canonicalize( "http://www.google.com/q?r?s" ) );
        Assert.assertEquals( "http://evil.com/foo", canon.canonicalize( "http://evil.com/foo#bar#baz" ) );
        Assert.assertEquals( "http://evil.com/foo;", canon.canonicalize( "http://evil.com/foo;" ) );
        Assert.assertEquals( "http://evil.com/foo?bar;", canon.canonicalize( "http://evil.com/foo?bar;" ) );
//        Assert.assertEquals( "http://%01%80.com/", hashPrefix.canonicalize( "http://\\x01\\x80.com/" ) );
        Assert.assertEquals( "http://notrailingslash.com/", canon.canonicalize( "http://notrailingslash.com" ) );
        Assert.assertEquals( "http://www.gotaport.com/", canon.canonicalize( "http://www.gotaport.com:1234/" ) );
        Assert.assertEquals( "http://www.google.com/", canon.canonicalize( "  http://www.google.com/  " ) );
        Assert.assertEquals( "http://%20leadingspace.com/", canon.canonicalize( "http:// leadingspace.com/" ) );
        Assert.assertEquals( "http://%20leadingspace.com/", canon.canonicalize( "http://%20leadingspace.com/" ) );
        Assert.assertEquals( "http://%20leadingspace.com/", canon.canonicalize( "%20leadingspace.com/" ) );
        Assert.assertEquals( "https://www.securesite.com/", canon.canonicalize( "https://www.securesite.com/" ) );
        Assert.assertEquals( "http://host.com/ab%23cd", canon.canonicalize( "http://host.com/ab%23cd" ) );
        Assert.assertEquals( "http://host.com/twoslashes?more//slashes", canon.canonicalize(
                             "http://host.com//twoslashes?more//slashes" ) );
    }

}

