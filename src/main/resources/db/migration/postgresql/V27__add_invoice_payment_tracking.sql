ALTER TABLE tuition_invoices ADD COLUMN payment_method VARCHAR(20);
ALTER TABLE tuition_invoices ADD COLUMN payos_order_code BIGINT UNIQUE;
