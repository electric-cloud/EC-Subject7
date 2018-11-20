public class SSClient extends BaseClient {

    def url
    def key

    SSClient (url, key) {
        this.url = url
        this.key = key
    }

    // Shared uri prefix for all API calls
    private String uriPrefix = "/api/v2/"

    Object doHttpGet(String requestUri, boolean failOnErrorCode = true, def query = null) {
        doHttpRequest(GET, url, uriPrefix + requestUri, ['Authorization': "Basic $key", 'accept': 'application/json'],
                failOnErrorCode, /*requestBody*/ null, query)
    }

    Object doHttpPost(String requestUri, Object requestBody, boolean failOnErrorCode = true, def query = null) {
        doHttpRequest(POST, url, uriPrefix + requestUri, ['Authorization': "Basic $key", 'Content-Type': 'application/json', 'accept': 'application/json'], failOnErrorCode, requestBody, query)
    }

    Object doHttpPut(String requestUri, Object requestBody = null, boolean failOnErrorCode = true, def query = null) {
        doHttpRequest(PUT, url, uriPrefix + requestUri, ['Authorization': "Basic $key", 'Content-Type': 'application/json', 'accept': 'application/json'], failOnErrorCode, requestBody, query)
    }

    def print() {
        println "$url"
    }


    def runExecution (Object body) {
        doHttpPost("executions", body)
    }

    def runLoad (Object body) {
        doHttpPost("load-plan-executions", body)
    }

    def cancelLoad (int id) {
        doHttpPut("load-plan-executions/$id")
    }

    def getExecution (int id) {
        doHttpGet("executions/$id")
    }

    def getTestCaseExecution (int id) {
        doHttpGet("test-case-executions/$id")
    }

    def getTestGroup (int id) {
        doHttpGet("executions/$id/test-groups")
    }

    def getDetailledExecution (int id, String caseName) {
       println "    getDetailledExecution ($id, $caseName)"
       doHttpGet("test-case-executions", true, ['test-case': caseName, 'execution': id])
    }

    def getLoad (int id) {
        doHttpGet("load-plan-executions/$id")
    }

    def waitForRun (String type, int id, int limit, int sleepTime){
        println "Waiting for $type test to be completed..."
        def counter = 0
        def status = ''
        while(status != 'PASS' && status != 'FAIL') {
            if (counter >= limit){
                println "Timeout reached, exiting EF calling step..."
                break
            }
            if (type == "functional") {
                def getTestResult = getExecution(id)
                status = getTestResult.data.executionStatus
            } else if (type == "load") {
                def getTestResult = getLoad(id)
                status = getTestResult.data.status
            } else {
                println "Invalid test type specified for wait - must be functional or load"
                break
            }
            println counter
            counter++
            sleep(sleepTime*1000)
        }
        if (status != 'PASS') {
            println "$type test execution failed with status \"$status\", exiting..."
            System.exit(1)
        } else {
            println "$type test execution completed."
        }
    }
}
