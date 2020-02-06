package com.github.langmo.gradlensis;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleScriptException;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFile;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;

public class GradleNsisTask extends DefaultTask 
{
	private final RegularFileProperty configuration;
	private final DirectoryProperty extractTo;
	private final DirectoryProperty runIn;
	private final MapProperty<String, String> variables;
	public GradleNsisTask() {
		configuration = getProject().getObjects().fileProperty();
		extractTo = getProject().getObjects().directoryProperty();
		runIn = getProject().getObjects().directoryProperty();
		variables = getProject().getObjects().mapProperty(String.class, String.class);
    }
	
	@Input
	@Optional
	public DirectoryProperty getRunIn() {
        return runIn;
    }
	
	@Input
	@Optional
	public MapProperty<String, String> getVariables() {
        return variables;
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
	public void createInstaller() throws GradleScriptException 
	{
		RegularFile installerFile = NsisExecutor.execute(getProject(), extractTo.get(), runIn.get(), variables.get(), configuration.get());
	}
}
