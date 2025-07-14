package com.dwit.migrator.infra.adapters.postgres;

import com.dwit.migrator.infra.util.DBUtil;
import com.dwit.migrator.infra.util.FileUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;

public class PostgresDBContext {

    public static Connection getConnection() {
        try {
            Map<String, Object> ds = DBUtil.DBConfig("postgres");
            String host = FileUtil.resolveEnv(ds.get("host").toString());
            String port = FileUtil.resolveEnv(ds.get("port").toString());
            String name = FileUtil.resolveEnv(ds.get("name").toString());
            String user = FileUtil.resolveEnv(ds.get("username").toString());
            String pass = FileUtil.resolveEnv(ds.get("password").toString());

            String url;
            Class.forName("org.postgresql.Driver");
            url = "jdbc:postgresql://" + host + ":" + port + "/" + name;
            return DriverManager.getConnection(url, user, pass);
        } catch (Exception e) {
            throw new RuntimeException("❌ Postgres DB Connection failed: " + e.getMessage(), e);
        }
    }
}
