package cn.bobdeng.tools.dbtool.domain;


import lombok.Getter;

public class TableField {
    @Getter
    private String type;
    @Getter
    private String name;

    public TableField(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public boolean isInteger() {
        return "int".endsWith(type);
    }
}
