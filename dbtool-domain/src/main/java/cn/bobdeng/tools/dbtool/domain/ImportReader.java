package cn.bobdeng.tools.dbtool.domain;

import java.util.List;
import java.util.Map;

public interface ImportReader {
    List<String> getTables();

    List<Map<String,Object>> getRows(String tableName);
}
