# Code Generator Protege Plugin

Protege plugin is a tool enabling the use of [Code Generator](../README.md) as a plugin of the [Protégé](https://protege.stanford.edu/) tool.


## Getting started

This tool wrap the core of Code Generator with a user interface.
See protge [plugins](https://protegewiki.stanford.edu/wiki/PluginAnatomy) documentation.
Once installed, and Protege has started, the user can access to the plugin selecting in Protege menu bar tools>CodeGenerator tool.

## Installing

You'll need maven.

```
mvn clean install
```

After [downloading](https://protege.stanford.edu/) Protege,the user must put the jar plugin file of CodeGenerator (available in the ```target``` folder) into the directory called "plugins" on Protege main installation directory.
Additionally, add the following bundles into "bundles" directory (search in the table the bundle link in the row called "files" and download the bundle file):
*  [org.apache.servicemix.bundles.commons-collections-3.2.1_3.jar](https://mvnrepository.com/artifact/org.apache.servicemix.bundles/org.apache.servicemix.bundles.commons-collections/3.2.1_3)
*  [org.apache.servicemix.bundles.commons-lang-2.4_6.jar](https://mvnrepository.com/artifact/org.apache.servicemix.bundles/org.apache.servicemix.bundles.commons-lang/2.4_6)
*  [jfact-4.0.4.jar](https://mvnrepository.com/artifact/net.sourceforge.owlapi/jfact/4.0.4)
*  [jsoup-1.11.3](https://mvnrepository.com/artifact/org.jsoup/jsoup/1.11.3) (download jar file)
*  [org.apache.servicemix.bundles.velocity-1.7_6.jar](https://mvnrepository.com/artifact/org.apache.servicemix.bundles/org.apache.servicemix.bundles.velocity/1.7_6)
*  [velocity-tools](https://mvnrepository.com/artifact/org.apache.servicemix.bundles/org.apache.servicemix.bundles.velocity-tools/2.0_1)

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
