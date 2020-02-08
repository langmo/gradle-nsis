package com.github.langmo.gradlensis;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleScriptException;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFile;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.logging.LogLevel;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.SetProperty;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;

public class GradleNsisTask extends DefaultTask 
{
	
	private final RegularFileProperty configuration;
	private final DirectoryProperty extractTo;
	private final DirectoryProperty runIn;
	private final MapProperty<String, String> variables;
	private final SetProperty<String> additionalPlugins;
	private final DirectoryProperty destinationFolder;
	private final Property<String> destinationName;
	public GradleNsisTask() {
		configuration = getProject().getObjects().fileProperty();
		extractTo = getProject().getObjects().directoryProperty();
		runIn = getProject().getObjects().directoryProperty();
		variables = getProject().getObjects().mapProperty(String.class, String.class);
		destinationFolder = getProject().getObjects().directoryProperty();
		destinationName = getProject().getObjects().property(String.class);
		additionalPlugins = getProject().getObjects().setProperty(String.class);
    
		GradleNsisPluginExtension extension = getProject().getConvention().getByType(GradleNsisPluginExtension.class);
		configuration.set(extension.getConfiguration());
		extractTo.set(extension.getExtractTo());
		runIn.set(extension.getRunIn());
		variables.set(extension.getVariables());
		additionalPlugins.set(extension.getAdditionalPlugins());
		destinationFolder.set(extension.getDestinationFolder());
		destinationName.set(extension.getDestinationName());
	}
	
	@Input
	@Optional
	public DirectoryProperty getRunIn() {
        return runIn;
    }
	
	@Input
	@Optional
	public SetProperty<String> getAdditionalPlugins() {
        return additionalPlugins;
    }
	
	@Input
	@Optional
	public Property<String> getDestinationName()
    {
    	return destinationName;
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
	@Optional
	public DirectoryProperty getExtractTo() {
        return extractTo;
    }
	@Input
	@Optional
	public DirectoryProperty getDestinationFolder() {
        return destinationFolder;
    }
	@TaskAction
	public void createInstaller() throws GradleScriptException 
	{
		RegularFile installerFile = NsisExecutor.execute(getProject(), extractTo.get(), runIn.get(), variables.get(), additionalPlugins.get(), configuration.get());
		RegularFile destination;
		if(destinationName.isPresent())
			destination = destinationFolder.file(destinationName.get()+".exe").get();
		else
			destination = destinationFolder.file(installerFile.getAsFile().getName()).get();
		getProject().mkdir(destinationFolder);
		try {
			Files.move(installerFile.getAsFile().toPath(), destination.getAsFile().toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
		} catch (IOException e) {
			throw new GradleScriptException("Cannot move installer from "+installerFile.toString()+" to "+destination.toString(), e);
		}
		getProject().getLogger().log(LogLevel.INFO, "Create installer at "+destination.toString());
	}
}
