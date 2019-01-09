package com.example.baselibrary.utils;


import android.text.TextUtils;

import com.orhanobut.logger.Logger;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dongdong on 2016/6/5.
 */
public class BleUtils {

    private static int[] g_CrcTable = {
            0x0000, 0x1021, 0x2042, 0x3063, 0x4084, 0x50A5, 0x60C6, 0x70E7,
            0x8108, 0x9129, 0xA14A, 0xB16B, 0xC18C, 0xD1AD, 0xE1CE, 0xF1EF,
            0x1231, 0x0210, 0x3273, 0x2252, 0x52B5, 0x4294, 0x72F7, 0x62D6,
            0x9339, 0x8318, 0xB37B, 0xA35A, 0xD3BD, 0xC39C, 0xF3FF, 0xE3DE,
            0x2462, 0x3443, 0x0420, 0x1401, 0x64E6, 0x74C7, 0x44A4, 0x5485,
            0xA56A, 0xB54B, 0x8528, 0x9509, 0xE5EE, 0xF5CF, 0xC5AC, 0xD58D,
            0x3653, 0x2672, 0x1611, 0x0630, 0x76D7, 0x66F6, 0x5695, 0x46B4,
            0xB75B, 0xA77A, 0x9719, 0x8738, 0xF7DF, 0xE7FE, 0xD79D, 0xC7BC,
            0x48C4, 0x58E5, 0x6886, 0x78A7, 0x0840, 0x1861, 0x2802, 0x3823,
            0xC9CC, 0xD9ED, 0xE98E, 0xF9AF, 0x8948, 0x9969, 0xA90A, 0xB92B,
            0x5AF5, 0x4AD4, 0x7AB7, 0x6A96, 0x1A71, 0x0A50, 0x3A33, 0x2A12,
            0xDBFD, 0xCBDC, 0xFBBF, 0xEB9E, 0x9B79, 0x8B58, 0xBB3B, 0xAB1A,
            0x6CA6, 0x7C87, 0x4CE4, 0x5CC5, 0x2C22, 0x3C03, 0x0C60, 0x1C41,
            0xEDAE, 0xFD8F, 0xCDEC, 0xDDCD, 0xAD2A, 0xBD0B, 0x8D68, 0x9D49,
            0x7E97, 0x6EB6, 0x5ED5, 0x4EF4, 0x3E13, 0x2E32, 0x1E51, 0x0E70,
            0xFF9F, 0xEFBE, 0xDFDD, 0xCFFC, 0xBF1B, 0xAF3A, 0x9F59, 0x8F78,
            0x9188, 0x81A9, 0xB1CA, 0xA1EB, 0xD10C, 0xC12D, 0xF14E, 0xE16F,
            0x1080, 0x00A1, 0x30C2, 0x20E3, 0x5004, 0x4025, 0x7046, 0x6067,
            0x83B9, 0x9398, 0xA3FB, 0xB3DA, 0xC33D, 0xD31C, 0xE37F, 0xF35E,
            0x02B1, 0x1290, 0x22F3, 0x32D2, 0x4235, 0x5214, 0x6277, 0x7256,
            0xB5EA, 0xA5CB, 0x95A8, 0x8589, 0xF56E, 0xE54F, 0xD52C, 0xC50D,
            0x34E2, 0x24C3, 0x14A0, 0x0481, 0x7466, 0x6447, 0x5424, 0x4405,
            0xA7DB, 0xB7FA, 0x8799, 0x97B8, 0xE75F, 0xF77E, 0xC71D, 0xD73C,
            0x26D3, 0x36F2, 0x0691, 0x16B0, 0x6657, 0x7676, 0x4615, 0x5634,
            0xD94C, 0xC96D, 0xF90E, 0xE92F, 0x99C8, 0x89E9, 0xB98A, 0xA9AB,
            0x5844, 0x4865, 0x7806, 0x6827, 0x18C0, 0x08E1, 0x3882, 0x28A3,
            0xCB7D, 0xDB5C, 0xEB3F, 0xFB1E, 0x8BF9, 0x9BD8, 0xABBB, 0xBB9A,
            0x4A75, 0x5A54, 0x6A37, 0x7A16, 0x0AF1, 0x1AD0, 0x2AB3, 0x3A92,
            0xFD2E, 0xED0F, 0xDD6C, 0xCD4D, 0xBDAA, 0xAD8B, 0x9DE8, 0x8DC9,
            0x7C26, 0x6C07, 0x5C64, 0x4C45, 0x3CA2, 0x2C83, 0x1CE0, 0x0CC1,
            0xEF1F, 0xFF3E, 0xCF5D, 0xDF7C, 0xAF9B, 0xBFBA, 0x8FD9, 0x9FF8,
            0x6E17, 0x7E36, 0x4E55, 0x5E74, 0x2E93, 0x3EB2, 0x0ED1, 0x1EF0};
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
        } else if (type == 4) {
            mask = 0x596A7164; //一键启动
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

    //售货柜 ,投币器加密专用
    public static byte checkSeekCode(byte[] bts) {
        byte checkCode = bts[0];
        for (int i = 1; i < bts.length; i++) {
            checkCode = (byte) (checkCode ^ bts[i]);
        }
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

    //int转byte
    public static byte[] int2Bytes(int value, int len) {
        byte[] b = new byte[len];
        for (int i = 0; i < len; i++) {
            b[len - i - 1] = (byte) ((value >> 8 * i) & 0xff);
        }
        return b;
    }

    //[FB,01,02,03,AB]
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

    //0XFB010203AB
    public static String byteToHexString(final byte[] array) {
        final StringBuilder sb = new StringBuilder();
        boolean firstEntry = true;
        sb.append("0x");
        for (final byte b : array) {
            sb.append(HEXES.charAt((b & 0xF0) >> 4));
            sb.append(HEXES.charAt((b & 0x0F)));
            firstEntry = false;
        }

        return sb.toString();
    }

//    //请求种子
//    public static byte[] requestSeed(byte device_type) {
//        byte b0 = (byte) Integer.parseInt("27", 16);
//        byte b1 = (byte) Integer.parseInt("01", 16);
//        byte b3 = (byte) Integer.parseInt("00", 16);
//        byte b4 = BleUtils.getCheckCode(new byte[]{b0, b1, device_type, b3});
//        byte[] value = new byte[]{b0, b1, device_type, b3, b4};
//        return value;
//    }
//
//    //校验密码
//    public static byte[] sendAndCheckSeed(byte[] keys,byte device_type) {
//        byte b0 = (byte) Integer.parseInt("27", 16);
//        byte b1 = (byte) Integer.parseInt("02", 16);
//        byte b3 = (byte) Integer.parseInt("04", 16);
//        byte b4 = keys[0];
//        byte b5 = keys[1];
//        byte b6 = keys[2];
//        byte b7 = keys[3];
//        byte b8 = BleUtils.getCheckCode(new byte[]{b0, b1, device_type, b3, b4, b5, b6, b7});
//        byte[] value = new byte[]{b0, b1, device_type, b3, b4, b5, b6, b7, b8};
//        return value;
//    }

    /**
     * CE查询设备时间
     *
     * @param way
     * @return
     */
    public static byte[] getCETime(String way, byte device_type) {
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
     *
     * @param way
     * @param action
     * @return
     */
    public static byte[] openCEAndCLoseDevice(String way, ACTION action, byte device_type) {
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
     *
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
     * 将两个byte数据转化为有符号int
     *
     * @param high : 高八位
     * @param low  : 低八位
     * @return
     */
    public static int twoByteToSignedInt(byte high, byte low) {
        return (high << 8) | low;
    }

    /**
     * 将两个byte数据转化为无符号int
     *
     * @param high : 高八位
     * @param low  : 低八位
     * @return
     */
    public static int twoByteToUnsignedInt(byte high, byte low) {
        return ((high << 8) & 0xffff) | (low & 0x00ff);
    }

    /**
     * 将int转换为两个byte
     *
     * @param numInt : 实际只取其中的低16位二进制数
     * @return 长度为2的byte数组 ，byte[0]为高8位，byte[1]为低八位
     */
    public static byte[] intToTwoByte(int numInt) {
        byte[] rest = new byte[2];
        if (numInt < -32768 || numInt > 32767) {
            return null;
        }
        rest[0] = (byte) (numInt >> 8);//高8位
        rest[1] = (byte) (numInt & 0x00ff);//低8位
        return rest;
    }


    public static int SSGetCrc16(byte[] mac) {
        return GetCrc16(mac, 0xABCD, g_CrcTable);
    }

    private static int GetCrc16(byte[] byteArrAddress,  int i, int[] g_crcTable) {
        int cRc_16 = i, temp;
        for (int j = 0; j <byteArrAddress.length; j++) {
            temp = cRc_16 & 0xFF;
            cRc_16 = (cRc_16 >> 8) ^ g_crcTable[(temp ^ byteArrAddress[j]) & 0xFF];
        }
        return cRc_16;
    }

    public static void makePackage(byte[] mac, int flag) {
        int tmp1, tmp2;
        tmp1 = (mac[0] << 24) + (mac[1] << 16) + (mac[2] << 8) + mac[3];
//        tmp2 = (CRYPT_FLAG << 16) + flag;
    }


    /**
     * 操作设备动作
     */
    public enum ACTION {
        OPEN,
        CLOSE
    }

    public enum DEVICE_TYPE {
        CE,
        CD
    }
}


