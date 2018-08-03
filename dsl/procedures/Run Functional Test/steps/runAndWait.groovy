$[/myProject/scripts/preamble]

def pluginProjectName = '$[/myProject/projectName]'
def configName = '$[config]'
def execution = '$[executionName]'
def limit = 40 // limit loops to poll; can potentially wrap into a timeout
def sleepTime = 10 // number of seconds to sleep

EFClient efClient = new EFClient()
def pluginConfig = efClient.getConfigValues('ec_plugin_cfgs', configName, pluginProjectName)

def url = pluginConfig.url
def clientId = pluginConfig.credential.userName
def clientSecret = pluginConfig.credential.password

def key = "$clientId:$clientSecret".bytes.encodeBase64().toString()
// construct body based on parameters
def body = [name: execution]
// if datasets are specified
if ('$[testCaseName]' && '$[dataSet]') {
    def testCase = '$[testCaseName]'
    def templates = '$[dataSet]'.split(/;\s*/)
    def i = 0
    def dataSetJson = []
    templates.each {
        def template = it.split(/\s*=\s*/)[0]
        def dataSets = it.split(/\s*=\s*/)[1].split(/\s*,\s*/)
        dataSetJson[i] = [testCaseame: testCase, templateName: template, dataSetNames: dataSets]
        i++
    }
    body.configuration = [dataSetSelection: dataSetJson]
    // body = [name: execution, configuration: [dataSetSelection: [[testCaseName: testCase, templateName: template, dataSetNames: dataSets]]]]
}
// json debug print
def builder = new groovy.json.JsonBuilder(body)
println builder.toString()

SSClient ssClient = new SSClient(url, key)
//ssClient.print()
println "Kick off Functional test $execution..."
//println body
def startTestResult = ssClient.runExecution(body)
def id = startTestResult.data.id
println "Fuctional test ID $id"
ssClient.waitForRun('functional', id, limit, sleepTime)