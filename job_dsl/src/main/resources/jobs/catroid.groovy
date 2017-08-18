folder("Catroid")

def catroid(String job_name, Closure closure) {
    new AndroidJobBuilder(job(job_name), new CatroidData()).make(closure)
}

catroid('Catroid/PartialTests') {
    htmlDescription(['Do <b>not</b> start this job manually.',
                     'A job to execute emulator tests defined by external jobs via exclude files.'])

    jenkinsUsersPermissions(Permission.JobRead, Permission.JobCancel)

    parameterizedGit()
    parameterizedAndroidVersion()
    parameterizedTestExclusionsFile()
    androidEmulator()
    gradle('connectedCatroidDebugAndroidTest')
}

catroid('Catroid/CustomBranch') {
    htmlDescription(['This job builds and runs static analysis and tests of the given REPO/BRANCH.'])

    jenkinsUsersPermissions(Permission.JobBuild, Permission.JobRead, Permission.JobCancel)

    parameterizedGit()
    parameterizedAndroidVersion()
    excludeTests()
    androidEmulator()
    gradle('check test connectedCatroidDebugAndroidTest')
    staticAnalysis()
    junit()
}

catroid('Catroid/ParallelTests-CustomBranch') {
    htmlDescription(['This job builds and runs UI tests of the given REPO/BRANCH.'])

    jenkinsUsersPermissions(Permission.JobBuild, Permission.JobRead, Permission.JobCancel)

    parameterizedGit()
    parameterizedAndroidVersion()
    parallelTests('Catroid/PartialTests', 2)
}

catroid('Catroid/PullRequest') {
    htmlDescription(['Job is automatically started when a pull request is created on github.'])

    jenkinsUsersPermissions(Permission.JobRead, Permission.JobCancel)

    pullRequest()
    androidEmulator(androidApi: 22)
    gradle('clean check test connectedCatroidDebugAndroidTest',
           '-Pandroid.testInstrumentationRunnerArguments.package=org.catrobat.catroid.test')
    staticAnalysis()
    junit()
}

catroid('Catroid/PullRequest-Espresso') {
    htmlDescription(['Job is manually trigger for pull requests on github to run Espresso tests.'])

    jenkinsUsersPermissions(Permission.JobRead, Permission.JobCancel)

    pullRequest(triggerPhrase: /.*please\W+run\W+espresso\W+tests.*/,
                onlyTriggerPhrase: true,
                context: 'Espresso Tests')
    androidEmulator(androidApi: 22)
    gradle('connectedCatroidDebugAndroidTest',
           '-Pandroid.testInstrumentationRunnerArguments.class=org.catrobat.catroid.uiespresso.testsuites.PullRequestTriggerSuite')
    junit()
}

catroid('Catroid/Nightly') {
    htmlDescription(['Nightly Catroid job.'])

    jenkinsUsersPermissions(Permission.JobRead)

    git()
    nightly()
    excludeTests()
    androidEmulator(androidApi: 22)
    gradle('check test assembleDebug connectedCatroidDebugAndroidTest',
           '-Pindependent="Code Nightly #${BUILD_NUMBER}"')
    uploadApkToFilesCatrobat()
    staticAnalysis()
    junit()
}