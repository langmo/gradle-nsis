package com.github.langmo.gradlensis;

import org.gradle.testfixtures.ProjectBuilder;
import org.gradle.api.Project;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * A simple unit test for the 'io.github.langmo.gradlensis' plugin.
 */
public class GradleNsisPluginTest {
    @Test public void pluginRegistersATask() {
        // Create a test project and apply the plugin
        Project project = ProjectBuilder.builder().build();
        project.getPlugins().apply("com.github.langmo.gradlensis");

        // Verify the result
        assertNotNull(project.getTasks().findByName("createInstaller"));
    }
}
