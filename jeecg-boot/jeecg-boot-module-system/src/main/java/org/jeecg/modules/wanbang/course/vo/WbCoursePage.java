package org.jeecg.modules.wanbang.course.vo;

import java.util.List;
import org.jeecg.modules.wanbang.course.entity.WbCourse;
import org.jeecg.modules.wanbang.course.entity.WbCourseComment;
import org.jeecg.modules.wanbang.course.entity.WbClass;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelEntity;
import org.jeecgframework.poi.excel.annotation.ExcelCollection;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;

/**
 * @Description: 万邦课程表
 * @Author: jeecg-boot
 * @Date:   2019-11-07
 * @Version: V1.0
 */
@Data
public class WbCoursePage {
	
	/**主键*/
	private java.lang.String id;
	/**创建人*/
	@Excel(name = "创建人", width = 15)
	private java.lang.String createBy;
	/**创建日期*/
	@Excel(name = "创建日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private java.util.Date createTime;
	/**更新人*/
	@Excel(name = "更新人", width = 15)
	private java.lang.String updateBy;
	/**更新日期*/
	@Excel(name = "更新日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private java.util.Date updateTime;
	/**所属部门*/
	@Excel(name = "所属部门", width = 15)
	private java.lang.String sysOrgCode;
	/**课程标题*/
	@Excel(name = "课程标题", width = 15)
	private java.lang.String title;
	/**所属类别*/
	@Excel(name = "所属类别", width = 15)
	private java.lang.String category;
	/**展示图片*/
	@Excel(name = "展示图片", width = 15)
	private java.lang.String image;
	/**视频文件*/
	@Excel(name = "视频文件", width = 15)
	private java.lang.String videoUrl;
	/**音频文件*/
	@Excel(name = "音频文件", width = 15)
	private java.lang.String audioUrl;
	/**课程简介*/
	@Excel(name = "课程简介", width = 15)
	private java.lang.String introduction;
	/**课程描述*/
	@Excel(name = "课程描述", width = 15)
	private java.lang.String description;
	/**发布日期*/
	@Excel(name = "发布日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
	private java.util.Date publishDate;
	/**讲师姓名*/
	@Excel(name = "讲师姓名", width = 15)
	private java.lang.String teacherName;
	/**价格*/
	@Excel(name = "价格", width = 15)
	private java.lang.Double price;
	/**时长*/
	@Excel(name = "时长", width = 15)
	private java.lang.String duration;
	/**总课时*/
	@Excel(name = "总课时", width = 15)
	private java.lang.Integer classNum;
	/**是否免费*/
	@Excel(name = "是否免费", width = 15)
	private java.lang.String isFree;
	/**是否置顶*/
	@Excel(name = "是否置顶", width = 15)
	private java.lang.String isTop;
	/**学习人数*/
	@Excel(name = "学习人数", width = 15)
	private java.lang.Integer learnNum;
	/**排序*/
	@Excel(name = "排序", width = 15)
	private java.lang.Double sortNo;
	
	@ExcelCollection(name="万邦课程评论")
	private List<WbCourseComment> wbCourseCommentList;	
	@ExcelCollection(name="万邦子课程表")
	private List<WbClass> wbClassList;	
	
}
