package org.jeecg.modules.wanbang.sharedaily.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.wanbang.sharedaily.entity.WbShareDaily;
import org.jeecg.modules.wanbang.sharedaily.entity.WbShareDailyCourse;
import org.jeecg.modules.wanbang.sharedaily.service.IWbShareDailyCourseService;
import org.jeecg.modules.wanbang.sharedaily.service.IWbShareDailyService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;

 /**
 * @Description: 每日必发
 * @Author: jeecg-boot
 * @Date:   2019-12-21
 * @Version: V1.0
 */
 @Api(tags="每日必发管理")
@RestController
@RequestMapping("/sharedaily/wbShareDaily")
@Slf4j
public class WbShareDailyController extends JeecgController<WbShareDaily, IWbShareDailyService> {

	@Autowired
	private IWbShareDailyService wbShareDailyService;

	 @Autowired
	 private IWbShareDailyCourseService wbShareDailyCourseService;
	
	/**
	 * 分页列表查询
	 *
	 * @param wbShareDailyCourse
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@ApiOperation(value = "每日必发列表", notes = "参数type  1为素材  2为课程")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(WbShareDailyCourse wbShareDailyCourse,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<WbShareDailyCourse> queryWrapper = QueryGenerator.initQueryWrapper(wbShareDailyCourse, req.getParameterMap());
		Page<WbShareDailyCourse> page = new Page<>(pageNo, pageSize);
		IPage<WbShareDailyCourse> pageList = wbShareDailyCourseService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param wbShareDaily
	 * @return
	 */
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody WbShareDaily wbShareDaily) {
		wbShareDailyService.save(wbShareDaily);
		return Result.ok("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param wbShareDaily
	 * @return
	 */
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody WbShareDaily wbShareDaily) {
		wbShareDailyService.updateById(wbShareDaily);
		return Result.ok("编辑成功!");
	}

	 /**
	  *  编辑
	  * @param id
	  * @return
	  */
	 @ApiOperation(value = "点赞")
	 @PutMapping(value = "/like/{id}")
	 public Result<?> likeShare(@PathVariable("id") String id) {
		 WbShareDaily wbShareDaily = wbShareDailyService.getById(id);
		 if(wbShareDaily!=null){
			 wbShareDaily.setLikeNum(wbShareDaily.getLikeNum()+1);
			 wbShareDailyService.updateById(wbShareDaily);
		 }
		 return Result.ok("点赞成功!");
	 }
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		wbShareDailyService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.wbShareDailyService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		WbShareDaily wbShareDaily = wbShareDailyService.getById(id);
		if(wbShareDaily==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(wbShareDaily);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param wbShareDaily
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, WbShareDaily wbShareDaily) {
        return super.exportXls(request, wbShareDaily, WbShareDaily.class, "每日必发");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, WbShareDaily.class);
    }

}
