package com.amalto.webapp.core.util;

import java.io.UnsupportedEncodingException;

import javax.crypto.Cipher;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.SecretKeySpec;

public class Crypt {

	private SecretKeySpec keySpec;

	private byte[] key;

	private String algorithm;
	
		
	public static Crypt getDESCryptInstance(String sharedSecret) throws ShortBufferException{
		byte[] key = new byte[8];
		byte[] bytes ;
		try {
			bytes = sharedSecret.getBytes("utf-8");
		} catch (UnsupportedEncodingException uee) {
			throw new ShortBufferException("The shared secret cannot be used as a key");
		}
		if (bytes.length<8) throw new ShortBufferException("The shared secret is too short");
		System.arraycopy(bytes, 0, key, 0, 8);
		return new Crypt(key,"DES");
	}

	/** Creates a new instance of Crypt */
	public Crypt(byte[] key, String algorithm) {
		this.key = key;
		this.algorithm = algorithm;
		this.keySpec = new SecretKeySpec(this.key, this.algorithm);
	}

	/**
	 * Encrypts the given String to a hex representation
	 */
	public String encryptHexString(String text) {
		return toHex(encryptString(text));
	}

	/**
	 * Decrypts the given hex representation
	 */
	public String decryptHexString(String text) {
		return decryptString(toByteArray(text));
	}
	
	/** Encrypts the give String to an array of bytes */
	private byte[] encryptString(String text) {
		try {
			Cipher cipher = Cipher.getInstance(this.algorithm);
			cipher.init(Cipher.ENCRYPT_MODE, this.keySpec);
			return cipher.doFinal(text.getBytes());
		} catch (Exception e) {
			return null;
		}
	}

	/** Decrypts the given array of bytes to a String */
	private String decryptString(byte[] b) {
		try {
			Cipher cipher = Cipher.getInstance(this.algorithm);
			cipher.init(Cipher.DECRYPT_MODE, this.keySpec);
			return new String(cipher.doFinal(b));
		} catch (Exception e) {
			return null;
		}
	}



	/** Converts the given array of bytes to a hex String */
	public static String toHex(byte[] buf) {
		char[] cbf = new char[buf.length * 2];
		for (int jj = 0, kk = 0; jj < buf.length; jj++) {
			cbf[kk++] = "0123456789ABCDEF".charAt((buf[jj] >> 4) & 0x0F);
			cbf[kk++] = "0123456789ABCDEF".charAt(buf[jj] & 0x0F);
		}
		return new String(cbf);
	}

	/** Converts a valid hex String to an array of bytes */
	public static byte[] toByteArray(String hex) {
		byte[] result = new byte[hex.length() / 2];
		for (int jj = 0, kk = 0; jj < result.length; jj++) {
			result[jj] = (byte) (("0123456789ABCDEF".indexOf(hex.charAt(kk++)) << 4) + "0123456789ABCDEF".indexOf(hex.charAt(kk++)));
		}
		return result;
	}
	
	public static void main(String[] args) {
		String s = "hello world";
		try {
			System.out.println("Original: "+s);
			Crypt c = Crypt.getDESCryptInstance("$£*µéàçè");
			String hex = c.encryptHexString(s);
			System.out.println("Encrypted hex: "+hex);
			String decypted = c.decryptHexString(hex);
			System.out.println("Decrypted: "+decypted);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
