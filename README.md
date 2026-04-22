# LifeShield AI Backend

LifeShield AI の管理システム向けバックエンドです。  
Spring Boot、MyBatis、Spring Security を用いて、危険イベント管理、監視設定、レポート閲覧、添付ファイル管理、通知履歴管理の API を提供します。

## 概要

このアプリケーションは、管理者と一般ユーザーが利用する Web アプリケーションのバックエンドです。

- 管理者
  - 危険イベントの登録、更新、削除
  - 添付ファイルのアップロード
  - 通知履歴の確認
- 一般ユーザー
  - 危険イベント一覧・詳細の確認
  - 監視設定の更新
  - レポート閲覧

## 技術スタック

- Java 17
- Spring Boot 3.5.11
- Spring Security
- MyBatis
- H2 Database
- JUnit 5
- Maven Wrapper

## パッケージ構成

```text
src/main/java/com/example/lifeshieldai
├─ config
├─ controller
├─ dto
│  ├─ request
│  └─ response
├─ entity
├─ exception
├─ mapper
├─ security
├─ service
│  └─ impl
└─ LifeShieldAiApplication.java
```

## 主な API

- `POST /api/sessions`
  - ログイン
- `DELETE /api/sessions/current`
  - ログアウト
- `GET /api/security-events`
  - 危険イベント一覧取得
- `GET /api/security-events/{id}`
  - 危険イベント詳細取得
- `POST /api/security-events`
  - 危険イベント新規登録
- `PUT /api/security-events/{id}`
  - 危険イベント更新
- `DELETE /api/security-events/{id}`
  - 危険イベント削除
- `GET /api/security-events/csv`
  - 危険イベント CSV 出力
- `GET /api/monitoring-settings/me`
  - 自分の監視設定取得
- `PUT /api/monitoring-settings/me`
  - 自分の監視設定更新
- `GET /api/reports`
  - レポート一覧取得
- `GET /api/reports/{id}`
  - レポート詳細取得
- `GET /api/reports/csv`
  - レポート CSV 出力
- `POST /api/attachments`
  - 添付ファイルアップロード
- `GET /api/security-events/{id}/attachments`
  - 添付ファイル一覧取得
- `GET /api/email-notifications`
  - 通知履歴取得

## セキュリティ方針

- 認証方式
  - セッションベース認証
  - 管理画面向けのため、ブラウザ連携と CSRF 対策を優先
- パスワード
  - BCrypt ハッシュを保存
- CSRF
  - Cookie ベースの CSRF トークンを利用
- XSS
  - JSON API を前提にしつつ、画面側では出力時エスケープを前提
- SQL インジェクション
  - MyBatis のバインド変数で対策
- 権限制御
  - `ADMIN` と `USER` を区別
  - 危険イベント作成・更新・削除、通知履歴参照は `ADMIN` 限定

## 起動方法

### 1. コンパイル

```powershell
.\mvnw.cmd -q -DskipTests compile
```

### 2. テスト実行

```powershell
.\mvnw.cmd -q test
```

### 3. アプリ起動

```powershell
.\mvnw.cmd spring-boot:run
```

起動後の確認先:

- API ベース
  - `http://localhost:8080`
- H2 Console
  - `http://localhost:8080/h2-console`

H2 Console の接続情報:

- JDBC URL: `jdbc:h2:mem:lifeshield`
- User Name: `sa`
- Password: 空欄

## 初期データ

アプリ起動時に `schema.sql` と `data.sql` が読み込まれます。

初期ユーザー例:

- 管理者
  - `admin@lifeshield.ai`
- 一般ユーザー
  - `taro.yamada@example.com`
  - `hanako.sato@example.com`

注意:
- `data.sql` のパスワードは BCrypt ハッシュで投入しています
- テストや実運用では、必要に応じてハッシュ値を差し替えてください

## ファイルアップロード

- 保存先: `uploads/`
- 許可形式:
  - `image/png`
  - `image/jpeg`
  - `application/pdf`
- 最大サイズ:
  - 5MB

## テスト

現在含まれている主なテスト:

- Service 単体テスト
  - `SecurityEventServiceImplTest`
- Controller テスト
  - `SessionControllerTest`
  - `SecurityEventControllerTest`

確認済みコマンド:

```powershell
.\mvnw.cmd -q test
```

## GitHub へ push する手順

```powershell
git init
git add .
git commit -m "Implement LifeShield AI backend"
git branch -M main
git remote add origin https://github.com/<your-account>/life-shield-ai-backend.git
git push -u origin main
```

## 注意点

- `docs/` 配下の設計資料更新は、このリポジトリ内で継続しています
- `uploads/` はローカル保存前提です
- メール通知は履歴保存を優先したスタブ実装です
- 本番利用時は以下の差し替えが必要です
  - 永続 DB
  - 本物のメール送信基盤
  - 本番用 CORS / CSRF / セッション設定
  - 保存ファイルの外部ストレージ化

## 関連ドキュメント

- [仕様書](/c:/academia/src/Portfolio_school_01/docs/07-specification.html)
- [DB設計書](/c:/academia/src/Portfolio_school_01/docs/08-db-design.html)
- [テスト計画・報告](/c:/academia/src/Portfolio_school_01/docs/09-test-report.html)
