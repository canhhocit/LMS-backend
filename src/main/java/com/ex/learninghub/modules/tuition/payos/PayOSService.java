package com.ex.learninghub.modules.tuition.payos;
import com.ex.learninghub.modules.tuition.dto.response.PayOSPaymentResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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
        if (!isConfigured()) throw new IllegalStateException("PayOS credentials are not configured.");
        long orderCode = System.currentTimeMillis() % 2000000000L;
        int intAmount = amount.intValueExact();
        String returnUrl = frontendUrl + "/student/tuition?invoiceId=" + invoiceId + "&orderCode=" + orderCode;
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
                JsonNode root = objectMapper.readTree(responseStr);
                if (root.has("code") && "00".equals(root.get("code").asText())) {
                    JsonNode data = root.get("data");
                    String checkoutUrl = data.has("checkoutUrl") ? data.get("checkoutUrl").asText() : "";
                    if (!data.hasNonNull("checkoutUrl") || data.get("checkoutUrl").asText().isBlank()) {
                        throw new IllegalStateException("PayOS response did not include a checkout URL.");
                    }
                    String rawQrCode = data.has("qrCode") ? data.get("qrCode").asText() : "";
                    String qrImageUrl;
                    if (rawQrCode.startsWith("000201")) {
                        // Convert EMVCo string payload into a renderable QR Code image URL
                        qrImageUrl = "https://api.qrserver.com/v1/create-qr-code/?size=350x350&data=" 
                                + URLEncoder.encode(rawQrCode, StandardCharsets.UTF_8);
                    } else if (!rawQrCode.isBlank()) {
                        qrImageUrl = rawQrCode;
                    } else qrImageUrl = "";
                    String accName = data.has("accountName") && !data.get("accountName").asText().isBlank() 
                            ? data.get("accountName").asText() : "";
                    String accNo = data.has("accountNumber") && !data.get("accountNumber").asText().isBlank() 
                            ? data.get("accountNumber").asText() : "";
                    String bank = data.has("bin") ? ("Bank BIN: " + data.get("bin").asText()) : "PayOS";
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
        throw new IllegalStateException("PayOS payment link creation failed. Check PayOS credentials and try again.");
    }
    public boolean isConfigured() {
        return !clientId.isBlank() && !apiKey.isBlank() && !checksumKey.isBlank()
                && !clientId.startsWith("demo-") && !apiKey.startsWith("demo-") && !checksumKey.startsWith("demo-");
    }

    public boolean isPaymentPaid(Long orderCode, BigDecimal expectedAmount) {
        if (!isConfigured()) throw new IllegalStateException("PayOS credentials are not configured.");
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-client-id", clientId);
        headers.set("x-api-key", apiKey);
        ResponseEntity<String> response = restTemplate.exchange(
                "https://api-merchant.payos.vn/v2/payment-requests/" + orderCode,
                HttpMethod.GET, new HttpEntity<>(headers), String.class);
        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode data = root.path("data");
            return "00".equals(root.path("code").asText())
                    && "PAID".equalsIgnoreCase(data.path("status").asText())
                    && data.path("orderCode").asLong(-1) == orderCode
                    && data.path("amountPaid").asLong(-1) == expectedAmount.longValueExact()
                    && data.path("amountRemaining").asLong(0) == 0;
        } catch (Exception e) {
            throw new IllegalStateException("Could not verify payment status with PayOS.", e);
        }
    }

    public PayOSPaymentResponse getPendingPaymentLink(Long invoiceId, Long orderCode, BigDecimal amount) {
        if (!isConfigured()) throw new IllegalStateException("PayOS credentials are not configured.");
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-client-id", clientId);
        headers.set("x-api-key", apiKey);
        ResponseEntity<String> response = restTemplate.exchange(
                "https://api-merchant.payos.vn/v2/payment-requests/" + orderCode,
                HttpMethod.GET, new HttpEntity<>(headers), String.class);
        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode data = root.path("data");
            String paymentLinkId = data.path("id").asText("");
            if (!"00".equals(root.path("code").asText())
                    || data.path("orderCode").asLong(-1) != orderCode
                    || !"PENDING".equalsIgnoreCase(data.path("status").asText())
                    || paymentLinkId.isBlank()) {
                throw new IllegalStateException("The existing PayOS payment is no longer pending.");
            }
            return PayOSPaymentResponse.builder()
                    .invoiceId(invoiceId)
                    .orderCode(orderCode)
                    .amount(amount)
                    .checkoutUrl("https://pay.payos.vn/web/" + paymentLinkId)
                    .qrCode("")
                    .accountName("")
                    .accountNumber("")
                    .bankName("PayOS")
                    .description("Tuition invoice " + invoiceId)
                    .status("PENDING")
                    .build();
        } catch (Exception e) {
            throw new IllegalStateException("Could not restore the PayOS payment link.", e);
        }
    }
    public boolean verifyWebhookData(Map<String, Object> data, String signature) {
        if (signature == null || signature.isBlank() || !isConfigured()) return false;
        try {
            StringJoiner fields = new StringJoiner("&");
            for (Map.Entry<String, Object> entry : new TreeMap<>(data).entrySet()) {
                Object value = entry.getValue();
                String canonicalValue = value == null ? "" : value instanceof Map || value instanceof List
                        ? objectMapper.writeValueAsString(value) : value.toString();
                fields.add(entry.getKey() + "=" + canonicalValue);
            }
            byte[] expected = hmacSha256(fields.toString(), checksumKey).getBytes(StandardCharsets.UTF_8);
            return java.security.MessageDigest.isEqual(expected, signature.toLowerCase(Locale.ROOT).getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) { return false; }
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
