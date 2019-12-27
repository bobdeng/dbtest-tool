package cn.bobdeng.tools.dbtool.domain;

import java.util.List;
import java.util.Map;

public interface SQLExecutor {
    void executeSql(String sql,Object[] values);

    List<TableField> getTableFields(String tableName);

    List<Map<String,String>> getTableRows(String tableName);
}
