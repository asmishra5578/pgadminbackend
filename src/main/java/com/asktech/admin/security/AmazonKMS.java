package com.asktech.admin.security;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.encryptionsdk.AwsCrypto;
import com.amazonaws.encryptionsdk.CommitmentPolicy;
import com.amazonaws.encryptionsdk.CryptoResult;
import com.amazonaws.encryptionsdk.kms.KmsMasterKey;
import com.amazonaws.encryptionsdk.kms.KmsMasterKeyProvider;

@Component
public class AmazonKMS {

	// static String keyArn = "arn:aws:kms:ap-south-1:838635235256:key/47e45523-c789-4e5c-ba1d-36c4e71689a8";
	// static AwsCrypto crypto = AwsCrypto.builder().withCommitmentPolicy(CommitmentPolicy.RequireEncryptRequireDecrypt)
	// 		.build();
	// static BasicAWSCredentials awsCreds = new BasicAWSCredentials("AKIA4GQUOE64NBORSXGD",
	// 		"uOCyNbgYRftaoJZjlXPJhM97mzT5IKeFLHVJ+Jk7");
	// static KmsMasterKeyProvider keyProvider = KmsMasterKeyProvider.builder().withCredentials(awsCreds)
	// 		.buildStrict(keyArn);
	// static Map<String, String> encryptionContext = Collections.singletonMap("ExampleContextKey", "ExampleContextValue");

// /* 
// <------------------------------------------------AsMishra AWS CREDS-------------------------------------------->
 static String keyArn = "arn:aws:kms:ap-south-1:670682363122:key/e18289a7-ec17-4ccd-a61c-57b204d07d8a";
	static AwsCrypto crypto = AwsCrypto.builder().withCommitmentPolicy(CommitmentPolicy.RequireEncryptRequireDecrypt)
			.build();
	static BasicAWSCredentials awsCreds = new BasicAWSCredentials("AKIAZYJ6IWTZBJC6KNX2",
			"JNtbNmjFLnCa1JKKhYzcDTnekG8PAVIPGH+szOYv");
	static KmsMasterKeyProvider keyProvider = KmsMasterKeyProvider.builder().withCredentials(awsCreds)
			.buildStrict(keyArn);
	static Map<String, String> encryptionContext = Collections.singletonMap("ExampleContextKey", "ExampleContextValue");

// 	---------------------------------------------------------------------------------------------------------------------------
//  */



	// static Map<String, String> encryptionContext = Collections.singletonMap(contextKey,contextValue); 
	
	// final static String keyArn = "arn:aws:kms:ap-south-1:667262922539:key/e934219f-7e95-442c-9a0b-a9788b461ae4";
	// final static AwsCrypto crypto = AwsCrypto.builder().withCommitmentPolicy(CommitmentPolicy.RequireEncryptRequireDecrypt).build();		
	// final static BasicAWSCredentials awsCreds = new BasicAWSCredentials("AKIAZWW7YF4V7GQ4HTJJ", "RgtKZhbR0I2zUtRMH2a3KGozAEXOqyEtqQKHjbb4");
	// final static KmsMasterKeyProvider keyProvider = KmsMasterKeyProvider.builder().withCredentials(awsCreds).buildStrict(keyArn);		
	// final static Map<String, String> encryptionContext = Collections.singletonMap("ExampleContextKey","ExampleContextValue");


	public static String encryptionWithKMS(String inputStr) {

		CryptoResult<byte[], KmsMasterKey> encryptResult = crypto.encryptData(keyProvider,
				inputStr.getBytes(StandardCharsets.UTF_8), encryptionContext);
		byte[] ciphertext = encryptResult.getResult();

		return Base64.getEncoder().encodeToString(ciphertext);

	}

	public static String decryptionWithKMS(String inputStr) {

		byte[] decode = Base64.getDecoder().decode(inputStr);
		CryptoResult<byte[], KmsMasterKey> decryptResult = crypto.decryptData(keyProvider, decode);

		if (!encryptionContext.entrySet().stream()
				.allMatch(e -> e.getValue().equals(decryptResult.getEncryptionContext().get(e.getKey())))) {
			throw new IllegalStateException("Wrong Encryption Context!");
		}

		return new String(decryptResult.getResult());
	}

}
