package org.jeecg.modules.wanbang.course.mapper;

import java.util.List;
import org.jeecg.modules.wanbang.course.entity.WbClass;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 万邦子课程表
 * @Author: jeecg-boot
 * @Date:   2019-11-07
 * @Version: V1.0
 */
public interface WbClassMapper extends BaseMapper<WbClass> {

	public boolean deleteByMainId(@Param("mainId") String mainId);
    
	public List<WbClass> selectByMainId(@Param("mainId") String mainId);
}
