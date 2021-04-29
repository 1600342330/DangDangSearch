package site.kexing.service;


import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ContentService {
    boolean bulkAddContent(String keyword) throws IOException;
    void deleteContent() throws IOException;
    List<Map<String,Object>> search(String keyword) throws IOException;
}
