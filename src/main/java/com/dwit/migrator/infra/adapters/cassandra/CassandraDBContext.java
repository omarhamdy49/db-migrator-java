package com.dwit.migrator.infra.adapters.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.dwit.migrator.infra.util.DBUtil;
import com.dwit.migrator.infra.util.FileUtil;

import java.net.InetSocketAddress;
import java.util.Map;

public class CassandraDBContext {

    public static String cassandraKeyspace() {
        return FileUtil.resolveEnv(cassandraConfig().get("keyspace").toString());
    }

    public static CqlSession getCassandraSession() {
        return CassandraSessionBuilder().build();
//                    .withKeyspace(CqlIdentifier.fromCql(keyspace))
    }

    public static CqlSession cqlKeySpaceSession() {
        return CassandraSessionBuilder()
                .withKeyspace(cassandraKeyspace())
                .build();
    }

    private static CqlSessionBuilder CassandraSessionBuilder() {
        Map<String, Object> ds = cassandraConfig();
        String host = FileUtil.resolveEnv(ds.get("contactPoints").toString());
        int port = Integer.parseInt(FileUtil.resolveEnv(ds.get("port").toString()));
        String datacenter = FileUtil.resolveEnv(ds.get("datacenter").toString());
//            String keyspace = resolveEnv(ds.get("keyspace").toString());
        String username = FileUtil.resolveEnv(ds.get("username").toString());
        String password = FileUtil.resolveEnv(ds.get("password").toString());
        return CqlSession.builder()
                .addContactPoint(new InetSocketAddress(host, port))
                .withLocalDatacenter(datacenter)
                .withAuthCredentials(username, password);
//                    .withKeyspace(CqlIdentifier.fromCql(keyspace))
    }

    public static Map<String, Object> cassandraConfig() {
        try {
            return DBUtil.DBConfig("cassandra");
        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to build Cassandra session: " + e.getMessage(), e);
        }
    }

}
