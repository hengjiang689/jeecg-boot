package org.jeecg.modules.wanbang.course.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.wanbang.course.entity.WbCourseHistory;
import org.jeecg.modules.wanbang.course.entity.WbCourseUserHistory;
import org.jeecg.modules.wanbang.course.mapper.WbCourseHistoryMapper;
import org.jeecg.modules.wanbang.course.mapper.WbCourseUserHistoryMapper;
import org.jeecg.modules.wanbang.course.service.IWbCourseHistoryService;
import org.jeecg.modules.wanbang.course.service.IWbCourseUserHistoryService;
import org.springframework.stereotype.Service;

/**
 * @Description: 万邦课程用户访问历史记录
 * @Author: jeecg-boot
 * @Date:   2019-11-09
 * @Version: V1.0
 */
@Service
public class WbCourseUserHistoryServiceImpl extends ServiceImpl<WbCourseUserHistoryMapper, WbCourseUserHistory> implements IWbCourseUserHistoryService {

}
