# gsb4j

Gsb4j is a Java client implementation of [Google Safe Browsing](https://developers.google.com/safe-browsing/) APIv4.
It has both Lookup API and Update API implementations. 

## Getting started

TODO:
- release to central and provide GAV coordinates
- quick start
- bootstrapping


## What's missing
Gsb4j is more or less a complete implementation of the APIv4. But there are some parts that are not inluded.
Missing parts do not influence the usability of the API but, nevertheless, they are missing for now :)

- Lookup API supports queries of up to 500 URLs but we query one URL at a time.
  One usually checks only one URL in hand and this is the sole reason we support single URL queries.
  This may change in future if needed.
- Rice compression of payloads ([doc reference](https://developers.google.com/safe-browsing/v4/compression))
