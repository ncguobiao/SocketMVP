package com.example.baselibrary.utils;


import android.text.TextUtils;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dongdong on 2016/6/5.
 */
public class BleUtils {
    public static final byte EQUIP_TYPE_CE = (byte) Integer.parseInt("D1", 16);
    public static final byte EQUIP_TYPE_CD = (byte) Integer.parseInt("B1", 16);
    private static List<Byte> list;

    public static byte[] seedToKey(byte[] originSeeds, int type) {
        int[] seeds = new int[4];
        byte[] keys = new byte[4];
        for (int i = 0; i < originSeeds.length; i++) {
            seeds[i] = (int) originSeeds[i] & 0xff;
        }
        int mask = 0;
        if (type == 1) {
            mask = 0xA8CFE5CB; //保险箱
        } else if (type == 2) {
            mask = 0x56AFCA45; //大门锁
        } else if (type == 3) {
            mask = 0x13BCDFA6; //卷闸门
        }
        int bigSeed = (seeds[0] << 24) + (seeds[1] << 16) + (seeds[2] << 8) + seeds[3];
        for (int i = 0; i < 35; i++) {
            if ((bigSeed & 0x80000000) != 0) {
                bigSeed = bigSeed << 1;
                bigSeed = bigSeed ^ mask;
            } else {
                bigSeed = bigSeed << 1;
            }
        }
        keys[0] = (byte) ((bigSeed >> 24) & 0xFF);
        keys[1] = (byte) (((bigSeed & 0x00FFFFFF) >> 16) & 0xFF);
        keys[2] = (byte) (((bigSeed & 0x0000FFFF) >> 8) & 0xFF);
        keys[3] = (byte) ((bigSeed & 0x000000FF) & 0xFF);

        return keys;
    }

    public static byte getCheckCode(byte[] bts) {
        byte checkCode = 0;
        for (int i = 0; i < bts.length; i++) {
            checkCode += bts[i];
        }
        checkCode = (byte) (~(checkCode & 0xFF));
        return checkCode;
    }

    public static byte checkCode(List<Byte> list) {
        byte checkCode = 0;
        for (int i = 0; i < list.size(); i++) {
            checkCode += list.get(i);
        }
        checkCode = (byte) (~(checkCode & 0xFF));
        return checkCode;
    }

    public static boolean checkReceiver(byte[] receiveValue) {
        if (list == null) {
            list = new ArrayList<>();
        } else {
            list.clear();
        }
        for (int i = 0; i < receiveValue.length - 1; i++) {
            list.add(receiveValue[i]);
        }
        byte checkCode = BleUtils.checkCode(list);
        if (checkCode == receiveValue[receiveValue.length - 1]) {
            return true;
        }
        return false;
    }

    public static String formatAddress(String address) {
//        String str = "";
//        String[] split1 = address.split(":");
//        for (String s : split1) {
//            str += s;
//        }
//        char[] addrChars = str.toCharArray();
        char[] addrChars = address.toCharArray();
        StringBuilder addrBuilder = new StringBuilder();
        for (int i = 0; i < addrChars.length; i++) {
            addrBuilder.append(addrChars[i]);
            if (i == 1 || i == 3 || i == 5 || i == 7 || i == 9) {
                addrBuilder.append(":");
            }
        }
        //正序
//        String[] split = addrBuilder.toString().split(":");
//        String formatAddress = split[5] + ":" + split[4] + ":" + split[3] + ":" + split[2] + ":" + split[1] + ":" + split[0];
//        return formatAddress;
        return addrBuilder.toString();
    }

    public static byte[] getByteArrAddress(String address) {
//        String formatAddress = formatAddress(address);
        String[] split = address.split(":");
        byte[] macBytes = new byte[6];
        for (int i = 0; i < split.length; i++) {
            macBytes[i] = (byte) Integer.parseInt(split[5 - i], 16);
        }
        return macBytes;
    }

    //    public static Comparator<CallBackEquip> equipComparator = new Comparator<CallBackEquip>() {
//        @Override
//        public int compare(CallBackEquip equip1, CallBackEquip equip2) {
//            if (equip1.getRssi() < equip2.getRssi()) {
//                return 1;
//            } else if (equip1.getRssi() > equip2.getRssi()) {
//                return -1;
//            } else {
//                return 0;
//            }
//        }
//    };
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


    //请求种子
    public static byte[] requestSeed(byte device_type) {
        byte b0 = (byte) Integer.parseInt("27", 16);
        byte b1 = (byte) Integer.parseInt("01", 16);
        byte b3 = (byte) Integer.parseInt("00", 16);
        byte b4 = BleUtils.getCheckCode(new byte[]{b0, b1, device_type, b3});
        byte[] value = new byte[]{b0, b1, device_type, b3, b4};
        return value;
    }

