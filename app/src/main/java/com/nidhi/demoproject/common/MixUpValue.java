package com.nidhi.demoproject.common;

import android.util.Base64;


import com.nidhi.demoproject.utils.TraceUtils;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class MixUpValue {

	public String getValues(String id) {

		int length = id.length();
		String val = "";

		for (int i = 1; i <= length; i++) {
			if (i % 2 != 0) {
				val = val + id.charAt(i - 1);
			}
		}

		return val;
	}

	public final String encryption(final String string) {
		try {

			MessageDigest digest = MessageDigest.getInstance("SHA-512");
			byte[] hash = digest.digest(string.getBytes("UTF-8"));

			StringBuffer hexString = new StringBuffer();

			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}

			return hexString.toString();

		} catch (Exception e) {
			TraceUtils.logException(e);
		}
		return "";
	}

	private static String seed = "I AM UNBREAKABLE";

	public static String encrypt(String clearText) {
		byte[] encryptedText = null;
		try {
			byte[] keyData = seed.getBytes();
			SecretKey ks = new SecretKeySpec(keyData, "AES");
			Cipher c = Cipher.getInstance("AES");
			c.init(Cipher.ENCRYPT_MODE, ks);
			encryptedText = c.doFinal(clearText.getBytes("UTF-8"));
			return Base64.encodeToString(encryptedText, Base64.DEFAULT);
		} catch (Exception e) {
			return null;
		}
	}

	public static String decrypt (String encryptedText) {
		byte[] clearText = null;
		try {
			byte[] keyData = seed.getBytes();
			SecretKey ks = new SecretKeySpec(keyData, "AES");
			Cipher c = Cipher.getInstance("AES");
			c.init(Cipher.DECRYPT_MODE, ks);
			clearText = c.doFinal(Base64.decode(encryptedText, Base64.DEFAULT));
			return new String(clearText, "UTF-8");
		} catch (Exception e) {
			return null;
		}
	}
}
