import java.io.File

def procName = 'Run Load Test'
procedure procName, {

    step 'runAndWait',
        command: new File(pluginDir, "dsl/procedures/Run Load Test/steps/runAndWait.groovy").text,
        errorHandling: 'failProcedure',
        exclusiveMode: 'none',
        shell: 'ec-groovy',
        alwaysRun: 'false',
        broadcast: 'false',
        parallel: 'false',
        releaseMode: 'none'


}
