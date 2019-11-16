package org.jeecg.modules.wanbang.payment.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CallBackResult {
    private String return_code;
    private String return_msg;
}
