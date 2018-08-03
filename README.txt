# Subject7 Plugin
Integration created for NHLBI to run tests with Subject7. Plugin procedures are written in Groovy as requested by NHLBI.

## Configurations
Configuration: config name
Credential: username and password
Description
URL: base url to accessing the Subject7 instance (e.g. https://proof.subject-7.com)


## Plugin Procedures
#### Run Functional Tests
#### Run Load Tests
#### Cancel Load Tests

## Misc. Notes
Groovy helper classes for these procedures are modeled after our existing Groovy plugins and defined in properties under /projects/NHLBI-Subject7-[version]/scripts. These helper classes are referenced via the preamble property, which is included at the start of every procedure involving API calls.
##### clientHelper
This property contains the helper classes BaseClient and EFClient.
BaseClient is a helper class for making generic REST API calls. Both EFClient and SSClient extend on top of this class for making tool-specific calls.
EFClient wraps up REST API calls to the EFlow server and contains methods that run EFlow related actions, such as retrieving plugin configuration data and setting a property.
##### Subject7Client
This property contains the helper class SSClient. It contains methods to construct and run REST requests to Subject7 using basic auth.</p>
The REST URI prefix is defined in this class under "uriPrefix". Update this value if Subject7 updates their API root path.</p>

Swagger documentation for Subject7 REST API:
https://proof.subject-7.com/spec/index.html