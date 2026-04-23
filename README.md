# LifeShield AI

LifeShield AI は、個人向けの AI セキュリティサービスを想定した制作課題プロジェクトです。  
このリポジトリでは、**サービス紹介用フロントエンド** と **管理システム用バックエンド** の両方を扱っています。

## プロジェクト概要

LifeShield AI は、AI が個人のデジタル行動を学習し、フィッシング、詐欺、不審メール、SNS 上の異常、アカウント攻撃の兆候を分かりやすく整理して伝えるサービスです。

このプロジェクトでは、次の 2 つを並行して制作しています。

- フロントエンド
  - サービス紹介サイト
  - LP、下層ページ、デザインガイド、ワイヤーフレーム反映
- バックエンド
  - 管理システム API
  - Spring Boot、MyBatis、Spring Security を用いた危険イベント管理機能

## このリポジトリに含まれるもの

### 1. フロントエンド

`lifeshield-ai/` 配下に、公開用サイトの HTML があります。

主なページ:

- [トップページ](/c:/academia/src/Portfolio_school_01/lifeshield-ai/index.html)
- [サービス](/c:/academia/src/Portfolio_school_01/lifeshield-ai/concept.html)
- [機能紹介](/c:/academia/src/Portfolio_school_01/lifeshield-ai/features.html)
- [ダッシュボード紹介](/c:/academia/src/Portfolio_school_01/lifeshield-ai/dashboard-preview.html)
- [料金](/c:/academia/src/Portfolio_school_01/lifeshield-ai/pricing.html)
- [お知らせ](/c:/academia/src/Portfolio_school_01/lifeshield-ai/news.html)
- [サポート](/c:/academia/src/Portfolio_school_01/lifeshield-ai/support.html)
- [お問い合わせ](/c:/academia/src/Portfolio_school_01/lifeshield-ai/contact.html)

主な技術:

- HTML
- CSS
- JavaScript
- GitHub Pages

### 2. バックエンド

`src/main/java/com/example/lifeshieldai/` 配下に、管理システムのバックエンドを実装しています。

主な API:

- `POST /api/sessions`
- `DELETE /api/sessions/current`
- `GET /api/security-events`
- `GET /api/security-events/{id}`
- `POST /api/security-events`
- `PUT /api/security-events/{id}`
- `DELETE /api/security-events/{id}`
- `GET /api/security-events/csv`
- `GET /api/monitoring-settings/me`
- `PUT /api/monitoring-settings/me`
- `GET /api/reports`
- `GET /api/reports/{id}`
- `GET /api/reports/csv`
- `POST /api/attachments`
- `GET /api/security-events/{id}/attachments`
- `GET /api/email-notifications`

主な技術:

- Java 17
- Spring Boot 3.5.11
- Spring Security
- MyBatis
- H2 Database
- JUnit 5

## 設計資料

設計・仕様の整理は `docs/` 配下で行っています。

- [企画提案書](/c:/academia/src/Portfolio_school_01/docs/01-proposal.html)
- [サイトマップ](/c:/academia/src/Portfolio_school_01/docs/04-sitemap.html)
- [デザインガイド](/c:/academia/src/Portfolio_school_01/docs/06-design-guide.html)
- [仕様書](/c:/academia/src/Portfolio_school_01/docs/07-specification.html)
- [DB設計書](/c:/academia/src/Portfolio_school_01/docs/08-db-design.html)
- [テスト報告書](/c:/academia/src/Portfolio_school_01/docs/09-test-report.html)

## フロントエンド確認方法

ローカルで確認する場合:

- `file:///C:/academia/src/Portfolio_school_01/lifeshield-ai/index.html`

公開 URL:

- `https://jamestoyahendricksen.github.io/Portfolio_school_01/`

## バックエンド起動方法

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

確認先:

- API ベース
  - `http://localhost:8080`
- H2 Console
  - `http://localhost:8080/h2-console`

H2 Console 接続情報:

- JDBC URL: `jdbc:h2:mem:lifeshield`
- User Name: `sa`
- Password: 空欄

## セキュリティ方針

- 認証方式
  - セッションベース認証
- パスワード
  - BCrypt ハッシュを保存
- CSRF
  - Cookie ベースの CSRF トークンを利用
- 権限制御
  - `ADMIN` と `USER` を区別
- SQL インジェクション対策
  - MyBatis のバインド変数を利用

## 初期データ

アプリ起動時に `schema.sql` と `data.sql` が読み込まれます。

主な初期データ:

- 管理者ユーザー 1 件
- 一般ユーザー 2 件
- 危険イベントサンプル 5 件
- レポートサンプル
- 監視設定
- 通知履歴

## テスト

バックエンドでは、以下を中心に確認しています。

- Service 単体テスト
- Controller テスト
- バリデーション
- 認証・認可
- 正常系 / 異常系 / 境界値

確認済みコマンド:

```powershell
.\mvnw.cmd -q test
```

## GitHub へ push する手順

```powershell
git init
git add .
git commit -m "Update LifeShield AI frontend and backend"
git branch -M main
git remote add origin https://github.com/<your-account>/life-shield-ai-backend.git
git push -u origin main
```

必要に応じて、既存のフロントエンド用リポジトリ名や運用方針に合わせて変更してください。

## 補足

- フロントエンドとバックエンドは同一リポジトリ内で進行中です
- `docs/` は設計・成果物整理用です
- `uploads/` はローカル保存前提です
- メール通知は履歴保存を優先したスタブ実装です
- 本番運用時は、永続 DB、メール送信基盤、保存先設計の差し替えが必要です

## Author

Toya Toyoda
