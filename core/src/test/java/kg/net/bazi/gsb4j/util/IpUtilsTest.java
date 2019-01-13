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

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author azilet
 */
public class IpUtilsTest {

    private IpUtils ipUtils = new IpUtils();

    private String ipDec = "195.127.0.11";
    private String ipHex = "0xc3.0x7f.0x0.0xb";
    private String ipOct = "0303.0177.00.013";
    private String ipBin = "11000011011111110000000000001011";
    private String ipNum = "3279880203";

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testIsIpAddress() {
        Assert.assertTrue(ipUtils.isIpAddress(ipDec));
        Assert.assertTrue(ipUtils.isIpAddress(ipHex));
        Assert.assertTrue(ipUtils.isIpAddress(ipOct));
        Assert.assertTrue(ipUtils.isIpAddress(ipBin));
        Assert.assertTrue(ipUtils.isIpAddress(ipNum));
    }

    @Test
    public void testIsDecimalIpAddress() {
        Assert.assertTrue(ipUtils.isDecimalIpAddress(ipDec));
        Assert.assertFalse(ipUtils.isDecimalIpAddress(ipHex));
        Assert.assertFalse(ipUtils.isDecimalIpAddress(ipOct));
        Assert.assertFalse(ipUtils.isDecimalIpAddress(ipBin));
        Assert.assertFalse(ipUtils.isDecimalIpAddress(ipNum));
    }

    @Test
    public void testIsBinaryIpAddress() {
        Assert.assertFalse(ipUtils.isBinaryIpAddress(ipDec));
        Assert.assertFalse(ipUtils.isBinaryIpAddress(ipHex));
        Assert.assertFalse(ipUtils.isBinaryIpAddress(ipOct));
        Assert.assertTrue(ipUtils.isBinaryIpAddress(ipBin));
        Assert.assertFalse(ipUtils.isBinaryIpAddress(ipNum));
    }

    @Test
    public void testIsOctalEncodedIpAddress() {
        Assert.assertFalse(ipUtils.isOctalEncodedIpAddress(ipDec));
        Assert.assertFalse(ipUtils.isOctalEncodedIpAddress(ipHex));
        Assert.assertTrue(ipUtils.isOctalEncodedIpAddress(ipOct));
        Assert.assertFalse(ipUtils.isOctalEncodedIpAddress(ipBin));
        Assert.assertFalse(ipUtils.isOctalEncodedIpAddress(ipNum));
    }

    @Test
    public void testIsHexEncodedIpAddress() {
        Assert.assertFalse(ipUtils.isHexEncodedIpAddress(ipDec));
        Assert.assertTrue(ipUtils.isHexEncodedIpAddress(ipHex));
        Assert.assertFalse(ipUtils.isHexEncodedIpAddress(ipOct));
        Assert.assertFalse(ipUtils.isHexEncodedIpAddress(ipBin));
        Assert.assertFalse(ipUtils.isHexEncodedIpAddress(ipNum));
    }

    @Test
    public void testIsNumericIpAddress() {
        Assert.assertFalse(ipUtils.isNumericIpAddress(ipDec));
        Assert.assertFalse(ipUtils.isNumericIpAddress(ipHex));
        Assert.assertFalse(ipUtils.isNumericIpAddress(ipOct));
        Assert.assertFalse(ipUtils.isNumericIpAddress(ipBin));
        Assert.assertTrue(ipUtils.isNumericIpAddress(ipNum));
    }

    @Test
    public void testToInetAddress() throws UnknownHostException {
        InetAddress inetAddr = InetAddress.getByName("195.127.0.11");

        Assert.assertEquals(inetAddr, ipUtils.toInetAddress(ipDec));
        Assert.assertEquals(inetAddr, ipUtils.toInetAddress(ipHex));
        Assert.assertEquals(inetAddr, ipUtils.toInetAddress(ipOct));
        Assert.assertEquals(inetAddr, ipUtils.toInetAddress(ipBin));
        Assert.assertEquals(inetAddr, ipUtils.toInetAddress(ipNum));
    }

}
