package cn.bobdeng.tool.dbtool.server;

import cn.bobdeng.tools.dbtool.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;

@RestController
public class DBToolController implements SQLExecutor {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void init() {
        Globals.sqlExecutor = this;
    }

    @PostMapping("/dbtool/import")
    public String importFile(@RequestParam("file") MultipartFile file) throws Exception {
        ImportReader reader = new ExcelReader(file.getInputStream());
        DataImporter dataImporter = new DataImporter(reader);
        dataImporter.importToDB();
        return "ok";
    }

    @GetMapping("/dbtool/export")
    public void export(@RequestParam("table") String[] tableNames, HttpServletResponse response) throws Exception {
        TableExporter tableExporter = new TableExcelExporter(Arrays.asList(tableNames));
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=\"export.xls\"");
        tableExporter.export(response.getOutputStream());
    }

    public void executeSql(String sql, Object[] values) {
        jdbcTemplate.update(sql, values);
    }

    @Override
    public List<TableField> getTableFields(String tableName) {
        List<TableField> result = new ArrayList<>();
        jdbcTemplate.query("select * from " + tableName, (ResultSetExtractor<Integer>) resultSet -> {
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnCount = rsmd.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                TableField column = new TableField(rsmd.getColumnName(i), getColumnTypeName(rsmd, i));
                result.add(column);
            }
            return columnCount;
        });
        return result;
    }

    private String getColumnTypeName(ResultSetMetaData rsmd, int i) throws SQLException {
        return "string";
    }

    @Override
    public List<Map<String, String>> getTableRows(String tableName) {
        return jdbcTemplate.query("select * from " + tableName, (RowMapper<Map<String, String>>) (resultSet, i) -> {
            Map<String, String> values = new HashMap<>();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int j = 0; j < columnCount; j++) {
                Object object = resultSet.getObject(j+1);
                if (object != null) {
                    values.put(metaData.getColumnName(j+1), object.toString());
                }
            }
            return values;
        });
    }
}
