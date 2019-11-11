package org.jeecg.modules.wanbang.course.entity;

import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import java.util.Date;

/**
 * @Description: 万邦子课程表
 * @Author: jeecg-boot
 * @Date:   2019-11-09
 * @Version: V1.0
 */
@Data
@TableName("wb_class")
public class WbClass implements Serializable {
    private static final long serialVersionUID = 1L;
    
	/**主键*/
	@TableId(type = IdType.ID_WORKER_STR)
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
	/**标题*/
	@Excel(name = "标题", width = 15)
	private java.lang.String title;
	/**是否免费*/
	@Excel(name = "是否免费", width = 15)
	private java.lang.String isFree;
	/**课程id*/
	private java.lang.String courseId;
	/**时长*/
	private java.lang.String duration;
	/**学习人数*/
	private java.lang.String learnNo;
	/**视频文件*/
	@Excel(name = "视频文件", width = 15)
	private java.lang.String videoUrl;
	/**音频文件*/
	@Excel(name = "音频文件", width = 15)
	private java.lang.String audioUrl;
	/**排序*/
	@Excel(name = "排序", width = 15)
	private java.lang.Double sortNo;
}
