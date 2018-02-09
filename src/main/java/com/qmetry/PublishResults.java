package com.qmetry;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.Input;

import java.util.Map;
import java.io.File;

public class PublishResults extends DefaultTask 
{
	public PublishResults()
	{
		setDescription("Faster way to linking automated test result to QMetry Test Management, to ship high quality products.");
		setGroup("QMetry Test Management");
	}
	
    @TaskAction
    public void publishResults() throws QTMException
	{
		String pluginName = "QMetry Test Management Gradle Plugin";
		try 
		{
			QTMGradleExtension config = getProject().getExtensions().findByType(QTMGradleExtension.class);
			if(config == null) throw new QTMException("Could not find QTM configuration! please provide qmetryConfig {... } block with appropriate parameters in your build.gradle file!");

			String displayName = pluginName + " : Starting Post Build Action";
			String repeated = new String(new char[displayName.length()]).replace("\0", "-");
			System.out.println("\n\n" + repeated + "\n" + displayName + "\n" + repeated);
			
			if(!config.getParsedTestSuite().isEmpty())
			{
				System.out.println(pluginName + " : Using Test Suite '"+config.getParsedTestSuite()+"'");
			}
			if(!config.getParsedPlatform().isEmpty())
			{
				System.out.println(pluginName + " : Using Platform '"+config.getParsedPlatform()+"'");
			}
			if(!config.getParsedBuildName().isEmpty())
			{
				System.out.println(pluginName + " : Using Cycle '"+config.getParsedBuildName()+"'");
			}
			String compfilepath = getProject().getBuildDir().toString() + "/" + config.getParsedTestResultFilePath();
			File resultFile = new File(compfilepath);
			if(resultFile==null || !resultFile.exists()) 
				throw new QTMException("Result file(s) '"+compfilepath+"' not Found!");
			
			QTMApiConnection conn = new QTMApiConnection(config.getParsedQtmUrl(), config.getParsedQtmAutomationApiKey());
			synchronized (conn) 
			{
				if(resultFile.isDirectory()) 
				{
					System.out.println(pluginName + " : Reading result files from Directory '" + compfilepath+ "'");
					File[] listOfFiles = resultFile.listFiles();

					for (int i = 0; i < listOfFiles.length; i++) 
					{
						if (listOfFiles[i].isFile() && (listOfFiles[i].getName().endsWith(".xml") || listOfFiles[i].getName().endsWith(".json"))) 
						{
							System.out.println("\n" +pluginName+ " : Result File Found '" + listOfFiles[i].getName() + "'");
							System.out.println(pluginName + " : Uploading result file...");
							conn.uploadFileToTestSuite(listOfFiles[i].getAbsolutePath(), 
														config.getParsedTestSuite(),
														config.getParsedAutomationFramework(), 
														config.getParsedBuildName(), 
														config.getParsedPlatform());
							System.out.println(pluginName + " : Result file successfully uploaded!");
						}
					}
				}
				else if(resultFile.isFile())
				{
					System.out.println(pluginName + " : Reading result file '" + compfilepath+ "'");
					System.out.println(pluginName + " : Uploading result file...");
					conn.uploadFileToTestSuite(compfilepath, 
												config.getParsedTestSuite(), 
												config.getParsedAutomationFramework(),
												config.getParsedBuildName(), 
												config.getParsedPlatform());
					System.out.println(pluginName + " : Result file successfully uploaded!");
				}
				else
				{
					throw new QTMException("Failed to read result file '"+compfilepath+"'");
				}
			}
		} 
		catch (QTMException e) 
		{
			System.out.println(pluginName + " : ERROR : " + e.getMessage());
		} 
		catch (Exception e) 
		{
			System.out.println(pluginName + " : ERROR : " + e.toString());
		}
		System.out.println("\n" + pluginName + " : Finished Post Build Action!");
    }
}