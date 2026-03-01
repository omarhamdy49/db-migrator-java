package com.dwit.migrator.infra;

import com.datastax.oss.driver.api.core.CqlSession;

import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;

public class DataSourceProvider {

    @SuppressWarnings("unchecked")
    public static Connection get(Map<String, Object> config) throws Exception {
        String url = (String) config.get("url");
        String username = (String) config.get("username");
        String password = (String) config.get("password");
        return DriverManager.getConnection(url, username, password);
    }

    @SuppressWarnings("unchecked")
    public static CqlSession getCassandraSession(Map<String, Object> config) {
        String host = (String) config.get("host");
        int port = (int) config.get("port");
        String username = (String) config.get("username");
        String password = (String) config.get("password");
        String keyspace = (String) config.get("keyspace");

        var builder = CqlSession.builder()
                .addContactPoint(new InetSocketAddress(host, port))
                .withKeyspace(keyspace);

        if (username != null && !username.isEmpty()) {
            builder.withAuthCredentials(username, password);
        }

        return builder.build();
    }
}
