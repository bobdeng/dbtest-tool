package cn.bobdeng.tools.dbtool.domain;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public interface TableExporter {
    void export(OutputStream outputStream) throws IOException;
}
