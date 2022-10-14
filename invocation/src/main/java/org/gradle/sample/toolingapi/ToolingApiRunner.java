package org.gradle.sample.toolingapi;

import org.gradle.api.plugins.GroovyPlugin;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;
import org.gradle.tooling.ModelBuilder;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.sample.plugins.toolingapi.custom.CustomModel;

import java.io.File;

public class ToolingApiRunner {
    public static void main(String[] args) {
        GradleConnector connector = GradleConnector.newConnector();
        connector.forProjectDirectory(new File("./sample"));

        try (ProjectConnection connection = connector.connect()) {
            ModelBuilder<CustomModel> customModelBuilder = connection.model(CustomModel.class);
            customModelBuilder.withArguments("--init-script", "init.gradle"); // Can use if applying plugin on the fly for a project
            CustomModel model = customModelBuilder.get();
            System.out.println("Project applies JavaPlugin? " + model.hasPlugin(JavaPlugin.class));
            System.out.println("Project applies Groovy? " + model.hasPlugin(GroovyPlugin.class));
            System.out.println("##########################");

        }
    }
}