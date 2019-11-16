package org.jeecg.modules.wanbang.payment.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.system.service.ISysDictService;
import org.jeecg.modules.wanbang.course.entity.WbCourse;
import org.jeecg.modules.wanbang.course.service.IWbCourseService;
import org.jeecg.modules.wanbang.payment.entity.WbPaymentTransaction;
import org.jeecg.modules.wanbang.payment.service.IWbPaymentTransactionService;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import weixin.popular.api.PayMchAPI;
import weixin.popular.api.SnsAPI;
import weixin.popular.bean.paymch.Unifiedorder;
import weixin.popular.bean.paymch.UnifiedorderResult;
import weixin.popular.bean.sns.Jscode2sessionResult;
import weixin.popular.bean.sns.SnsToken;
import weixin.popular.util.JsonUtil;
import weixin.popular.util.PayUtil;

/**
 * @Description: 万邦支付交易表
 * @Author: jeecg-boot
 * @Date:   2019-11-15
 * @Version: V1.0
 */
@RestController
@RequestMapping("/payment/wbPaymentTransaction")
@Slf4j
public class WbPaymentTransactionController extends JeecgController<WbPaymentTransaction, IWbPaymentTransactionService> {

	@Value("${jeecg.mini-program.appId}")
	private String miniAppId;

	@Value("${jeecg.mini-program.appSecret}")
	private String miniAppSecret;

	@Value("${jeecg.mobile.appId}")
	private String mobileAppId;

	@Value("${jeecg.mobile.appSecret}")
	private String mobileSecret;

	@Value("${jeecg.payment.weixin.mchId}")
	private String wxMchId;

	@Value("${eecg.payment.weixin.mchKey}")
	private String wxMchKey;

	@Value("${eecg.payment.weixin.callBackUrl}")
	private String wxCallBackUrl;

	@Autowired
	private IWbPaymentTransactionService wbPaymentTransactionService;

	@Autowired
	private ISysDictService sysDictService;

	@Autowired
	private IWbCourseService wbCourseService;

	@ApiOperation(value = "微信生成支付unifiedorder", notes = "微信生成支付unifiedorder,courseId 为可选{\n" +
			"  \"code\": \"d8df70\",\n" +
			"  \"courseId\": 193293823080\n" +
			"}")
	@PostMapping("/unifiedorder/weixin")
	public String wxUnifiedOrder(@RequestBody JSONObject jsonObject){
		String openId=null;
		String tradeType;
		Unifiedorder unifiedorder =new Unifiedorder();
		if(jsonObject.getString("code")!=null){
			Jscode2sessionResult jscode2sessionResult = SnsAPI.jscode2session(miniAppId, miniAppSecret, jsonObject.getString("code"));
			openId=jscode2sessionResult.getOpenid();
			tradeType = "JSAPI";
			unifiedorder.setAppid(miniAppId);
		}else{
			tradeType = "APP";
			unifiedorder.setAppid(mobileAppId);
		}
		unifiedorder.setMch_id(wxMchId);
		unifiedorder.setNotify_url(wxCallBackUrl);
		unifiedorder.setTrade_type(tradeType);
		String body;
		String totalFee;
		if(StringUtils.isEmpty(jsonObject.getBigInteger("courseId"))){
			body="万邦教育VIP会员";
			totalFee = Integer.parseInt(sysDictService.queryDictTextByKey("system_config", "membership_fee"))*100+"";
		}else{
			WbCourse wbCourse = wbCourseService.getById(jsonObject.getBigInteger("courseId"));
			body="购买课程:"+wbCourse.getTitle();
			totalFee = wbCourse.getPrice()*100+"";
		}
		unifiedorder.setBody(body);
		unifiedorder.setOut_trade_no(UUID.randomUUID().toString().replace("-",""));
		unifiedorder.setTotal_fee(totalFee);
		unifiedorder.setNonce_str(UUID.randomUUID().toString().replace("-",""));
		unifiedorder.setOpenid(openId);
		UnifiedorderResult unifiedorderResult = PayMchAPI.payUnifiedorder(unifiedorder,wxMchKey);
		WbPaymentTransaction wbPaymentTransaction = new WbPaymentTransaction();
		wbPaymentTransaction.setBody(unifiedorder.getBody());
		wbPaymentTransaction.setCourseId(jsonObject.getString("courseId"));
		wbPaymentTransaction.setOutTradeNo(unifiedorder.getOut_trade_no());
		wbPaymentTransaction.setPlatform(0);
		wbPaymentTransaction.setPrepayId(unifiedorderResult.getPrepay_id());
		wbPaymentTransaction.setStatus(0);
		wbPaymentTransaction.setTotalFee(Integer.getInteger(unifiedorder.getTotal_fee()));
		wbPaymentTransaction.setTradeType(unifiedorder.getTrade_type());
		wbPaymentTransactionService.save(wbPaymentTransaction);
		if(tradeType.equalsIgnoreCase("APP")){
			return JsonUtil.toJSONString(PayUtil.generateMchAppData(unifiedorderResult.getPrepay_id(),unifiedorder.getAppid(),unifiedorder.getMch_id(),wxMchKey));
		}else{
			return PayUtil.generateMchPayJsRequestJson(unifiedorderResult.getPrepay_id(),unifiedorderResult.getAppid(),wxMchKey);
		}
	}

	@PostMapping("/weixin/notify")
	public String wxNotify(@RequestBody JSONObject jsonObject){

		return "";
	}


	/**
	 * 分页列表查询
	 *
	 * @param wbPaymentTransaction
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@GetMapping(value = "/list")
	public Result<?> queryPageList(WbPaymentTransaction wbPaymentTransaction,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<WbPaymentTransaction> queryWrapper = QueryGenerator.initQueryWrapper(wbPaymentTransaction, req.getParameterMap());
		Page<WbPaymentTransaction> page = new Page<WbPaymentTransaction>(pageNo, pageSize);
		IPage<WbPaymentTransaction> pageList = wbPaymentTransactionService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param wbPaymentTransaction
	 * @return
	 */
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody WbPaymentTransaction wbPaymentTransaction) {
		wbPaymentTransactionService.save(wbPaymentTransaction);
		return Result.ok("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param wbPaymentTransaction
	 * @return
	 */
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody WbPaymentTransaction wbPaymentTransaction) {
		wbPaymentTransactionService.updateById(wbPaymentTransaction);
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
		wbPaymentTransactionService.removeById(id);
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
		this.wbPaymentTransactionService.removeByIds(Arrays.asList(ids.split(",")));
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
		WbPaymentTransaction wbPaymentTransaction = wbPaymentTransactionService.getById(id);
		if(wbPaymentTransaction==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(wbPaymentTransaction);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param wbPaymentTransaction
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, WbPaymentTransaction wbPaymentTransaction) {
        return super.exportXls(request, wbPaymentTransaction, WbPaymentTransaction.class, "万邦支付交易表");
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
        return super.importExcel(request, response, WbPaymentTransaction.class);
    }

}
