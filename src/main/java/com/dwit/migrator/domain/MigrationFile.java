package com.dwit.migrator.domain;

import java.util.List;

public class MigrationFile {
    public final String name;
    public final String tag;
    public final String upSql;
    public final String downSql;
    public final List<String> upSqlList;
    public final List<String> downSqlList;

    public MigrationFile(String name, String tag, String upSql, String downSql, List<String> upSqlList, List<String> downSqlList) {
        this.name = name;
        this.tag = tag;
        this.upSql = upSql;
        this.downSql = downSql;
        this.upSqlList = upSqlList;
        this.downSqlList = downSqlList;
    }
}
