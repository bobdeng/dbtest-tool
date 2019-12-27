package cn.bobdeng.tools.dbtool.domain;

import java.io.IOException;
import java.io.OutputStream;

public interface TableExporter {
    void export(OutputStream outputStream) throws IOException;
}
