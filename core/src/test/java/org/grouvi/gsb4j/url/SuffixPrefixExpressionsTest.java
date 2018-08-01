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


import java.util.Set;

import org.grouvi.gsb4j.util.IpUtils;
import org.grouvi.gsb4j.util.UrlSplitter;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 *
 * @author azilet
 */
public class SuffixPrefixExpressionsTest
{
    private SuffixPrefixExpressions expressions = new SuffixPrefixExpressions();


    @Before
    public void setUp()
    {
        expressions.urlSplitter = new UrlSplitter();
        expressions.ipUtils = new IpUtils();
    }


    @After
    public void tearDown()
    {
    }


    @Test
    public void testMakeExpressions1()
    {
        Set<String> set = expressions.makeExpressions( "http://a.b.c/1/2.html?param=1" );
        Assert.assertEquals( 8, set.size() );
        Assert.assertTrue( set.contains( "a.b.c/1/2.html?param=1" ) );
        Assert.assertTrue( set.contains( "a.b.c/1/2.html" ) );
        Assert.assertTrue( set.contains( "a.b.c/" ) );
        Assert.assertTrue( set.contains( "a.b.c/1/" ) );
        Assert.assertTrue( set.contains( "b.c/1/2.html?param=1" ) );
        Assert.assertTrue( set.contains( "b.c/1/2.html" ) );
        Assert.assertTrue( set.contains( "b.c/" ) );
        Assert.assertTrue( set.contains( "b.c/1/" ) );
    }


    @Test
    public void testMakeExpressions2()
    {
        Set<String> set = expressions.makeExpressions( "http://a.b.c.d.e.f.g/1.html" );
        Assert.assertEquals( 10, set.size() );
        Assert.assertTrue( set.contains( "a.b.c.d.e.f.g/1.html" ) );
        Assert.assertTrue( set.contains( "a.b.c.d.e.f.g/" ) );
        Assert.assertTrue( set.contains( "c.d.e.f.g/1.html" ) );
        Assert.assertTrue( set.contains( "c.d.e.f.g/" ) );
        Assert.assertTrue( set.contains( "d.e.f.g/1.html" ) );
        Assert.assertTrue( set.contains( "d.e.f.g/" ) );
        Assert.assertTrue( set.contains( "e.f.g/1.html" ) );
        Assert.assertTrue( set.contains( "e.f.g/" ) );
        Assert.assertTrue( set.contains( "f.g/1.html" ) );
        Assert.assertTrue( set.contains( "f.g/" ) );
    }


    @Test
    public void testMakeExpressions3()
    {
        Set<String> set = expressions.makeExpressions( "http://1.2.3.4/1/" );
        Assert.assertEquals( 2, set.size() );
        Assert.assertTrue( set.contains( "1.2.3.4/1/" ) );
        Assert.assertTrue( set.contains( "1.2.3.4/" ) );

    }
}

