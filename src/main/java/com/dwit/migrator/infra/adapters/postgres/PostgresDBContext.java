package com.dwit.migrator.infra.adapters.postgres;

import com.dwit.migrator.infra.util.DBUtil;
import com.dwit.migrator.infra.util.FileUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;
import java.util.Properties;

public class PostgresDBContext {

    public static Connection getConnection() {
        try {
            Map<String, Object> ds = DBUtil.DBConfig("postgres");
            String host = FileUtil.resolveEnv(ds.get("host").toString());
            String port = FileUtil.resolveEnv(ds.get("port").toString());
            String name = FileUtil.resolveEnv(ds.get("name").toString());
            String user = FileUtil.resolveEnv(ds.get("username").toString());
            String pass = FileUtil.resolveEnv(ds.get("password").toString());

            // Get schema from config, default to "public" if not specified
            String schema = ds.get("schema") != null
                ? FileUtil.resolveEnv(ds.get("schema").toString())
                : "public";

            // Build JDBC URL
            String url = "jdbc:postgresql://" + host + ":" + port + "/" + name;

            // Use Properties for connection parameters
            Properties props = new Properties();
            props.setProperty("user", user);
            props.setProperty("password", pass);

            // If schema is not public, set search_path to include both schemas
            // We use options instead of currentSchema because currentSchema
            // overrides search_path and excludes public, which breaks uuid-ossp
            if (schema != null && !schema.isEmpty() && !schema.equals("public")) {
                // Set search path to target schema first (for new objects), then public (for extensions)
                props.setProperty("options", "-c search_path=" + schema + ",public");
            }

            return DriverManager.getConnection(url, props);
        } catch (Exception e) {
            throw new RuntimeException("❌ Postgres DB Connection failed: " + e.getMessage(), e);
        }
    }
}
