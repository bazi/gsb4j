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


import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 *
 * @author azilet
 */
public class HashingTest
{

    private Hashing hashing = new Hashing();


    @Before
    public void setUp()
    {
    }


    @After
    public void tearDown()
    {
    }


    @Test
    public void testComputeHashPrefixes1()
    {
        Set<String> expressions = new HashSet<>();
        expressions.add( "abc" );

        Set<String> hashes = hashing.computeHashPrefixes( expressions, 4 );
        Assert.assertTrue( hashes.contains( "ba7816bf" ) );
    }


    @Test
    public void testComputeHashPrefixes2()
    {
        Set<String> expressions = new HashSet<>();
        expressions.add( "abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq" );

        Set<String> hashes = hashing.computeHashPrefixes( expressions, 6 );
        Assert.assertTrue( hashes.contains( "248d6a61d206" ) );
    }


    @Test( expected = IllegalArgumentException.class )
    public void testComputeHashPrefixesWithInvalidParam1()
    {
        hashing.computeHashPrefixes( new HashSet<>(), 1 );
    }


    @Test( expected = IllegalArgumentException.class )
    public void testComputeHashPrefixesWithInvalidParam2()
    {
        hashing.computeHashPrefixes( new HashSet<>(), 2 );
    }


    @Test( expected = IllegalArgumentException.class )
    public void testComputeHashPrefixesWithInvalidParam3()
    {
        hashing.computeHashPrefixes( new HashSet<>(), 3 );
    }


    @Test( expected = IllegalArgumentException.class )
    public void testComputeHashPrefixesWithInvalidParam4()
    {
        hashing.computeHashPrefixes( new HashSet<>(), 33 );
    }


    @Test
    public void testComputeHashPrefix()
    {
        String prefix = hashing.computeHashPrefix( "abc", 4 );
        Assert.assertEquals( "ba7816bf", prefix );
    }


    @Test( expected = IllegalArgumentException.class )
    public void testComputeHashPrefixWithInvalidParam1()
    {
        hashing.computeHashPrefix( "abc", 0 );
    }


    @Test( expected = IllegalArgumentException.class )
    public void testComputeHashPrefixWithInvalidParam2()
    {
        hashing.computeHashPrefix( "abc", 1 );
    }


    @Test( expected = IllegalArgumentException.class )
    public void testComputeHashPrefixWithInvalidParam3()
    {
        hashing.computeHashPrefix( "abc", 2 );
    }


    @Test( expected = IllegalArgumentException.class )
    public void testComputeHashPrefixWithInvalidParam4()
    {
        hashing.computeHashPrefix( "abc", 3 );
    }


    @Test( expected = IllegalArgumentException.class )
    public void testComputeHashPrefixWithInvalidParam5()
    {
        hashing.computeHashPrefix( "abc", 33 );
    }


    @Test
    public void testComputeFullHash()
    {
    }
}

