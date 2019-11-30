package cn.bobdeng.tools.dbtool.domain;

import java.util.Map;
import java.util.stream.Collectors;

import static cn.bobdeng.tools.dbtool.domain.Globals.sqlExecutor;

public class DataImporter {
    private ImportReader reader;

    public DataImporter(ImportReader reader) {

        this.reader = reader;
    }

    public void importToDB() {
        this.reader.getTables().forEach(this::importTable);
    }

    private void importTable(String tableName) {
        sqlExecutor.executeSql("truncate table "+tableName);
        this.reader.getRows(tableName).stream().forEach(row->this.insertRowToTable(tableName,row));
    }

    private void insertRowToTable(String tableName, Map<String, Object> row) {
        String names = row.entrySet().stream().map(stringObjectEntry -> stringObjectEntry.getKey())
                .map(name->"`"+name+"`")
                .collect(Collectors.joining(",", "(", ")"));
        String values = row.entrySet().stream().map(stringObjectEntry -> stringObjectEntry.getValue())
                .map(Object::toString)
                .map(value->"'"+value+"'")
                .collect(Collectors.joining(",", "(", ")"));
        sqlExecutor.executeSql("insert into "+tableName +" "+names+" values"+values);
    }
}
