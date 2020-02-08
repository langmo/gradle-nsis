package com.github.langmo.gradlensis;

import org.gradle.api.Project;
import org.gradle.api.Action;

import org.gradle.api.Plugin;

/**
 * A plugin to generate NSIS installers in gradle.
 */
public class GradleNsisPlugin implements Plugin<Project> 
{
    private static final String TASK_GROUP = "NSIS";
	private static final String EXTENSION_NAME = "nsis";
	private static final String TASK_RUN_NAME = "createInstaller";
	
	@Override	    
    public void apply(Project project) 
    {
		project.getExtensions().create(EXTENSION_NAME, GradleNsisPluginExtension.class, project);
		project.getTasks().register(TASK_RUN_NAME, GradleNsisTask.class, new Action<GradleNsisTask>() {

			@Override
			public void execute(GradleNsisTask task) {
				task.setGroup(TASK_GROUP);
				task.setDescription("Runs NSIS to generate an installer");
			}
			
		});
    }
}
