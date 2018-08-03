$[/myProject/scripts/preamble]

def pluginProjectName = '$[/myProject/projectName]'
def configName = '$[config]'
def loadPlanName = '$[loadPlanName]'
def limit = 40 // limit loops to poll; can wrap into a timeout
def sleepTime = 10 // number of seconds to sleep

EFClient efClient = new EFClient()
def pluginConfig = efClient.getConfigValues('ec_plugin_cfgs', configName, pluginProjectName)

def url = pluginConfig.url
def clientId = pluginConfig.credential.userName
def clientSecret = pluginConfig.credential.password

def key = "$clientId:$clientSecret".bytes.encodeBase64().toString()
def body = [name: loadPlanName]

SSClient ssClient = new SSClient(url, key)
println "Kick off load test $loadPlanName..."
startTestResult = ssClient.runLoad(body)
def id = startTestResult.data.id
println "Fuctional test ID $id"
ssClient.waitForRun('load', id, limit, sleepTime)