# REST plugin

REST plugin gives the possibility to use the code generator core through a REST api. 

## Getting started

The REST api is exposed at {URL}:8181/GenerateCode

**Request**

```json
Method: POST
Content-type: application/json
Request body: 

{
  "template": "",
  "ontologies": [
    {
      "url": "",
      "recursive": ""
    }
  ],
  "variables": {
    "varname": "varvalue"
  }
}
```
* template: XML template URL
* ontologies: array of object containing
  * url: ontology URL
  * recursive: true/false 
* variables : indicate aditional variables to add:
  * varname: variable name
  * varvalue: variable value
  

## Installing

You'll need maven.

```
mvn clean install
```

to execute we recommend using the prepared [karaf container](../codegenerator.rest.karaf).

## Testing
Is automatically performed for the instalation process. Check tests in the code (/src/test/java). Execute the test with 

```
mvn test
```
## Further information

Check out the course at [here](https://poliformat.upv.es/portal/site/ESP_0_2626/tool/4136ab45-e867-4287-ac8e-d5eed63f8307/ShowPage?returnView=&studentItemId=0&backPath=&errorMessage=&messageId=&clearAttr=&source=&title=&sendingPage=6007389&newTopLevel=false&postedComment=false&itemId=6007390&addBefore=&path=push&topicId=&addTool=-1&recheck=&id=&forumId=) or read the wiki entry for the REST plugin.

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
