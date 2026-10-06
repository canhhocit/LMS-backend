ALTER TABLE tuition_invoices ADD COLUMN IF NOT EXISTS payment_method VARCHAR(20);
ALTER TABLE tuition_invoices ADD COLUMN IF NOT EXISTS payos_order_code BIGINT UNIQUE;
