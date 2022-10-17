package org.gradle.sample.plugin.toolingapi.standalone;

import java.util.List;

public interface CustomModel {
    boolean hasPlugin(Class type);
    List<String> getAllPlugins();

}
