package com.worthsoft.rxtwitter.utils;

import android.text.TextUtils;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class AuthUtils {

    public static final String UTF8 = "UTF-8";

    public static final String TIMESTAMP = "oauth_timestamp";
    public static final String SIGN_METHOD = "oauth_signature_method";
    public static final String SIGNATURE = "oauth_signature";
    public static final String CONSUMER_SECRET = "oauth_consumer_secret";
    public static final String CONSUMER_KEY = "oauth_consumer_key";
    public static final String CALLBACK = "oauth_callback";
    public static final String CALLBACK_CONFIRMED = "oauth_callback_confirmed";
    public static final String VERSION = "oauth_version";
    public static final String NONCE = "oauth_nonce";
    public static final String REALM = "realm";
    public static final String PARAM_PREFIX = "oauth_";
    public static final String TOKEN = "oauth_token";
    public static final String TOKEN_SECRET = "oauth_token_secret";
    public static final String OUT_OF_BAND = "oob";
    public static final String VERIFIER = "oauth_verifier";
    public static final String HEADER = "Authorization";

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
        try {
            SecretKeySpec keySpec = new SecretKeySpec(signingKey.getBytes(UTF8), "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(keySpec);
            byte[] result = mac.doFinal(signatureBase.getBytes(UTF8));
            return Base64.encodeToString(result, Base64.NO_WRAP);

        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | InvalidKeyException exception) {
            exception.printStackTrace();
        }

        return null;
    }

    public static void insertOAuthParams(HashMap<String, String> params, String consumerKey, String nonce, String token, String timestamp) {
        params.put(CONSUMER_KEY, consumerKey);
        params.put(NONCE, nonce);
        params.put(SIGN_METHOD, "HMAC-SHA1");
        params.put(TIMESTAMP, timestamp);
        params.put(VERSION, "1.0");

        if (!TextUtils.isEmpty(token)) {
            params.put(TOKEN, token);
        }
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

    public static String generateSignatureBaseString(String httpMethod, String url, String parameterString) {
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
                return URLEncoder.encode(data, UTF8)
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

    public static String generateAuthorizationHeader(
            HashMap<String, String> params,
            String consumerKey,
            String consumerSecret,
            String token,
            String callback,
            String method,
            String url,
            String nonce,
            String timestamp) {

        AuthUtils.insertOAuthParams(params, consumerKey, nonce, token, timestamp);

        if (!TextUtils.isEmpty(callback)) {
            params.put(CALLBACK, callback);
        }

        final String signingKey = AuthUtils.generateSigningKey(consumerSecret, null);
        final String parameterString = AuthUtils.generateParameterString(params);
        final String signatureBase = AuthUtils.generateSignatureBaseString(method, url, parameterString);
        final String signature = AuthUtils.generateSignature(signingKey, signatureBase);

        params.put(SIGNATURE, signature);

        StringBuilder stringBuilder = new StringBuilder("OAuth ");

        Iterator<Map.Entry<String, String>> headerIterator = params.entrySet().iterator();
        while (headerIterator.hasNext()) {
            Map.Entry<String, String> entry = headerIterator.next();
            stringBuilder.append(percentEncode(entry.getKey()));
            stringBuilder.append("=" + "\"");
            stringBuilder.append(percentEncode(entry.getValue()));
            stringBuilder.append("\"");

            if (headerIterator.hasNext()) {
                stringBuilder.append(",");
            }
        }

        return stringBuilder.toString();
    }

    public static String generateTimestamp() {
        return String.valueOf(System.currentTimeMillis() / 1000);
    }

    public static String generateNonce() {
        byte[] bytes = new byte[32];
        new Random().nextBytes(bytes);
        return Base64.encodeToString(bytes, Base64.URL_SAFE).replaceAll("[^A-Za-z0-9]", "");
    }
}
