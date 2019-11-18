package org.jeecg.modules.wanbang.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.wanbang.course.entity.WbCourseHistory;

/**
 * @Description: 万邦课程用户访问历史记录
 * @Author: jeecg-boot
 * @Date:   2019-11-09
 * @Version: V1.0
 */
public interface WbCourseHistoryMapper extends BaseMapper<WbCourseHistory> {
    public WbCourseHistory selectUserHistoryByCourseId(@Param("courseId") String courseId,@Param("username") String username);
}
