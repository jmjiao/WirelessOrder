package com.wirelessorder.util;

import org.kobjects.base64.Base64;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Base64ToImage {
	public static Bitmap GenerateImage(String imgStr) {// 对字节数组字符串进行Base64解码并生成图片
		if (imgStr == null)// 图像数据为空
			return null;
		try {
			byte[] bytes = Base64.decode(imgStr);// Base64解码
			for (int i = 0; i < bytes.length; ++i) {
				if (bytes[i] < 0) {// 调整异常数据
					bytes[i] += 256;
				}
			}
			return getPicFromBytes(bytes);
		} catch (Exception e) {
			return null;
		}
	}

	public static Bitmap getPicFromBytes(byte[] bytes) {
		if (bytes.length != 0) {
			return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		} else {
			return null;
		}

	}
}
