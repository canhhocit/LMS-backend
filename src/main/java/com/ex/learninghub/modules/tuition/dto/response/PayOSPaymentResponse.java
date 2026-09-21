package com.ex.learninghub.modules.tuition.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayOSPaymentResponse {
    private Long invoiceId;
    private Long orderCode;
    private BigDecimal amount;
    private String checkoutUrl;
    private String qrCode;
    private String accountName;
    private String accountNumber;
    private String bankName;
    private String description;
    private String status;
}
