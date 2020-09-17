# Code Generator Protege Plugin

Protege plugin is a tool to use [Code Generator](../README.md) as a plugin of [Protégé](https://protege.stanford.edu/) tool  


## Getting started

This tool wrap the core of Code Generator with a user interface  
See protge [plugins](https://protegewiki.stanford.edu/wiki/PluginAnatomy) documentation.


## Installing

You'll need maven.

```
mvn clean install
```

for usage, read wiki.

## Testing

Is automatically performed for the instalation process. Check tests in the code (/src/test/java). Execute the test with 

```
mvn test
```

## Further information

Check out the course in Code Generator wiki [here](../README.md)

## Contributing

Pull requests are always appreciated. 
	
Any generated template can be hosted on the own git repository. This way the template can be referenced by public URLs in this tool. Currently there is not central database for templates.

## Credits

This software is manteined by: 
* Alejandro Medrano <amedrano@lst.tfo.upm.es> 
* Eduardo Bhuhid <ebuhid@lst.tfo.upm.es> 

## Licence

Code generator and all of its modules are released under [Apache Software Licence](http://www.apache.org/licenses/) version 2.0.