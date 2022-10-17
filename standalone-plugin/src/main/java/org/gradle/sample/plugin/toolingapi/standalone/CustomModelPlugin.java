package org.gradle.sample.plugin.toolingapi.standalone;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.GroovyPlugin;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ModelBuilder;
import org.gradle.tooling.ProjectConnection;
import org.gradle.tooling.provider.model.ToolingModelBuilder;
import org.gradle.tooling.provider.model.ToolingModelBuilderRegistry;

import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CustomModelPlugin implements Plugin<Project> {

    private final ToolingModelBuilderRegistry registry;

    @Inject
    public CustomModelPlugin(ToolingModelBuilderRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void apply(Project project) {
        // tried beforeEvaluate too
//        project.beforeEvaluate(s -> {
//            System.out.println("Before evaluate");
//                registry.register(new CustomToolingModelBuilder());});
        registry.register(new CustomToolingModelBuilder());

        //Adding the task afterEvaluate
        project.afterEvaluate(task1 ->
                project.task("getPlugins")
                        .doLast(task -> {
                            myMethod(project);
                        })
        );

    }

    private void myMethod(Project project) {
        GradleConnector connector = GradleConnector.newConnector();
        connector.forProjectDirectory(new File("."));
        try (ProjectConnection connection = connector.connect()) {
            ModelBuilder<CustomModel> customModelBuilder = connection.model(CustomModel.class);
            /*
             * TODO this is where the model is not found
             */
            CustomModel model = customModelBuilder.get();

            System.out.println("Project applies JavaPlugin? " + model.hasPlugin(JavaPlugin.class));
            System.out.println("Project applies Groovy? " + model.hasPlugin(GroovyPlugin.class));
            System.out.println("\n>>>> Listing all plugins applied to the project");
            System.out.println("-------------------------------------------------");
            model.getAllPlugins().forEach(System.out::println);
            System.out.println("-------------------------------------------------");

        }
    }

    private static class CustomToolingModelBuilder implements ToolingModelBuilder {
        @Override
        public boolean canBuild(String modelName) {
            return modelName.equals(CustomModel.class.getName());
        }

        @Override
        public Object buildAll(String modelName, Project project) {
            List<String> pluginClassNames = new ArrayList<String>();

            for (Plugin plugin : project.getPlugins()) {
                pluginClassNames.add(plugin.getClass().getName());
            }

            return new DefaultModel(pluginClassNames);
        }
    }
}
