package org.jeecg.modules.wanbang.singlepage.controller;

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
import org.jeecg.modules.wanbang.singlepage.entity.WbSinglepage;
import org.jeecg.modules.wanbang.singlepage.service.IWbSinglepageService;

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
 * @Description: 单页表单
 * @Author: jeecg-boot
 * @Date:   2019-11-19
 * @Version: V1.0
 */
 @Api(tags="单页面管理")
@RestController
@RequestMapping("/singlepage/wbSinglepage")
@Slf4j
public class WbSinglepageController extends JeecgController<WbSinglepage, IWbSinglepageService> {
	@Autowired
	private IWbSinglepageService wbSinglepageService;
	
	/**
	 * 分页列表查询
	 *
	 * @param wbSinglepage
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@ApiOperation(value = "单页列表")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(WbSinglepage wbSinglepage,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<WbSinglepage> queryWrapper = QueryGenerator.initQueryWrapper(wbSinglepage, req.getParameterMap());
		Page<WbSinglepage> page = new Page<WbSinglepage>(pageNo, pageSize);
		IPage<WbSinglepage> pageList = wbSinglepageService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param wbSinglepage
	 * @return
	 */
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody WbSinglepage wbSinglepage) {
		wbSinglepageService.save(wbSinglepage);
		return Result.ok("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param wbSinglepage
	 * @return
	 */
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody WbSinglepage wbSinglepage) {
		wbSinglepageService.updateById(wbSinglepage);
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
		wbSinglepageService.removeById(id);
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
		this.wbSinglepageService.removeByIds(Arrays.asList(ids.split(",")));
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
		WbSinglepage wbSinglepage = wbSinglepageService.getById(id);
		if(wbSinglepage==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(wbSinglepage);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param wbSinglepage
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, WbSinglepage wbSinglepage) {
        return super.exportXls(request, wbSinglepage, WbSinglepage.class, "单页表单");
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
        return super.importExcel(request, response, WbSinglepage.class);
    }

}
