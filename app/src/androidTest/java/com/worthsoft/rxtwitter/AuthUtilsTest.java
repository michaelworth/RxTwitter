package com.worthsoft.rxtwitter;

import com.worthsoft.rxtwitter.utils.AuthUtils;

import junit.framework.TestCase;

import java.util.HashMap;

public class AuthUtilsTest extends TestCase {
    public void testGenerateSigningKey() throws Exception {
        final String testConsumer = "kAcSOqF21Fu85e7zjz7ZN2U4ZRhfV3WpwPAoE3Z7kBw";
        final String testToken = "LswwdoUaIvS8ltyTt5jkRh4J50vUPVVHtR2YPi5kE";
        final String expectedSigningKey = "kAcSOqF21Fu85e7zjz7ZN2U4ZRhfV3WpwPAoE3Z7kBw&LswwdoUaIvS8ltyTt5jkRh4J50vUPVVHtR2YPi5kE";

        String signingKey = AuthUtils.generateSigningKey(testConsumer, testToken);
        assertEquals("Key wasn't correct", expectedSigningKey, signingKey);
    }

    public void testGenerateSigningKeyNoToken() throws Exception {
        final String testConsumer = "kAcSOqF21Fu85e7zjz7ZN2U4ZRhfV3WpwPAoE3Z7kBw";
        final String testToken = null;
        final String expectedSigningKey = "kAcSOqF21Fu85e7zjz7ZN2U4ZRhfV3WpwPAoE3Z7kBw&";

        String signingKey = AuthUtils.generateSigningKey(testConsumer, testToken);
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
}
