package com.dwit.migrator.infra.util;

import java.util.Map;

import static com.dwit.migrator.infra.util.FileUtil.loadPropertiesYaml;

public class DBUtil {

    public static Map<String, Object> DBConfig(String engine) throws Exception {
        Map<String, Object> config = loadPropertiesYaml();
        Map<String, Object> dbConfig = (Map<String, Object>) ((Map<String, Object>) config.get("engines")).get(engine);
        return (Map<String, Object>) dbConfig.get("datasource");
    }
}
