import com.electriccloud.client.groovy.ElectricFlow
$[/myProject/scripts/preamble]

boolean DEBUG=true;
def pluginProjectName = '$[/myProject/projectName]'
def configName = '$[config]'
def testExecutionId = $[executionId]
String metadataPropertyPath = '$[metadataPropertyPath]'?'$[metadataPropertyPath]':'/myJob/subject7-$[executionId]'


def _baseDrilldownUrl="https://www.subject-7.com/"
int _buildNumber=0              // NUMBER
def _category='$[testCategory]' // required by filter
int _duration=0                 // NUMBER in ms
boolean _manual=false           // BOOLEAN
def _pluginConfiguration=configName
def _pluginName=pluginProjectName
def _releaseName="$[/javascript myRelease.releaseName]"
def _releaseProjectName="$[/javascript myRelease.projectName]"
def _releaseUri=""
def _runId=""
def _source="Subject7"
def _sourceUrl="https://www.subject-7.com/"
def _status=""
def _timestamp="$[/timestamp YYYY-MM-dd]T$[/timestamp HH:mm:ss].000Z"          // DATETIME

int _successfulTests=0
int _failedTests=0              // NUMBER
int _skippedTests=0             // NUMBER
def _totalTests=0

ElectricFlow ef = new ElectricFlow()
EFClient efClient = new EFClient()
def pluginConfig = efClient.getConfigValues('ec_plugin_cfgs', configName, pluginProjectName)

def url = pluginConfig.url
def clientId = pluginConfig.credential.userName
def clientSecret = pluginConfig.credential.password

def key = "$clientId:$clientSecret".bytes.encodeBase64().toString()

SSClient ssClient = new SSClient(url, key)

def sleeptime=15
def limit=80
boolean done=false
def counter=1
def resultString=""

println "Waiting for test $testExecutionId to complete..."
while ((counter < limit) && (!done)) {
  println "Loop $counter:"
  def te=ssClient.getExecution(testExecutionId).data
  println te.toString()
  def _state=te.executionState
  resultString=te.result
  _status=te.executionStatus
  if (DEBUG) {
    println "  State: _state"
    println "  partial result: $resultString"
    println "  partial status: $_status"
  }
  if (_state.equals("COMPLETED")) {
    done=true
    break
  }
  counter++
}
println ""

if (! done) {
  println "Test never completed"
  ef.setProperty(propertyName: "outcome", value: "warning")
  ef.setProperty(propertyName: "summary", value: "Test did not complete")
}

println "Test results from $testExecutionId are $resultString"

def res = resultString.split('/')
_successfulTests += res[0].toInteger()
_totalTests += res[1].toInteger()
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
}""".stripIndent()

  println "payload: " + _payload

ef.sendReportingData(
    reportObjectTypeName: 'quality',
    payload: _payload,
    /* success handler */ { response, data ->
      println('Data Sent correctly!')
    },
    /* failure handler */ { response, data ->
      println('Error sending data...')
    }
  )

println "\nSaving metatada to $metadataPropertyPath"
ef.setProperty(propertyName: "$metadataPropertyPath/successfulTests" , value: "$_successfulTests")
ef.setProperty(propertyName: "$metadataPropertyPath/failedTests" , value: "$_failedTests")
ef.setProperty(propertyName: "$metadataPropertyPath/skippedTests" , value: "$_skippedTests")
ef.setProperty(propertyName: "$metadataPropertyPath/totalTests" , value: "$_totalTests")

ef.setProperty(propertyName: "summary", value: """\
Passed:  $_successfulTests
Failed: $_failedTests
Skipped: $_skippedTests
Total: $_totalTests""")
