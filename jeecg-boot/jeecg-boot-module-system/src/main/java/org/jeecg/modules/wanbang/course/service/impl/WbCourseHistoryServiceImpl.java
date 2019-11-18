package org.jeecg.modules.wanbang.course.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.wanbang.course.entity.WbCourseComment;
import org.jeecg.modules.wanbang.course.entity.WbCourseHistory;
import org.jeecg.modules.wanbang.course.entity.WbCourseUserComment;
import org.jeecg.modules.wanbang.course.entity.WbCourseUserHistory;
import org.jeecg.modules.wanbang.course.mapper.WbCourseCommentMapper;
import org.jeecg.modules.wanbang.course.mapper.WbCourseHistoryMapper;
import org.jeecg.modules.wanbang.course.service.IWbCourseCommentService;
import org.jeecg.modules.wanbang.course.service.IWbCourseHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: 万邦课程用户访问历史记录
 * @Author: jeecg-boot
 * @Date:   2019-11-09
 * @Version: V1.0
 */
@Service
public class WbCourseHistoryServiceImpl extends ServiceImpl<WbCourseHistoryMapper, WbCourseHistory> implements IWbCourseHistoryService {

    @Autowired
    WbCourseHistoryMapper wbCourseHistoryMapper;

    @Override
    public WbCourseHistory selectUserHistoryByCourseId(String courseId,String username) {
        return wbCourseHistoryMapper.selectUserHistoryByCourseId(courseId,username);
    }
}
