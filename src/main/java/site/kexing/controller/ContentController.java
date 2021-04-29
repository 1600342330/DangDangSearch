package site.kexing.controller;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import site.kexing.service.ContentService;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
public class ContentController {
    @Autowired
    private ContentService contentService;

    @RequestMapping("/parse/{keyword}")
    @ResponseBody
    public boolean parseKeyword(@PathVariable String keyword) throws IOException {
        boolean res = contentService.bulkAddContent(keyword);
        return res;
    }

    @RequestMapping("/delete")
    @ResponseBody
    public void deleteContent() throws IOException {
        contentService.deleteContent();
    }

    @RequestMapping("/search/{keyword}")
    public String search(@PathVariable String keyword, Model model) throws IOException, InterruptedException {
        System.out.println("search");
        //根据keyword先爬取数据
        contentService.bulkAddContent(keyword);
        Thread.sleep(700);
        //搜索
        List<Map<String, Object>> lis = contentService.search(keyword);
        model.addAttribute("lis",lis);
        System.out.println(lis);
        return "index";
    }
}

