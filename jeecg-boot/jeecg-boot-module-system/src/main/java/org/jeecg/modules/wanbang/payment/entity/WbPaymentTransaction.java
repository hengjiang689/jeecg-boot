package org.jeecg.modules.wanbang.payment.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecg.common.aspect.annotation.Dict;

/**
 * @Description: 万邦支付交易表
 * @Author: jeecg-boot
 * @Date:   2019-11-15
 * @Version: V1.0
 */
@Data
@TableName("wb_payment_transaction")
public class WbPaymentTransaction implements Serializable {
    private static final long serialVersionUID = 1L;
    
	/**主键*/
	@TableId(type = IdType.ID_WORKER_STR)
    private java.lang.String id;
	/**创建人*/
	@Excel(name = "创建人", width = 15)
    private java.lang.String createBy;
	/**创建日期*/
	@Excel(name = "创建日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private java.util.Date createTime;
	/**更新人*/
	@Excel(name = "更新人", width = 15)
    private java.lang.String updateBy;
	/**更新日期*/
	@Excel(name = "更新日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private java.util.Date updateTime;
	/**所属部门*/
	@Excel(name = "所属部门", width = 15)
    private java.lang.String sysOrgCode;
	/**商品描述*/
	@Excel(name = "商品描述", width = 15)
    private java.lang.String body;
	/**状态*/
	@Excel(name = "状态", width = 15)
    private java.lang.Integer status;
	/**订单优惠标记*/
	@Excel(name = "订单优惠标记", width = 15)
    private java.lang.String goodsTag;
	/**商户订单号*/
	@Excel(name = "商户订单号", width = 15)
    private java.lang.String outTradeNo;
	/**预支付交易会话标识*/
	@Excel(name = "预支付交易会话标识", width = 15)
    private java.lang.String prepayId;
	/**交易类型*/
	@Excel(name = "交易类型", width = 15)
    private java.lang.String tradeType;
	/**标价金额*/
	@Excel(name = "标价金额", width = 15)
    private java.lang.Integer totalFee;
	/**现金金额*/
	@Excel(name = "现金金额", width = 15)
    private java.lang.Integer cashFee;
	/**交易ID*/
	@Excel(name = "交易ID", width = 15)
    private java.lang.String transactionId;
	/**交易结束时间*/
    private String timeEnd;
	/**支付平台*/
	@Excel(name = "支付平台", width = 15)
    private java.lang.Integer platform;
	/**课程ID*/
	@Excel(name = "课程ID", width = 15)
    private java.lang.String courseId;
}
