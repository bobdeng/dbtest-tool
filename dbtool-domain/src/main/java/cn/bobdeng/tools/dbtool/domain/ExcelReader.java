package cn.bobdeng.tools.dbtool.domain;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ExcelReader implements ImportReader {
    private final Workbook workbook;

    public ExcelReader(InputStream inputStream) throws IOException {
        workbook= WorkbookFactory.create(inputStream);
    }

    @Override
    public List<String> getTables() {
        return IntStream.range(0,workbook.getNumberOfSheets())
                .mapToObj(i->workbook.getSheetAt(i).getSheetName())
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getRows(String tableName) {
        return null;
    }
}
