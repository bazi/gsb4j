# gsb4j
[![Build Status](https://travis-ci.org/bazi/gsb4j.svg?branch=master)](https://travis-ci.org/bazi/gsb4j)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/kg.net.bazi.gsb4j/gsb4j-core/badge.svg)](https://maven-badges.herokuapp.com/maven-central/kg.net.bazi.gsb4j/gsb4j-core)
[![Javadoc](https://javadoc.io/badge/kg.net.bazi.gsb4j/gsb4j-core.svg?color=brightgreen)](http://javadoc.io/doc/kg.net.bazi.gsb4j/gsb4j-core)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=kg.net.bazi.gsb4j%3Agsb4j-parent&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=kg.net.bazi.gsb4j%3Agsb4j-parent)

Gsb4j is a Java client implementation of [Google Safe Browsing](https://developers.google.com/safe-browsing/) API v4.
It has both Lookup API and Update API implementations.

Gsb4j requires Java 8 or newer.

Refer to [Feedback](#feedback) section of this page if you have questions regarding this project.


## Get started

You can use Gsb4j by including dependency declaration in your project. If you use Maven, include the following in your POM file:
```xml
<dependency>
    <groupId>kg.net.bazi.gsb4j</groupId>
    <artifactId>gsb4j-core</artifactId>
    <version>1.0.5</version>
</dependency>
```

Gsb4j happily uses [Guice](https://github.com/google/guice) dependency injection framework.
Don't be afraid if you are not familiar with Guice -- Gsb4j handles it all and provides methods to bootstrap Gsb4j modules
and to get instances of API client implementations. Code fragment below illustrates sample usage of Gsb4j.

```java
Gsb4j gsb4j = Gsb4j.bootstrap(); // (1)
SafeBrowsingApi api = gsb4j.getApiClient(SafeBrowsingApi.Type.LOOKUP_API); // (2)
ThreatMatch threat = api.check(url); // (3)
if (threat != null) {
    // URL is not safe
} else {
    // URL is safe
}
gsb4j.shutdown(); // (4)
```

1. Bootstrap Gsb4j when starting up your application. **This shall be done exactly once!**
   Note that when Gsb4j is bootstrapped this way (i.e. without specifying any properties), it expects
   configuration parameters be specified as system properties (`-Dapi.key=...`). Read below about configurations.
1. Get an API client implementation instance, either Lookup API (as shown above) or Update API.
1. Check your URL. If URL is recognized as unsafe by Google Safe Browsing, then non-null object representing the threat
   is returned. Otherwise, `null` is returned which means URL is safe and does not impose any threat.
1. Shutdown is optional but highly recommended so that we are all clean. It releases resources held by Gsb4j.
   Usually such methods are called prior to application exit, e.g. in JVM shutdown hooks.
   **Shutdown Gsb4j only when you are all done and will NOT be using Gsb4j anymore**.

## Configuration
Gsb4j needs **API key** to access Google Safe Browsing API. It can be obtained as described in API docs [here](https://developers.google.com/safe-browsing/v4/get-started).
At this time, *API key is the only required configuration parameter* for Gsb4j.

### Configuration parameter keys

- **api.key** *(required)*: API key to access the Safe Browsing API; read [this page](https://developers.google.com/safe-browsing/v4/get-started) to setup and obtain API key
- **api.http.referrer** *(optional)*: if you have specified HTTP Referrer value for your API key, then you should supply it here
- **data.dir** *(optional, defaults to `gsb4j` directory in home directory of the current user)*: this is the directory where Gsb4j will store its data. All kind of API related metadata and local database files will be stored in this directory.

There are two ways you can set configuration parameters: (1) using system properties, and (2) using properties file which should be compatible with standard [Properties](https://docs.oracle.com/javase/8/docs/api/java/util/Properties.html) class.

**System properties** are set as VM options in a command line as shown below. Almost all IDEs provide ways to set VM options as well.

    java -Dapi.key=AIza...qwSg -Ddata.dir=/home/user1/other/gsb4j -jar app.jar

**Properties file** should be maintained and located by you. To bootstrap Gsb4j using your properties file, first you have to
create an instance of [Properties](https://docs.oracle.com/javase/8/docs/api/java/util/Properties.html) class and load
your properties file into that instance. Then you bootstrap Gsb4j with this Properties instance as shown below.

```java
Properties properties = new Properties();
try (Reader reader = new FileReader("/path/to/your/file.properties")) {
    properties.load(reader);
}
Gsb4j gsb4j = Gsb4j.bootstrap(properties);
```

## HTTP Proxy
There is a ready HTTP proxy for Gsb4j. This is handy for those who want a quick run to see all things working.
You can download an archive, extract it, and launch it right away - and you are ready to check URLs.
Here is how to launch HTTP proxy and how to check URLs.

Download tarball or zip archive from [releases page](https://github.com/bazi/gsb4j/releases/latest)
and extract it to your desired location (example uses tarball):

    tar xzf gsb4j-http-${version}-bundle.tgz -C /home/user1/test/

Change current directory to the location where you extracted archive contents and launch jar file with your API key:

    java -Dapi.key=AIza...qwSg -jar gsb4j-http-${version}.jar 

This will start up a web server on port 8080. Now you can send GET requests to `/gsb4j/api/lookup`
endpoint with query parameter named **url** which should contain a URL string value you want to check against Google Safe Browsing API.
Below is a sample request from command line:

    curl http://localhost:8080/gsb4j/api/lookup?url=http://testsafebrowsing.appspot.com/apiv4/ANY_PLATFORM/MALWARE/URL/

Please note that your URL supplied as a query parameter **must be URL-encoded** to avoid confusions. In the above example,
URL *is not* encoded as *curl* takes care of it but ideally that URL should be URL-encoded.

More details on [wiki page](https://github.com/bazi/gsb4j/wiki/HTTP-Proxy).


## What's missing
Gsb4j is more or less a complete implementation of the API v4. But there are some parts that are not supported.
Those parts do not influence the overall usability of the API but, nevertheless, they are not supported for now :)

- Google Safe Browsing Lookup API supports queries of up to 500 URLs in a single request but we query one URL at a time.
  One usually checks only one URL in hand and this is the sole reason we support single URL queries.
  This may change in future if needed. ([doc reference](https://developers.google.com/safe-browsing/v4/lookup-api))
- Rice compression of payloads ([doc reference](https://developers.google.com/safe-browsing/v4/compression))
- Back-off mode for unsuccessful HTTP responses from API ([doc reference](https://developers.google.com/safe-browsing/v4/request-frequency))


## Feedback
Your feedback and comments are welcome and appreciated. You can use [Gsb4j mailing list](https://groups.google.com/d/forum/gsb4j)
to ask any kind of questions or to share your thoughts on various topics related to Gsb4j.
This mailing list is open to everything Gsb4j related.

If you find any bugs or issues related to working of Gsb4j, then you should be creating 
an issue in Github. Contributions are always welcome!


[![SonarCloud](https://sonarcloud.io/images/project_badges/sonarcloud-white.svg)](https://sonarcloud.io/dashboard?id=kg.net.bazi.gsb4j%3Agsb4j-parent)

