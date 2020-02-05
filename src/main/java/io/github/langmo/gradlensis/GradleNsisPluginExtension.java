package io.github.langmo.gradlensis;

import java.io.File;

import org.gradle.api.Project;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFile;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;

public class GradleNsisPluginExtension 
{
	private static final String BINARY_DIRECTORY = "tmp/nsis";
	private final RegularFileProperty nsisConfig;
	private final DirectoryProperty extractTo;

    public GradleNsisPluginExtension(Project project) {
    	nsisConfig = project.getObjects().fileProperty();
    	nsisConfig.convention(project.getLayout().getProjectDirectory().file("config.nsis"));
    	
    	extractTo = project.getObjects().directoryProperty();
    	extractTo.convention(project.getLayout().getBuildDirectory().dir(BINARY_DIRECTORY));
    }

    public DirectoryProperty getExtractTo() {
        return extractTo;
    }
    
    public RegularFileProperty getNsisConfig() {
        return nsisConfig;
    }

}
