package com.github.langmo.gradlensis;

import java.util.HashMap;

import org.gradle.api.Project;
import org.gradle.api.Transformer;
import org.gradle.api.file.Directory;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFile;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.MapProperty;

public class GradleNsisPluginExtension 
{
	private static final String BINARY_DIRECTORY = "tmp/nsis";
	private final RegularFileProperty configuration;
	private final DirectoryProperty extractTo;
	private final DirectoryProperty runIn;
	private final MapProperty<String, String> variables;
    public GradleNsisPluginExtension(final Project project) {
    	configuration = project.getObjects().fileProperty();
    	configuration.convention(project.getLayout().getProjectDirectory().file("config.nsis"));
    	
    	runIn = project.getObjects().directoryProperty();
    	runIn.convention(configuration.map(new Transformer<Directory, RegularFile>() 
    	{
			@Override
			public Directory transform(RegularFile org) 
			{
				String relative = project.getProjectDir().toURI().relativize(org.getAsFile().getParentFile().toURI()).getPath();
				if(relative != null && relative.length()>0)
					return project.getLayout().getProjectDirectory().dir(relative);
				else
					return project.getLayout().getProjectDirectory();
			}
		}));
    	
    	extractTo = project.getObjects().directoryProperty();
    	extractTo.convention(project.getLayout().getBuildDirectory().dir(BINARY_DIRECTORY));
    	
    	variables = project.getObjects().mapProperty(String.class, String.class);
    	variables.convention(new HashMap<String, String>());
    }

    public DirectoryProperty getExtractTo() {
        return extractTo;
    }
    public DirectoryProperty getRunIn() {
        return runIn;
    }
    public MapProperty<String, String> getVariables() {
        return variables;
    }
    
    public RegularFileProperty getConfiguration() {
        return configuration;
    }

}
