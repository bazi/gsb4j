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

import java.util.Base64;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author azilet
 */
public class RiceCompressionTest {

    private RiceCompression riceCompression = new RiceCompression();

    private String data1 = "BgAABAAAEAAADAAAEAAACgAACAAADAAACgAADAAADgAAAgAAEAAABAAACAAAAgAAAgAADgAABAAABAAAGAAAFAAAFgAAAgAACgAAKAAADgAARAAAAgAACgAABAAAPAAAGgAABgAABgAACAAAFgAACgAABAAADgAABgAAGgAAAgAADgAABAAADAAABgAABAAAJgAACAAACAAACgAADgAABgAACAAAAgAACgAAEAAAGgAABgAAAgAACgAAFAAAFgAAEgAABgAABAAAAgAAFgAABAAACAAAAgAAFAAAMgAABgAAEgAAAgAAGgAAFgAABgAABAAACAAAAgAAJAAAHgAAKAAAFgAAAgAABAAAEgAABAAACgAABgAABAAACgAABgAACAAACgAABgAAAgAADAAAAgAACAAADgAAGAAABgAABAAAJgAAEgAAAgAABAAAFgAAAgAACAAADAAACgAADAAADgAAGgAAAgAAHgAABAAAFAAABAAADAAABAAAAgAABgAACgAACAAAFAAACAAAAgAABAAABAAABAAACgAAFgAAAgAAAgAABAAACAAACAAABAAAEAAAGAAACgAABgAALgAAFgAAGAAAEAAAJAAADgAAEAAAHAAADAAACgAABAAAFAAABAAABAAAFAAAAgAAFgAAMAAABgAABgAABAAAAgAABgAABAAADAAABgAACgAABgAAHAAABAAAFgAACAAADgAACgAABAAABgAAAgAAGAAACgAADAAADgAABAAABgAAFAAABgAADAAADAAACgAACAAADAAAAgAAAgAAEAAACgAAAgAAAgAABAAACAAACgAABAAAAgAABgAAEAAAEgAABAAAAgAACAAAAgAAPgAACAAAAgAABAAAIAAAMAAAAgAAAgAAEgAADAAACgAAFAAABgAACAAAFAAACAAAAgAAEAAABAAACAAABgAAAgAACAAACAAABgAAEgAAAgAAIAAABAAABAAAKAAAEgAAFAAAAgAAAgAABAAAEgAAAgAACAAABgAAAgAAEgAADAAABgAAAgAAEgAACgAABAAAFAAAEgAABAAABAAAGgAAGAAAEAAAEAAAJgAADAAABAAABAAAFgAABAAACgAAHAAABAAADAAAHgAAGAAAAgAACgAAFgAALAAABgAABgAAAgAADgAAEgAACAAAEgAABAAADgAAAgAACgAACgAAEAAABgAAEAAAGAAACAAAAgAACAAACgAABAAADgAABAAAEgAABgAACAAACAAAAgAAAgAAIgAABgAAEAAACgAAHAAAJAAAEgAACAAACgAAJAAAEAAABgAABAAABAAAGgAACAAAEAAACAAADAAA";
    private String data2 = "iUJVA4vFTpeP8rO/Z4t+YRgR5F3FJKVLdSLxOg24kVRFftBPF7Md9CpFGz+YS+/gG3UlrTEQJ7VH8ykklhUFbDzVxjlVMXb0Tv3gHwPeFjunnO5LaOo+zvqvUgsbTBNfRSQRG0k403QOLD1GzWYOxTU16XZspky9e5IEZ1CRO4dxmVIqqxoZgLwXH7hohhYa/0IroGzN7+sIYlm+v6Qpx1/yrU/E/2skm+A37Df0KAuOyK68KTvo1Wxi6kvCPTOCBPqXzqrTxzk0LEoyAmJP8E0TEjhk3xYWLyLlRZC05ZIcGb4xOsQ8hmVLArZwA5n8CtWCYFmbXLFDwclzNAGMvsxh4maJ6u1V4aM7Ur956K9vle/a2pK58CT0txNqfx2cwsp25wKYqWP1fKc7bHEbU+OF/msNDnZo02aDrGcdm6zU1W4n9IMXLtylDMSEPfTm5ujzK9VlL7jWmdg9+w4AgD99waUJfVQILu60hp13mf5byg4wkS9RC6AtyuJ7zBlUTqHmHBfaeqgqrVbCE4XV+ZlVfcRb7qi8rIhqjKLlplTONcQU9ECB78Gse2rgDs2S4bUdx+8nH/xKORJnKbCcaWJ9Yzw/FWesIkaQnCICXGGkuLgtri1EI3p0tbnbKHlby+XRk18vxPar5U+5CpH+veXFbPvQr4UfJ8uyOSfrF7BgRUdy5hN0ZPZGq+6j/MxHqa0GMSwM5s7azJyMQQkxQdVg30SJs8bgky119oKg6+dpCuw2W+ZKcYIW0IcwG2NIepGgLxbDcYupoqsoBVfTohJYP1SolAwa9RsEuTB0Nir2Lngkq4zD0E76gWlEHiLL9z8XBZUoPzcAa8nA+nYCdrY2LQ5z8/z71rLS0KSGQ3qmpPZRGTNlEJTRVGpjoDdhlykSV06doAltkVWPy1K1ndvHZnxFFvqo+crfvm3PZW8g2lWpXhjJt+oVbq/aZWKR4klkh5MLDtTcwyvMeu56/AkaMmiSoXGlXF8/nJErbDPJl5XXY4WP3+IHPYdHqzGXC+88iAjEhtE4anSKYyWhYW9mRBaSfmxi+HVsQNycrU6YuKwqSPGdDa2KDQpI95QsL1G0SkCRJKjE3nCL+cyWDZJi4FhQphiHohFwtAj7pSYn95RhLJVeT5hIwKJMlOuoMRtmQwjfJ3n2m+xwRkmT7/UOjdUSntwRRBmuURHmQuq1ds3sw2clShw40xNH/Gqw9DSmix6d4NzENJ6dLwaBln3AzUTI5BSINcEBwo/pWk8QhN9X1ZhRlVHzHb901vixxJOM5DL5tLZKROBhKcAVl1lvxbrhEZ5PLsbQCwg/EyTjQ7ONRw9sIt2tYfMm2NNTCGx8G7dReZmAidjleWUkAwXA/Xuq1QZB7C0zrCvmtNZCyL+aHCQw7as5taE3";
    private int parameter = 23;

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testDecompress1() {
        byte[] bytes = Base64.getDecoder().decode(data1);
        List<Integer> ls = riceCompression.decompress(parameter, bytes);

        Assert.assertEquals(336, ls.size());
    }

    @Test
    public void testDecompress2() {
        byte[] bytes = Base64.getDecoder().decode(data2);
        List<Integer> ls = riceCompression.decompress(parameter, bytes);

        Assert.assertEquals(336, ls.size());
    }

}
