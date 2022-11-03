package com.heima.minio;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@SpringBootTest
class MinioDemoApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void test1(){
        FileInputStream fileInputStream = null ;

        try {
            fileInputStream = new FileInputStream("G:\\JavaFile\\tree-leadnews-master\\FreemarkerFile\\list.html");
            //1.创建minio链接客户端
            MinioClient minioClient = MinioClient.builder()
                    .credentials("minio", "minio123")
                    .endpoint("http://192.168.44.128:9000")
                    .build();

            //2.上传
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .object("list.html")//文件名
                    .contentType("text/html")//文件类型
                    .bucket("leadnews")//桶名词  与minio创建的名词一致
                    .stream(fileInputStream, fileInputStream.available(), -1) //文件流
                    .build();

            minioClient.putObject(putObjectArgs);

            System.out.println("http://192.168.200.128:9001/leadnews/list.html");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
