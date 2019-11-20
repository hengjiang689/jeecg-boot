package org.jeecg.modules.wanbang.finance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 万邦提款信息表
 * @Author: jeecg-boot
 * @Date:   2019-11-19
 * @Version: V1.0
 */
@Data
@TableName("wb_finance_withdraw")
public class WbFinanceWithdraw implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ID_WORKER_STR)
    private String id;
	/**创建人*/
	@Excel(name = "创建人", width = 15)
    private String createBy;
	/**创建日期*/
	@Excel(name = "创建日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createTime;
	/**更新人*/
	@Excel(name = "更新人", width = 15)
    private String updateBy;
	/**更新日期*/
	@Excel(name = "更新日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
	/**所属部门*/
	@Excel(name = "所属部门", width = 15)
    private String sysOrgCode;
	/**金额*/
	@Excel(name = "金额", width = 15)
    private Double amount;
	/**状态*/
	@Excel(name = "状态", width = 15)
    private String status;
	/**用户id*/
	@Excel(name = "用户id", width = 15)
	private String userId;
	/**用户名*/
	@Excel(name = "用户名", width = 15)
    private String username;
	/**真实姓名*/
	@Excel(name = "真实姓名", width = 15)
	private String realname;
	/**类型*/
	@Excel(name = "类型", width = 15)
	private String type;
	/**身份证号*/
	@Excel(name = "身份证号", width = 15)
	private String identityNo;
	/**银行卡类型*/
	@Excel(name = "银行卡类型", width = 15)
	private String cardType;
	/**银行卡号*/
	@Excel(name = "银行卡号", width = 15)
	private String cardNo;
	/**绑定手机号*/
	@Excel(name = "绑定手机号", width = 15)
	private String cardPhone;
	/**开户行*/
	@Excel(name = "开户行", width = 15)
	private String bankName;



}
