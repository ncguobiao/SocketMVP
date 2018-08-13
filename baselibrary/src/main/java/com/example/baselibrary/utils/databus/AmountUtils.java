package com.example.baselibrary.utils.databus;

import android.text.TextUtils;


import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * com.util.AmountUtils
 *
 * @author zcm0708@sina.com
 * @description 金额元分之间转换工具类
 * @2012-2-7下午12:58:00
 */

public class AmountUtils {
    /**
     * 正则表达式：验证手机号
     */
    public static final String REGEX_MOBILE = "^1(3|4|5|7|8)\\d{9}$";

    /**
     * 校验手机号
     *
     * @param mobile
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isMobile(String mobile) {
        return Pattern.matches(REGEX_MOBILE, mobile);
    }

    /**
     * 正则表达式:验证密码(不包含特殊字符)
     */
    public static final String REGEX_PASSWORD = "^[a-zA-Z0-9]{6,16}$";
    /**
     * 校验密码
     * @param pwd
     * @return
     */
    public static boolean checkPwd(String pwd) {
        if(TextUtils.isEmpty(pwd)) {
            return false;
        }
        return Pattern.matches(REGEX_PASSWORD, pwd);
    }

    /**
     * 金额为分的格式
     */
    public static final String CURRENCY_FEN_REGEX = "\\-?[0-9]+";

    /**
     * 将分为单位的转换为元并返回金额格式的字符串 （除100）
     *
     * @param amount
     * @return
     * @throws Exception
     */
    public static String changeF2Y(Long amount) throws Exception {
        if (!amount.toString().matches(CURRENCY_FEN_REGEX)) {
            throw new Exception("金额格式有误");
        }

        int flag = 0;
        String amString = amount.toString();
        if (amString.charAt(0) == '-') {
            flag = 1;
            amString = amString.substring(1);
        }
        StringBuffer result = new StringBuffer();
        if (amString.length() == 1) {
            result.append("0.0").append(amString);
        } else if (amString.length() == 2) {
            result.append("0.").append(amString);
        } else {
            String intString = amString.substring(0, amString.length() - 2);
            for (int i = 1; i <= intString.length(); i++) {
                if ((i - 1) % 3 == 0 && i != 1) {
                    result.append(",");
                }
                result.append(intString.substring(intString.length() - i, intString.length() - i + 1));
            }
            result.reverse().append(".").append(amString.substring(amString.length() - 2));
        }
        if (flag == 1) {
            return "-" + result.toString();
        } else {
            return result.toString();
        }
    }

    /**
     * 将分为单位的转换为元 （除100）
     *
     * @param amount
     * @return
     * @throws Exception
     */
    public static String changeF2Y(String amount) throws Exception {
        if (!amount.matches(CURRENCY_FEN_REGEX)) {
            throw new Exception("金额格式有误");
        }
        return BigDecimal.valueOf(Long.valueOf(amount)).divide(new BigDecimal(100)).toString();
    }

    /**
     * 将元为单位的转换为分 （乘100）
     *
     * @param amount
     * @return
     */
    public static String changeY2F(Long amount) {
        return BigDecimal.valueOf(amount).multiply(new BigDecimal(100)).toString();
    }

    /**
     * 将元为单位的转换为分 替换小数点，支持以逗号区分的金额
     *
     * @param amount
     * @return
     */
    public static String changeY2F(String amount) {
        String currency = amount.replaceAll("\\$|\\￥|\\,", "");  //处理包含, ￥ 或者$的金额
        int index = currency.indexOf(".");
        int length = currency.length();
        Long amLong = 0l;
        if (index == -1) {
            amLong = Long.valueOf(currency + "00");
        } else if (length - index >= 3) {
            amLong = Long.valueOf((currency.substring(0, index + 3)).replace(".", ""));
        } else if (length - index == 2) {
            amLong = Long.valueOf((currency.substring(0, index + 2)).replace(".", "") + 0);
        } else {
            amLong = Long.valueOf((currency.substring(0, index + 1)).replace(".", "") + "00");
        }
        return amLong.toString();
    }

    /**
        * 提供精确的乘法运算
        * @param v1
        * @param v2
        * @return 两个参数的数学积，以字符串格式返回
        */
     public static String multiply (String v1, String v2)throws Exception
      {
              BigDecimal b1 = new BigDecimal(v1);
              BigDecimal b2 = new BigDecimal(v2);
              return b1.multiply(b2).toString();
          }

    //比较金额大小
     public static int compare(String v1, String v2)
      {
              BigDecimal b1 = new BigDecimal(v1);
              BigDecimal b2 = new BigDecimal(v2);
              return b1.compareTo(b2);
          }


    //计算金额
    public static String mathMoney(String time, String yuan) throws Exception {
        float timeFloat = Float.valueOf(time);
        int i = (int) (timeFloat * 10) % 10 > 0 ? (int) timeFloat + 1 : (int) timeFloat;
        String timeInt = String.valueOf(i);
        return AmountUtils.multiply(timeInt, yuan);
    }


    //将long类型的时间格式化
    public static String getTimeFormat(long l){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        java.util.Date now = df.parse("2004-03-26 13:31:40");
//        java.util.Date date=df.parse("2004-01-02 11:30:24");
//        long l=now.getTime()-date.getTime();
        long day=l/(24*60*60*1000);
        long hour=(l/(60*60*1000)-day*24);
        long min=((l/(60*1000))-day*24*60-hour*60);
        long s=(l/1000-day*24*60*60-hour*60*60-min*60);
//        return ""+day+"天"+hour+"小时"+min+"分"+s+"秒";
        return ((day*24)+ hour)+"小时"+min+"分"+s+"秒";

    }


    private static final String HEXES = "0123456789ABCDEF";

    public static String byteArrayToHexString(final byte[] array) {
        final StringBuilder sb = new StringBuilder();
        boolean firstEntry = true;
        sb.append('[');

        for (final byte b : array) {
            if (!firstEntry) {
                sb.append(", ");
            }
            sb.append(HEXES.charAt((b & 0xF0) >> 4));
            sb.append(HEXES.charAt((b & 0x0F)));
            firstEntry = false;
        }

        sb.append(']');
        return sb.toString();
    }

//    /**
//     * 解析xml
//     * @param data
//     * @return
//     * @throws DocumentException
//     */
//    public static List<String> parseXML(String data) throws DocumentException {
//        List<String> images = new ArrayList<>();
//        Document doc = DocumentHelper.parseText(data);
//        Element rootElt = doc.getRootElement(); // 获取根节点
//        LogUtil.e("根节点：" + rootElt.getName()); // 拿到根节点的名称
//        Iterator iter = rootElt.elementIterator("advertisement"); //获取根节点下的子节点
//        while (iter.hasNext()) {
//            Element recordEle = (Element) iter.next();
//            Iterator iters = recordEle.elementIterator("high");
//            while (iters.hasNext()){
//                Element itemEle = (Element) iters.next();
//                String url = itemEle.elementTextTrim("url");
//                if(!StringUtils.isEmpty(url)) {
//                    images.add(url);
//                }
//                LogUtil.e("url:"+url);
//            }
//        }
//        return images;
//    }
}
