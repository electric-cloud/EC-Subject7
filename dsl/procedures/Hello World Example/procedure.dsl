import java.io.File

def procName = 'Hello World Example'
procedure procName, {

    step 'sayHello',
        command: new File(pluginDir, "dsl/procedures/Hello World Example/steps/sayHello.pl").text,
        errorHandling: 'failProcedure',
        exclusiveMode: 'none',
        shell: 'ec-perl',
        alwaysRun: 'false',
        broadcast: 'false',
        parallel: 'false',
        releaseMode: 'none'


}
