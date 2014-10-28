package com.worthsoft.rxtwitter;

import com.worthsoft.rxtwitter.utils.AuthUtils;

import junit.framework.TestCase;

import java.util.HashMap;

public class AuthUtilsTest extends TestCase {

    public void testGenerateSigningKeyWithNullTokenSecret() throws Exception {
        final String testConsumerSecret = "L8qq9PZyRg6ieKGEKhZolGC0vJWLw8iEJ88DRdyOg";

        final String expectedSignature = "F1Li3tvehgcraF8DMJ7OyxO4w9Y%3D";

        final String oauth_callback = "http://localhost/sign-in-with-twitter/";
        final String oauth_consumer_key = "cChZNFj6T5R0TigYB9yd1w";
        final String oauth_nonce = "ea9ec8429b68d6b77cd5600adbbb0456";
        final String oauth_signature_method = "HMAC-SHA1";
        final String oauth_timestamp = "1318467427";
        final String oauth_version = "1.0";

        HashMap<String, String> params = new HashMap<>();

        // Insert out of alpha order to ensure method sorts correctly
        params.put("oauth_callback", oauth_callback);
        params.put("oauth_consumer_key", oauth_consumer_key);
        params.put("oauth_version", oauth_version);
        params.put("oauth_timestamp", oauth_timestamp);
        params.put("oauth_nonce", oauth_nonce);
        params.put("oauth_signature_method", oauth_signature_method);

        String signingKey = AuthUtils.generateSigningKey(testConsumerSecret, null);
        String parameterString = AuthUtils.generateParameterString(params);
        String signatureBase = AuthUtils.generateSignatureBaseString("POST", "https://api.twitter.com/oauth/request_token", parameterString);
        String signature = AuthUtils.generateSignature(signingKey, signatureBase);

        assertEquals("Signature didn't match", expectedSignature, AuthUtils.percentEncode(signature));

    }

    public void testGenerateAuthorizationHeaderWithNullTokenSecret() throws Exception {
        final String consumerKey = "cChZNFj6T5R0TigYB9yd1w";
        final String consumerSecret = "L8qq9PZyRg6ieKGEKhZolGC0vJWLw8iEJ88DRdyOg";
        final String callback = "http://localhost/sign-in-with-twitter/";
        final String method = "POST";
        final String url = "https://api.twitter.com/oauth/request_token";
        final String nonce = "ea9ec8429b68d6b77cd5600adbbb0456";
        final String timestamp = "1318467427";

        final String authPrefix = "OAuth ";
        final String authCallback = "oauth_callback=\"http%3A%2F%2Flocalhost%2Fsign-in-with-twitter%2F\"";
        final String authConsumer = "oauth_consumer_key=\"cChZNFj6T5R0TigYB9yd1w\"";
        final String authNonce = "oauth_nonce=\"ea9ec8429b68d6b77cd5600adbbb0456\"";
        final String authSignature = "oauth_signature=\"F1Li3tvehgcraF8DMJ7OyxO4w9Y%3D\"";
        final String authSignatureMethod = "oauth_signature_method=\"HMAC-SHA1\"";
        final String authTimestamp = "oauth_timestamp=\"1318467427\"";
        final String authVersion = "oauth_version=\"1.0\"";

        HashMap<String, String> params = new HashMap<>();
        final String authorizationHeader = AuthUtils.generateAuthorizationHeader(params, consumerKey, consumerSecret, null, null, callback, method, url, nonce, timestamp);
        assertTrue("OAuth prefix missing or incorrect", authorizationHeader.startsWith(authPrefix));
        assertTrue("Auth callback missing or incorrect", authorizationHeader.contains(authCallback));
        assertTrue("Auth consumer missing or incorrect", authorizationHeader.contains(authConsumer));
        assertTrue("Auth nonce missing or incorrect", authorizationHeader.contains(authNonce));
        assertTrue("Auth signature missing or incorrect", authorizationHeader.contains(authSignature));
        assertTrue("Auth signature method missing or incorrect", authorizationHeader.contains(authSignatureMethod));
        assertTrue("Auth timestamp missing or incorrect", authorizationHeader.contains(authTimestamp));
        assertTrue("Auth version missing or incorrect", authorizationHeader.contains(authVersion));
    }

