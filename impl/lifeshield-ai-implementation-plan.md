# LifeShield AI 実装メモ

## 中間構成を前提にした全体構成図

```text
[個人PCユーザー]
        │
        │ ブラウザ操作
        ▼
[フロントエンド]
HTML / CSS / JavaScript
公開サイト + 管理画面
        │
        │ HTTP / JSON
        ▼
[Spring Boot バックエンド]
Controller
  ├─ 認証・認可
  ├─ 危険イベント管理
  ├─ 監視設定管理
  ├─ レポート管理
  ├─ CSV出力
  ├─ ファイルアップロード
  └─ メール通知
        │
        │ 業務処理
        ▼
[Service]
  ├─ ルール判定前処理
  ├─ Hugging Face API連携
  ├─ 判定結果整形
  ├─ 通知判定
  └─ レポート生成
        │
        ├─────────────┐
        │             │
        │             │ 外部API呼び出し
        ▼             ▼
[MyBatis / Mapper]   [Hugging Face Inference API]
        │             └─ 詐欺検知
        │             └─ メール監視判定
        │             └─ SNS文面判定
        │
        ▼
[MySQL / H2]
  ├─ users
  ├─ security_events
  ├─ event_categories
  ├─ monitoring_settings
  ├─ reports
  ├─ attachments
  └─ email_notifications
        │
        │ 判定結果・履歴保存
        ▼
[管理システム画面]
  ├─ ログイン
  ├─ ダッシュボード
  ├─ 危険イベント一覧 / 詳細
  ├─ 監視設定
  ├─ レポート
  └─ 通知確認
```

## 構成の考え方

- フロントエンドは既存の `lifeshield-ai/` を活用する
- バックエンドは Spring Boot + MyBatis で構築する
- 危険判定そのものは Hugging Face API を利用して負荷を下げる
- 判定結果、履歴、設定、通知は自前DBへ保存する
- 管理画面は自前で持ち、外部AIは判定エンジンとして使う

## この構成の役割分担

### フロントエンド

- 入力フォームの表示
- 危険イベントやレポートの可視化
- 監視設定の変更
- ログイン後の画面操作

### バックエンド

- 認証と権限制御
- フロントからの入力バリデーション最終確認
- Hugging Face API への問い合わせ
- 判定結果の保存
- CSV出力、添付ファイル、通知処理

### Hugging Face

- 文章ベースの危険判定
- メール文面やSNS文面の分類
- AI 詐欺検知の補助

### データベース

- ユーザー情報
- 危険イベント履歴
- 監視設定
- レポート
- 通知履歴
- 添付ファイル情報

## 実装の進め方

1. まずは `メール監視` を1機能目として実装する
2. 判定対象データを手動入力で受ける
3. Spring Boot から Hugging Face API を呼ぶ
4. 判定結果を `security_events` に保存する
5. 管理画面で一覧・詳細表示する
6. 次に `AI 詐欺検知`、`SNS 保護`、`通信異常検知` へ横展開する

## 次の具体タスク

- メール監視機能の入力項目を確定する
- 判定ルールと Hugging Face 利用対象を確定する
- メール監視用 API の入出力を定義する
- `security_events` への保存項目を対応付ける
- 一覧・詳細画面へ表示する項目を決める
