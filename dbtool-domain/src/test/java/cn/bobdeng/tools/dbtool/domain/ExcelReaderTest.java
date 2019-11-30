package cn.bobdeng.tools.dbtool.domain;

import com.google.common.io.Resources;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class ExcelReaderTest {
    @Test
    public void test_t_user()throws Exception{
        byte[] bytes = Resources.toByteArray(Resources.getResource("t_user.xlsx"));
        ImportReader reader=new ExcelReader(new ByteArrayInputStream(bytes));
        List<String> tables = reader.getTables();
        assertThat(tables.size(),is(1));
    }
}
