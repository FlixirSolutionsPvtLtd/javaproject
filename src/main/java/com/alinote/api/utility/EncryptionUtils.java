package com.alinote.api.utility;

import com.alinote.api.constants.*;
import lombok.extern.slf4j.*;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.text.*;

@Slf4j
public class EncryptionUtils {

    private static final String key = "@esEncrypT^(@PP)";
    private static final String initVector = "(encrypt^ion@In)";

    public static String encrypt(String value) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            byte[] encrypted = cipher.doFinal(value.getBytes());
            return Base64.encodeBase64String(encrypted);
        } catch (Exception ex) {
            log.error("Exception while encrypting", ex);
        }
        return null;
    }

    public static String decrypt(String encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));
            return new String(original);
        } catch (Exception ex) {
            log.error("Exception while decrypt", ex);
        }
        return null;
    }

    public static void main(String[] args) throws ParseException {

//		String encrypt=encrypt("pass@123");
//		System.out.println(encrypt);
//		System.out.println(decrypt("roOzHB7NWPx5Bt/lOuYNSw=="));

//        SimpleDateFormat dateParser = new SimpleDateFormat(GlobalConstants.StringConstants.STT_UTTERANCE_TIME_FORMAT_PATTERN);
//        SimpleDateFormat dateFormat = new SimpleDateFormat(GlobalConstants.StringConstants.UTTERANCE_TIME_FORMAT_PATTERN);
//        System.out.println(dateFormat.format(dateParser.parse("01:03:01.730")));

        System.out.println(DateTimeUtils.timeDiffInMinutes(1639725663608l, System.currentTimeMillis()));
    }
}