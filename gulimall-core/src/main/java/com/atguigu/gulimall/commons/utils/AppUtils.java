package com.atguigu.gulimall.commons.utils;

public class AppUtils {

    public static String arrayToStringWithSeperator(String[] arr,String sep){
        StringBuffer stringBuffer = new StringBuffer();
        String substring = "";

        if(arr != null && arr.length>0){
            for (String segment : arr) {
                stringBuffer.append(segment);
                stringBuffer.append(sep);
            }

            // length -1 是索引位的最后一位，所以要-2,去掉最后一个sep
            // 但是subString是左闭右开的，-1即可
            substring = stringBuffer.toString().substring(0, stringBuffer.length() - 1);
        }


        return substring;
    }
}
