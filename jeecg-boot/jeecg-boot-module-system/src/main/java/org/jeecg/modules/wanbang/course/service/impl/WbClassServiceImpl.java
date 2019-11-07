package org.jeecg.modules.wanbang.course.service.impl;

import org.jeecg.modules.wanbang.course.entity.WbClass;
import org.jeecg.modules.wanbang.course.mapper.WbClassMapper;
import org.jeecg.modules.wanbang.course.service.IWbClassService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 万邦子课程表
 * @Author: jeecg-boot
 * @Date:   2019-11-07
 * @Version: V1.0
 */
@Service
public class WbClassServiceImpl extends ServiceImpl<WbClassMapper, WbClass> implements IWbClassService {
	
	@Autowired
	private WbClassMapper wbClassMapper;
	
	@Override
	public List<WbClass> selectByMainId(String mainId) {
		return wbClassMapper.selectByMainId(mainId);
	}
}
