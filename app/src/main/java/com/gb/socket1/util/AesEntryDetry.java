package com.gb.socket1.util;

import java.util.Arrays;
import java.util.Formatter;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
 
public class AesEntryDetry {
 
	// 加密方法
	private static byte[] encrypt(byte[] key, byte[] clear) throws Exception {
		SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
		Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		byte[] encrypted = cipher.doFinal(clear);
		return encrypted;
	}
 
	// 解密方法
	private static byte[] decrypt(byte[] key, byte[] encrypted)
			throws Exception {
		SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
		Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
		cipher.init(Cipher.DECRYPT_MODE, skeySpec);
		byte[] decrypted = cipher.doFinal(encrypted);
		return decrypted;
	}
 
	// 字节数组按照一定格式转换拼装成字符串用于打印显示
	private static String BytetohexString(byte[] b) {
		int len = b.length;
		StringBuilder sb = new StringBuilder(b.length * (2 + 1));
		Formatter formatter = new Formatter(sb);
 
		for (int i = 0; i < len; i++) {
			if (i < len - 1)
				formatter.format("0x%02X:", b[i]);
			else
				formatter.format("0x%02X", b[i]);
 
		}
		formatter.close();
 
		return sb.toString();
	}
	
	public static String getPassword(String aesKey,String sourceBuf) throws Exception {
		byte[] aesKeyArr = new byte[16];
		byte[] sourceBufArr = new byte[16];
		Arrays.fill(aesKeyArr,(byte)0xff);
		Arrays.fill(sourceBufArr,(byte)0xff);
		System.arraycopy(aesKey.getBytes(), 0, aesKeyArr,0,aesKey.getBytes().length);
		System.arraycopy(sourceBuf.getBytes(), 0, sourceBufArr,0,sourceBuf.getBytes().length);
		byte[] encryBuf = encrypt(aesKeyArr, sourceBufArr);
		return ParseSystemUtil.parseByte2HexStr(encryBuf).substring(0,16);
	}
	
	
	public static void main(String[] args) throws Exception {
//		System.out.println("insert into t_device(id,device_name,mac,password,team_id,status,device_type,times,device_times,alias,create_user,create_date,update_user,update_date)values('CDX1000000057koQSHIk','SF2350A000047','F2350A000047','"+getPassword("sensor668", "F2350A000047")+"','TEM1000000013koQSHIk','0','cdx',0,'0','测试设备47','MAG1000000001koQSHIk',NOW(),'MAG1000000001koQSHIk',NOW());");
		System.out.println("F2350A000199:"+getPassword("sensor668","F2350A000199"));
		System.out.println("F2350A000200:"+getPassword("sensor668","F2350A000200"));
	}
}

