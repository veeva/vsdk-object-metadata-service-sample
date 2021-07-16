# Vault Java SDK Sample - vsdk-object-metadata-sample

**Please see the [project wiki](https://github.com/veeva/vsdk-object-metadata-service-sample/wiki) for a detailed walkthrough**.

The **vsdk-object-metadata-sample** project covers the use of the SDK Object Metadata Service in a SDK Trigger. The Trigger will accomplish the following:

- Demonstrate how to get field types and value types of an object field
- Demonstrate how to use field types to get the full value of a long text field using VQL


## How to import

Import the project as a Maven project. This will automatically pull in the required Vault Java SDK dependencies. 

For Intellij this is done by:
-	File -> Open -> Navigate to project folder -> Select the 'pom.xml' file -> Open as Project

For Eclipse this is done by:
-	File -> Import -> Maven -> Existing Maven Projects -> Navigate to project folder -> Select the 'pom.xml' file


## Setup

For this project, the custom trigger and necessary vault components are contained in the two separate vault packages (VPK). The VPKs are located in the project's **deploy-vpk** directory  and **need to be deployed to your vault** prior to debugging these use cases:

1.  Clone or download the sample Maven project [vSDK Object Metadata Sample project](https://github.com/veeva/vsdk-object-metadata-sample) from Github.
2.  Run through the [Getting Started](https://developer.veevavault.com/sdk/#Getting_Started) guide to setup your development environment.
3.  Log in to your vault and navigate to **Admin > Deployment > Inbound Packages** and click **Import**:
4.  Locate and select the following file in your downloaded project file:

    >Deploy vault components: Select the \deploy-vpk\components\vsdk-object-metadata-sample-components.vpk file.
 
5.  From the **Actions** menu (gear icon), select **Review & Deploy**. Vault displays a list of all components in the package.   
6.  Review the prompts to deploy the package. You will receive an email when vault completes the deployment.
7.  Repeat steps 3-6 for the vault code:
    
    > **Custom Trigger** code: **\deploy-vpk\code\vsdk-object-metadata-sample-code.vpk** file.

 
## License

This code serves as an example and is not meant to be used for production use.

Copyright 2018 Veeva Systems Inc.
 
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
 
    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
