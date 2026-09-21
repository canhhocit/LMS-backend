package com.ex.learninghub.modules.tuition.payos;

import com.ex.learninghub.modules.tuition.dto.response.PayOSPaymentResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Slf4j
@Service
public class PayOSService {

    @Value("${payos.client-id:demo-client-id}")
    private String clientId;

    @Value("${payos.api-key:demo-api-key}")
    private String apiKey;

    @Value("${payos.checksum-key:demo-checksum-key}")
    private String checksumKey;

    @Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PayOSPaymentResponse createPaymentLink(Long invoiceId, BigDecimal amount, String description, String studentName) {
        long orderCode = System.currentTimeMillis() % 2000000000L;
        int intAmount = amount.intValue();

        String returnUrl = frontendUrl + "/student/tuition?status=PAID&invoiceId=" + invoiceId + "&orderCode=" + orderCode;
        String cancelUrl = frontendUrl + "/student/tuition?status=CANCELLED&invoiceId=" + invoiceId;

        // Clean description for VietQR / PayOS (max 25 chars, alphanumeric & spaces)
        String cleanDesc = ("HP " + invoiceId + " " + (description != null ? description : "")).replaceAll("[^a-zA-Z0-9 ]", "");
        if (cleanDesc.length() > 25) {
            cleanDesc = cleanDesc.substring(0, 25);
        }

        Map<String, Object> body = new HashMap<>();
        body.put("orderCode", orderCode);
        body.put("amount", intAmount);
        body.put("description", cleanDesc);
        body.put("returnUrl", returnUrl);
        body.put("cancelUrl", cancelUrl);

        // Add items array required by PayOS v2 API
        List<Map<String, Object>> items = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("name", "Hoc phi HD #" + invoiceId);
        item.put("quantity", 1);
        item.put("price", intAmount);
        items.add(item);
        body.put("items", items);

        // Compute HMAC SHA256 Signature for PayOS v2:
        // Key alphabetical order: amount, cancelUrl, description, orderCode, returnUrl
        String signatureData = String.format("amount=%d&cancelUrl=%s&description=%s&orderCode=%d&returnUrl=%s",
                intAmount, cancelUrl, cleanDesc, orderCode, returnUrl);

        String signature = hmacSha256(signatureData, checksumKey);
        body.put("signature", signature);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-client-id", clientId);
            headers.set("x-api-key", apiKey);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            log.info("Gửi request khởi tạo thanh toán tới PayOS API: clientId={}, orderCode={}", clientId, orderCode);

            String responseStr = restTemplate.postForObject("https://api-merchant.payos.vn/v2/payment-requests", request, String.class);

            if (responseStr != null) {
                log.info("Nhận kết quả từ PayOS API: {}", responseStr);
                JsonNode root = objectMapper.readTree(responseStr);
                if (root.has("code") && "00".equals(root.get("code").asText())) {
                    JsonNode data = root.get("data");

                    String checkoutUrl = data.has("checkoutUrl") ? data.get("checkoutUrl").asText() : returnUrl;
                    String rawQrCode = data.has("qrCode") ? data.get("qrCode").asText() : "";
                    String qrImageUrl;

                    if (rawQrCode.startsWith("000201")) {
                        // Convert EMVCo string payload into a renderable QR Code image URL
                        qrImageUrl = "https://api.qrserver.com/v1/create-qr-code/?size=350x350&data=" 
                                + URLEncoder.encode(rawQrCode, StandardCharsets.UTF_8);
                    } else if (!rawQrCode.isBlank()) {
                        qrImageUrl = rawQrCode;
                    } else {
                        qrImageUrl = generateVietQrLink(amount, cleanDesc, data.has("accountNumber") ? data.get("accountNumber").asText() : "");
                    }

                    String accName = data.has("accountName") && !data.get("accountName").asText().isBlank() 
                            ? data.get("accountName").asText() : "PHAM HUU CANH";
                    String accNo = data.has("accountNumber") && !data.get("accountNumber").asText().isBlank() 
                            ? data.get("accountNumber").asText() : "26061122334455";
                    String bank = data.has("bin") ? ("Ngân hàng (BIN: " + data.get("bin").asText() + ")") : "MBBank / PayOS";

                    return PayOSPaymentResponse.builder()
                            .invoiceId(invoiceId)
                            .orderCode(orderCode)
                            .amount(amount)
                            .checkoutUrl(checkoutUrl)
                            .qrCode(qrImageUrl)
                            .accountName(accName)
                            .accountNumber(accNo)
                            .bankName(bank)
                            .description(cleanDesc)
                            .status("PENDING")
                            .build();
                } else {
                    log.error("PayOS API trả về lỗi: code={}, desc={}", root.path("code").asText(), root.path("desc").asText());
                }
            }
        } catch (Exception e) {
            log.error("Lỗi khi kết nối PayOS API: {}", e.getMessage(), e);
        }

        // Fallback VietQR Quick Link format if PayOS credentials invalid / offline
        String vietQrQuickUrl = generateVietQrLink(amount, cleanDesc, "26061122334455");
        return PayOSPaymentResponse.builder()
                .invoiceId(invoiceId)
                .orderCode(orderCode)
                .amount(amount)
                .checkoutUrl("https://pay.payos.vn")
                .qrCode(vietQrQuickUrl)
                .accountName("PHAM HUU CANH")
                .accountNumber("26061122334455")
                .bankName("MBBank (VietQR PayOS)")
                .description(cleanDesc)
                .status("PENDING")
                .build();
    }

    public boolean verifyWebhookSignature(String webhookBody, String signature) {
        if (signature == null || signature.isBlank()) return false;
        try {
            String calculated = hmacSha256(webhookBody, checksumKey);
            return calculated.equalsIgnoreCase(signature);
        } catch (Exception e) {
            return false;
        }
    }

    private String generateVietQrLink(BigDecimal amount, String description, String accNo) {
        String account = (accNo != null && !accNo.isBlank()) ? accNo : "26061122334455";
        return String.format("https://img.vietqr.io/image/MB-%s-compact2.png?amount=%d&addInfo=%s&accountName=PHAM%20HUU%20CANH",
                account,
                amount.intValue(),
                description.replace(" ", "%20"));
    }

    private String hmacSha256(String data, String key) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Lỗi tạo HmacSHA256", e);
            return "";
        }
    }
}

