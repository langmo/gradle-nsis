**Table of contents**
* [Introduction](#introduction)
* [Configuration](#configuration)
* [Examples](#Examples)

# Introduction
The plugin uses [NSIS (Nullsoft Scriptable Install System)](https://nsis.sourceforge.io) to create a Windows installer for any gradle project, i.e. a single .EXE file which extract all binary files generated by the project to, for example, _C:/Program Files/MyProgram_. 

Different to other gradle plugins, this plugin comes with its own NSIS binaries which are automatically installed to a temporary folder. Therefore, no separate NSIS installation is needed, rendering this plugin suitable to be included e.g. in automatic builds of github repositories via _Actions_.

# Configuration
## How to include
The gradle-nsis plugin can be included via the standard plugin mechanism of Gradle 2.1 or higher:

    plugins {
      // Load gradle-nsis plugin
      id "com.github.langmo.gradlensis" version "0.1.0"
    }
    nsis {
      // Enter here the configuration settings of the gradle-nsis plugin. 
      // Note: a task "createInstaller" is automatically generated.
      
      // path to the NSIS configuration file
      configuration = file("${rootProject.projectDir}/installer_configuration.nsi")
      
      // folder where the NSIS installer generator is run. All paths 
      // in the NSIS configuration file are relative to this folder.
      runIn =  file("${rootProject.projectDir}")
    }

## How to configure
| Property Name | Default Value | Description |
|---------------|---------------|---------|
|File configuration      |`file("${rootProject.projectDir}/config.nsis")`|Configuration file of the NSIS installer. See examples and https://nsis.sourceforge.io/Docs/ for syntax.|
|File extractTo          |`file("${rootProject.buildDir}/tmp/nsis/")`|Folder where the NSIS binaries are automatically extracted. Usually, there is no need to change this property.|
|File runIn              |(Folder where NSIS configuration file is located)|Folder where the Windows task creating the NSIS installer (_makensis.exe_) is executed in (home/current directory). All file references in NSIS configuration file must be relative to this directory. Typically, this file is set to the base folder of the binaries created by the gradle project, e.g. `runIn =  file("${rootProject.buildDir}/install/my_application_name/")` would be suited when using the gradle application/distribution plugin.|
|File destinationFolder    |`file("${rootProject.buildDir}/distributions/")`|Folder where the NSIS installer, once created, is saved. Note: Do not include file name in this property, use _destinationName_ instead. Usually, there is no need to change this property.|
|Map<String, String> variables  |`[]`|Variables passed to NSIS installer generator, and which can be used in the NSIS configuration file. For example, when setting `variables = ['WIN64':'True']`, this is equivalent to the statement `!define WIN64 True` in the NSIS configuration file, and can e.g. be used by `!ifdef WIN64`...`!endif` conditional statements.|
|Set<String> additionalPlugins  |`[]`|Names of additional NSIS plugins on which the NSIS configuration file depends. Currently, only the plugin [AccessControl](https://nsis.sourceforge.io/AccessControl_plug-in) is available, which can be made available via `additionalPlugins = ['AccessControl']`|
|String destinationName |(see description)| Name of the installer which is created. Usually, the name of the installer is set in the NSIS configuration file via the _OutFile_ property, e.g. `OutFile "MyInstaller.exe"`, and there is no need to set this parameter here.|

# Examples
## Basic Example
This example creates an installer copying the file _basic.nsi_ into the installation folder. The raw files for this example can be found [here](https://github.com/langmo/gradle-nsis/tree/master/examples/basic).
Run `gradlew createInstaller` to generate the installer (the respective task is automatically generated).

File _/settings.gradle_:

    rootProject.name = 'basic'

File _/build.gradle_:

    // Load gradle-nsis plugin
    plugins {
        id "com.github.langmo.gradlensis" version "0.1.0"
    }

    // Configuration of gradle-nsis plugin.
    nsis {
        // path to the NSIS configuration file
        configuration = file("${rootProject.projectDir}/basic.nsi")
        // folder where the NSIS installer generator is run. All paths 
        // in the NSIS configuration file are relative to this folder.
        runIn =  file("${rootProject.projectDir}")
    }

File _/basic.nsi_ (see https://nsis.sourceforge.io/Docs/ for syntax):

    ;Include Modern UI
    !include "MUI2.nsh"
    
    ;Basic configuration
    Name "gradle-nsis basic example"
    OutFile "basic.exe"
    Unicode True
    ;Default installation folder
    InstallDir "$PROGRAMFILES64\gradle_nsis_basic_example"
    ;Request admin privileges for Vista/7/8/10
    RequestExecutionLevel admin
    !define MUI_ABORTWARNING
    
    ;Pages
    !insertmacro MUI_PAGE_LICENSE "LICENSE"
    !insertmacro MUI_PAGE_COMPONENTS
    !insertmacro MUI_PAGE_DIRECTORY
    !insertmacro MUI_PAGE_INSTFILES  
      
    ;Languages
    !insertmacro MUI_LANGUAGE "English"
    
    ;Installer Sections
    Section "gradle-nsis basic example" SecExample
        SetOutPath "$INSTDIR"
        FILE basic.nsi
    SectionEnd
    
    ;Descriptions
    LangString DESC_SecExample ${LANG_ENGLISH} "Copies the NSIS configuration."
    !insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
        !insertmacro MUI_DESCRIPTION_TEXT ${SecExample} $(DESC_SecExample)
    !insertmacro MUI_FUNCTION_DESCRIPTION_END
