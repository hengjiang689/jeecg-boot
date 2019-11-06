package org.jeecg.modules.wanbang.course.service;

import org.jeecg.modules.wanbang.course.entity.WbCourseComment;
import org.jeecg.modules.wanbang.course.entity.WbCourse;
import com.baomidou.mybatisplus.extension.service.IService;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @Description: 万邦课程表
 * @Author: jeecg-boot
 * @Date:   2019-11-05
 * @Version: V1.0
 */
public interface IWbCourseService extends IService<WbCourse> {

	/**
	 * 添加一对多
	 * 
	 */
	public void saveMain(WbCourse wbCourse,List<WbCourseComment> wbCourseCommentList) ;
	
	/**
	 * 修改一对多
	 * 
	 */
	public void updateMain(WbCourse wbCourse,List<WbCourseComment> wbCourseCommentList);
	
	/**
	 * 删除一对多
	 */
	public void delMain (String id);
	
	/**
	 * 批量删除一对多
	 */
	public void delBatchMain (Collection<? extends Serializable> idList);
	
}
