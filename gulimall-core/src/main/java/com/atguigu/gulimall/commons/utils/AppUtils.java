package com.atguigu.gulimall.commons.utils;

public class AppUtils {

    public static String arrayToStringWithSeperator(String[] arr,String sep){
        StringBuffer stringBuffer = new StringBuffer();

        if(arr != null && arr.length>0){
            for (String segment : arr) {
                stringBuffer.append(segment);
                stringBuffer.append(sep);
            }
        }
        //length -1 是索引位的最后一位，所以要-2
        String substring = stringBuffer.toString().substring(0, stringBuffer.length() - 2);

        return substring;
    }
}
