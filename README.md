# Code Generator

The Code generator creates code from a given ontology using a pluggable template system, depending on which output may be SQL statements, or framework-specific interfaces or any text based files.

The user just need to provide a XML file to set basic configuration, and set the output directory for the generated


## Getting started

The code generator tool has a?[Protege Plugin](./codegenerator.protege.plugin/README.md)?to be used in Protege Ontology editor, a [Maven plugin](./codegenerator.maven.plugin/README.md), and a [REST service](./codegenerator.rest.plugin/README.md) to use as a set of web services.
Checkout any of those flavours for specific instuctions about how to use them.

For usage, read wiki.

## Installing

You'll need maven.

```
mvn clean install
```

for docker image of REST service, once installed through maven, go to codegenerator.rest.karaf/target and:

```
docker build -t code.generator .
```

## Testing

Is automatically performed for the instalation process. Check tests in the code (/src/test/java). Execute the test with 

```
mvn test
```

## Further information

Check out the course at [here](https://poliformat.upv.es/portal/site/ESP_0_2626/tool/4136ab45-e867-4287-ac8e-d5eed63f8307/ShowPage?returnView=&studentItemId=0&backPath=&errorMessage=&messageId=&clearAttr=&source=&title=&sendingPage=6007389&newTopLevel=false&postedComment=false&itemId=6007390&addBefore=&path=push&topicId=&addTool=-1&recheck=&id=&forumId=)

## Contributing

Pull requests are always appreciated. 
	
Any generated template can be hosted on the own git repository. This way the template can be referenced by public URLs in this tool. Currently there is not central database for templates.

## Credits

This software is manteined by: 
* Alejandro Medrano <amedrano@lst.tfo.upm.es> 
* Eduardo Bhuhid <ebuhid@lst.tfo.upm.es> 

## Licence
```
   Copyright 2018 Universidad Politécnica de Madrid

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```
