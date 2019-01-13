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

/**
 *
 * @author azilet
 */
public class UrlSplitterTest {

    private UrlSplitter splitter = new UrlSplitter();

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSplit1() {
        UrlSplitter.UrlParts parts = splitter.split("http://example.com:8080/path/here?param=1");

        Assert.assertEquals("http://", parts.getScheme());
        Assert.assertEquals("example.com", parts.getHost());
        Assert.assertEquals(":8080", parts.getPort());
        Assert.assertEquals("/path/here", parts.getPath());
        Assert.assertEquals("?param=1", parts.getQuery());
    }

    @Test
    public void testSplit2() {
        UrlSplitter.UrlParts parts = splitter.split("http://example.com?param=1");

        Assert.assertEquals("http://", parts.getScheme());
        Assert.assertEquals("example.com", parts.getHost());
        Assert.assertNull(parts.getPort());
        Assert.assertNull(parts.getPath());
        Assert.assertEquals("?param=1", parts.getQuery());

    }

    @Test
    public void testSplit3() {
        UrlSplitter.UrlParts parts = splitter.split("http://example.com/path/1/");

        Assert.assertEquals("http://", parts.getScheme());
        Assert.assertEquals("example.com", parts.getHost());
        Assert.assertNull(parts.getPort());
        Assert.assertEquals("/path/1/", parts.getPath());
        Assert.assertNull(parts.getQuery());
    }
}
