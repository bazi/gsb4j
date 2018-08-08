# gsb4j
[![Build Status](https://travis-ci.org/bazi/gsb4j.svg?branch=master)](https://travis-ci.org/bazi/gsb4j)

Gsb4j is a Java client implementation of [Google Safe Browsing](https://developers.google.com/safe-browsing/) API v4.
It has both Lookup API and Update API implementations.

## Requirements:
- JDK 8
- Maven

## Get started

TODO:
- release to central and provide GAV coordinates


You can use Gsb4j by including the following dependency declaration in your POM file:
```xml
<dependency>
    <groupId>${project.groupId}</groupId>
    <artifactId>gsb4j-core</artifactId>
    <version>${project.version}</version>
</dependency>
```

Gsb4j happily uses [Guice](https://github.com/google/guice) dependency injection framework.
Don't be afraid if you are not familiar with Guice -- Gsb4j has methods to bootstrap Gsb4j modules
and to get an instance of API client implementations. Code fragment below illustrates the usage of Gsb4j.

```java
Gsb4j gsb4j = Gsb4j.bootstrap(); // (1)
SafeBrowsingApi api = gsb4j.getApiClient( SafeBrowsingApi.LOOKUP_API ); // (2)
ThreatMatch threat = api.check( url ); // (3)
if ( threat != null )
{
    // URL is not safe
}
else
{
    // URL is safe
}
gsb4j.shutdown(); // (4)
```

1. Bootstrap Gsb4j when starting up your application. **This shall be done exactly once!** Note that when Gsb4j is bootstrapped this way (i.e. without specifying any properties), it expects configuration parameters be specified as system properties (`-Dapi.key=...`). Read below about configurations.
1. Get an API client implementation instance, either Lookup API (as shown above) or Update API.
1. Check your URL. If URL is recognized as unsafe by Google Safe Browsing, then a non-null object representing the threat is returned. Otherwise, `null` is returned which means URL in hand does not have any threat.
1. Shutdown is optional but highly recommended so that we are all clean. It releases resources held by Gsb4j. Usually such methods are called prior to application exit, e.g. in JVM shutdown hooks.

## Configuration
Gsb4j needs **API key** to access Google Safe Browsing API. It can be obtained as described in API docs [here](https://developers.google.com/safe-browsing/v4/get-started).
At this time, *API key is the only required configuration parameter* for Gsb4j.

### Configuration parameter keys

- **api.key** *(required)*: API key to access the Safe Browsing API; read [this page](https://developers.google.com/safe-browsing/v4/get-started) to setup and obtain API key
- **api.http.referrer** *(optional)*: if you have specified HTTP Referrer value for your API key, then you should supply it here
- **data.dir** *(optional, defaults to `gsb4j` directory in home directory of the current user)*: this is the directory where Gsb4j will store its data - all kind of API related metadata and local database files will be stored in this directory

There are two ways you can set configuration parameters: (1) using system properties, and (2) using properties file which should be compatible with standard [Properties](https://docs.oracle.com/javase/8/docs/api/java/util/Properties.html) class.

**System properties** are set as VM options in a command line as shown below. Almost all IDEs provide ways to set VM options as well.

    java -Dapi.key=AIza...qwSg -Ddata.dir=/home/user1/other/gsb4j -jar app.jar

**Properties file** should be maintained and located by you. To bootstrap Gsb4j using properties file, first you have to
create an instance of [Properties](https://docs.oracle.com/javase/8/docs/api/java/util/Properties.html) class and load
your properties file into that instance. Then you bootstrap Gsb4j with this Properties instance as shown below.

```java
Properties properties = new Properties();
try ( Reader reader = new FileReader( "/path/to/your/file.properties" ) )
{
    properties.load( reader );
}
Gsb4j gsb4j = Gsb4j.bootstrap( properties );
```

## HTTP Proxy
TODO


## What's missing
Gsb4j is more or less a complete implementation of the API v4. But there are some parts that are not supported.
Those parts do not influence the overall usability of the API but, nevertheless, they are not supported for now :)

- Lookup API supports queries of up to 500 URLs but we query one URL at a time.
  One usually checks only one URL in hand and this is the sole reason we support single URL queries.
  This may change in future if needed.
- Rice compression of payloads ([doc reference](https://developers.google.com/safe-browsing/v4/compression))


