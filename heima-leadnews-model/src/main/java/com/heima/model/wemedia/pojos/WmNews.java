package com.heima.model.wemedia.pojos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.util.Date;

/**
 * 自媒体图文内容信息表
 */
@Data
@TableName("wm_news")
public class WmNews implements Serializable {

    private static final long serialVersionUID = 1L;

    //主键
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    //自媒体用户ID
    @TableField("user_id")
    private Integer userId;

    //标题
    @TableField("title")
    private String title;

    //图文内容
    @TableField("content")
    private String content;

    //文章布局 0 无图文章    1 单图文章    3 多图文章     -1 自动
    @TableField("type")
    private Integer type;

    //图文频道ID
    @TableField("channel_id")
    private Integer channelId;

    @TableField("labels")
    private String labels;

    //创建时间
    @TableField("created_time")
    private Date createdTime;

    //提交时间
    @TableField("submited_time")
    private Date submitedTime;

    //当前状态 0草稿  1提交(待审核)  2审核失败  3人工审核  4人工审核通过  8审核通过(待发布)  9已发布
    @TableField("status")
    private Integer status;

    //定时发布时间，不定时则为空
    @TableField("publish_time")
    private Date publishTime;

    //拒绝理由
    @TableField("reason")
    private String reason;

    //发布库文章ID
    @TableField("article_id")
    private Long articleId;

    //图片用逗号分隔
    @TableField("images")
    private String images;

    //是否上架 0 未上架    1 上架
    @TableField("enable")
    private Integer enable;

    //状态枚举类
    @Alias("WmNewsStatus")
    public enum Status {
        NORMAL(0),    //0草稿
        SUBMIT( 1),    //1提交(待审核)
        FAIL( 2),      //2审核失败
        ADMIN_AUTH( 3),//3人工审核
        ADMIN_SUCCESS( 4),//4人工审核通过
        SUCCESS( 8),   //8审核通过(待发布)
        PUBLISHED( 9); //9已发布

        int code;

        Status(int code) {
            this.code = code;
        }

        public int getCode() {
            return this.code;
        }
    }
}