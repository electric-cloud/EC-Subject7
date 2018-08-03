import java.io.File

def procName = 'CreateConfiguration'
procedure procName, {

    step 'createConfiguration',
        command: new File(pluginDir, "dsl/procedures/CreateConfiguration/steps/createConfiguration.pl").text,
        errorHandling: 'abortProcedure',
        exclusiveMode: 'none',
        shell: 'ec-perl',
        alwaysRun: 'false',
        broadcast: 'false',
        parallel: 'false',
        postProcessor: 'postp',
        releaseMode: 'none'

    step 'createAndAttachCredential',
        command: new File(pluginDir, "dsl/procedures/CreateConfiguration/steps/createAndAttachCredential.pl").text,
        errorHandling: 'failProcedure',
        exclusiveMode: 'none',
        shell: 'ec-perl',
        alwaysRun: 'false',
        broadcast: 'false',
        parallel: 'false',
        releaseMode: 'none'


}
