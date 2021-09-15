# Contributing to this repository
## Types of Contributions
### Translations
The software is mainly written in English language. You can contribute
to make it available in another language by simply opening a pull request on Locale files.
We will have to check your translation to make sure it is an accurate translation.
We do not allow any translation that comes from an automated machine (i.e. Google Translate/Yandex Translate) for the better user experience.
### Pull Requests
Pull request is a way to suggest changes or help us fix issues in this repository.
See our naming guidelines, and our code structure before submitting a pull request to
make you understand better of how our code written.
## Naming Guidelines
All java classes should follow the standard java naming convention.
For better readability, all resource files must use camel case typing, but this one does not 
apply to locale files since they are code specific file name.
## Code Structure
There are 4 sub-modules inside this repository.
### BasePlugin
BasePlugin is the bundled plugin that included in the first launch/install of the software.
### BuildHelper
BuildHelper is a maven plugin to help build the project.
### Bundler
Bundler is a dummy project to package JDK 16 along with the software.
### CodeEngine
This sub-module handles the byte code generation used by the software for Java Export
option. This sub-module used by Internal as dependencies.
### Configuration
This sub-module contains configuration API used by Internal module.
### Engine
This sub-module handles the Node Blueprint engine used by the software to render nodes
into JavaFX components. This sub-module used by Internal as dependencies.
### Internal
This is the main sub-module that handles how the software works. It basically contains everything
that makes the software runs.
### Launcher
Launcher module help the software to run by loading its dependencies and wrap them as an executable.
### Library
This sub-module provides functional and useful utilities. This sub-module contains everything
the software need to make it run.
### Resources
Resources module contains all media/locales/graphics/fonts the software needed to display its UI.
### Setup
Setup a.k.a Updater is a standalone app that update the software files without downloading the entire software files.
### docs
The nodeflow website
## Resources Guidelines
Resources such as Images, Icons, Fonts, etc, must follow these rules:
* The resource must be DMCA free or have the original author to give us permission to use it on this project.
* We have legal access and permission to use the resource on this project.

If any of existing resources violated one of these rules, they will be removed from the repository and will be
replaced with a new one.

Submit an issue if you found your resource being used illegally in the project.

Anything that written/submitted exclusively for this project will be automatically owned by this project.
In other words, layouts, css's, images, icons, etc that are submitted is protected under the repository LICENSE.

## Development
We recommend you to use IntelliJ alongside with these plugins to help you develop the software:
* [Easy I18n](https://plugins.jetbrains.com/plugin/16316-easy-i18n/)