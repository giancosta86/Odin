# Odin

*Java library for German lexicons*


## Introduction

**Odin** is the very heart of the [Asgard](https://github.com/giancosta86/Asgard) project, because it extends [Balmung](https://github.com/giancosta86/Balmung) by providing:

* A **Lexicon** interface, implemented by **HibernateLexicon** - which can be used with any *SessionFactory* or, even better, with the one created by the included **LocalDatabase** (i.e., an in-memory or on-file HyperSQL db)

* **Parser** and **ParserResult** interfaces, to parse data sources and store lemmas into a *Lexicon* of choice

* **WiktionaryParser** - a parser capable of creating a filtered, simplified view of [Wiktionary's sources](http://download.wikipedia.org/dewiktionary/latest/dewiktionary-latest-pages-articles.xml.bz2). It internally employs *SAX parsing* and *multithreading* (via Java's *ExecutorService*) for maximum performance


As an interesting point, Odin's *HibernateLexicon* employs the currently new *Java-8 streaming feature* for queries, supported by Hibernate.



## Requirements

Java 8u144 or later is recommended to employ the library.


## Referencing the library

Odin is available on [Hephaestus](https://bintray.com/giancosta86/Hephaestus) and can be declared as a Gradle or Maven dependency; please refer to [its dedicated page](https://bintray.com/giancosta86/Hephaestus/Odin).

Alternatively, you could download the JAR file from Hephaestus and manually add it to your project structure.




## Further references

* [Asgard](https://github.com/giancosta86/Asgard)

* [Balmung](https://github.com/giancosta86/Balmung)

* [Valkyrie](https://github.com/giancosta86/Valkyrie)

* [Facebook page](https://www.facebook.com/Asgard-Exploring-German-1992307761040815/)
