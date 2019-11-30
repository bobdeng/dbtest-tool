package cn.bobdeng.tool.dbtool.server;

import cn.bobdeng.tools.dbtool.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;

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

    public void executeSql(String sql) {
        jdbcTemplate.execute(sql);
    }
}