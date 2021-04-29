package site.kexing.service.impl;

import com.alibaba.fastjson.JSON;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import site.kexing.pojo.Book;
import site.kexing.service.ContentService;
import site.kexing.util.HtmlParse;

import javax.swing.text.Highlighter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ContentServiceImpl implements ContentService {
    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient client;
    @Autowired
    private HtmlParse htmlParse;

    /**
     * 解析搜索关键字 批量放到es索引库中
     * @param keyword
     * @return
     * @throws IOException
     */
    @Override
    public boolean bulkAddContent(String keyword) throws IOException {
        List<Book> books = htmlParse.parseDangDang(keyword);
        //批量插入
        BulkRequest bulkRequest = new BulkRequest();
        for (int i = 0; i < books.size(); i++) {
            bulkRequest.add(new IndexRequest("dangdang_book")
                                .id(i+"")
                    .source(JSON.toJSONString(books.get(i)), XContentType.JSON)
            );
        }
        BulkResponse bulkItemResponses = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        return !bulkItemResponses.hasFailures();
    }

    @Override
    public void deleteContent() throws IOException {
        for (int i = 0; i < 60; i++) {
            client.delete(new DeleteRequest("dangdang_book", i + ""), RequestOptions.DEFAULT);
        }
    }

    /**
     * 根据keyword 搜索内容
     * @param keyword
     * @return
     * @throws IOException
     */
    @Override
    public List<Map<String, Object>> search(String keyword) throws IOException {
        long before = System.currentTimeMillis();
        SearchRequest searchRequest = new SearchRequest("dangdang_book");

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchQuery("title",keyword));
        sourceBuilder.timeout(TimeValue.timeValueSeconds(30000));
        sourceBuilder.size(30);
        //设置高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.preTags("<span style='color: red'>");
        highlightBuilder.postTags("</span>");
        sourceBuilder.highlighter(highlightBuilder);

        searchRequest.source(sourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        //获取响应 结果集
        SearchHit[] hits = searchResponse.getHits().getHits();
        ArrayList<Map<String,Object>> res = new ArrayList<>();
        for (SearchHit hit : hits) {
            //获取高亮字段
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField title = highlightFields.get("title");
            //原来的结果
            Map<String, Object> result = hit.getSourceAsMap();
            if(title != null){
                Text[] texts = title.getFragments();
                StringBuilder stringBuilder = new StringBuilder();
                for (Text text : texts) {
                    stringBuilder.append(text);
                }
                result.put("title",stringBuilder);
            }
            res.add(result);
        }
        long after = System.currentTimeMillis();
        System.out.println(after-before);
        return res;
    }

}
