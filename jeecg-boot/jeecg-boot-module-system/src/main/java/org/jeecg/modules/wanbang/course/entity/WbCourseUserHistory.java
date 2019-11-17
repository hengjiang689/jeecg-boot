package org.jeecg.modules.wanbang.course.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 万邦课程用户访问历史记录
 * @Author: jeecg-boot
 * @Date:   2019-11-09
 * @Version: V1.0
 */
@Data
@TableName("wb_course_user_history")
public class WbCourseUserHistory implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ID_WORKER_STR)
	private String id;
	/**创建人*/
	@Excel(name = "创建人", width = 15)
	private String createBy;
	/**创建日期*/
	@Excel(name = "创建日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date createTime;
	/**更新人*/
	@Excel(name = "更新人", width = 15)
	private String updateBy;
	/**更新日期*/
	@Excel(name = "更新日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date updateTime;
	/**所属部门*/
	@Excel(name = "所属部门", width = 15)
	private String sysOrgCode;
	/**外键*/
	private String courseId;
	private java.lang.String title;
	/**展示图片*/
	private java.lang.String image;
	/**课程描述*/
	private java.lang.String description;
	/**所属类别*/
	private java.lang.String category;
	/**类别值*/
	private java.lang.String categoryCode;
	/**视频文件*/
	private java.lang.String videoUrl;
	/**音频文件*/
	private java.lang.String audioUrl;
	/**课程简介*/
	private java.lang.String introduction;
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
	/**是否支付*/
	private java.lang.String isPaid;

}
