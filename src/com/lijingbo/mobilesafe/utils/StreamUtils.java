package com.lijingbo.mobilesafe.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtils {

	public static String readFromStream(InputStream inputStream)
			throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] b = new byte[1024];
		int len;
		while ((len = inputStream.read(b)) != -1) {
			baos.write(b, 0, len);

		}

		String result = baos.toString();
		baos.close();
		return result;
	}

}
