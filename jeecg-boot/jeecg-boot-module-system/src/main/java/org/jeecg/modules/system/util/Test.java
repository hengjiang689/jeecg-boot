package org.jeecg.modules.system.util;

import java.util.UUID;

import static cn.hutool.crypto.SecureUtil.md5;

public class Test {
    public static void main(String[] args){
        System.out.println(UUID.randomUUID().toString().replace("o","").replace("0","").substring(0,6).toUpperCase());
    }
}
