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
        Row typeRow = sheet.getRow(0);
        Row columnRow = sheet.getRow(1);
        List<TableField> columns = IntStream.range(0, typeRow.getLastCellNum())
                .mapToObj(i -> new TableField(typeRow.getCell(i).getStringCellValue(), columnRow.getCell(i).getStringCellValue()))
                .collect(Collectors.toList());
        int lastRowNum = sheet.getLastRowNum();
        return IntStream.range(2, lastRowNum + 1)
                .mapToObj(i -> {
                    Row row = sheet.getRow(i);

                    Map<String, Object> values = readRow(columns, row);
                    return values;
                })
                .collect(Collectors.toList());
    }

    private Map<String, Object> readRow(List<TableField> columns, Row row) {

        Map<String, Object> result = new LinkedHashMap();
        for (int i = 0; i < columns.size(); i++) {
            Cell cell = row.getCell(i);
            String cellValue = getCellValueString(cell);
            if (columns.get(i).isInteger()) {
                result.put(columns.get(i).getName(), Integer.parseInt(cellValue));
                continue;
            }
            result.put(columns.get(i).getName(), cellValue);

        }
        return result;
    }

    private String getCellValueString(Cell cell) {
        String valueString = "";
        if (cell.getCellTypeEnum() == CellType.NUMERIC) {
            return NumberToTextConverter.toText(cell.getNumericCellValue());
        }
        valueString = cell.getStringCellValue();
        return valueString;
    }
}
