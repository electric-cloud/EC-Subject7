import java.io.File

def procName = 'collectData'
procedure procName, {

  step 'collectData',
    command: new File(pluginDir, "dsl/procedures/$procName/steps/collectData.groovy").text,
    errorHandling: 'failProcedure',
    shell: 'ec-groovy'
}
