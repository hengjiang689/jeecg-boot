package org.jeecg.modules.wanbang.course.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.io.resource.ResourceUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.jeecg.modules.system.service.ISysCategoryService;
import org.jeecg.modules.wanbang.course.entity.*;
import org.jeecg.modules.wanbang.course.service.*;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.system.vo.LoginUser;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.wanbang.course.vo.WbCoursePage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import com.alibaba.fastjson.JSON;

 /**
 * @Description: 万邦课程表
 * @Author: jeecg-boot
 * @Date:   2019-11-07
 * @Version: V1.0
 */
 @CacheConfig(cacheNames = "course")
 @RestController
 @RequestMapping("/course/wbCourse")
 @Api(tags="课程管理")
@Slf4j
public class WbCourseController {
	@Autowired
	private IWbCourseService wbCourseService;
	@Autowired
	private IWbCourseCommentService wbCourseCommentService;
	@Autowired
	private IWbCourseHistoryService wbCourseHistoryService;
	@Autowired
	private IWbCourseUserHistoryService wbCourseUserHistoryService;
	@Autowired
	private IWbClassService wbClassService;
	@Autowired
	private ISysCategoryService sysCategoryService;
	
	/**
	 * 分页列表查询
	 *
	 * @param wbCourse
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
//	@Cacheable
	@ApiOperation(value = "课程列表", notes = "课程列表 type 1 为志慧学堂 2 为幸福学院 3 为百家讲坛 ")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(WbCourse wbCourse,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		if(!StringUtils.isBlank(wbCourse.getTitle())){
			wbCourse.setTitle("*"+wbCourse.getTitle()+"*");
		}
		if(!StringUtils.isBlank(wbCourse.getTeacherName())){
			wbCourse.setTeacherName("*"+wbCourse.getTeacherName()+"*");
		}
		if(!StringUtils.isBlank(wbCourse.getCategory())){
			wbCourse.setCategoryCode(sysCategoryService.getById(wbCourse.getCategory()).getCode()+"*");
			wbCourse.setCategory(null);
		}
		QueryWrapper<WbCourse> queryWrapper = QueryGenerator.initQueryWrapper(wbCourse, req.getParameterMap());
		Page<WbCourse> page = new Page<WbCourse>(pageNo, pageSize);
		IPage<WbCourse> pageList = wbCourseService.page(page, queryWrapper);
		return Result.ok(pageList);
	}

	 @GetMapping(value = "/comment/list")
	 public Result<?> queryCommentPageList(WbCourseComment wbCourseComment,
									@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									@RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									HttpServletRequest req) {
		 QueryWrapper<WbCourseComment> queryWrapper = QueryGenerator.initQueryWrapper(wbCourseComment, req.getParameterMap());
		 Page<WbCourseComment> page = new Page<>(pageNo, pageSize);
		 IPage<WbCourseComment> pageList = wbCourseCommentService.page(page, queryWrapper);
		 return Result.ok(pageList);
	 }

	 @ApiOperation(value = "用户访问课程历史记录", notes = "/history/list?column=createTime&order=desc&pageNo=1&pageSize=10 是否支付参数isPaid 0为未支付 1为已支付")
	 @GetMapping(value = "/history/list")
	 public Result<?> queryHistoryPageList(WbCourseUserHistory wbCourseUserHistory,
										   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
										   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
										   HttpServletRequest req) {
		 LoginUser sysUser = (LoginUser)SecurityUtils.getSubject().getPrincipal();
		 wbCourseUserHistory.setCreateBy(sysUser.getUsername());
		 QueryWrapper<WbCourseUserHistory> queryWrapper = QueryGenerator.initQueryWrapper(wbCourseUserHistory, req.getParameterMap());
		 Page<WbCourseUserHistory> page = new Page<>(pageNo, pageSize);
		 IPage<WbCourseUserHistory> pageList = wbCourseUserHistoryService.page(page, queryWrapper);
		 return Result.ok(pageList);
	 }


	
	/**
	 *   添加
	 *
	 * @param wbCoursePage
	 * @return
	 */
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody WbCoursePage wbCoursePage) {
		WbCourse wbCourse = new WbCourse();
		BeanUtils.copyProperties(wbCoursePage, wbCourse);
		if(!StringUtils.isBlank(wbCourse.getCategory())){
			wbCourse.setCategoryCode(sysCategoryService.getById(wbCourse.getCategory()).getCode());
		}
		wbCourseService.saveMain(wbCourse, wbCoursePage.getWbCourseCommentList(),wbCoursePage.getWbClassList());
		return Result.ok("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param wbCoursePage
	 * @return
	 */
	@CacheEvict(allEntries = true)
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody WbCoursePage wbCoursePage) {
		WbCourse wbCourse = new WbCourse();
		BeanUtils.copyProperties(wbCoursePage, wbCourse);
		if(!StringUtils.isBlank(wbCourse.getCategory())){
			wbCourse.setCategoryCode(sysCategoryService.getById(wbCourse.getCategory()).getCode());
		}
		WbCourse wbCourseEntity = wbCourseService.getById(wbCourse.getId());
		if(wbCourseEntity==null) {
			return Result.error("未找到对应数据");
		}
		wbCourseService.updateMain(wbCourse, wbCoursePage.getWbCourseCommentList(),wbCoursePage.getWbClassList());
		return Result.ok("编辑成功!");
	}

	 @PutMapping(value = "/comment/edit")
	 public Result<?> commentEdit(@RequestBody WbCourseComment wbCourseComment) {
		 WbCourseComment wbCourseComment1 = wbCourseCommentService.getById(wbCourseComment.getId());
		 if(wbCourseComment1==null) {
			 return Result.error("未找到对应数据");
		 }
		 wbCourseComment1.setContent(wbCourseComment.getContent());
		 wbCourseComment1.setPublish(wbCourseComment.getPublish());
		 wbCourseCommentService.updateById(wbCourseComment1);
		 return Result.ok("编辑成功!");
	 }

	 @ApiOperation(value = "新增评论", notes = "新增评论 {\n" +
			 "  \"content\": \"string\",\n" +
			 "  \"courseId\": \"courseId\"\n" +
			 "}")
	 @PostMapping(value = "/addComment")
	 public Result<?> addComment(@RequestBody WbCourseComment wbCourseComment) {
		 WbCourse wbCourseEntity = wbCourseService.getById(wbCourseComment.getCourseId());
		 if(wbCourseEntity==null) {
			 return Result.error("未找到对应数据");
		 }
		 wbCourseComment.setId(null);
		 wbCourseCommentService.save(wbCourseComment);
		 return Result.ok("添加成功!");
	 }

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@CacheEvict(allEntries = true)
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		wbCourseService.delMain(id);
		return Result.ok("删除成功!");
	}

	 @DeleteMapping(value = "/comment/delete")
	 public Result<?> deleteComment(@RequestParam(name="id",required=true) String id) {
		 wbCourseCommentService.removeById(id);
		 return Result.ok("删除成功!");
	 }

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@CacheEvict(allEntries = true)
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.wbCourseService.delBatchMain(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
//	@Cacheable
	@ApiOperation(value = "未登陆状态下根据id查询课程详情")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		WbCourse wbCourse = wbCourseService.getById(id);
		if(wbCourse==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(wbCourse);
	}

	 @ApiOperation(value = "根据id查询课程详情使用用户登陆状态")
	 @GetMapping(value = "/queryByIdWithUser")
	 public Result<?> queryByIdWithUser(@RequestParam(name="id",required=true) String id) {
		 WbCourse wbCourse = wbCourseService.getById(id);
		 if(wbCourse==null) {
			 return Result.error("未找到对应数据");
		 }
//		 LoginUser sysUser = (LoginUser)SecurityUtils.getSubject().getPrincipal();
		 WbCourseHistory wbCourseHistory = wbCourseHistoryService.selectHistoryByCourseId(id);
		 if(wbCourseHistory==null){
			 wbCourseHistory = new WbCourseHistory();
			 wbCourseHistory.setCourseId(id);
			 wbCourseHistoryService.save(wbCourseHistory);
			 wbCourse.setIsPaid("0");
		 }else{
			 wbCourse.setIsPaid(wbCourseHistory.getIsPaid());
		 }
		 return Result.ok(wbCourse);
	 }
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@GetMapping(value = "/queryWbCourseCommentByMainId")
	public Result<?> queryWbCourseCommentListByMainId(@RequestParam(name="id",required=true) String id) {
		List<WbCourseComment> wbCourseCommentList = wbCourseCommentService.selectByMainId(id);
		return Result.ok(wbCourseCommentList);
	}

	 /**
	  * 通过id查询
	  *
	  * @param id
	  * @return
	  */
	 @ApiOperation(value = "根据课程id查询课程评论列表")
	 @GetMapping(value = "/queryWbCourseUserCommentByMainId")
	 public Result<?> queryWbCourseUserCommentListByMainId(@RequestParam(name="id",required=true) String id) {
		 List<WbCourseUserComment> wbCourseUserCommentList = wbCourseCommentService.selectByMainIdWithUser(id);
		 return Result.ok(wbCourseUserCommentList);
	 }

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@ApiOperation(value = "根据课程id查询子课程列表")
	@GetMapping(value = "/queryWbClassByMainId")
	public Result<?> queryWbClassListByMainId(@RequestParam(name="id",required=true) String id) {
		List<WbClass> wbClassList = wbClassService.selectByMainId(id);
		return Result.ok(wbClassList);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param wbCourse
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, WbCourse wbCourse) {
      // Step.1 组装查询条件查询数据
      QueryWrapper<WbCourse> queryWrapper = QueryGenerator.initQueryWrapper(wbCourse, request.getParameterMap());
      LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

      //Step.2 获取导出数据
      List<WbCourse> queryList = wbCourseService.list(queryWrapper);
      // 过滤选中数据
      String selections = request.getParameter("selections");
      List<WbCourse> wbCourseList = new ArrayList<WbCourse>();
      if(oConvertUtils.isEmpty(selections)) {
          wbCourseList = queryList;
      }else {
          List<String> selectionList = Arrays.asList(selections.split(","));
          wbCourseList = queryList.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
      }

      // Step.3 组装pageList
      List<WbCoursePage> pageList = new ArrayList<WbCoursePage>();
      for (WbCourse main : wbCourseList) {
          WbCoursePage vo = new WbCoursePage();
          BeanUtils.copyProperties(main, vo);
          List<WbCourseComment> wbCourseCommentList = wbCourseCommentService.selectByMainId(main.getId());
          vo.setWbCourseCommentList(wbCourseCommentList);
          List<WbClass> wbClassList = wbClassService.selectByMainId(main.getId());
          vo.setWbClassList(wbClassList);
          pageList.add(vo);
      }

      // Step.4 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      mv.addObject(NormalExcelConstants.FILE_NAME, "万邦课程表列表");
      mv.addObject(NormalExcelConstants.CLASS, WbCoursePage.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("万邦课程表数据", "导出人:"+sysUser.getRealname(), "万邦课程表"));
      mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
      return mv;
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
      MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
      Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
      for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
          MultipartFile file = entity.getValue();// 获取上传文件对象
          ImportParams params = new ImportParams();
          params.setTitleRows(2);
          params.setHeadRows(1);
          params.setNeedSave(true);
          try {
              List<WbCoursePage> list = ExcelImportUtil.importExcel(file.getInputStream(), WbCoursePage.class, params);
              for (WbCoursePage page : list) {
                  WbCourse po = new WbCourse();
                  BeanUtils.copyProperties(page, po);
                  wbCourseService.saveMain(po, page.getWbCourseCommentList(),page.getWbClassList());
              }
              return Result.ok("文件导入成功！数据行数:" + list.size());
          } catch (Exception e) {
              log.error(e.getMessage(),e);
              return Result.error("文件导入失败:"+e.getMessage());
          } finally {
              try {
                  file.getInputStream().close();
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }
      }
      return Result.ok("文件导入失败！");
    }

}
