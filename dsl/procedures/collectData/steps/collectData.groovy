import com.electriccloud.client.groovy.ElectricFlow
$[/myProject/scripts/preamble]

def pluginProjectName = '$[/myProject/projectName]'
def configName = '$[config]'
def executionId = $[executionId]

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
//ssClient.print()
println "gather data from test $executionId..."
def tce=ssClient.getTestCaseExecution(executionId).data
println tce.toString()

println "id: " + tce.id.toString()
println "status: " + tce.status
println "duration: " + tce.duration

_runId=tce.id
_status=tce.status
_duration=tce.duration * 1000

println "gather test groups"
def tg=ssClient.getTestGroup(executionId).data
println tg.toString()

def totalTests=0
tg.each {
  def resultString=it.result
  def res = resultString.split('/')
  _successfulTests += res[0].toInteger()
  totalTests += res[1].toInteger()
}
_failedTests = totalTests - _successfulTests
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
println result.toString()
