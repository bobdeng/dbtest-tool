package cn.bobdeng.tools.dbtool.domain;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.*;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class ImportFromExcelTest {


    @Before
    public void setup() {
        Globals.sqlExecutor = mock(SQLExecutor.class);
    }

    @Test
    public void should_do_nothing_import_empty_table() {
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        ImportReader reader = mock(ImportReader.class);
        when(reader.getTables()).thenReturn(Arrays.asList());

        DataImporter dataImporter = new DataImporter(reader);
        dataImporter.importToDB();
        List<String> allValues = sqlCaptor.getAllValues();
        assertThat(allValues.isEmpty(), is(true));
    }

    @Test
    public void should_truncate_table_when_no_data() {
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object[]> valuesCaptor = ArgumentCaptor.forClass(Object[].class);
        ImportReader reader = mock(ImportReader.class);
        when(reader.getTables()).thenReturn(Arrays.asList("table_1"));

        DataImporter dataImporter = new DataImporter(reader);
        dataImporter.importToDB();

        verify(Globals.sqlExecutor).executeSql(sqlCaptor.capture(),valuesCaptor.capture());
        List<String> allValues = sqlCaptor.getAllValues();
        assertThat(allValues.size(), is(1));
        assertThat(allValues.get(0), is("truncate table table_1"));
    }

    @Test
    public void should_truncate_table_and_insert_when_has_data() {
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object[]> valuesCaptor = ArgumentCaptor.forClass(Object[].class);
        ImportReader reader = mock(ImportReader.class);
        when(reader.getTables()).thenReturn(Arrays.asList("table_1"));
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("id", 1);
        row.put("name", "bob");
        when(reader.getRows("table_1")).thenReturn(Arrays.asList(row));

        DataImporter dataImporter = new DataImporter(reader);
        dataImporter.importToDB();

        verify(Globals.sqlExecutor, times(2)).executeSql(sqlCaptor.capture(),valuesCaptor.capture());
        List<String> allValues = sqlCaptor.getAllValues();
        assertThat(allValues.size(), is(2));
        assertThat(allValues.get(0), is("truncate table table_1"));
        assertThat(allValues.get(1), is("insert into table_1 (`id`,`name`) values(?,?)"));
        assertThat(valuesCaptor.getAllValues().get(1)[0],is(1));
    }

    @Test
    public void should_not_insert_when_value_is_null_when_has_data() {
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object[]> valuesCaptor = ArgumentCaptor.forClass(Object[].class);
        ImportReader reader = mock(ImportReader.class);
        when(reader.getTables()).thenReturn(Arrays.asList("table_1"));
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("id", 1);
        row.put("name", "bob");
        row.put("address", null);
        when(reader.getRows("table_1")).thenReturn(Arrays.asList(row));

        DataImporter dataImporter = new DataImporter(reader);
        dataImporter.importToDB();

        verify(Globals.sqlExecutor, times(2)).executeSql(sqlCaptor.capture(),valuesCaptor.capture());
        List<String> allValues = sqlCaptor.getAllValues();
        assertThat(allValues.size(), is(2));
        assertThat(allValues.get(0), is("truncate table table_1"));
        assertThat(allValues.get(1), is("insert into table_1 (`id`,`name`) values(?,?)"));
    }

}
