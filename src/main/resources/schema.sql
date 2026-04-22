CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL
);

CREATE TABLE event_categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_code VARCHAR(50) NOT NULL UNIQUE,
    category_name VARCHAR(100) NOT NULL,
    description VARCHAR(255) NULL,
    sort_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE security_events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    risk_level VARCHAR(10) NOT NULL,
    status VARCHAR(20) NOT NULL,
    channel VARCHAR(30) NOT NULL,
    detected_at TIMESTAMP NOT NULL,
    reason CLOB NOT NULL,
    recommendation CLOB NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    CONSTRAINT fk_security_events_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE monitoring_settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    email_monitoring BOOLEAN NOT NULL DEFAULT FALSE,
    sns_monitoring BOOLEAN NOT NULL DEFAULT FALSE,
    network_monitoring BOOLEAN NOT NULL DEFAULT FALSE,
    notification_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    notification_email VARCHAR(255) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_monitoring_settings_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE reports (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    report_type VARCHAR(50) NOT NULL,
    target_period_start DATE NOT NULL,
    target_period_end DATE NOT NULL,
    summary CLOB NULL,
    generated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    CONSTRAINT fk_reports_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE attachments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    security_event_id BIGINT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(255) NOT NULL,
    file_type VARCHAR(50) NOT NULL,
    file_size BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    CONSTRAINT fk_attachments_security_event FOREIGN KEY (security_event_id) REFERENCES security_events(id)
);

CREATE TABLE email_notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    security_event_id BIGINT NOT NULL,
    notification_type VARCHAR(50) NOT NULL,
    recipient_email VARCHAR(255) NOT NULL,
    sent_at TIMESTAMP NULL,
    send_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_email_notifications_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_email_notifications_security_event FOREIGN KEY (security_event_id) REFERENCES security_events(id)
);

CREATE INDEX idx_security_events_user_id ON security_events(user_id);
CREATE INDEX idx_security_events_detected_at ON security_events(detected_at);
CREATE INDEX idx_security_events_risk_level ON security_events(risk_level);
CREATE INDEX idx_security_events_status ON security_events(status);
CREATE INDEX idx_security_events_channel ON security_events(channel);
CREATE INDEX idx_event_categories_sort_order ON event_categories(sort_order);
CREATE INDEX idx_reports_user_id ON reports(user_id);
CREATE INDEX idx_reports_generated_at ON reports(generated_at);
CREATE INDEX idx_reports_report_type ON reports(report_type);
CREATE INDEX idx_reports_target_periods ON reports(target_period_start, target_period_end);
CREATE INDEX idx_attachments_security_event_id ON attachments(security_event_id);
CREATE INDEX idx_email_notifications_user_id ON email_notifications(user_id);
CREATE INDEX idx_email_notifications_security_event_id ON email_notifications(security_event_id);
CREATE INDEX idx_email_notifications_sent_at ON email_notifications(sent_at);
CREATE INDEX idx_email_notifications_send_status ON email_notifications(send_status);
