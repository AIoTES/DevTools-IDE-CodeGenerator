# Code Generator Maven Plugin 
The Maven plugin is an adaptation of the [Code Generator](../README.md) to be used in the [Maven](https://maven.apache.org/) lifecycle. 
It consists of a single goal *generate*, which by default is executed during the *generate-sources* phase; meaning the results could be compiled right after.

## Getting started

add the following to your pom.xml:
```xml
...
<build>
    <plugins>
      <plugin>
        <groupId>es.upm.tfo.lst</groupId>
        <artifactId>codegenerator.maven.plugin</artifactId>
        <version>0.0.1-SNAPSHOT</version>
        <executions>
          <execution>
            <id>generate-code</id>
            <goals>
              <goal>generate</goal>
            </goals>
          </execution>
        </executions>
		<configuration>
			<xmlTemplate>file:///myTemplate/template.xml</xmlTemplate>
		</configuration>
      </plugin>
    </plugins>
  </build>
...
```
**NOTE:** change the xmlTemplate value to the url referencing the template to use.

Code generation will occur on generate-sources phase, when installing.

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

Check out the course in Code Generator wiki [here](../README.md).
For more information on the parameters of the plugin, read the wiki entry for the Maven plugin.

## Contributing

Pull requests are always appreciated. 

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
