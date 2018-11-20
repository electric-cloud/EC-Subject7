@Grab('org.codehaus.groovy.modules.http-builder:http-builder:0.7.1' )

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import groovy.transform.InheritConstructors

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC
import static groovyx.net.http.Method.GET
import static groovyx.net.http.Method.POST
import static groovyx.net.http.Method.PUT

public class EFClient extends BaseClient {

    def getServerUrl() {
        def commanderServer = System.getenv('COMMANDER_SERVER')
        def commanderPort = System.getenv("COMMANDER_HTTPS_PORT")
        def secure = Integer.getInteger("COMMANDER_SECURE", 1).intValue()
        def protocol = secure ? "https" : "http"

        return "$protocol://$commanderServer:$commanderPort"
    }

    // Shared uri prefix for all API calls
    private String uriPrefix = "/rest/v1.0/"

    public static def splitCommaSeparatedList( String list ) {
        if ( !list ) {
            return null
        }
        return list.split(/,\s/)
    }

    Object doHttpGet(String requestUri, boolean failOnErrorCode = true, def query = null) {
        def sessionId = System.getenv('COMMANDER_SESSIONID')
        doHttpRequest(GET, getServerUrl(), uriPrefix + requestUri, ['Cookie': "sessionId=$sessionId"],
                failOnErrorCode, /*requestBody*/ null, query)
    }

    Object doHttpPost(String requestUri, Object requestBody, boolean failOnErrorCode = true, def query = null) {
        def sessionId = System.getenv('COMMANDER_SESSIONID')
        doHttpRequest(POST, getServerUrl(), uriPrefix + requestUri, ['Cookie': "sessionId=$sessionId"], failOnErrorCode, requestBody, query)
    }

    Object doHttpPut(String requestUri, Object requestBody, boolean failOnErrorCode = true, def query = null) {
        def sessionId = System.getenv('COMMANDER_SESSIONID')
        doHttpRequest(PUT, getServerUrl(), uriPrefix + requestUri, ['Cookie': "sessionId=$sessionId"], failOnErrorCode, requestBody, query)
    }


    def setProperty( String jobStepId, String propertyName, String value) {
        def query = [
            propertyName: propertyName,
            value: value,
            jobStepId: jobStepId
        ]
        doHttpPost("properties", /* request body */ null, /* fail on error*/ true, query)
    }

    def getConfigValues(def configPropertySheet, def config, def pluginProjectName) {

        // Get configs property sheet
        def result = doHttpGet("projects/$pluginProjectName/$configPropertySheet", false)

        def configPropSheetId = result.data?.property?.propertySheetId
        if (!configPropSheetId) {
            throw new RuntimeException("No plugin configurations exist!")
        }

        result = doHttpGet("propertySheets/$configPropSheetId", false)
        // Get the property sheet id of the config from the result
        def configProp = result.data.propertySheet.property.find{
            it.propertyName == config
        }

        if (!configProp) {
            throw new RuntimeException("Configuration $config does not exist!")
        }

        result = doHttpGet("propertySheets/$configProp.propertySheetId")

        def values = result.data.propertySheet.property.collectEntries{
            [(it.propertyName): it.value]
        }

        logger(1, "Config values: " + values)

        def cred = getCredentials(config)
        values << [credential: [userName: cred.userName, password: cred.password]]

        logger(1, "After Config values: " + values ) // TODO DANGER!! CREDENTIALS!!!

        if ( values.debugLevel ) {
            values.debugLevel = values.debugLevel as int
        }
        else {
            values.debugLevel = 1
        }

        values
    }

    def getCredentials(def credentialName) {
        def jobStepId = '$[/myJobStep/jobStepId]'
        def result = doHttpGet("jobsSteps/$jobStepId/credentials/$credentialName")
        logger(1, result)
        result.data.credential
    }


    def handleError (String msg) {
        println "ERROR: $msg"
        System.exit(-1)
    }
}

public class BaseClient {

    def logLevel = 2

    Object doHttpRequest(Method method, String requestUrl,
                         String requestUri, def requestHeaders,
                         Boolean failOnErrorCode = true,
                         Object requestBody = null,
                         def queryArgs = null) {

        logger(1, "requestUrl: $requestUrl")
        logger(1, "URI: $requestUri")
        logger(1, "Query: $queryArgs")
        if (requestBody) logger(1, "Payload: $requestBody")

        def http = new HTTPBuilder(requestUrl)
        http.ignoreSSLIssues()

        http.request(method, JSON) {
            uri.path = requestUri
            headers = requestHeaders
            body = requestBody
            uri.query = queryArgs

            response.success = { resp, json ->
                logger(1, "request was successful $resp.statusLine.statusCode $json")
                [statusLine: resp.statusLine,
                 data      : json]
            }

            response.failure = { resp, reader ->
                println "request failed $resp.statusLine Error details:\n$reader"
                if ( failOnErrorCode ) {
                    handleError("Request failed with $resp.statusLine")
                }
                [statusLine: resp.statusLine]
            }
        }
    }

    def logger (int level, def message) {
        if ( level >= this.logLevel ) {
            println message
        }
    }
}

public class Validation {
    def static int readInteger(String param, String fieldName) {
        int value
        try {
            value = param as int
        } catch (def exception) {
            println "ERROR: Field $fieldName should contain an integer value!"
            System.exit(-1)
        }
        return value
    }
}

@InheritConstructors
class PluginException extends Exception {}
