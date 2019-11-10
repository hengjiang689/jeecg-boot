package org.jeecg.modules.wanbang.course.mapper;

import java.util.List;
import org.jeecg.modules.wanbang.course.entity.WbCourseComment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.wanbang.course.entity.WbCourseUserComment;

/**
 * @Description: 万邦课程评论
 * @Author: jeecg-boot
 * @Date:   2019-11-09
 * @Version: V1.0
 */
public interface WbCourseCommentMapper extends BaseMapper<WbCourseComment> {

	public boolean deleteByMainId(@Param("mainId") String mainId);
    
	public List<WbCourseComment> selectByMainId(@Param("mainId") String mainId);

	public List<WbCourseUserComment> selectByMainIdWithUser(@Param("mainId") String mainId);

}
