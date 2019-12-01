# add dependency
```
<dependency>
            <groupId>cn.bobdeng.test</groupId>
            <artifactId>dbtool-server</artifactId>
            <version>1.0-SNAPSHOT</version>
</dependency>
```
        
# config
```
            @Bean
    @Profile({"dev","test"})
    public DBToolController dbToolController() {
        return new DBToolController();
    }
```
# use
curl -F 'file=@test_data/abc.xlsx' http://localhost:8080/dbtool/import

# excel exapmle

dbtoo-domain/test/resource/*.xlsx
