package org.jeecg.modules.wanbang.course.service.impl;

import org.jeecg.modules.wanbang.course.entity.WbCourseComment;
import org.jeecg.modules.wanbang.course.mapper.WbCourseCommentMapper;
import org.jeecg.modules.wanbang.course.service.IWbCourseCommentService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 万邦课程评论
 * @Author: jeecg-boot
 * @Date:   2019-11-05
 * @Version: V1.0
 */
@Service
public class WbCourseCommentServiceImpl extends ServiceImpl<WbCourseCommentMapper, WbCourseComment> implements IWbCourseCommentService {
	
	@Autowired
	private WbCourseCommentMapper wbCourseCommentMapper;
	
	@Override
	public List<WbCourseComment> selectByMainId(String mainId) {
		return wbCourseCommentMapper.selectByMainId(mainId);
	}
}
