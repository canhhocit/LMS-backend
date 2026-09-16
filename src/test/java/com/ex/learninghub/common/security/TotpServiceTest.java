package com.ex.learninghub.common.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TotpServiceTest {

    private TotpService totpService;

    @BeforeEach
    void setUp() {
        totpService = new TotpService();
    }

    @Test
    void generateSecretKey_returnsValidBase32Key() {
        String secret = totpService.generateSecretKey();
        assertThat(secret).isNotNull();
        assertThat(secret).matches("^[A-Z2-7]+$");
    }

    @Test
    void getQrCodeUri_returnsValidOtpauthUrl() {
        String secret = "JBSWY3DPEHPK3PXP";
        String uri = totpService.getQrCodeUri(secret, "admin@test.com");

        assertThat(uri).contains("otpauth://totp/");
        assertThat(uri).contains("secret=JBSWY3DPEHPK3PXP");
        assertThat(uri).contains("admin%40test.com");
    }
}
