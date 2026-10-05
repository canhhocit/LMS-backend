ALTER TABLE tuition_invoices ADD COLUMN IF NOT EXISTS payos_checkout_url VARCHAR(2048);
ALTER TABLE tuition_invoices ADD COLUMN IF NOT EXISTS payos_qr_code TEXT;
ALTER TABLE tuition_invoices ADD COLUMN IF NOT EXISTS payos_account_name VARCHAR(255);
ALTER TABLE tuition_invoices ADD COLUMN IF NOT EXISTS payos_account_number VARCHAR(100);
ALTER TABLE tuition_invoices ADD COLUMN IF NOT EXISTS payos_bank_name VARCHAR(255);
ALTER TABLE tuition_invoices ADD COLUMN IF NOT EXISTS payos_description VARCHAR(255);
