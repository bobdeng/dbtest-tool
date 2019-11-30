package cn.bobdeng.tools.dbtool.domain;

import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
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
        Row firstRow = sheet.getRow(0);
        List<String> columns = IntStream.range(0, firstRow.getLastCellNum())
                .mapToObj(i -> firstRow.getCell(i).getStringCellValue())
                .collect(Collectors.toList());
        int lastRowNum = sheet.getLastRowNum();
        return IntStream.range(1, lastRowNum + 1)
                .mapToObj(i -> {
                    Row row = sheet.getRow(i);

                    Map<String, Object> values = readRow(columns, row);
                    return values;
                })
                .collect(Collectors.toList());
    }

    private Map<String, Object> readRow(List<String> columns, Row row) {


        Map<String, Object> result = new LinkedHashMap();
        for (int i = 0; i < columns.size(); i++) {
            Cell cell = row.getCell(i);
            if (cell.getCellTypeEnum() == CellType.NUMERIC) {
                result.put(columns.get(i), NumberToTextConverter.toText(cell.getNumericCellValue()));
                continue;
            }
            result.put(columns.get(i), cell.getStringCellValue());

        }
        return result;
    }
}
