package org.jeecg.modules.wanbang.carousel.controller;

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
import org.jeecg.modules.wanbang.carousel.entity.WbCarousel;
import org.jeecg.modules.wanbang.carousel.service.IWbCarouselService;

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
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;

 /**
 * @Description: wb_carousel
 * @Author: jeecg-boot
 * @Date:   2019-10-28
 * @Version: V1.0
 */
@CacheConfig(cacheNames = "carousel")
@RestController
@RequestMapping("/carousel/wbCarousel")
@Api(tags="轮播图管理")
@Slf4j
public class WbCarouselController extends JeecgController<WbCarousel, IWbCarouselService> {
	@Autowired
	private IWbCarouselService wbCarouselService;
	
	/**
	 * 分页列表查询
	 *
	 * @param wbCarousel
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@ApiOperation(value = "轮播图列表", notes = "轮播图列表 carouselType 1 为首页 2 为家庭教育")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(WbCarousel wbCarousel,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<WbCarousel> queryWrapper = QueryGenerator.initQueryWrapper(wbCarousel, req.getParameterMap());
		Page<WbCarousel> page = new Page<WbCarousel>(pageNo, pageSize);
		IPage<WbCarousel> pageList = wbCarouselService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param wbCarousel
	 * @return
	 */
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody WbCarousel wbCarousel) {
		wbCarouselService.save(wbCarousel);
		return Result.ok("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param wbCarousel
	 * @return
	 */
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody WbCarousel wbCarousel) {
		wbCarouselService.updateById(wbCarousel);
		return Result.ok("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		wbCarouselService.removeById(id);
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
		this.wbCarouselService.removeByIds(Arrays.asList(ids.split(",")));
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
		WbCarousel wbCarousel = wbCarouselService.getById(id);
		if(wbCarousel==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(wbCarousel);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param wbCarousel
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, WbCarousel wbCarousel) {
        return super.exportXls(request, wbCarousel, WbCarousel.class, "wb_carousel");
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
        return super.importExcel(request, response, WbCarousel.class);
    }

}
