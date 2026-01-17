package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class OneNetTokenService {

    @Value("${onet.author-key}")
    private String authorKey;
    @Value("${onet.user-id}")
    private String userId;
    @Value("${onet.version}")
    private String version;

    public String createCommonToken() {
        try {
            byte[] accessKeyBytes = Base64.getDecoder().decode(authorKey);
            String res = "userid/" + userId;
            long et = (System.currentTimeMillis() + 365L * 24 * 3600 * 1000) / 1000;
            String method = "sha1";
            String stringToSign = et + "\n" + method + "\n" + res + "\n" + version;

            Mac mac = Mac.getInstance("HmacSHA1");
            SecretKeySpec secretKeySpec = new SecretKeySpec(accessKeyBytes, "HmacSHA1");
            mac.init(secretKeySpec);
            byte[] hmacBytes = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));

            String sign = Base64.getEncoder().encodeToString(hmacBytes);
            String encodedRes = URLEncoder.encode(res, StandardCharsets.UTF_8);
            String encodedSign = URLEncoder.encode(sign, StandardCharsets.UTF_8);

            return String.format("version=%s&res=%s&et=%s&method=%s&sign=%s",
                    version, encodedRes, et, method, encodedSign);

        } catch (Exception e) {
            System.err.println("创建 OneNET Token 失败: " + e.getMessage());
            throw new RuntimeException("无法创建 Token", e);
        }
    }
}