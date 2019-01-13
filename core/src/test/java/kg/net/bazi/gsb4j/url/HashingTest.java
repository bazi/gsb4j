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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author azilet
 */
public class HashingTest {

    private Hashing hashing = new Hashing();

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testComputeHashPrefix1() {
        String expression = "abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq";
        String prefix = hashing.computeHashPrefix(expression, 6);
        Assert.assertEquals("248d6a61d206", prefix);
    }

    @Test
    public void testComputeHashPrefix2() {
        String prefix = hashing.computeHashPrefix("abc", 4);
        Assert.assertEquals("ba7816bf", prefix);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testComputeHashPrefixWithInvalidParam1() {
        hashing.computeHashPrefix("abc", 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testComputeHashPrefixWithInvalidParam2() {
        hashing.computeHashPrefix("abc", 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testComputeHashPrefixWithInvalidParam3() {
        hashing.computeHashPrefix("abc", 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testComputeHashPrefixWithInvalidParam4() {
        hashing.computeHashPrefix("abc", 3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testComputeHashPrefixWithInvalidParam5() {
        hashing.computeHashPrefix("abc", 33);
    }

    @Test
    public void testComputeFullHash() {
        String fullHash = hashing.computeFullHash("abcdefg");
        Assert.assertEquals("7d1a54127b222502f5b79b5fb0803061152a44f92b37e23c6527baf665d4da9a", fullHash);
    }
}
