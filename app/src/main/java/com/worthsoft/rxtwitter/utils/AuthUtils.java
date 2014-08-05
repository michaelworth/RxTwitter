package com.worthsoft.rxtwitter.utils;

import android.text.TextUtils;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class AuthUtils {

    public enum HttpMethod {
        GET("GET"),
        POST("POST"),
        DELETE("DELETE"),
        PUT("PUT");

        private final String value;

        private HttpMethod(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public static String generateSigningKey(String consumerKey, String token) throws IllegalArgumentException {
        if (consumerKey == null) {
            throw new IllegalArgumentException("Consumer key cannot be null");
        }

        // Token can be null for signing key when user is not yet signed in. Ampersand is still appended in this case.
        StringBuilder stringBuilder = new StringBuilder(consumerKey + "&");
        if (!TextUtils.isEmpty(token)) {
            stringBuilder.append(token);
        }

        return stringBuilder.toString();
    }

    public static String generateSignature(String signingKey, String signatureBase) {
        SecretKeySpec keySpec = new SecretKeySpec(
                signingKey.getBytes(),
                "HmacSHA1");

        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(keySpec);
            byte[] result = mac.doFinal(signatureBase.getBytes());
            return Base64.encodeToString(result, Base64.NO_WRAP);

        } catch (NoSuchAlgorithmException | InvalidKeyException exception) {
            exception.printStackTrace();
        }

        return null;
    }

    public static String generateParameterString(HashMap<String, String> params) {
        TreeMap<String, String> percentEncodedMap = new TreeMap<>();

        // percent encode each key and value
        Set<String> keys = params.keySet();
        for (String key : keys) {
            String value = params.get(key);

            String encodedKey = percentEncode(key);
            String encodedValue = percentEncode(value);
            percentEncodedMap.put(encodedKey, encodedValue);
        }

        // sort list alphabetically by key (done automatically by collection)

        // Build string output
        StringBuilder outputStringBuilder = new StringBuilder();

        Iterator<String> keyIterator = percentEncodedMap.keySet().iterator();
        while (keyIterator.hasNext()) {
            String key = keyIterator.next();
            outputStringBuilder.append(key);
            outputStringBuilder.append("=");
            outputStringBuilder.append(percentEncodedMap.get(key));

            if (keyIterator.hasNext()) {
                outputStringBuilder.append("&");
            }
        }

        return outputStringBuilder.toString();
    }

    public static String generateSignatureBaseString(HttpMethod httpMethod, String url, String parameterString) {
        StringBuilder outputStringBuilder = new StringBuilder();
        outputStringBuilder.append(httpMethod);
        outputStringBuilder.append("&");
        outputStringBuilder.append(percentEncode(url));
        outputStringBuilder.append("&");
        outputStringBuilder.append(percentEncode(parameterString));
        return outputStringBuilder.toString();
    }

    public static String percentEncode(String data) {
        if (data != null) {
            try {
                return URLEncoder.encode(data, "UTF-8")
                        // OAuth encodes some characters differently:
                        .replace("+", "%20")
                        .replace("*", "%2A")
                        .replace("%7E", "~");
            } catch (UnsupportedEncodingException uee) {
                uee.printStackTrace();
            }
        }

        return "";
    }
}
