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
package org.grouvi.gsb4j.data;


/**
 * POJO to represent an individual threat; for example, a malicious URL or its hash representation. Only one of these
 * fields should be set.
 *
 * @author azilet
 */
public class ThreatEntry
{
    private String hash;
    private String url;
    private String digest;


    public String getHash()
    {
        return hash;
    }


    public void setHash( String hash )
    {
        this.hash = hash;
    }


    public String getUrl()
    {
        return url;
    }


    public void setUrl( String url )
    {
        this.url = url;
    }


    public String getDigest()
    {
        return digest;
    }


    public void setDigest( String digest )
    {
        this.digest = digest;
    }

}

