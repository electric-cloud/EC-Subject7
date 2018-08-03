import java.io.File

def procName = 'Cancel Load Test'
procedure procName, {

    step 'cancelTest',
        command: new File(pluginDir, "dsl/procedures/Cancel Load Test/steps/cancelTest.groovy").text,
        errorHandling: 'failProcedure',
        exclusiveMode: 'none',
        shell: 'ec-groovy',
        alwaysRun: 'false',
        broadcast: 'false',
        parallel: 'false',
        releaseMode: 'none'


}