    public void testGenerateAuthorizationHeader() throws Exception {
        final String consumerKey = "ioEcs86I43YA7ChqVCsaz7Il0";
        final String consumerSecret = "nZt9dbZibRlaCoLpfkrbV2RoN7sChIuDY7uLVYLWK0tSoQYX8W";

        final String method = "GET";
        final String token = "52752711-JoHUCFKCFb7qoyhf5uDXeIZtuqTBKzqpfgnMeRbJQ";
        final String tokenSecret = "yrXo4lGpq16NsGsUp4bYQOhCJeNQQzQFDkyzBjMbffj7R";
        final String url = "https://api.twitter.com/1.1/account/verify_credentials.json";
        final String nonce = "dd1b9e46ec5f79ba8f67c29ef801a0c8";
        final String timestamp = "1414474075";

        final String authPrefix = "OAuth ";
        final String authConsumer = "oauth_consumer_key=\"ioEcs86I43YA7ChqVCsaz7Il0\"";
        final String authNonce = "oauth_nonce=\"dd1b9e46ec5f79ba8f67c29ef801a0c8\"";
        final String authSignature = "oauth_signature=\"IpArqNj24mDyhQPB4t%2FHGA638fg%3D\"";
        final String authSignatureMethod = "oauth_signature_method=\"HMAC-SHA1\"";
        final String authTimestamp = "oauth_timestamp=\"1414474075\"";
        final String authToken = "oauth_token=\"52752711-JoHUCFKCFb7qoyhf5uDXeIZtuqTBKzqpfgnMeRbJQ\"";
        final String authVersion = "oauth_version=\"1.0\"";

        HashMap<String, String> params = new HashMap<>();
        final String authorizationHeader = AuthUtils.generateAuthorizationHeader(params, consumerKey, consumerSecret, token, tokenSecret, null, method, url, nonce, timestamp);
        assertTrue("OAuth prefix missing or incorrect", authorizationHeader.startsWith(authPrefix));
        assertTrue("Auth consumer missing or incorrect", authorizationHeader.contains(authConsumer));
        assertTrue("Auth nonce missing or incorrect", authorizationHeader.contains(authNonce));
        assertTrue("Auth signature missing or incorrect", authorizationHeader.contains(authSignature));
        assertTrue("Auth signature method missing or incorrect", authorizationHeader.contains(authSignatureMethod));
        assertTrue("Auth timestamp missing or incorrect", authorizationHeader.contains(authTimestamp));
        assertTrue("Auth token missing or incorrect", authorizationHeader.contains(authToken));
        assertTrue("Auth version missing or incorrect", authorizationHeader.contains(authVersion));
    }

    public void testPercentEncode() throws Exception {
        assertEquals("Ladies%20%2B%20Gentlemen", AuthUtils.percentEncode("Ladies + Gentlemen"));
        assertEquals("An%20encoded%20string%21", AuthUtils.percentEncode("An encoded string!"));
        assertEquals("Dogs%2C%20Cats%20%26%20Mice", AuthUtils.percentEncode("Dogs, Cats & Mice"));
        assertEquals("%E2%98%83", AuthUtils.percentEncode("☃"));

    }

    public void testGenerateSigningKey() throws Exception {
        final String testConsumerSecret = "kAcSOqF21Fu85e7zjz7ZN2U4ZRhfV3WpwPAoE3Z7kBw";
        final String testTokenSecret = "LswwdoUaIvS8ltyTt5jkRh4J50vUPVVHtR2YPi5kE";
        final String expectedSigningKey = "kAcSOqF21Fu85e7zjz7ZN2U4ZRhfV3WpwPAoE3Z7kBw&LswwdoUaIvS8ltyTt5jkRh4J50vUPVVHtR2YPi5kE";

        String signingKey = AuthUtils.generateSigningKey(testConsumerSecret, testTokenSecret);
        assertEquals("Key wasn't correct", expectedSigningKey, signingKey);
    }

