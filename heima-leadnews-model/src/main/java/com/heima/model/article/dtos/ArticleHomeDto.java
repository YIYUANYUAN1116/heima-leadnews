package com.heima.model.article.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Data
public class ArticleHomeDto {

    // 最大时间
    Date maxBehotTime;
    // 最小时间
    Date minBehotTime;
    //加载类型
    Integer loaddir;
    // 分页size
    Integer size;
    // 频道ID
    String tag;
}