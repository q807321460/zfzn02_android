package com.jia.ezcamera.utils;


import android.content.Context;
import android.text.TextUtils;


import com.jia.znjj2.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * Created by ls on 15/2/5.
 */
public class StringUtils {

    public static String format(int i) {
        String s = "" + i;
        return i>=0&&s.length()<3?(s.trim().length() == 1)?"0"+s.trim():""+i:"00";
    }

    public static String format(String i) {
        return !TextUtils.isEmpty(i)?((i.trim().length() == 1)?"0"+i.trim():""+i):"00";
    }

    public static String formatAllTime(String i) {
        if (TextUtils.isEmpty(i)){
            return "00";
        }
        String[] starts = i.split(":");
        return (starts.length == 2)?format(starts[0])+":"+format(starts[1]):"00";
    }

    public static boolean checkPhoneNumber(String username) {
        boolean flag = false;
        if(username.length()!=11){
            return flag;
        }
        try {
            String check = "[0-9]*";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(username);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    public static boolean checkPassword(String username) {
        boolean flag = false;
        if(username.length()<6)
            return flag;
        try {
            String check = "^[a-zA-Z0-9/?%+/#@!&=-_]*$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(username);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    public static boolean checkDevPassword(String username) {
        boolean flag = false;
        if(username.length()<5)
            return flag;
        try {
            String check = "^[a-zA-Z0-9/?%+/#@!&=-_]*$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(username);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    public static boolean checkEmail(String email) {
        boolean flag = false;
        if(email.equals("")){
            return false;
        }
        try {
            String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(email);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    public static boolean checkUsername(String username) {
        // ^[a-zA-Z0-9_\s]*$
        boolean flag = false;
        if(username.length()<6||username.length()>18){
            return flag;
        }
        if (username != null && !"".equals(username)) {
            // try {
            // int i = Integer.parseInt(username);
            // return false;
            // } catch (Exception e) {
            // // TODO: handle exception
            // }
            if (checkFirstIfEng(username)) {
                try {
                    String check = "^[a-zA-Z0-9_]*$";
                    Pattern regex = Pattern.compile(check);
                    Matcher matcher = regex.matcher(username);
                    flag = matcher.matches();
                } catch (Exception e) {
                    flag = false;
                }
            }
        }
        return flag;
    }
    public static boolean checkFirstIfEng(String str) {
        boolean flag = false;
        try {
            String firstStr = str.substring(0, 1);
            Pattern regex = Pattern.compile("[a-zA-Z]+");
            Matcher matcher = regex.matcher(firstStr);
            flag = matcher.matches();
        } catch (Exception e) {
            // TODO: handle exception
            flag = false;
        }
        return flag;
    }
    
    
    /** 
     * �Ƚϰ汾�ŵĴ�С,ǰ�ߴ��򷵻�һ������,���ߴ󷵻�һ������,����򷵻�0 
     * @param version1 
     * @param version2 
     * @return 
     */  
    public static int compareVersion(String version1, String version2) {  
        if (version1 == null || version2 == null) {  
            return 0;
        }  
        String[] versionArray1 = version1.split("\\.");//ע��˴�Ϊ����ƥ�䣬������"."��  
        String[] versionArray2 = version2.split("\\.");

        int idx = 0;  
        int minLength = Math.min(versionArray1.length, versionArray2.length);//ȡ��С����ֵ

        int diff = 0;
//        while (idx < minLength
//                && (diff = versionArray1[idx].length() - versionArray2[idx].length()) == 0//�ȱȽϳ���
//                && (diff = versionArray1[idx].compareTo(versionArray2[idx])) == 0) {//�ٱȽ��ַ�
//            ++idx;
//
//        }

        while (idx < minLength
                && (diff = versionArray1[idx].compareTo(versionArray2[idx])) == 0) {//�ٱȽ��ַ�
            ++idx;

        }
        //����Ѿ��ֳ���С����ֱ�ӷ��أ����δ�ֳ���С�����ٱȽ�λ�������Ӱ汾��Ϊ��
        diff = (diff != 0) ? diff : versionArray1.length - versionArray2.length;  
        return diff;  
    }  
    
    
    
    public static boolean isNumeric(String str){
  	  for (int i = 0; i < str.length(); i++){
  	   System.out.println(str.charAt(i));
  	   if (!Character.isDigit(str.charAt(i))){
  	    return false;
  	   }
  	  }
  	  return true;
	}
    
    
    
    
    
    
    
    
    
    public static String getEventTypeString(Context myContext, int eventsType) {
        String retString = null;
        switch (eventsType) {
            case 1:
                retString = myContext
                        .getString(R.string.events_type0);
                break;
            case 10001:
                retString = myContext
                        .getString(R.string.events_type1);
                break;
            case 10002:
                retString = myContext
                        .getString(R.string.events_type2);
                break;
            case 10003:
                retString = myContext
                        .getString(R.string.events_type3);
                break;
            case 10004:
                retString = myContext
                        .getString(R.string.events_type4);
                break;
            case 10005:
                retString = myContext
                        .getString(R.string.events_type5);
                break;
            case 10006:
                retString = myContext
                        .getString(R.string.events_type6);
                break;
            case 10007:
                retString = myContext
                        .getString(R.string.events_type7);
                break;
            case 10008:
                retString = myContext
                        .getString(R.string.events_type8);
                break;
            case 10009:
                retString = myContext
                        .getString(R.string.events_type9);
                break;
            case 10010:
                retString = myContext.getString(
                        R.string.events_type10);
                break;
            case 10011:
                retString = myContext.getString(
                        R.string.events_type11);
                break;
            case 10012:
                retString = myContext.getString(
                        R.string.events_type12);
                break;
            case 10013:
                retString = myContext.getString(
                        R.string.events_type13);
                break;
            case 10014:
                retString = myContext.getString(
                        R.string.events_type14);
                break;
            case 10015:
                retString = myContext.getString(
                        R.string.events_type15);
                break;
            case 10016:
                retString = myContext.getString(
                        R.string.events_type16);
                break;
            case 10017:
                retString = myContext.getString(
                        R.string.events_type17);
                break;
            case 10018:
                retString = myContext.getString(
                        R.string.events_type18);
                break;
            case 10019:
                retString = myContext.getString(
                        R.string.events_type19);
                break;
            case 10020:
                retString = myContext.getString(
                        R.string.events_type20);
                break;
            case 10021:
                retString = myContext.getString(
                        R.string.events_type21);
                break;
            case 10022:
                retString = myContext.getString(
                        R.string.events_type22);
                break;
            case 10023:
                retString = myContext.getString(
                        R.string.events_type23);
                break;

            default:
                retString = myContext
                        .getString(R.string.events_type0);
                break;
        }
        return retString;
    }
}
