import java.io.File

def procName = 'Run Functional Test'
procedure procName, {

    step 'runAndWait',
        command: new File(pluginDir, "dsl/procedures/Run Functional Test/steps/runAndWait.groovy").text,
        errorHandling: 'failProcedure',
        exclusiveMode: 'none',
        postLogFileName: '-postp',
        shell: 'ec-groovy',
        alwaysRun: 'false',
        broadcast: 'false',
        parallel: 'false',
        releaseMode: 'none'


}
