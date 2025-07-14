package com.dwit.migrator.infra.adapters.mysql;

import com.dwit.migrator.infra.util.DBUtil;
import com.dwit.migrator.infra.util.FileUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;

public class MysqlDBContext {

    public static Connection getConnection() {
        try {
            Map<String, Object> ds = DBUtil.DBConfig("mysql");
            String host = FileUtil.resolveEnv(ds.get("host").toString());
            String port = FileUtil.resolveEnv(ds.get("port").toString());
            String name = FileUtil.resolveEnv(ds.get("name").toString());
            String user = FileUtil.resolveEnv(ds.get("username").toString());
            String pass = FileUtil.resolveEnv(ds.get("password").toString());

            String url;
            Class.forName("com.mysql.cj.jdbc.Driver");
            url = "jdbc:mysql://" + host + ":" + port + "/" + name + "?useSSL=false&allowPublicKeyRetrieval=true";
            return DriverManager.getConnection(url, user, pass);
        } catch (Exception e) {
            throw new RuntimeException("❌ Mysql DB Connection failed: " + e.getMessage(), e);
        }
    }
}
