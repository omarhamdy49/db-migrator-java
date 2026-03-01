package com.dwit.migrator.infra.util;

import java.util.Map;

import static com.dwit.migrator.infra.util.FileUtil.loadPropertiesYaml;

public class DBUtil {

    @SuppressWarnings("unchecked")
    public static Map<String, Object> DBConfig(String engine) throws Exception {
        Map<String, Object> config = loadPropertiesYaml();
        Map<String, Object> engines = (Map<String, Object>) config.get("engines");
        if (engines == null) throw new RuntimeException("Missing 'engines' section in configuration");
        Map<String, Object> dbConfig = (Map<String, Object>) engines.get(engine);
        if (dbConfig == null) throw new RuntimeException("No configuration found for engine: " + engine);
        Map<String, Object> datasource = (Map<String, Object>) dbConfig.get("datasource");
        if (datasource == null) throw new RuntimeException("No datasource configuration found for engine: " + engine);
        return datasource;
    }
}
