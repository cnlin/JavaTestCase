package com.aqa.test.string.charsetLength;

import java.io.UnsupportedEncodingException;

public class CharsetLength {
	
	public static void main(String[] args) throws UnsupportedEncodingException {
		
		byte[] utf8Cn = "中国".getBytes("UTF-8");
		System.out.printf("The lenght of 中国  in UTF-8 charset is %d ,content is [%s]\n",utf8Cn.length,utf8Cn[0]);
		
		byte[] gbkCn = "中国".getBytes("GBK");
		System.out.printf("The lenght of 中国  in GBK charset is %d ,content is [%s]\n",gbkCn.length,gbkCn[0]);
		
		System.out.println("========汉字在UTF-8中占用三个字节，在GBK中占用两个字节==========");
		
		byte[] utf8En = "abc".getBytes("UTF-8");
		System.out.println("The lenght of abc in UTF-8 charset is "+utf8En.length);
		
		byte[] gbkEn = "abc".getBytes("GBK");
		System.out.println("The lenght of abc in GBK charset is "+gbkEn.length);
		

	}
	

}
