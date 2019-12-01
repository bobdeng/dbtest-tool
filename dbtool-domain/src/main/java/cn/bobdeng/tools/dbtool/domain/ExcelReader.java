package cn.bobdeng.tools.dbtool.domain;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.NumberToTextConverter;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ExcelReader implements ImportReader {
    private final Workbook workbook;

    public ExcelReader(InputStream inputStream) throws Exception {
        workbook = WorkbookFactory.create(inputStream);
    }

    @Override
    public List<String> getTables() {
        return IntStream.range(0, workbook.getNumberOfSheets())
                .mapToObj(i -> workbook.getSheetAt(i).getSheetName())
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getRows(String tableName) {
        Sheet sheet = workbook.getSheet(tableName);
        List<TableField> columns = getTableFields(sheet);
        return IntStream.range(2, sheet.getLastRowNum() + 1)
                .mapToObj(i -> readRowValue(sheet, columns, i))
                .collect(Collectors.toList());
    }

    private Map<String, Object> readRowValue(Sheet sheet, List<TableField> columns, int rowIndex) {
        Row row = sheet.getRow(rowIndex);
        Map<String, Object> values = readRow(columns, row);
        return values;
    }

    private List<TableField> getTableFields(Sheet sheet) {
        Row typeRow = sheet.getRow(0);
        Row columnRow = sheet.getRow(1);
        return IntStream.range(0, typeRow.getLastCellNum())
                .mapToObj(i -> new TableField(typeRow.getCell(i).getStringCellValue(), columnRow.getCell(i).getStringCellValue()))
                .collect(Collectors.toList());
    }

    private Map<String, Object> readRow(List<TableField> columns, Row row) {
        Map<String, Object> result = new LinkedHashMap();
        for (int i = 0; i < columns.size(); i++) {
            Cell cell = row.getCell(i);
            TableField column = columns.get(i);
            readCellValueToRowMap(result, cell, column);
        }
        return result;
    }

    private void readCellValueToRowMap(Map<String, Object> result, Cell cell, TableField column) {
        String cellValue = getCellValueString(cell);
        if (column.isInteger()) {
            result.put(column.getName(), Integer.parseInt(cellValue));
            return;
        }
        result.put(column.getName(), cellValue);
    }

    private String getCellValueString(Cell cell) {
        if(cell == null){
            return null;
        }
        //for poi 3
        if (cell.getCellTypeEnum() == CellType.NUMERIC) {
            return NumberToTextConverter.toText(cell.getNumericCellValue());
        }
        return cell.getStringCellValue();
    }
}
