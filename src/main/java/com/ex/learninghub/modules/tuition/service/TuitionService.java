package com.ex.learninghub.modules.tuition.service;

import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.tuition.dto.request.TuitionRateRequest;
import com.ex.learninghub.modules.tuition.dto.response.TuitionInvoiceResponse;
import com.ex.learninghub.modules.tuition.dto.response.TuitionRateResponse;

import java.util.List;

public interface TuitionService {
    // ---- Admin: quản lý tuition_rates ----
    TuitionRateResponse createRate(TuitionRateRequest request);
    TuitionRateResponse updateRate(Long id, TuitionRateRequest request);
    void deleteRate(Long id);
    List<TuitionRateResponse> listRates();
    TuitionRateResponse getRate(Long id);

    // ---- Student: xem hóa đơn của mình ----
    List<TuitionInvoiceResponse> getMyInvoices(UserPrincipal principal);

    // ---- Admin: generate / mark paid ----
    TuitionInvoiceResponse generateInvoice(Long studentId, String semester, String academicYear);
    TuitionInvoiceResponse markPaid(Long invoiceId);
}
