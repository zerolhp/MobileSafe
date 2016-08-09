package com.lhp.mobilesafe.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtil {

	public static String Stream2String(InputStream is) {
		// 使用ByteArrayOutputStream作为输入流，应为其包含toString()。
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		// 写操作需要缓存数组
		byte[] buffer = new byte[1024];
		int temp = -1;
		try {
			while ((temp = is.read()) != -1) { // 读到输入流尾
				bos.write(buffer, 0, temp); // 根据实际读取长度写入
			}
			return bos.toString(); // 返回输出流的String类型
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
				}if (bos != null) {
					bos.close();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
