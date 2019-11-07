package org.jeecg.modules.wanbang.course.entity;

import java.io.Serializable;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @Description: 万邦课程表
 * @Author: jeecg-boot
 * @Date:   2019-11-07
 * @Version: V1.0
 */
@Data
@TableName("wb_course")
public class WbCourse implements Serializable {
    private static final long serialVersionUID = 1L;
    
	/**主键*/
	@TableId(type = IdType.ID_WORKER_STR)
    private java.lang.String id;
	/**创建人*/
    private java.lang.String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private java.util.Date createTime;
	/**更新人*/
    private java.lang.String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private java.util.Date updateTime;
	/**所属部门*/
    private java.lang.String sysOrgCode;
	/**课程标题*/
    private java.lang.String title;
	/**所属类别*/
    private java.lang.String category;
	/**展示图片*/
    private java.lang.String image;
	/**视频文件*/
    private java.lang.String videoUrl;
	/**音频文件*/
    private java.lang.String audioUrl;
	/**课程简介*/
    private java.lang.String introduction;
	/**课程描述*/
    private java.lang.String description;
	/**发布日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private java.util.Date publishDate;
	/**讲师姓名*/
    private java.lang.String teacherName;
	/**价格*/
    private java.lang.Double price;
	/**时长*/
    private java.lang.String duration;
	/**总课时*/
    private java.lang.Integer classNum;
	/**是否免费*/
    private java.lang.String isFree;
	/**是否置顶*/
    private java.lang.String isTop;
	/**学习人数*/
    private java.lang.Integer learnNum;
	/**排序*/
    private java.lang.Double sortNo;
}
