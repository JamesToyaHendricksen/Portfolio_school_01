INSERT INTO users (id, name, email, password_hash, role, created_at, updated_at, deleted_at)
VALUES
  (1, '管理者ユーザー', 'admin@lifeshield.ai', '$2a$10$7EqJtq98hPqEX7fNZaFWoOhiB6Lr7sRKqGZo4PMBVXsfS5aXoaZy.', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
  (2, '山田 太郎', 'taro.yamada@example.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoOhiB6Lr7sRKqGZo4PMBVXsfS5aXoaZy.', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
  (3, '佐藤 花子', 'hanako.sato@example.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoOhiB6Lr7sRKqGZo4PMBVXsfS5aXoaZy.', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL);

INSERT INTO event_categories (id, category_code, category_name, description, sort_order, created_at, updated_at)
VALUES
    (1, 'PHISHING', 'フィッシング', '偽サイトや偽リンクによる誘導', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 'SCAM', '詐欺', '金銭や個人情報をだまし取る行為', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (3, 'MAIL', '不審メール', '本文や送信元に違和感があるメール', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (4, 'SNS', 'SNSなりすまし', 'SNS上の偽アカウントや不審DM', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (5, 'NETWORK', '通信異常', '通常と異なる外部通信やアクセス', 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO monitoring_settings (
    id, user_id, email_monitoring, sns_monitoring, network_monitoring, notification_enabled, notification_email, created_at, updated_at
)
VALUES
    (1, 1, TRUE, TRUE, TRUE, TRUE, 'admin@lifeshield.ai', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 2, TRUE, TRUE, TRUE, TRUE, 'taro.yamada@example.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (3, 3, TRUE, FALSE, TRUE, TRUE, 'hanako.sato@example.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO security_events (
    id, user_id, event_type, risk_level, status, channel, detected_at, reason, recommendation, created_at, updated_at, deleted_at
)
VALUES
    (1, 2, 'PHISHING', '高', '未対応', 'メール', TIMESTAMP '2026-04-20 09:15:00', '金融機関を装ったURL付きメールを検知', 'リンクを開かず送信元を確認し、該当メールを削除する', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
    (2, 2, 'MAIL', '中', '確認中', 'メール', TIMESTAMP '2026-04-20 11:30:00', '請求を急がせる表現を含む不審メールを検知', '添付ファイルを開かず、正規窓口へ確認する', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
    (3, 3, 'SNS', '高', '未対応', 'SNS', TIMESTAMP '2026-04-21 08:40:00', '知人を装ったアカウントから送金依頼DMを検知', '返信せず、別経路で本人確認を行う', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
    (4, 3, 'NETWORK', '中', '対応済み', '通信', TIMESTAMP '2026-04-21 19:05:00', '通常利用時間外の外部通信を検知', '接続元アプリを確認し、不要な通信を停止する', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
    (5, 2, 'SCAM', '低', '対応済み', 'Web', TIMESTAMP '2026-04-22 13:20:00', '懸賞当選を装う画面遷移を検知', '個人情報を入力せずブラウザを閉じる', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL);

INSERT INTO reports (
    id, user_id, report_type, target_period_start, target_period_end, summary, generated_at, created_at, updated_at, deleted_at
)
VALUES
    (1, 2, '週次レポート', DATE '2026-04-14', DATE '2026-04-20', '危険イベント2件を検知。メール由来のリスクが多く、注意喚起を実施。', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
    (2, 3, '週次レポート', DATE '2026-04-14', DATE '2026-04-20', 'SNSなりすましと通信異常を各1件検知。設定見直しを推奨。', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL);

INSERT INTO attachments (
    id, security_event_id, file_name, file_path, file_type, file_size, created_at, updated_at, deleted_at
)
VALUES
    (1, 1, 'phishing_mail_header.png', '/uploads/phishing_mail_header.png', 'image/png', 245760, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
    (2, 3, 'sns_dm_capture.jpg', '/uploads/sns_dm_capture.jpg', 'image/jpeg', 198420, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL);

INSERT INTO email_notifications (
    id, user_id, security_event_id, notification_type, recipient_email, sent_at, send_status, created_at, updated_at
)
VALUES
    (1, 2, 1, '危険イベント通知', 'taro.yamada@example.com', TIMESTAMP '2026-04-20 09:16:00', 'SENT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 2, 2, '危険イベント通知', 'taro.yamada@example.com', TIMESTAMP '2026-04-20 11:31:00', 'SENT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (3, 3, 3, '危険イベント通知', 'hanako.sato@example.com', TIMESTAMP '2026-04-21 08:41:00', 'SENT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (4, 3, 4, '危険イベント通知', 'hanako.sato@example.com', TIMESTAMP '2026-04-21 19:06:00', 'SENT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (5, 2, 5, '危険イベント通知', 'taro.yamada@example.com', TIMESTAMP '2026-04-22 13:21:00', 'SENT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

ALTER TABLE users ALTER COLUMN id RESTART WITH 4;
ALTER TABLE event_categories ALTER COLUMN id RESTART WITH 6;
ALTER TABLE monitoring_settings ALTER COLUMN id RESTART WITH 4;
ALTER TABLE security_events ALTER COLUMN id RESTART WITH 6;
ALTER TABLE reports ALTER COLUMN id RESTART WITH 3;
ALTER TABLE attachments ALTER COLUMN id RESTART WITH 3;
ALTER TABLE email_notifications ALTER COLUMN id RESTART WITH 6;
