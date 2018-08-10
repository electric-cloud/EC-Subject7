$[/myProject/scripts/preamble]

// test execution timeout settings for waiting for a run to complete
def limit = 60 // limit loops to poll; can potentially wrap into a timeout
def sleepTime = 10 // number of seconds to sleep per loop

def pluginProjectName = '$[/myProject/projectName]'
def configName = '$[config]'
def loadPlan = '$[loadPlanName]'

EFClient efClient = new EFClient()
def pluginConfig = efClient.getConfigValues('ec_plugin_cfgs', configName, pluginProjectName)

def url = pluginConfig.url
def clientId = pluginConfig.credential.userName
def clientSecret = pluginConfig.credential.password

def key = "$clientId:$clientSecret".bytes.encodeBase64().toString()
// construct body based on parameters
def body = [name: loadPlan, configuration: [:]]
if ('$[pool]'){
// add pool
    body.configuration.pool = '$[pool]'
}
// if datasets are specified
if ('$[testCaseName]' && '$[dataSet]') {
    def testCase = '$[testCaseName]'
    def templates = '$[dataSet]'.split(/;\s*/)
    def i = 0
    def dataSetJson = []
    templates.each {
        def template = it.split(/\s*=\s*/)[0]
        def dataSets = it.split(/\s*=\s*/)[1].split(/\s*,\s*/)
        dataSetJson[i] = [testCaseName: testCase, templateName: template, dataSetNames: dataSets]
        i++
    }
    body.configuration.dataSetSelection = dataSetJson
}

// print request body json to step log
def builder = new groovy.json.JsonBuilder(body)
println builder.toString()

SSClient ssClient = new SSClient(url, key)
println "Kick off load test $loadPlan..."
startTestResult = ssClient.runLoad(body)

def id = startTestResult.data.id
println "Fuctional test ID $id"
ssClient.waitForRun('load', id, limit, sleepTime)