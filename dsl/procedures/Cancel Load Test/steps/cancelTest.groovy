$[/myProject/scripts/preamble]

def pluginProjectName = '$[/myProject/projectName]'
def configName = '$[config]'
def testId = '$[testId]'

EFClient efClient = new EFClient()
def pluginConfig = efClient.getConfigValues('ec_plugin_cfgs', configName, pluginProjectName)

def url = pluginConfig.url
def clientId = pluginConfig.credential.userName
def clientSecret = pluginConfig.credential.password

def key = "$clientId:$clientSecret".bytes.encodeBase64().toString()

SSClient ssClient = new SSClient(url, key)
//ssClient.print()
println "Canceling load test $testId..."
//println body
startTestResult = ssClient.doHttpPut("load-plan-executions/$testId")
println "Canceled load test $testId"