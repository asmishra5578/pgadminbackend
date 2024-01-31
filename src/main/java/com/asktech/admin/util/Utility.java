package com.asktech.admin.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;

import com.asktech.admin.dto.utility.ErrorResponseDto;
import com.asktech.admin.enums.FormValidationExceptionEnums;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Utility {
	
	
	/**@author abhimanyu-kumar*/
	public static long getDistributorID() {
		// 12 digits.

		long id = Long.parseLong(String.valueOf(System.currentTimeMillis()).substring(1, 13));
		inc = (inc + 1) % 10;
		return id;
	}
	 public static long StringToLong (String numberAsString) {
	    	
	    	long longValue1 = 0;
	    	//long longvalue2 = 0;
	        try {
	        	longValue1 = Long.parseLong(numberAsString);
	        	//longvalue2 = Long.valueOf(numberAsString).longValue();
	           System.out.println("long value1 is  = " + longValue1);
	           //System.out.println("long value2 is  = " + longvalue2);
	        } catch (NumberFormatException e) {
	           System.out.println("NumberFormatException: " + e.getMessage());
	        }
	        //return longvalue2;
			return longValue1;
	    }
	public static String convertIndianRupeeToPaise(String stringValue) {
		
		

		if(stringValue != null){
			BigDecimal amount = new BigDecimal(stringValue);
			amount = amount.multiply(new BigDecimal(100));
			stringValue = amount.toString();
		}
		//System.out.println(stringValue + "  "+convertIndianPaiseToRupee(stringValue));
		return stringValue;
	}
	
	public static String convertIndianPaiseToRupee(String stringValue) {

		if(stringValue != null){
			BigDecimal amount = new BigDecimal(stringValue);
			amount = amount.divide(new BigDecimal(100));
			stringValue=amount.toString();
		}
		return stringValue;
	}
	
	 public static boolean validateJavaDateFormat(String strDate){
	 	
	 /*Set preferred date format,For example yyyy-mm-dd*/
	    	
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd");
		simpleDateFormat.setLenient(false);
		//Create Date object parse the string into date
		//If a string will be converted into a date object then string is valid and contain a valid date value in desire format
		try {
			Date javaDate = simpleDateFormat.parse(strDate.trim());
			System.out.println(strDate + " is valid date format  : "+ javaDate);
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}

	
		return true;
	 }
	 public static String UsingJava8_GeneratingRandomAlphanumericString() {
	    	//The ASCII value of the lowercase alphabet is from 97 to 122. And, the ASCII value of the uppercase alphabet is from 65 to 90.
	    	//It can be observed that ASCII value of digits [0 – 9] ranges from [48 – 57].
	        int leftLimit = 48; // numeral '0'
	        int rightLimit = 122; // letter 'z'
	        int targetStringLength = 10;
	        Random random = new Random();

	        String generatedString = random.ints(leftLimit, rightLimit + 1)
	          .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
	          .limit(targetStringLength)
	          .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
	          .toString();

	        System.out.println(generatedString);
	        return generatedString;
	    }
	 public static Map<Object, Object> AES256EncryptionDecryption(String userString) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException {
		 
		// 1.
			KeyGenerator AES256keyGenrator = KeyGenerator.getInstance("AES");
			AES256keyGenrator.init(256);// 128bits
			SecretKey secretKeyByAES256 = AES256keyGenrator.generateKey();
			byte[] secretKeyByAES256ByteArray = secretKeyByAES256.getEncoded();
			String secretKeyByAES256ByteArrayTOString = Base64.getEncoder().encodeToString(secretKeyByAES256ByteArray);
			System.out.println(secretKeyByAES256ByteArrayTOString);
			
			// 2. Encryption Data process
			
			Cipher encryptionCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			
			IvParameterSpec ivParameterSpec = new IvParameterSpec(new byte[16]);
			
			encryptionCipher.init(Cipher.ENCRYPT_MODE,secretKeyByAES256,ivParameterSpec);
			//String userString = "Java8SpringBootSpringFrameworkJPA";
			
	        //byte[] encryptedDataBytesArray = encryptionCipher.doFinal((userString+"@"+123).getBytes());
	        byte[] encryptedDataBytesArray = encryptionCipher.doFinal((userString+UsingJava8_GeneratingRandomAlphanumericString()).getBytes());
	        String encryptedDataBytesArrayToString = Base64.getEncoder().encodeToString(encryptedDataBytesArray);
	        
	        Map<Object, Object> encryptedAnddecryptedDataMap = new HashMap<>();
	        encryptedAnddecryptedDataMap.put("EncryptedData", encryptedDataBytesArrayToString);
	        System.out.println("Encrypted Data using AES256 is = " + encryptedDataBytesArrayToString);
	        
	      // 3. Decryption Data process
	        Cipher decryptionCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	        decryptionCipher.init(Cipher.DECRYPT_MODE,secretKeyByAES256,ivParameterSpec);
	        byte[] decryptedDataBytesArray = decryptionCipher.doFinal(encryptedDataBytesArray);
	        String decryptedDataBytesArrayToString = new String(decryptedDataBytesArray);
	        encryptedAnddecryptedDataMap.put("DecryptedData", decryptedDataBytesArrayToString);
	        System.out.println("Decrypted Data using AES256 is " + decryptedDataBytesArrayToString);
		 
		 
		 return encryptedAnddecryptedDataMap;
	 }
	 
	 public static Map<Object, Object> RSAEncryptionDecryption(String userString) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		 
			// 1.
			KeyPairGenerator rsaKeyPairGenerator = KeyPairGenerator.getInstance("RSA");
			
			// 2.
			SecureRandom secureRandom = new SecureRandom();
			rsaKeyPairGenerator.initialize(2048,secureRandom);
			KeyPair keyPair = rsaKeyPairGenerator.generateKeyPair();
			
			
			// 3.
			
			PublicKey publicKey = keyPair.getPublic();
			String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
			System.out.println("Public Key is : "+ publicKeyString);
			
			// 4.
			
			PrivateKey privateKey = keyPair.getPrivate();
			String privateKeyString = Base64.getEncoder().encodeToString(privateKey.getEncoded());
			System.out.println("Private Key is : " + privateKeyString);
			
			// 5.
			
			Cipher encryptionCipher = Cipher.getInstance("RSA");
			encryptionCipher.init(Cipher.ENCRYPT_MODE, publicKey);
			
			// 6. userString to encrypt by PublicKey
			
			//String userString = "Java8SpringBootSpringFrameworkJPA";
			byte[] encryptedByteArray = encryptionCipher.doFinal(userString.getBytes());
			String encryptedByteArrayToString = Base64.getEncoder().encodeToString(encryptedByteArray);
			Map<Object, Object> encryptedAndDecryptedDataMap = new HashMap<>();
			encryptedAndDecryptedDataMap.put("EncryptedData", encryptedByteArrayToString);
			System.out.println("Encrypted Data By RSA KeyPair and PublicKey : "+ encryptedByteArrayToString);
			
			// 7. decrypt Data by PrivateKey
			Cipher decryptionCipher = Cipher.getInstance("RSA");
			decryptionCipher.init(Cipher.DECRYPT_MODE, privateKey);
			byte[] decryptedByteArray = decryptionCipher.doFinal(encryptedByteArray);
			String decryptedByteArrayToString = new String(decryptedByteArray);
			encryptedAndDecryptedDataMap.put("DecryptedData", decryptedByteArrayToString);
			System.out.println("Decrypted Data By RSA KeyPair and PrivateKey : "+ decryptedByteArrayToString);
		 
		  
		  
		  return encryptedAndDecryptedDataMap;
	 }
	/***/

	public static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	public static Instant getTimestamp() {

		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		Instant instant = timestamp.toInstant();

		return instant;
	}

	public static String convertDTO2JsonString(Object json) throws JsonProcessingException {
		ObjectMapper Obj = new ObjectMapper();
		String jsonStr = Obj.writeValueAsString(json);
		return jsonStr;
	}

	public static boolean validateBalance(int fromBalabce, int toBalance) {

		if (fromBalabce >= toBalance) {
			return true;
		}

		return false;
	}

	public static Long getEpochTIme() throws ParseException {
		Date today = Calendar.getInstance().getTime();
		SimpleDateFormat crunchifyFormat = new SimpleDateFormat("MMM dd yyyy HH:mm:ss.SSS zzz");
		String currentTime = crunchifyFormat.format(today);
		Date date = crunchifyFormat.parse(currentTime);
		long epochTime = date.getTime();
		return epochTime;
	}

	public static String beautifyJson(String strJson) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		String prettyStaff1 = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(strJson);
		return null;
	}

	public static String getJsonFileCreate(JSONObject jsonObject, String requestType) throws ParseException {

		// JSONObject jsonObject = new JSONObject(jsonStr);
		String fileName = requestType + "_" + getEpochTIme() + ".json";
		System.out.println("File Name :: " + fileName);
		try (FileWriter file = new FileWriter("/home/asktech/AskTech/Webhook/" + fileName)) {
			// try (FileWriter file = new
			// FileWriter("/home/asktech/AskTech/Webhook/fromPostMan/"+fileName)) {
			file.write(jsonObject.toString());
			file.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileName;
	}

	private static int inc = 0;

	public static long getMerchantsID() {
		// 12 digits.

		long id = Long.parseLong(String.valueOf(System.currentTimeMillis()).substring(1, 13));
		inc = (inc + 1) % 10;
		return id;
	}

	public static String generateAppId() {

		UUID uuid = UUID.randomUUID();
		String uuidAsString = uuid.toString().replace("-", "");

		return uuidAsString;
	}

	public static ErrorResponseDto populateErrorDto(FormValidationExceptionEnums fieledNotFound,
			Map<String, Object> extraData, String msg, boolean status, int statusCode) {
		ErrorResponseDto errorResponseDto = new ErrorResponseDto();
		errorResponseDto.getMsg().add(msg);
		errorResponseDto.setStatus(status);
		errorResponseDto.setStatusCode(statusCode);

		return errorResponseDto;
	}

	public static String convertDatetoMySqlDateFormat(String dateIn) throws ParseException {

		DateFormat originalFormat = new SimpleDateFormat("dd-MM-yyyy");
		DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = originalFormat.parse(dateIn);
		String formattedDate = targetFormat.format(date);

		return formattedDate;
	}

	public static Integer randomNumberForOtp(int sizeOfOtp) {

		int rand = (new Random()).nextInt(90000000) + 10000000;
		return rand;
	}

	public static String maskCardNumber(String cardNumber) {
		if(cardNumber==null) {
			return null;
		}
		StringBuilder maskedNumber = new StringBuilder();
		for (int i = 0; i < cardNumber.length(); i++) {
			if (i >= ((cardNumber.length() / 2) - 2) && i <= ((cardNumber.length() / 2) + 2)) {
				maskedNumber.append("X");
			} else {
				maskedNumber.append(cardNumber.charAt(i));
			}

		}
		return maskedNumber.toString();
	}

	public static String getAmountConversion(String amount) {

		return String.format("%.2f", Double.parseDouble(amount) / 100);

	}

	public static String randomStringGenerator(int sizeOfString) {

		return RandomStringUtils.randomAlphanumeric(sizeOfString);

	}
	
	public static String maskUpiCode(String upiCode) {
		if(upiCode == null) {
			return null;
		}
		return upiCode.substring(0,upiCode.lastIndexOf("@")).replaceAll("\\S", "*")+upiCode.substring(upiCode.lastIndexOf("@"));
	}
	
	public static boolean validateIFSCCode(String strIFCS) {
		String regex = "^[A-Z]{4}0[A-Z0-9]{6}$";
		return strIFCS.matches(regex); 
	}
	
	public static boolean checkNumericValue(String strValue) {
		String regex = "[0-9]+";
		return strValue.matches(regex);
	}
	
	public static String[] functionSplit(String str, String seperator) {
		
		String[] result = str.split(",");
		return result;
	}
}
