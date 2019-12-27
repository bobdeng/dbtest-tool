package cn.bobdeng.tools.dbtool.domain;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TableExportTest {

    public static final String TABLE_USER = "t_user";

    @Before
    public void setup() {
        Globals.sqlExecutor = mock(SQLExecutor.class);
    }

    @Test
    public void test_export_to_excel() throws Exception {
        TableField fieldId = new TableField("int", "id");
        TableField fieldName = new TableField("string", "name");
        when(Globals.sqlExecutor.getTableFields(TABLE_USER)).thenReturn(Arrays.asList(fieldId,fieldName));
        Map<String, String> row = new HashMap<>();
        row.put("id", 123 + "");
        row.put("name", "bobdeng");
        when(Globals.sqlExecutor.getTableRows(TABLE_USER)).thenReturn(Arrays.asList(row));
        TableExcelExporter tableExporter = new TableExcelExporter(Arrays.asList(TABLE_USER));
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        tableExporter.export(outputStream);

        ImportReader reader=new ExcelReader(new ByteArrayInputStream(outputStream.toByteArray()));
        List<String> tables = reader.getTables();
        assertThat(tables.size(),is(1));
        List<Map<String, Object>> rows = reader.getRows("t_user");
        assertThat(rows.size(),is(1));
        assertThat(rows.get(0).get("id"),is(123));
        assertThat(rows.get(0).get("name"),is("bobdeng"));
    }

}
