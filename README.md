# NodeFlow
NodeFlow is a software focused to create spigot (or any spigot forks) server plugins without coding.
The software uses Node Blueprint technology similar to UnrealEngine Blueprint.
The goal of this project is to provide simple yet powerful-enough tools for server owners to create a plugin
without having to code anything.

## Limitations
There are few limitations when using this software
* Java Class Abstraction - You can create a class that extends an interface/abstract class, however
you can't create an abstract class that is extendable for other class.
* Non-OOP - The software applies Event-Driven programming instead of pure OOP, but we also provide a record module
  (similar to record feature in the latest java version) that is good enough to provide OOP functionality.
  
## Building
To build this repository, you will need JDK 16 installed on your device.
* Clone this repo
* Run `set MAVEN_OPTS=--illegal-access=permit & set NODEFLOW_STOREPASS=<PASSWORD> & mvn clean package`
* The build will be available at `target` directory
