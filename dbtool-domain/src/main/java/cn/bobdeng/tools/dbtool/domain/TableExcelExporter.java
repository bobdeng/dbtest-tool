package cn.bobdeng.tools.dbtool.domain;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import static cn.bobdeng.tools.dbtool.domain.Globals.sqlExecutor;

public class TableExcelExporter implements TableExporter {
    private Workbook workbook;
    private List<String> tableNames;

    public TableExcelExporter(List<String> tableNames) {

        this.tableNames = tableNames;
        try {
            workbook = WorkbookFactory.create(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void export(OutputStream outputStream) throws IOException {
        this.tableNames.forEach(this::readTable);
        workbook.write(outputStream);
    }

    private void readTable(String tableName) {
        Sheet sheet = workbook.createSheet(tableName);
        List<TableField> tableFields = createTableFields(tableName, sheet);
        insertTableValues(tableName, sheet, tableFields);
    }

    private void insertTableValues(String tableName, Sheet sheet, List<TableField> tableFields) {
        List<Map<String, String>> values = sqlExecutor.getTableRows(tableName);
        for (int i = 0; i < values.size(); i++) {
            insertRowValues(sheet, tableFields, values, i);
        }
    }

    private void insertRowValues(Sheet sheet, List<TableField> tableFields, List<Map<String, String>> values, int rowIndex) {
        Map<String, String> row = values.get(rowIndex);
        Row valueRow = sheet.createRow(rowIndex + 2);
        for (int i = 0; i < tableFields.size(); i++) {
            valueRow.createCell(i);
            valueRow.getCell(i).setCellValue(row.get(tableFields.get(i).getName()));
        }
    }

    private List<TableField> createTableFields(String tableName, Sheet sheet) {
        Row rowTypes = sheet.createRow(0);
        Row rowNames = sheet.createRow(1);
        List<TableField> tableFields = sqlExecutor.getTableFields(tableName);
        for (int i = 0; i < tableFields.size(); i++) {
            TableField tableField = tableFields.get(i);
            rowTypes.createCell(i);
            rowNames.createCell(i);
            rowTypes.getCell(i).setCellValue(tableField.getType());
            rowNames.getCell(i).setCellValue(tableField.getName());
        }
        return tableFields;
    }
}
