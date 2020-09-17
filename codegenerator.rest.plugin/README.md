# REST plugin

REST plugin give the possibility to use the code generator core through a REST api 

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

Code generator and all of its modules are released under [Apache Software Licence](http://www.apache.org/licenses/) version 2.0. 