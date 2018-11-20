import com.electriccloud.client.groovy.ElectricFlow
$[/myProject/scripts/preamble]

def pluginProjectName = '$[/myProject/projectName]'
def configName = '$[config]'
def testExecutionId = $[executionId]
String metadataPropertyPath = '$[metadataPropertyPath]'?'$[metadataPropertyPath]':'/myJob/subject7-$[executionId]'

def _baseDrilldownUrl="https://www.subject-7.com/"
int _buildNumber=0              // NUMBER
def _category="system-test"     // required by filter
int _duration=0                 // NUMBER in ms
int _failedTests=0              // NUMBER
boolean _manual=false           // BOOLEAN
def _pluginConfiguration=configName
def _pluginName=pluginProjectName
def _releaseName="$[/javascript myRelease.releaseName]"
def _releaseProjectName="$[/javascript myRelease.projectName]"
def _releaseUri=""
def _runId=""
int _skippedTests=0                     // NUMBER
def _source="Subject7"
def _sourceUrl="https://www.subject-7.com/"
def _status=""
int _successfulTests=0
def _timestamp="$[/timestamp YYYY-MM-dd]T$[/timestamp HH:mm:ss].000Z"          // DATETIME

EFClient efClient = new EFClient()
def pluginConfig = efClient.getConfigValues('ec_plugin_cfgs', configName, pluginProjectName)

def url = pluginConfig.url
def clientId = pluginConfig.credential.userName
def clientSecret = pluginConfig.credential.password

def key = "$clientId:$clientSecret".bytes.encodeBase64().toString()

SSClient ssClient = new SSClient(url, key)
println "gather data from test $testExecutionId..."
def tce=ssClient.getTestCaseExecution(testExecutionId).data
println tce.toString()

_executionId=tce.executionId
_status=tce.status
_duration=tce.duration * 1000

println ""
println "gather test groups $_executionId"
def tg=ssClient.getTestGroup(_executionId).data
println tg.toString()

def _totalTests=0
def loop=0
tg.each {
  def resultString=it.result
  def testCaseName=it.testCaseName
  def res = resultString.split('/')
  _successfulTests += res[0].toInteger()
  _totalTests += res[1].toInteger()
  loop++
}

_failedTests = _totalTests - _successfulTests
println ""
println "Recap:"
println "------------------------------------------------"
println "baseDrilldownUrl: " + _baseDrilldownUrl
println "buildNumber: " + _buildNumber
println "category: " + _category
println "duration: " + _duration
println "failedTests: " + _failedTests
println "manual: " + _manual
println "pluginConfiguration: " + _pluginConfiguration
println "pluginName: " + _pluginName
println "releaseName: " + _releaseName
println "releaseProjectName: " + _releaseProjectName
println "releaseUri: " + _releaseUri
println "runId: " + _runId
println "skippedTests: " + _skippedTests
println "source: " + _source
println "sourceUrl: " + _sourceUrl
println "status: " + _status
println "successfulTests: " + _successfulTests
println "timestamp: " + _timestamp


def _payload="""{
  "baseDrilldownUrl":"$_baseDrilldownUrl",
  "buildNumber":$_buildNumber,
  "category":"$_category",
  "duration":$_duration,
  "failedTests":$_failedTests,
  "manual":"$_manual",
  "pluginConfiguration":"$_pluginConfiguration",
  "pluginName":"$_pluginName",
  "releaseName":"$_releaseName",
  "releaseProjectName":"$_releaseProjectName",
  "releaseUri":"$_releaseUri",
  "runId":"$_runId",
  "skippedTests":$_skippedTests,
  "source":"$_source",
  "sourceUrl":"$_sourceUrl",
  "status":"$_status",
  "successfulTests":$_successfulTests,
  "timestamp":"$_timestamp"
}"""

ElectricFlow ef = new ElectricFlow()
def result = ef.sendReportingData(
  reportObjectTypeName: 'quality',
  payload: _payload)

println "\nSaving metatada to $metadataPropertyPath"
ef.setProperty(propertyName: "$metadataPropertyPath/successfulTests" , value: "$_successfulTests")
ef.setProperty(propertyName: "$metadataPropertyPath/failedTests" , value: "$_failedTests")
ef.setProperty(propertyName: "$metadataPropertyPath/skippedTests" , value: "$_skippedTests")
ef.setProperty(propertyName: "$metadataPropertyPath/totalTests" , value: "$_totalTests")
