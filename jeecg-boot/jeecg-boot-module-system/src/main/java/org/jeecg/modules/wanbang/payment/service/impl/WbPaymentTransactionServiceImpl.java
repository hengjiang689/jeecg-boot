package org.jeecg.modules.wanbang.payment.service.impl;

import org.jeecg.modules.wanbang.payment.entity.WbPaymentTransaction;
import org.jeecg.modules.wanbang.payment.mapper.WbPaymentTransactionMapper;
import org.jeecg.modules.wanbang.payment.service.IWbPaymentTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 万邦支付交易表
 * @Author: jeecg-boot
 * @Date:   2019-11-15
 * @Version: V1.0
 */
@Service
public class WbPaymentTransactionServiceImpl extends ServiceImpl<WbPaymentTransactionMapper, WbPaymentTransaction> implements IWbPaymentTransactionService {

    @Autowired
    private WbPaymentTransactionMapper wbPaymentTransactionMapper;

    @Override
    public WbPaymentTransaction getByOutTradeNo(String outTradeNo) {
        return wbPaymentTransactionMapper.getByOutTradeNo(outTradeNo);
    }
}