    public void testGenerateSigningKeyNoToken() throws Exception {
        final String testConsumerSecret = "kAcSOqF21Fu85e7zjz7ZN2U4ZRhfV3WpwPAoE3Z7kBw";
        final String testTokenSecret = null;
        final String expectedSigningKey = "kAcSOqF21Fu85e7zjz7ZN2U4ZRhfV3WpwPAoE3Z7kBw&";

        String signingKey = AuthUtils.generateSigningKey(testConsumerSecret, testTokenSecret);
        assertEquals("Key wasn't correct", expectedSigningKey, signingKey);
    }

    public void testGenerateSignature() throws Exception {
        final String signingKey = "kAcSOqF21Fu85e7zjz7ZN2U4ZRhfV3WpwPAoE3Z7kBw&LswwdoUaIvS8ltyTt5jkRh4J50vUPVVHtR2YPi5kE";
        final String signatureBase = "POST&https%3A%2F%2Fapi.twitter.com%2F1%2Fstatuses%2Fupdate.json&include_entities%3Dtrue%26oauth_consumer_key%3Dxvz1evFS4wEEPTGEFPHBog%26oauth_nonce%3DkYjzVBB8Y0ZFabxSWbWovY3uYSQ2pTgmZeNu2VS4cg%26oauth_signature_method%3DHMAC-SHA1%26oauth_timestamp%3D1318622958%26oauth_token%3D370773112-GmHxMAgYyLbNEtIKZeRNFsMKPR9EyMZeS9weJAEb%26oauth_version%3D1.0%26status%3DHello%2520Ladies%2520%252B%2520Gentlemen%252C%2520a%2520signed%2520OAuth%2520request%2521";
        final String expectedOAuthSignature = "tnnArxj06cWHq44gCs1OSKk/jLY=";

        String oAuthSignature = AuthUtils.generateSignature(signingKey, signatureBase);
        assertEquals("Signature didn't match", expectedOAuthSignature, oAuthSignature);
    }

    public void testGenerateParameterString() throws Exception {
        final String status = "Hello Ladies + Gentlemen, a signed OAuth request!";
        final String include_entities = "true";
        final String oauth_consumer_key = "xvz1evFS4wEEPTGEFPHBog";
        final String oauth_nonce = "kYjzVBB8Y0ZFabxSWbWovY3uYSQ2pTgmZeNu2VS4cg";
        final String oauth_signature_method = "HMAC-SHA1";
        final String oauth_timestamp = "1318622958";
        final String oauth_token = "370773112-GmHxMAgYyLbNEtIKZeRNFsMKPR9EyMZeS9weJAEb";
        final String oauth_version = "1.0";
        final String expectedParameterString = "include_entities=true&oauth_consumer_key=xvz1evFS4wEEPTGEFPHBog&oauth_nonce=kYjzVBB8Y0ZFabxSWbWovY3uYSQ2pTgmZeNu2VS4cg&oauth_signature_method=HMAC-SHA1&oauth_timestamp=1318622958&oauth_token=370773112-GmHxMAgYyLbNEtIKZeRNFsMKPR9EyMZeS9weJAEb&oauth_version=1.0&status=Hello%20Ladies%20%2B%20Gentlemen%2C%20a%20signed%20OAuth%20request%21";

        HashMap<String, String> params = new HashMap<>();

        // Insert out of alpha order to ensure method sorts correctly
        params.put("status", status);
        params.put("oauth_consumer_key", oauth_consumer_key);
        params.put("oauth_version", oauth_version);
        params.put("oauth_timestamp", oauth_timestamp);
        params.put("oauth_nonce", oauth_nonce);
        params.put("oauth_token", oauth_token);
        params.put("oauth_signature_method", oauth_signature_method);
        params.put("include_entities", include_entities);

        String parameterString = AuthUtils.generateParameterString(params);
        assertEquals("Parameter string doesn't match", expectedParameterString, parameterString);
    }

