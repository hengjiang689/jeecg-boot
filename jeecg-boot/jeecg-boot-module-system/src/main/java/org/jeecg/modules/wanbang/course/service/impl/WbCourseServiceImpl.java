package org.jeecg.modules.wanbang.course.service.impl;

import org.jeecg.modules.wanbang.course.entity.WbCourse;
import org.jeecg.modules.wanbang.course.entity.WbCourseComment;
import org.jeecg.modules.wanbang.course.mapper.WbCourseCommentMapper;
import org.jeecg.modules.wanbang.course.mapper.WbCourseMapper;
import org.jeecg.modules.wanbang.course.service.IWbCourseService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.io.Serializable;
import java.util.List;
import java.util.Collection;

/**
 * @Description: 万邦课程表
 * @Author: jeecg-boot
 * @Date:   2019-10-28
 * @Version: V1.0
 */
@Service
public class WbCourseServiceImpl extends ServiceImpl<WbCourseMapper, WbCourse> implements IWbCourseService {

	@Autowired
	private WbCourseMapper wbCourseMapper;
	@Autowired
	private WbCourseCommentMapper wbCourseCommentMapper;
	
	@Override
	@Transactional
	public void saveMain(WbCourse wbCourse, List<WbCourseComment> wbCourseCommentList) {
		wbCourseMapper.insert(wbCourse);
		if(wbCourseCommentList!=null && wbCourseCommentList.size()>0) {
			for(WbCourseComment entity:wbCourseCommentList) {
				//外键设置
				entity.setCourseId(wbCourse.getId());
				if(entity.getContent().length()>0){
					wbCourseCommentMapper.insert(entity);
				}
			}
		}
	}

	@Override
	@Transactional
	public void updateMain(WbCourse wbCourse,List<WbCourseComment> wbCourseCommentList) {
		wbCourseMapper.updateById(wbCourse);
		
		//1.先删除子表数据
		wbCourseCommentMapper.deleteByMainId(wbCourse.getId());
		
		//2.子表数据重新插入
		if(wbCourseCommentList!=null && wbCourseCommentList.size()>0) {
			for(WbCourseComment entity:wbCourseCommentList) {
				//外键设置
				entity.setCourseId(wbCourse.getId());
				if(entity.getContent().length()>0){
					wbCourseCommentMapper.insert(entity);
				}
			}
		}
	}

	@Override
	@Transactional
	public void delMain(String id) {
		wbCourseCommentMapper.deleteByMainId(id);
		wbCourseMapper.deleteById(id);
	}

	@Override
	@Transactional
	public void delBatchMain(Collection<? extends Serializable> idList) {
		for(Serializable id:idList) {
			wbCourseCommentMapper.deleteByMainId(id.toString());
			wbCourseMapper.deleteById(id);
		}
	}
	
}
