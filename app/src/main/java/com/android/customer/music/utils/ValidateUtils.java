package com.android.customer.music.utils;

import java.util.regex.Pattern;

/**
 * Description: 校验类
 * Created by gy(1984629668@qq.com)
 * Created Time on 2019/12/8 11:24
 */
public class ValidateUtils {
    private static final String PATTERN_IDCARD = "([0-9]{17}([0-9]|X|x))|([0-9]{15})";

    /**
     * 判断身份证位数或格式的正确性
     *
     * @param idNumber
     * @return
     */

    public static boolean checkIdNumber(String idNumber) {
        return Pattern.matches(PATTERN_IDCARD, idNumber);
    }
}
