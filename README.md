# NodeFlow
NodeFlow is a software focused to create spigot (or any spigot forks) server plugins without coding.
The software uses Node Blueprint technology similar to UnrealEngine Blueprint.
The goal of this project is to provide simple yet powerful-enough tools for server owners to create a plugin
without having to code anything.

## Limitations
There are few limitations when using this software
* Java 17 - The project cannot be compiled and ran using JDK 17+ due to illegal reflective access. But you can still run it on JDK 17.
  
## Building
To build this repository, you will need JDK 16 installed on your device.
* Clone this repo
* Run `set MAVEN_OPTS=--illegal-access=permit & set NODEFLOW_STOREPASS=<PASSWORD> & mvn clean package`
* The build will be available at `target` directory
