package me.the1withspaghetti.FooaahhAPI;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class Encryption {
	
	private static final String ALGO = "AES"; // Default uses ECB PKCS5Padding
	private static SecureRandom rand;
	
	public static String encrypt(String Data, String secret) 
			throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
		Key key = generateKey(secret);
		Cipher c = Cipher.getInstance(ALGO);
		c.init(Cipher.ENCRYPT_MODE, key);
		byte[] encVal = c.doFinal(Data.getBytes());
		String encryptedValue = Base64.getEncoder().encodeToString(encVal);
		return encryptedValue;
	}
	
	public static String decrypt(String strToDecrypt, String secret) 
			throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
		Key key = generateKey(secret);
		Cipher cipher = Cipher.getInstance(ALGO);
		cipher.init(Cipher.DECRYPT_MODE, key);
		return new String(cipher.doFinal(
				Base64.getDecoder().decode(strToDecrypt)));
	}
	
	private static Key generateKey(String secret) {
		byte[] decoded = Base64.getDecoder().decode(secret.getBytes());
		Key key = new SecretKeySpec(decoded, ALGO);
		return key;
	}
	
	public static String decodeKey(String str) {
		byte[] decoded = Base64.getDecoder().decode(str.getBytes());
		return new String(decoded);
	}
	
	public static String encodeKey(byte[] bytes) {
		byte[] encoded = Base64.getEncoder().encode(bytes);
		return new String(encoded);
	}
	
	public static String newBase64Key() throws NoSuchAlgorithmException {
		if (rand == null) rand = SecureRandom.getInstanceStrong();
        byte[] key = new byte[16];
        rand.nextBytes(key);
		return encodeKey(key);
	}
	
	public static void main(String a[]) throws Exception {
		String base64Key = newBase64Key();
		System.out.println("Base64Key: " + base64Key); // This needs to be sent to client
		//System.out.println("toDecodeBase64Key = "+decodeKey(encodedBase64Key));
		String toEncrypt = "42";
		System.out.println("Plain Text: " + toEncrypt);
		
		
		String encrStr = Encryption.encrypt(toEncrypt, base64Key);
		System.out.println("Encrypted Text: " + encrStr);
		
		
		String decrStr = Encryption.decrypt(encrStr, base64Key);
		System.out.println("Decrypted Text: " + decrStr);
	}
}
