package site.kexing.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import site.kexing.pojo.Book;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Component
public class HtmlParse {
    public List<Book> parseDangDang(String bookName) throws IOException {
        String url = "http://search.dangdang.com/?key="+bookName+"&act=input&page_index="+"1";
        Document document = Jsoup.parse(new URL(url),300000);
        Element element = document.getElementsByClass("bigimg").get(0);
        Elements lis = element.getElementsByTag("li");

        List<Book> res = new ArrayList<>();
        for(Element el : lis){
            //获取图片url
            String img = el.getElementsByTag("img").attr("data-original");
            //获得书名
            String title = el.getElementsByClass("name").text();
            //获取价格
            String price = el.getElementsByClass("search_now_price").text();
            //获取图书详情
            String detail = el.getElementsByClass("detail").text();
            String author = el.getElementsByClass("search_book_author").text();
            Book book = new Book();

            book.setImg(img);
            book.setTitle(title);
            book.setPrice(price);
            book.setAuthor(author);
            book.setDetail(detail);
            res.add(book);
        }
        return res;
    }
}
