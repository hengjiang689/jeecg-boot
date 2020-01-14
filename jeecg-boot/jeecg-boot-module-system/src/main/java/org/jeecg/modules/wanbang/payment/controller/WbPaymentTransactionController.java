package org.jeecg.modules.wanbang.payment.controller;

import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.*;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecg.modules.wanbang.course.entity.WbCourse;
import org.jeecg.modules.wanbang.course.entity.WbCourseHistory;
import org.jeecg.modules.wanbang.course.service.IWbCourseHistoryService;
import org.jeecg.modules.wanbang.course.service.IWbCourseService;
import org.jeecg.modules.wanbang.payment.entity.CallBackResult;
import org.jeecg.modules.wanbang.payment.entity.WbPaymentTransaction;
import org.jeecg.modules.wanbang.payment.service.IWbPaymentTransactionService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import weixin.popular.api.PayMchAPI;
import weixin.popular.api.SnsAPI;
import weixin.popular.bean.paymch.MchPayNotify;
import weixin.popular.bean.paymch.Unifiedorder;
import weixin.popular.bean.paymch.UnifiedorderResult;
import weixin.popular.bean.sns.Jscode2sessionResult;
import weixin.popular.util.*;

/**
 * @Description: 万邦支付交易表
 * @Author: jeecg-boot
 * @Date:   2019-11-15
 * @Version: V1.0
 */
