package io.github.langmo.gradlensis;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

public class GradleNsisTask extends DefaultTask 
{
	private final RegularFileProperty nsisConfig;
	private final DirectoryProperty extractTo;

	public GradleNsisTask() {
		nsisConfig = getProject().getObjects().fileProperty();
		extractTo = getProject().getObjects().directoryProperty();
    }
	
	@Input
    public RegularFileProperty getNsisConfig() {
        return nsisConfig;
    }
	@Input
    public DirectoryProperty getExtractTo() {
        return extractTo;
    }
	
	@TaskAction
	public void createInstaller() throws Exception
	{
		System.out.println("Extracting NSIS to: " + extractTo.get());
		NsisExtractor.extract(getProject(), extractTo.get());
		System.out.println("NSIS configuration file: " + nsisConfig.get());
	}
}
