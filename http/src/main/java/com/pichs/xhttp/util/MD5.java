package com.pichs.xhttp.util;

import android.os.Build;
import android.util.Base64;

import androidx.annotation.RequiresApi;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * md5 tool class
 * @author Shijie.Wei
 *
 */
@RequiresApi(api = Build.VERSION_CODES.FROYO)
public class MD5 {
	public synchronized static final String encode2Base64(String data) {
		byte[] b = null;
		try {
			b = data.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String s = null;
		if (b != null) {
			s = Base64.encodeToString(b, Base64.NO_WRAP);
		}
		return s;
	}

	public synchronized static final String encode2Hex(String data) {
		MessageDigest digest = null;
		if (digest == null) {
			try {
				digest = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}
		try {
			digest.update(data.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return encodeHex(digest.digest());
	}

	private final static String encodeHex(byte[] bytes) {
		StringBuffer buf = new StringBuffer(bytes.length * 2);
		int i;
		for (i = 0; i < bytes.length; i++) {
			if (((int) bytes[i] & 0xff) < 0x10) {
				buf.append("0");
			}
			buf.append(Long.toString((int) bytes[i] & 0xff, 16));
		}
		return buf.toString();
	}
}
