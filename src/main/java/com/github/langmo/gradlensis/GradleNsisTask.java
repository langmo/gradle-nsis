package com.github.langmo.gradlensis;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;

public class GradleNsisTask extends DefaultTask 
{
	private final RegularFileProperty configuration;
	private final DirectoryProperty extractTo;
	private final DirectoryProperty runIn;
	public GradleNsisTask() {
		configuration = getProject().getObjects().fileProperty();
		extractTo = getProject().getObjects().directoryProperty();
		runIn = getProject().getObjects().directoryProperty();
    }
	
	@Input
	@Optional
	public DirectoryProperty getRunIn() {
        return runIn;
    }
	
	@Input
    public RegularFileProperty getConfiguration() {
        return configuration;
    }
	@Input
    public DirectoryProperty getExtractTo() {
        return extractTo;
    }
	
	@TaskAction
	public void createInstaller() throws Exception
	{
		System.out.println("Extracting NSIS to: " + extractTo.get());
		//if(!runIn.isPresent())
		//	runIn.set(configuration.get().getAsFile().getParentFile());
		NsisExecutor.execute(getProject(), extractTo.get(), runIn.get(), configuration.get());
		System.out.println("NSIS configuration file: " + configuration.get());
	}
}
