# Contributing

GSb4j is newly released to open hosting and it is yet unknown if there will be any contributions.
Anyways, contributing to Gsb4j is possible and would be appreciated. The contributing path
would be no different from that of most other open source projects. You find an issue or
want to add new features, you do a fix or implementation on yourself, and finally open a
pull request. Pull requests, naturally, are expected to fullful some basic requirements:

- PR shall be providing a really useful changes or additions
- code shall be clean and streamlined with existsing code
- code formatting shall match project's formatting; more about this below
- code shall be documented enough using javadoc syntax or plain comments as needed
  (there is no need to document every method but all user accessible public classes
  and methods shall be documented)


## Code formatting

### Current
Starting from v1.0.4, Gsb4j has adopted [Google Java Style Guice](https://google.github.io/styleguide/javaguide.html)
with some exceptions. Exceptions are reflected in [suppressions file](checkstyle-suppressions.xml)
of Maven Checkstyle plugin. That file has notes about customizations. The most evident exceptions are
identation and line lenght rules.

### Legacy (v1.0.3 and before)

**Please note that code style described below is not used anymore and is left for previous version source code reference.**

Every project should follow a predefined set of code formatting conventions.
There is no need to explain its benefits. Conventions should be
accepted as it is. Gsb4j has historically adopted a space-abundant code style
based on [Allman](https://en.wikipedia.org/wiki/Indentation_style#Allman_style)
indentation style.

I have exported code formatting settings which are available [here](https://github.com/bazi/gsb4j/tree/master/xfiles).
This is for NetBeans IDE. I hope there will be settings files for other popular
IDEs in the future either provided by me or as a contribution from others.

Here are some highlights:
- put braces on the new line (as in Allman indentation style)
- always use braces, even for single statement blocks
- use spaces, no tab character
- use seperating spaces where-ever possible
- use single class imports and never use start import (yes, this item may seem pointless but it is part of convention)
- (others)

Below is a sample code fragment to illustrate basic formatting rules. Please note spaces apprering everywhere - 
after keywords, after opening parenthesis and before closing parenthesis, around operators, and so on.

```java
void someMethod( int num, String str )
{
    if ( num < 0 )
    {
        throw new IllegalArgumentException();
    }
    for ( int i = 0; i < num; i++ )
    {
        String result = otherMethod( str );
        System.out.println( result );
    }
}
```

