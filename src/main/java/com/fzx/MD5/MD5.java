package com.fzx.MD5;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5 {
    /**
     * @Description: 带秘钥加密
     * @Param: [text, key]
     * @return: java.lang.String
     * @Author: fuzhixuan
     * @Date: 2019/12/21
     */
    public static String md5Encryption(String text, String key) throws Exception {
        return DigestUtils.md5Hex(text + key);
    }

    /**
     * @Description: 不带秘钥加密
     * @Param: [text, key]
     * @return: java.lang.String
     * @Author: fuzhixuan
     * @Date: 2019/12/21
     */
    public static String md5Encryption(String text) throws Exception {
        return DigestUtils.md5Hex(text);
    }
}
