# Code Generator REST Karaf container

This is a [Karaf](http://karaf.apache.org) container prepared with the Code Generator [REST Plugin](../codegenerator.rest.plugin/README.md).

## Getting started

Either start the service by executing `/target/assembly/bin/karaf` binary, or run the docker image port 8181 (exposed or mapped) accessible..

## Installing

You'll need maven.

```
mvn clean install
```

additionally the docker image is built by:

```
cd target
docker build -t code.generator .
```

## Further information

Check out the course in Code Generator wiki [here](../README.md).

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
