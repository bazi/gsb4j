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

package kg.net.bazi.gsb4j.util;


import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class PercentEncoderTest
{
    private PercentEncoder penc = new PercentEncoder();


    @Before
    public void setUp()
    {
    }


    @After
    public void tearDown()
    {
    }


    @Test
    public void testEncode()
    {
        Assert.assertEquals( "query%3Dworld%20nomads%26count%3D5", penc.encode( "query=world nomads&count=5" ) );
        Assert.assertEquals( "query%3DHe%20is%20%22Joe%22", penc.encode( "query=He is \"Joe\"" ) );
        Assert.assertEquals( "pattern%3Da%7Cb", penc.encode( "pattern=a|b" ) );
    }


    @Test
    public void testDecode()
    {
        Assert.assertEquals( "query=world nomads&count=5", penc.decode( "query%3Dworld%20nomads%26count%3D5" ) );
        Assert.assertEquals( "query=He is \"Joe\"", penc.decode( "query%3DHe%20is%20%22Joe%22" ) );
        Assert.assertEquals( "pattern=a|b", penc.decode( "pattern%3Da%7Cb" ) );
    }

}

