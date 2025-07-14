package com.dwit.migrator.domain;

public interface IMigrationService {
    void checkConnection() throws Exception;

    void migrateAll() throws Exception;

    void rollbackByTag(String tag) throws Exception;

    void rollbackByFileName(String fileName) throws Exception;
}
