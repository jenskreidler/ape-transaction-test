# ape-transaction-test
Arquillian Persistence Extension transactional test case for bug reproduction

Simple mavenized ejb project to reproduce a supposed-to-be bug in the arquillian-persistence extension. Using transactions combined with @ShouldMatchDataset will fail when using version 1.2.0.2, in version 1.1.13.0 it succeeds.

## Prerequisites

* Maven >3.3
* Java 11
* Wildfly 15+
* (Eclipse)

## Setup

In pom.xml, you can modify the properties in order to adjust the setup to your local installation:

* `integrationtests.directory`: Main directory for integration test
* `version.wildfly-dist`: The Wildfly distribution version running the Arquillian integration test, ex. 15.0.1.Final
* `wildfly-embedded.directory`: The absolute directory of the local Wildfly installation to use
* Your local Wildfly application server should've got the H2 datasource configured in JNDI under `java:/jboss/datasources/ExampleDS`

## Usage

Simply run `mvn clean install`

When running the integration test in Eclipse, use following sample VM arguments in the launch configuration:

`-Darquillian.debug=false -Darquillian.launch=wildfly-embedded -Djava.util.logging.manager=org.jboss.logmanager.LogManager
-Darquillian.server-runtime=/your/path/to/wildfly-15.0.1.Final --add-modules=java.se`

## Expectations

In the pom.xml, you'll find the property `version.arquillian-persistence` that configures the Arquillian Persistence Extension to use. 
If you swap versions, you will see that the given integrationtest `UserService_IT` fails or succeeds:

* Version `1.2.0.2`  = failure
* Version `1.1.13.0` = success
