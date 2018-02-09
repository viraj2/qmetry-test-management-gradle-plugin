# qmetry-test-management-gradle-plugin
QMetry Test Management plugin for Gradle has been designed to seamlessly integrate your CI/CD pipeline with QMetry.

QMetry Gradle Plugin uploads result file(s) generated in a Gradle project to QMetry Test Management.
The plugin, if used in a gradle project, provides an additional gradle task 'publishResults'

## How to use the plugin?
### Getting data from QMetry Test Management
To get qmetry configuration from QMetry Test Management :-
1. Login to QMetry Test Management application.
2. Choose an existing project or create new.
2. Goto *Apps > Automation App > Automation API*

OR directly visit https://testmanagement.qmetry.com/#/automation-api
### Installing plugin from Gradle's remote repository
Use the plugin from any machine (should have permission to download dependencies from internet) in a gradle project, by including a configuration in your **build.gradle** file.
The task `publishResults` always looks for `qmetryConfig {... }` in **build.gradle** file of your project. Provide following details :-

* **qtmUrl** - URL to your QMetry instance
* **qtmAutomationApiKey** - Automation API Key
* **automationFramework** - JUNIT/TESTNG/CUCUMBER/QAS/HPUFT
* **testSuite** (optional) - Key of test suite.
* **buildName** (optional) - Name of cycle linked to test suite
* **testResultFilePath** - path to result file (or directory for multiple files) relative to build directory
* **platform** (optional) - Name of the platform to connect the suite

Include the following code in your **build.gradle** file. Change `qmetryConfig {... }` values as required.
```
apply plugin: 'com.qmetry.qtmgradleplugin'

qmetryConfig
{
	qtmUrl='https://testmanagement.qmetry.com/'
	qtmAutomationApiKey='zEzs7iy77D8ARWX8xMFzdhfsrgh6W0LCyaK6xdec'
	automationFramework='JUNIT'
	testResultFilePath='/test-results/test/TEST-ispl.sample.AppSecondTest.xml'
	platform='chrome'
	testSuite='STR-TS-01'
	buildName='Default Cycle'
}

buildscript
{
	repositories
	{
        	mavenLocal()
		mavenCentral()
    	}
    	dependencies
	{
        	classpath 'com.qmetry:QTMGradlePlugin:1.0'
    	}
}
```
now, use the following command from your project directory.
```
gradle test publishResults
```
### Installing plugin in your local repository
to install the plugin in your local maven repository, clone this repository and use following command
```
gradle clean install
```
Now include the `qmetryConfig {... }` in **build.gradle** file of your target project and run the `publishResults` Task.
**Or** alternatively, use the following command on this repository to directly use executable jar file generated
```
gradle clean build
```
Use the jar file directly as a dependency in your Gradle project **build.gradle** file.
### Important Points
* BuildName refers to the *Cycle Name* in QMetry Test Management, and must be included in *Default Release* of your project.
* TestSuite should include *Test Suite Key* from your QMetry Test Management project. Ignore the field if you want to create a new Test Suite for the results.
* Platform (if specified), must be included in your QMetry Test Management project, before the task is executed.
