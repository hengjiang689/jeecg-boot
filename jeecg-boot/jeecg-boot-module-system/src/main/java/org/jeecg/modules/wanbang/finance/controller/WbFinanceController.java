package org.jeecg.modules.wanbang.finance.controller;

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
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.wanbang.finance.entity.WbFinance;
import org.jeecg.modules.wanbang.finance.entity.WbFinanceWithdraw;
import org.jeecg.modules.wanbang.finance.service.IWbFinanceService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.wanbang.finance.service.IWbFinanceWithdrawService;
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
 * @Description: 万邦财务信息表
 * @Author: jeecg-boot
 * @Date:   2019-11-19
 * @Version: V1.0
 */
 @Api(tags="财务管理")
@RestController
@RequestMapping("/finance/wbFinance")
@Slf4j
public class WbFinanceController extends JeecgController<WbFinance, IWbFinanceService> {
	@Autowired
	private IWbFinanceService wbFinanceService;

	@Autowired
	private IWbFinanceWithdrawService wbFinanceWithdrawService;
	
	/**
	 * 分页列表查询
	 *
	 * @param wbFinanceWithdraw
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	 @GetMapping(value = "/list")
	 public Result<?> queryPageList(WbFinanceWithdraw wbFinanceWithdraw,
									@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									@RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									HttpServletRequest req) {
		 QueryWrapper<WbFinanceWithdraw> queryWrapper = QueryGenerator.initQueryWrapper(wbFinanceWithdraw, req.getParameterMap());
		 Page<WbFinanceWithdraw> page = new Page<>(pageNo, pageSize);
		 IPage<WbFinanceWithdraw> pageList = wbFinanceWithdrawService.page(page, queryWrapper);
		 return Result.ok(pageList);
	 }

	 @ApiOperation(value = "用户财务管理", notes = "type 0为收入 1为提现")
	 @GetMapping(value = "/user/list")
	 public Result<?> userQueryPageList(WbFinanceWithdraw wbFinanceWithdraw,
											@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
											@RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
											HttpServletRequest req) {
		 LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		 wbFinanceWithdraw.setUserId(sysUser.getId());
		 return queryPageList(wbFinanceWithdraw,pageNo,pageSize,req);
	 }

	
	/**
	 *   添加
	 *
	 * @param wbFinance
	 * @return
	 */
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody WbFinance wbFinance) {
		wbFinanceService.save(wbFinance);
		return Result.ok("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param wbFinance
	 * @return
	 */
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody WbFinance wbFinance) {
		wbFinanceService.updateById(wbFinance);
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
		wbFinanceService.removeById(id);
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
		this.wbFinanceService.removeByIds(Arrays.asList(ids.split(",")));
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
		WbFinance wbFinance = wbFinanceService.getById(id);
		if(wbFinance==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(wbFinance);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param wbFinance
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, WbFinance wbFinance) {
        return super.exportXls(request, wbFinance, WbFinance.class, "万邦财务信息表");
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
        return super.importExcel(request, response, WbFinance.class);
    }

}
