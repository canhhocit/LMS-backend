package com.ex.learninghub.modules.tuition.service;

import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.tuition.dto.request.TuitionRateRequest;
import com.ex.learninghub.modules.tuition.dto.response.PayOSPaymentResponse;
import com.ex.learninghub.modules.tuition.dto.response.TuitionInvoiceResponse;
import com.ex.learninghub.modules.tuition.dto.response.TuitionRateResponse;

import java.util.List;
import java.util.Map;

public interface TuitionService {
    // ---- Admin: quản lý tuition_rates ----
    TuitionRateResponse createRate(TuitionRateRequest request);
    TuitionRateResponse updateRate(Long id, TuitionRateRequest request);
    void deleteRate(Long id);
    List<TuitionRateResponse> listRates();
    TuitionRateResponse getRate(Long id);

    // ---- Student: xem hóa đơn của mình & thanh toán ----
    List<TuitionInvoiceResponse> getMyInvoices(UserPrincipal principal);
    TuitionInvoiceResponse payMyInvoice(Long invoiceId, UserPrincipal principal);

    // ---- PayOS Payment Integration ----
    PayOSPaymentResponse createPayOSPayment(Long invoiceId, UserPrincipal principal);
    TuitionInvoiceResponse verifyPayOSPayment(Long invoiceId, UserPrincipal principal);
    TuitionInvoiceResponse processPayOSWebhook(Map<String, Object> payload);

    // ---- Admin: generate / mark paid ----
    TuitionInvoiceResponse generateInvoice(Long studentId, String semester, String academicYear);
    TuitionInvoiceResponse markPaid(Long invoiceId);
}
