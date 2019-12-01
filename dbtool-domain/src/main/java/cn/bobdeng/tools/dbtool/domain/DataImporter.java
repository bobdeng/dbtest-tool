package cn.bobdeng.tools.dbtool.domain;

import java.util.Map;
import java.util.Objects;
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
        sqlExecutor.executeSql("truncate table " + tableName,new Object[]{});
        this.reader.getRows(tableName).stream().forEach(row -> this.insertRowToTable(tableName, row));
    }

    private void insertRowToTable(String tableName, Map<String, Object> row) {
        String names = getNames(row);
        String valuesParameter = getParameters(row);
        sqlExecutor.executeSql("insert into " + tableName + " " + names + " values" + valuesParameter,getValues(row));
    }

    private String getParameters(Map<String, Object> row) {
        return row.entrySet().stream().map(stringObjectEntry -> stringObjectEntry.getValue())
                    .filter(Objects::nonNull)
                    .map(obj->"?")
                    .collect(Collectors.joining(",", "(", ")"));
    }

    private Object[] getValues(Map<String, Object> row) {
        return row.entrySet().stream().map(stringObjectEntry -> stringObjectEntry.getValue())
                .filter(Objects::nonNull)
                .toArray();
    }

    private String getNames(Map<String, Object> row) {
        return row.entrySet().stream()
                    .filter(stringObjectEntry -> stringObjectEntry.getValue() != null)
                    .map(stringObjectEntry -> stringObjectEntry.getKey())
                    .map(name -> "`" + name + "`")
                    .collect(Collectors.joining(",", "(", ")"));
    }

}
