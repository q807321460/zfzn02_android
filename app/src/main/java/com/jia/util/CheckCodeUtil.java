package com.jia.util;

/**
 * Created by Administrator on 2017/3/17.
 */
public class CheckCodeUtil {

    /**
     * 指令格式<AAA00FF00XH**********FF>
     * 校验1~length-1
     * @param code
     * @return
     */
    public static String addCheckCode(String code){
        String checkCode = getCheckCode(code.substring(1,code.length()-3));
        return code.substring(0,code.length()-3)+checkCode+">";
    }

    public static String getCheckCode(String code){
        int result = 0;
        char[] cs = code.toCharArray();
        for (char c : cs) {
            result += c;
        }
        String checkCode = Integer.toHexString(result).toUpperCase();
        System.out.println(checkCode);
        if(checkCode.length()>=2){
            return checkCode.substring(checkCode.length()-2,checkCode.length());
        }else {
            return "0"+checkCode;
        }

    }


    public static boolean testCheckCode(String code){
        String checkCode = getCheckCode(code.substring(1,code.length()-3));
        if(checkCode.equals(code.substring(code.length()-3,code.length()-1))){
            return true;
        }
        return false;
    }
}