    //校验密码
    public static byte[] sendAndCheckSeed(byte[] keys,byte device_type) {
        byte b0 = (byte) Integer.parseInt("27", 16);
        byte b1 = (byte) Integer.parseInt("02", 16);
        byte b3 = (byte) Integer.parseInt("04", 16);
        byte b4 = keys[0];
        byte b5 = keys[1];
        byte b6 = keys[2];
        byte b7 = keys[3];
        byte b8 = BleUtils.getCheckCode(new byte[]{b0, b1, device_type, b3, b4, b5, b6, b7});
        byte[] value = new byte[]{b0, b1, device_type, b3, b4, b5, b6, b7, b8};
        return value;
    }

    /**
     * CE查询设备时间
     *
     * @param way
     * @return
     */
    public static byte[] getCETime(String way,byte device_type) {
        byte b0 = (byte) Integer.parseInt("27", 16);
        byte b1 = (byte) Integer.parseInt("22", 16);
        byte b3 = (byte) Integer.parseInt("01", 16);
        byte b4 = Byte.parseByte(way);
        byte b5 = BleUtils.getCheckCode(new byte[]{b0, b1, device_type, b3, b4});
        byte[] value = new byte[]{b0, b1, device_type, b3, b4, b5};
        return value;
    }


    /**
     * CE打开或者关闭设备
     * @param way
     * @param action
     * @return
     */
    public static byte[] openCEAndCLoseDevice(String way, ACTION action,byte device_type) {

        try {
            byte b0 = (byte) Integer.parseInt("27", 16);
            byte b1 = (byte) Integer.parseInt("21", 16);
            byte b3 = (byte) Integer.parseInt("02", 16);
            if (TextUtils.isEmpty(way)) {
                return null;
            }
            byte b4 = Byte.parseByte(way);//路数
//            byte b5 = 1;//开启
            byte b5;
            switch (action) {
                case OPEN:
                    b5 = 1;
                    break;
                case CLOSE:
                    b5 = 0;
                    break;
                default:
                    b5 = 1;
                    break;
            }
            byte b6 = BleUtils.getCheckCode(new byte[]{b0, b1, device_type, b3, b4, b5});
            byte[] value = new byte[]{b0, b1, device_type, b3, b4, b5, b6};
            return value;
        } catch (Exception e) {
            Logger.e("打开或关操作转换异常：" + e.toString());
            return null;
        }
    }


    /**
     * CE打开操作
     *
     * @param time
     * @param way
     * @param equipElectiic
     * @return
     */
    public static byte[] openCEBleDevice(int time, String way, String equipElectiic) {
        if (TextUtils.isEmpty(equipElectiic)) {
            equipElectiic = "2000";
        }
        try {
            int integerValue = Integer.valueOf(equipElectiic);
            byte b0 = (byte) Integer.parseInt("27", 16);
            byte b1 = (byte) Integer.parseInt("10", 16);
            byte b3 = (byte) Integer.parseInt("08", 16);
            if (TextUtils.isEmpty(way)) {
                return null;
            }
            byte b4 = (byte) Integer.parseInt(way);//路数
//            byte b5 = 1;//开启
            byte b5 = (byte) (time >> 8);//时间
            byte b6 = (byte) time;//时间
            byte b7 = (byte) (integerValue >> 8);//电流
            byte b8 = (byte) integerValue;//电流
            byte b9 = (byte) (1000 >> 8);//过流
            byte b10 = (byte) 1000;
            byte b11 = (byte) Integer.parseInt("12", 16);
            byte b12 = (byte) Integer.parseInt("34", 16);
            byte b13 = (byte) Integer.parseInt("56", 16);
            byte b14 = (byte) Integer.parseInt("78", 16);
            byte b15 = BleUtils.getCheckCode(new byte[]{b0, b1, EQUIP_TYPE_CE, b3, b4, b5, b6, b7, b8, b9, b10, b11, b12, b13, b14});
            byte[] value = new byte[]{b0, b1, EQUIP_TYPE_CE, b3, b4, b5, b6, b7, b8, b9, b10, b11, b12, b13, b14, b15};
            return value;
        } catch (Exception e) {
            Logger.e("打开操作转换异常：" + e.toString());
            return null;
        }
    }




    /**
     * 计算两个直接数据
     * @param one
     * @param two
     * @return
     */
    public static Integer formatTwoData(byte one, byte two) {
        String electricity1 = Integer.toHexString(one & 0XFF);
        String electricity2 = Integer.toHexString(two & 0XFF);
        if (electricity1.length() == 1) {
            electricity1 = "0" + electricity1;
        }
        if (electricity2.length() == 1) {
            electricity2 = "0" + electricity2;
        }
        return Integer.parseInt(
                electricity1 + electricity2, 16);
    }

    /**
     * 操作设备动作
     */
    public enum ACTION {
        OPEN,
        CLOSE
    }

    public enum DEVICE_TYPE{
        CE,
        CD
    }



}