    public void testGenerateSignatureBase() throws Exception {
        final String httpMethod = "POST";
        final String url = "https://api.twitter.com/1/statuses/update.json";
        final String parameterString = "include_entities=true&oauth_consumer_key=xvz1evFS4wEEPTGEFPHBog&oauth_nonce=kYjzVBB8Y0ZFabxSWbWovY3uYSQ2pTgmZeNu2VS4cg&oauth_signature_method=HMAC-SHA1&oauth_timestamp=1318622958&oauth_token=370773112-GmHxMAgYyLbNEtIKZeRNFsMKPR9EyMZeS9weJAEb&oauth_version=1.0&status=Hello%20Ladies%20%2B%20Gentlemen%2C%20a%20signed%20OAuth%20request%21";
        final String expectedSignatureBase = "POST&https%3A%2F%2Fapi.twitter.com%2F1%2Fstatuses%2Fupdate.json&include_entities%3Dtrue%26oauth_consumer_key%3Dxvz1evFS4wEEPTGEFPHBog%26oauth_nonce%3DkYjzVBB8Y0ZFabxSWbWovY3uYSQ2pTgmZeNu2VS4cg%26oauth_signature_method%3DHMAC-SHA1%26oauth_timestamp%3D1318622958%26oauth_token%3D370773112-GmHxMAgYyLbNEtIKZeRNFsMKPR9EyMZeS9weJAEb%26oauth_version%3D1.0%26status%3DHello%2520Ladies%2520%252B%2520Gentlemen%252C%2520a%2520signed%2520OAuth%2520request%2521";

        String signatureBase = AuthUtils.generateSignatureBaseString(httpMethod, url, parameterString);
        assertEquals("Signature base string didn't match", expectedSignatureBase, signatureBase);
    }

    public void testGenerateSignatureBaseV2() throws Exception {
        final String httpMethod = "POST";
        final String url = "https://api.twitter.com/1/statuses/update.json";
        final String parameterString = "include_entities=true&oauth_consumer_key=xvz1evFS4wEEPTGEFPHBog&oauth_nonce=kYjzVBB8Y0ZFabxSWbWovY3uYSQ2pTgmZeNu2VS4cg&oauth_signature_method=HMAC-SHA1&oauth_timestamp=1318622958&oauth_token=370773112-GmHxMAgYyLbNEtIKZeRNFsMKPR9EyMZeS9weJAEb&oauth_version=1.0&status=Hello%20Ladies%20%2B%20Gentlemen%2C%20a%20signed%20OAuth%20request%21";
        final String expectedSignatureBase = "POST&https%3A%2F%2Fapi.twitter.com%2F1%2Fstatuses%2Fupdate.json&include_entities%3Dtrue%26oauth_consumer_key%3Dxvz1evFS4wEEPTGEFPHBog%26oauth_nonce%3DkYjzVBB8Y0ZFabxSWbWovY3uYSQ2pTgmZeNu2VS4cg%26oauth_signature_method%3DHMAC-SHA1%26oauth_timestamp%3D1318622958%26oauth_token%3D370773112-GmHxMAgYyLbNEtIKZeRNFsMKPR9EyMZeS9weJAEb%26oauth_version%3D1.0%26status%3DHello%2520Ladies%2520%252B%2520Gentlemen%252C%2520a%2520signed%2520OAuth%2520request%2521";

        String signatureBase = AuthUtils.generateSignatureBaseString(httpMethod, url, parameterString);
        assertEquals("Signature base string didn't match", expectedSignatureBase, signatureBase);
    }
}
