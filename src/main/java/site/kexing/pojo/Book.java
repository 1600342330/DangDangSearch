package site.kexing.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    //书籍图片地址
    private String img;
    //书名
    private String title;
    //书价
    private String price;
    //作者 日期 出版社
    private String author;
    //图书详情
    private String detail;
}