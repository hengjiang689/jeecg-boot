package org.jeecg.modules.wanbang.payment.controller;

import java.nio.charset.Charset;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
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

	@Autowired
	private IWbCourseHistoryService wbCourseHistoryService;

	@Autowired
	private IWbPaymentTransactionService wbPaymentTransactionService;

	@Autowired
	private IWbCourseService wbCourseService;

	@Autowired
	private ISysUserService sysUserService;

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
//			totalFee = membershipFee*100+"";
			totalFee = membershipFee+"";
		}else{
			WbCourse wbCourse = wbCourseService.getById(jsonObject.getBigInteger("courseId"));
			body="购买课程:"+wbCourse.getTitle();
			totalFee = Integer.parseInt(wbCourse.getPrice()*100+"")+"";
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
		wbPaymentTransaction.setCourseId(jsonObject.getString("courseId"));
		wbPaymentTransaction.setOutTradeNo(unifiedorder.getOut_trade_no());
		wbPaymentTransaction.setPlatform("0");
		wbPaymentTransaction.setPrepayId(unifiedorderResult.getPrepay_id());
		wbPaymentTransaction.setStatus("0");
		log.info("=total_fee=="+totalFee);
		wbPaymentTransaction.setTotalFee(Integer.getInteger(totalFee));
		wbPaymentTransaction.setTradeType(unifiedorder.getTrade_type());
		wbPaymentTransactionService.save(wbPaymentTransaction);
		if(tradeType.equalsIgnoreCase("APP")){
			return JsonUtil.toJSONString(PayUtil.generateMchAppData(unifiedorderResult.getPrepay_id(),unifiedorder.getAppid(),unifiedorder.getMch_id(),wxMchKey));
		}else{
			return PayUtil.generateMchPayJsRequestJson(unifiedorderResult.getPrepay_id(),unifiedorderResult.getAppid(),wxMchKey);
		}
	}

	@SneakyThrows
	@RequestMapping("/weixin/notify")
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
				log.info("=outTradeNo=="+mchPayNotify.getOut_trade_no());
				WbPaymentTransaction wbPaymentTransaction = wbPaymentTransactionService.getByOutTradeNo(mchPayNotify.getOut_trade_no());
				if(wbPaymentTransaction.getStatus().equalsIgnoreCase("0")){
					wbPaymentTransaction.setCashFee(mchPayNotify.getCash_fee());
					wbPaymentTransaction.setStatus("1");
					wbPaymentTransaction.setTimeEnd(mchPayNotify.getTime_end());
					wbPaymentTransaction.setTransactionId(mchPayNotify.getTransaction_id());
					wbPaymentTransactionService.updateById(wbPaymentTransaction);
					if(wbPaymentTransaction.getCourseId()!=null){
						WbCourseHistory wbCourseHistory = wbCourseHistoryService.selectHistoryByCourseId(wbPaymentTransaction.getCourseId());
						wbCourseHistory.setIsPaid("1");
						wbCourseHistoryService.updateById(wbCourseHistory);
					}else{
						SysUser sysUser = sysUserService.getUserByName(wbPaymentTransaction.getCreateBy());
						sysUser.setIsMember(true);
						sysUserService.updateById(sysUser);
					}
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
