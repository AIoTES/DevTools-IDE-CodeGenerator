# Code Generator

The Code generator creates code from a given ontology using a pluggable template system, depending on which output may be SQL statements, or framework-specific interfaces or any text based files.

The user just need to provide a XML file to set basic configuration, and set the output directory for the generated


## Getting started

The code generator tool has a [Protege Plugin](./codegenerator.protege.plugin/README.md) to be used in Protege Ontology editor, a Maven plugin, and a REST service to use as a set of web services.
Checkout any of those flavours for specific instuctions about how to use them.


## Installing

You'll need maven.

```
mvn clean install
```

for installation of protege plugin read the wiki.

for docker image of REST service, go to codegenerator.rest.karaf/target and:

```
docker build -t code.generator .
```

for usage, read wiki.

## Testing
Is automatically performed for the instalation process. Check tests in the code (/src/test/java). Execute the test with 

```
mvn test
```