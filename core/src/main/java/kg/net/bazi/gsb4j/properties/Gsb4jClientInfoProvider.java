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

package kg.net.bazi.gsb4j.properties;

import com.google.inject.ProvisionException;
import com.google.inject.Singleton;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import kg.net.bazi.gsb4j.data.ClientInfo;

/**
 * Information about Google Safe Browsing API client implementation. Client info should uniquely identify a client
 * implementation, not an individual user. This class provides client info using project group id and version values.
 *
 * @author <a href="https://github.com/bazi">bazi</a>
 */
@Singleton
public class Gsb4jClientInfoProvider {

    private final String clientId;
    private final String clientVersion;

    Gsb4jClientInfoProvider() {
        Properties prop = new Properties();
        try ( InputStream is = ClassLoader.getSystemResourceAsStream("client-info.properties")) {
            prop.load(is);
        } catch (IOException ex) {
            throw new ProvisionException("Failed to load client implementation info", ex);
        }
        clientId = prop.getProperty("client.id");
        clientVersion = prop.getProperty("client.version");
    }

    /**
     * Gets the client ID that uniquely identifies the client implementation of the Safe Browsing API.
     *
     * @return client id value
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * Gets the version of the client implementation.
     *
     * @return client version
     */
    public String getClientVersion() {
        return clientVersion;
    }

    /**
     * Makes a client info for this implementation.
     *
     * @return client info instance
     */
    public ClientInfo make() {
        ClientInfo ci = new ClientInfo();
        ci.setClientId(clientId);
        ci.setClientVersion(clientVersion);
        return ci;
    }

}
