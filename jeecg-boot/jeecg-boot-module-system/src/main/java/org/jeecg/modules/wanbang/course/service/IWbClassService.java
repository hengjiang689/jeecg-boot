package org.jeecg.modules.wanbang.course.service;

import org.jeecg.modules.wanbang.course.entity.WbClass;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 万邦子课程表
 * @Author: jeecg-boot
 * @Date:   2019-11-07
 * @Version: V1.0
 */
public interface IWbClassService extends IService<WbClass> {

	public List<WbClass> selectByMainId(String mainId);
}
