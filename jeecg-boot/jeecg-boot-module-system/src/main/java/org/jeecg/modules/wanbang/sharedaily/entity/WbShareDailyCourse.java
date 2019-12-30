package org.jeecg.modules.wanbang.sharedaily.entity;

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
 * @Description: 每日必发
 * @Author: jeecg-boot
 * @Date:   2019-12-21
 * @Version: V1.0
 */
@Data
@TableName("wb_share_daily")
public class WbShareDailyCourse implements Serializable {
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
	/**文案*/
	@Excel(name = "文案", width = 15)
    private String content;
	/**课程ID*/
	@Excel(name = "课程ID", width = 15)
    private String courseId;
	/**课程标题*/
	private String courseTitle;
	/**课程封面*/
	private String courseImage;
	/**类型*/
	@Excel(name = "类型", width = 15)
    private Integer type;
	/**点赞*/
	@Excel(name = "点赞", width = 15)
    private Integer likeNum;
	/**图片*/
	@Excel(name = "图片", width = 15)
    private String images;
}
