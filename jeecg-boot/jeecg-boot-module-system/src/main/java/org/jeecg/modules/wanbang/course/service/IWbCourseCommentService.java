package org.jeecg.modules.wanbang.course.service;

import org.jeecg.modules.wanbang.course.entity.WbCourseComment;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 万邦课程评论
 * @Author: jeecg-boot
 * @Date:   2019-10-28
 * @Version: V1.0
 */
public interface IWbCourseCommentService extends IService<WbCourseComment> {

	public List<WbCourseComment> selectByMainId(String mainId);
}
