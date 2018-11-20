project "EC-Subject7_Test", {
  procedure "simpleTest", {
    step 'version',
      command: '''ectool setProperty "summary" "Subject7 v$[/plugins/EC-Subject7/pluginVersion]" '''

    step 'call',
      afterStep: 'version',
      subproject: '/plugins/EC-Subject7/project',
      subprocedure: 'collectData',
      actualParameter: [
        config: 'subject7',
        executionId: '13'
      ]
  }
}
