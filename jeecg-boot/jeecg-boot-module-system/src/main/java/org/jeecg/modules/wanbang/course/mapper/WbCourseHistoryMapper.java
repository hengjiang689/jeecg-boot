package org.jeecg.modules.wanbang.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.wanbang.course.entity.WbCourseComment;
import org.jeecg.modules.wanbang.course.entity.WbCourseHistory;
import org.jeecg.modules.wanbang.course.entity.WbCourseUserComment;
import org.jeecg.modules.wanbang.course.entity.WbCourseUserHistory;

import java.util.List;

/**
 * @Description: 万邦课程用户访问历史记录
 * @Author: jeecg-boot
 * @Date:   2019-11-09
 * @Version: V1.0
 */
public interface WbCourseHistoryMapper extends BaseMapper<WbCourseHistory> {
    public WbCourseHistory selectHistoryByCourseId(@Param("courseId") String courseId);
}
