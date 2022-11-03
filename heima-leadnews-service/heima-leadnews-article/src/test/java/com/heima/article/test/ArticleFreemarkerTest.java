package com.heima.article.test;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.article.ArticleApplication;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.common.autoconfig.template.MinIOTemplate;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleContent;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ArticleApplication.class)
public class ArticleFreemarkerTest {

    @Autowired
    private Configuration configuration;
    @Autowired
    private MinIOTemplate minIOTemplate;
    @Autowired
    private ApArticleMapper apArticleMapper;
    @Autowired
    private ApArticleContentMapper apArticleContentMapper;

    @Test
    public void test() throws Exception {
        //获取文本内容
        ApArticleContent content = apArticleContentMapper.selectOne(Wrappers.lambdaQuery(ApArticleContent.class)
                .eq(ApArticleContent::getArticleId, 1302862387124125698L));

        if (content != null && StringUtils.isNotBlank(content.getContent())) {
            //生成html文件
            Map<String, Object> params = new HashMap<>();
            params.put("content", JSONArray.parse(content.getContent()));

            StringWriter out = new StringWriter();
            Template template = configuration.getTemplate("article.ftl");
            template.process(params, out);

            //上传MinIO
            InputStream inputStream = new ByteArrayInputStream(out.toString().getBytes());

            String url = minIOTemplate.uploadHtmlFile("",
                    content.getArticleId() + ".html", inputStream);

            System.out.println(url);

            //更新article
            ApArticle article = new ApArticle();
            article.setId(content.getArticleId());
            article.setStaticUrl(url);
            apArticleMapper.updateById(article);
        }
    }
}