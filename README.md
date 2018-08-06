# gsb4j
[![Build Status](https://travis-ci.org/bazi/gsb4j.svg?branch=master)](https://travis-ci.org/bazi/gsb4j)

Gsb4j is a Java client implementation of [Google Safe Browsing](https://developers.google.com/safe-browsing/) APIv4.
It has both Lookup API and Update API implementations. 

## Getting started

TODO:
- release to central and provide GAV coordinates
- quick start
- bootstrapping

You can use Gsb4j by including the following dependency declaration in your POM file:
```xml
<dependency>
    <groupId>${project.groupId}</groupId>
    <artifactId>gsb4j-core</artifactId>
    <version>${project.version}</version>
</dependency>
```

Gsb4j happily uses [Guice](https://github.com/google/guice) dependency injection framework. Don't be afraid if you are not familiar with Guice -- all you have to do is: (1) bootstrap Gsb4j modules, (2) inject API implementation instance into your class. And you are ready to use it! Below are excerpts that illustriate these steps.

Bootstrap Gsb4j when starting up your application. This shall be done only once!
```java
Gsb4j gsb4j = Gsb4j.bootstrap();
```



## What's missing
Gsb4j is more or less a complete implementation of the APIv4. But there are some parts that are not inluded.
Missing parts do not influence the usability of the API but, nevertheless, they are missing for now :)

- Lookup API supports queries of up to 500 URLs but we query one URL at a time.
  One usually checks only one URL in hand and this is the sole reason we support single URL queries.
  This may change in future if needed.
- Rice compression of payloads ([doc reference](https://developers.google.com/safe-browsing/v4/compression))
