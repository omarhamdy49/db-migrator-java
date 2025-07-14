package com.dwit.migrator.infra;

import com.datastax.oss.driver.api.core.CqlSession;

import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;

public class DataSourceProvider {
    public static Connection get(Map<String, Object> config) throws Exception {
        String url = (String) config.get("url");
        String username = (String) config.get("username");
        String password = (String) config.get("password");
        Class.forName((String) config.get("driver"));
        return DriverManager.getConnection(url, username, password);
    }

    public static CqlSession getCassandraSession(Map<String, Object> config) {
        return CqlSession.builder()
                .addContactPoint(new InetSocketAddress((String) config.get("host"), (int) config.get("port")))
                .withAuthCredentials((String) config.get("username"), (String) config.get("password"))
                .withKeyspace((String) config.get("keyspace"))
                .build();
    }
}