@Api(tags="万邦支付管理")
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

	@Value("${jeecg.payment.weixin.mchKey}")
	private String wxMchKey;

	@Value("${jeecg.payment.weixin.callBackUrl}")
	private String wxCallBackUrl;

	@Value("${jeecg.membershipFee}")
	private Integer membershipFee;

	@Value("${jeecg.payment.alipay.appId}")
	private String aliPayAppId;

	@Value("${jeecg.payment.alipay.publicKey}")
	private String alipayPublicKey;

	@Value("${jeecg.payment.alipay.aliPublicKey}")
	private String aliPublicKey;

	@Value("${jeecg.payment.alipay.privateKey}")
	private String alipayPrivateKey;

	@Value("${jeecg.payment.alipay.callBackUrl}")
	private String alipayCallBackUrl;


	@Autowired
	private IWbCourseHistoryService wbCourseHistoryService;

	@Autowired
	private IWbPaymentTransactionService wbPaymentTransactionService;

	@Autowired
	private IWbCourseService wbCourseService;

	@Autowired
	private ISysUserService sysUserService;

	private AlipayClient alipayClient;

	@PostConstruct
	public void initClient(){
		alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", aliPayAppId, alipayPrivateKey, "json", "utf-8", alipayPublicKey, "RSA2");
	}

	@ApiOperation(value = "membershipfee")
	@GetMapping("/membershipfee")
	public Integer getMembershipFee(){
		return membershipFee;
	}

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
		String body,detail=null;
		String totalFee;
		if(StringUtils.isEmpty(jsonObject.getBigInteger("courseId"))){
			body="万邦教育-购买VIP会员";
			totalFee = membershipFee*100+"";
//			totalFee = membershipFee+"";
		}else{
			WbCourse wbCourse = wbCourseService.getById(jsonObject.getBigInteger("courseId"));
			body="万邦教育-购买课程";
			detail=wbCourse.getTitle();
			totalFee = wbCourse.getPrice().toString().replace(".","").replaceFirst("^0*", "");
		}
		unifiedorder.setBody(body);
		unifiedorder.setOut_trade_no(UUID.randomUUID().toString().replace("-",""));
		unifiedorder.setTotal_fee(totalFee);
		unifiedorder.setNonce_str(UUID.randomUUID().toString().replace("-",""));
		unifiedorder.setOpenid(openId);
		UnifiedorderResult unifiedorderResult = PayMchAPI.payUnifiedorder(unifiedorder,wxMchKey);
		if(unifiedorderResult.getPrepay_id()==null){
			return "";
		}
		WbPaymentTransaction wbPaymentTransaction = new WbPaymentTransaction();
		wbPaymentTransaction.setBody(unifiedorder.getBody());
		wbPaymentTransaction.setDetail(detail);
		wbPaymentTransaction.setCourseId(jsonObject.getString("courseId"));
		wbPaymentTransaction.setOutTradeNo(unifiedorder.getOut_trade_no());
		wbPaymentTransaction.setPlatform("0");
		wbPaymentTransaction.setPrepayId(unifiedorderResult.getPrepay_id());
		wbPaymentTransaction.setStatus("0");
		wbPaymentTransaction.setTotalFee(Integer.parseInt(totalFee));
		wbPaymentTransaction.setTradeType(unifiedorder.getTrade_type());
		wbPaymentTransactionService.save(wbPaymentTransaction);
		if(tradeType.equalsIgnoreCase("APP")){
			return JsonUtil.toJSONString(PayUtil.generateMchAppData(unifiedorderResult.getPrepay_id(),unifiedorder.getAppid(),unifiedorder.getMch_id(),wxMchKey));
		}else{
			return PayUtil.generateMchPayJsRequestJson(unifiedorderResult.getPrepay_id(),unifiedorderResult.getAppid(),wxMchKey);
		}
	}

	@ApiOperation(value = "支付宝unifiedorder", notes = "支付宝unifiedorder,courseId 为可选{\"courseId\": 193293823080}")
	@PostMapping("/unifiedorder/alipay")
		public String aliPayUnifiedOrder(@RequestBody JSONObject jsonObject){
		DecimalFormat fnum = new DecimalFormat("##0.00");
		String result=null;
		AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
		AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
		String body,detail=null;
		String totalFee;
		if(StringUtils.isEmpty(jsonObject.getBigInteger("courseId"))){
			body="万邦教育-VIP会员";
			totalFee = membershipFee.toString();
		}else{
			WbCourse wbCourse = wbCourseService.getById(jsonObject.getBigInteger("courseId"));
			body="万邦教育-购买课程";
			detail = wbCourse.getTitle();
			totalFee = wbCourse.getPrice().toString();
		}
		model.setSubject(body);
		model.setBody(detail);
		model.setOutTradeNo(UUID.randomUUID().toString().replace("-",""));
		model.setTimeoutExpress("30m");
		model.setTotalAmount(fnum.format(Double.parseDouble(totalFee)));
		model.setProductCode("QUICK_MSECURITY_PAY");
		request.setBizModel(model);
		request.setNotifyUrl(alipayCallBackUrl);
		try {
			//这里和普通的接口调用不同，使用的是sdkExecute
			AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
			result=response.getBody();
			WbPaymentTransaction wbPaymentTransaction = new WbPaymentTransaction();
			wbPaymentTransaction.setBody(model.getSubject());
			wbPaymentTransaction.setDetail(model.getBody());
			wbPaymentTransaction.setCourseId(jsonObject.getString("courseId"));
			wbPaymentTransaction.setOutTradeNo(model.getOutTradeNo());
			wbPaymentTransaction.setPlatform("1");
			wbPaymentTransaction.setPrepayId(response.getTradeNo());
			wbPaymentTransaction.setStatus("0");
			wbPaymentTransaction.setTotalFee(Integer.parseInt(model.getTotalAmount().replace(".","")));
			wbPaymentTransaction.setTradeType("ALIPAY");
			wbPaymentTransactionService.save(wbPaymentTransaction);
		} catch (AlipayApiException e) {
			e.printStackTrace();
		}
		return result;
	}

	@SneakyThrows
	@RequestMapping("/notify/weixin")
	public String wxNotify(HttpServletRequest request){
		log.info("================================================开始处理微信的异步通知");
		String xmlResult = StreamUtils.copyToString(request.getInputStream(), Charset.forName("UTF-8"));
		log.info("=返回值=="+xmlResult);
		Map<String, String> resultMap = XMLConverUtil.convertToMap(xmlResult);
		MchPayNotify mchPayNotify = XMLConverUtil.convertToObject(MchPayNotify.class,xmlResult);
		String resultCode;
		String resultMsg;
		if(SignatureUtil.validateSign(resultMap,wxMchKey)){
			log.info("=resultCode=="+mchPayNotify.getResult_code());
			if ("SUCCESS".equalsIgnoreCase(mchPayNotify.getResult_code())) {
				//业务结果为SUCCESS
				WbPaymentTransaction wbPaymentTransaction = wbPaymentTransactionService.getByOutTradeNo(mchPayNotify.getOut_trade_no());
				if(wbPaymentTransaction.getStatus().equalsIgnoreCase("0")){
					wbPaymentTransaction.setCashFee(mchPayNotify.getCash_fee());
					wbPaymentTransaction.setStatus("1");
					wbPaymentTransaction.setTimeEnd(mchPayNotify.getTime_end());
					wbPaymentTransaction.setTransactionId(mchPayNotify.getTransaction_id());
					handlePaymentTransaction(wbPaymentTransaction);
				}
				resultCode = "SUCCESS";
				resultMsg = "成功";
			} else {
				resultCode = "FAIL";
				resultMsg = "业务结果为FAIL";
			}
		}else{
			resultCode = "FAIL";
			resultMsg = "验签未通过";
		}
		return XMLConverUtil.convertToXML(new CallBackResult(resultCode,resultMsg));
	}

	@SneakyThrows
	@RequestMapping("/notify/alipay")
	public String aliPayNotify(HttpServletRequest request){
		log.info("================================================开始处理支付宝的异步通知");
		Map<String,String> params = new HashMap<String,String>();
		Map requestParams = request.getParameterMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
						: valueStr + values[i] + ",";
			}
			//乱码解决，这段代码在出现乱码时使用。
			//valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
			params.put(name, valueStr);
		}
		boolean flag = AlipaySignature.rsaCheckV1(params, aliPublicKey, "utf-8","RSA2");
		if(flag){
			//业务逻辑
			if("TRADE_SUCCESS".equalsIgnoreCase(params.get("trade_status"))){
				WbPaymentTransaction wbPaymentTransaction = wbPaymentTransactionService.getByOutTradeNo(params.get("out_trade_no"));
				if(wbPaymentTransaction.getStatus().equalsIgnoreCase("0")){
					wbPaymentTransaction.setCashFee(Integer.parseInt(params.get("buyer_pay_amount").replace(".","")));
					wbPaymentTransaction.setStatus("1");
					wbPaymentTransaction.setTimeEnd(params.get("gmt_payment").replaceAll("-","").replaceAll(":","").replaceAll(" ",""));
					wbPaymentTransaction.setTransactionId(params.get("trade_no"));
					handlePaymentTransaction(wbPaymentTransaction);
				}
				return "success";
			}else{
				return "failure";
			}
		}else{
			return "failure";
		}
	}

	private void handlePaymentTransaction(WbPaymentTransaction wbPaymentTransaction){
		wbPaymentTransactionService.updateById(wbPaymentTransaction);
		if(wbPaymentTransaction.getCourseId()!=null){
			WbCourseHistory wbCourseHistory = wbCourseHistoryService.selectUserHistoryByCourseId(wbPaymentTransaction.getCourseId(),wbPaymentTransaction.getCreateBy());
			wbCourseHistory.setIsPaid("1");
			wbCourseHistoryService.updateById(wbCourseHistory);
		}else{
			SysUser sysUser = sysUserService.getUserByName(wbPaymentTransaction.getCreateBy());
			sysUser.setIsMember(true);
			sysUserService.updateById(sysUser);
		}
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
	 * 分页列表查询
	 *
	 * @param wbPaymentTransaction
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@ApiOperation(value = "用户支付记录", notes = "用户支付记录 timeEnd格式为201911 不传则查询所有支付记录 pageSize默认为10")
	@GetMapping(value = "/user/list")
	public Result<?> queryPageUserList(WbPaymentTransaction wbPaymentTransaction,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		wbPaymentTransaction.setCreateBy(sysUser.getUsername());
		wbPaymentTransaction.setStatus("1");
		if(wbPaymentTransaction.getTimeEnd()!=null){
			wbPaymentTransaction.setTimeEnd(wbPaymentTransaction.getTimeEnd()+"*");
		}
		return queryPageList(wbPaymentTransaction,pageNo,pageSize,req);
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
