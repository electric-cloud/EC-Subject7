import java.io.File

def procName = 'DeleteConfiguration'
procedure procName, {

    step 'deleteConfiguration',
        command: new File(pluginDir, "dsl/procedures/DeleteConfiguration/steps/deleteConfiguration.pl").text,
        errorHandling: 'failProcedure',
        exclusiveMode: 'none',
        shell: 'ec-perl',
        alwaysRun: 'false',
        broadcast: 'false',
        parallel: 'false',
        releaseMode: 'none'


}
