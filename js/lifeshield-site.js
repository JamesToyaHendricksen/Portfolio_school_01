(() => {
  const DEFAULT_REMOTE_API = 'https://portfolio-school-01.onrender.com';
  const DEFAULT_LOCAL_API = 'http://localhost:8080';
  const DEMO_MODE_KEY = 'lifeshieldDemoMode';
  const DEMO_USER_KEY = 'lifeshieldDemoUser';
  const DEMO_EVENTS_KEY = 'lifeshieldDemoEvents';

  const header = document.querySelector('.site-header');
  const toggle = document.querySelector('.mobile-nav-toggle');
  const navLinks = document.querySelectorAll('.site-nav a');
  const menuToggle = document.querySelector('.brand-menu-toggle');
  const contactForm = document.querySelector('.contact-form:not(.email-monitoring-form):not(.login-form)');
  const formStatus = contactForm?.querySelector('.form-status');
  const loginForm = document.querySelector('.login-form');
  const logoutButtons = document.querySelectorAll('[data-logout]');
  const emailMonitoringForm = document.querySelector('.email-monitoring-form');
  const sessionRequired = document.querySelector('[data-require-session]');
  const dashboardSummary = document.querySelector('[data-dashboard-summary]');
  const securityEventsTable = document.querySelector('[data-security-events-table]');

  initializeDemoMode();
  setupHeader();
  setupSmoothScroll();
  setupContactForm();
  setupLoginForm();
  setupLogout();
  setupEmailMonitoringForm();
  setupSessionRequiredViews();
  loadDashboardSummary();
  loadSecurityEventsTable();
  renderDemoBadgeIfNeeded();

  function initializeDemoMode() {
    const mode = new URLSearchParams(window.location.search).get('mode');
    if (mode === 'demo') {
      localStorage.setItem(DEMO_MODE_KEY, 'true');
      seedDemoDataIfNeeded();
      return;
    }
    if (mode === 'live') {
      localStorage.setItem(DEMO_MODE_KEY, 'false');
      return;
    }
    if (isDemoMode()) seedDemoDataIfNeeded();
  }

  function isDemoMode() {
    return localStorage.getItem(DEMO_MODE_KEY) === 'true';
  }

  function enableDemoMode() {
    localStorage.setItem(DEMO_MODE_KEY, 'true');
    seedDemoDataIfNeeded();
    renderDemoBadgeIfNeeded();
  }

  function seedDemoDataIfNeeded() {
    if (!sessionStorage.getItem(DEMO_EVENTS_KEY)) {
      const now = new Date();
      const seed = [
        {
          id: 1,
          userId: 1,
          eventType: 'MAIL',
          riskLevel: '中',
          status: '未対応',
          channel: 'メール',
          detectedAt: new Date(now.getTime() - 3600 * 1000).toISOString(),
          reason: '疑うべき項目: 文面キーワード「確認してください」、本文にURLが含まれている。',
          recommendation: '本文と送信元を再確認し、少しでも不審なら操作を中止してください。'
        }
      ];
      sessionStorage.setItem(DEMO_EVENTS_KEY, JSON.stringify(seed));
    }
  }

  function renderDemoBadgeIfNeeded() {
    if (!isDemoMode()) return;
    if (document.querySelector('[data-demo-badge]')) return;
    const badge = document.createElement('div');
    badge.setAttribute('data-demo-badge', 'true');
    badge.textContent = 'デモモード: API停止中でも操作できます';
    badge.style.position = 'fixed';
    badge.style.right = '16px';
    badge.style.bottom = '16px';
    badge.style.zIndex = '9999';
    badge.style.padding = '10px 14px';
    badge.style.borderRadius = '999px';
    badge.style.background = 'rgba(15, 23, 42, 0.85)';
    badge.style.color = '#fff';
    badge.style.fontSize = '12px';
    badge.style.boxShadow = '0 8px 24px rgba(15, 23, 42, 0.3)';
    document.body.appendChild(badge);
  }

  function setupHeader() {
    if (!header || !toggle) return;
    header.setAttribute('data-menu', 'false');

    toggle.addEventListener('click', () => {
      const isOpen = header.getAttribute('data-open') === 'true';
      header.setAttribute('data-open', String(!isOpen));
      toggle.setAttribute('aria-expanded', String(!isOpen));
    });

    if (menuToggle) {
      menuToggle.addEventListener('click', (event) => {
        event.stopPropagation();
        const isOpen = header.getAttribute('data-menu') === 'true';
        header.setAttribute('data-menu', String(!isOpen));
        menuToggle.setAttribute('aria-expanded', String(!isOpen));
      });

      document.addEventListener('click', (event) => {
        if (!header.contains(event.target)) closeMenus();
      });

      document.addEventListener('keydown', (event) => {
        if (event.key === 'Escape') closeMenus();
      });
    }

    navLinks.forEach((link) => {
      link.addEventListener('click', () => {
        closeMenus();
        if (window.innerWidth < 769) {
          header.setAttribute('data-open', 'false');
          toggle.setAttribute('aria-expanded', 'false');
        }
      });
    });

    window.addEventListener('resize', () => {
      if (window.innerWidth >= 769) {
        header.setAttribute('data-open', 'false');
        toggle.setAttribute('aria-expanded', 'false');
      }
      closeMenus();
    });
  }

  function closeMenus() {
    if (!header) return;
    header.setAttribute('data-menu', 'false');
    if (menuToggle) menuToggle.setAttribute('aria-expanded', 'false');
  }

  function setupSmoothScroll() {
    document.querySelectorAll('a[href^="#"]').forEach((anchor) => {
      anchor.addEventListener('click', (event) => {
        const href = anchor.getAttribute('href');
        if (!href || href === '#') return;
        const target = document.querySelector(href);
        if (!target) return;
        event.preventDefault();
        target.scrollIntoView({ behavior: 'smooth', block: 'start' });
      });
    });
  }

  function setupContactForm() {
    if (!contactForm || !formStatus) return;
    contactForm.addEventListener('submit', (event) => {
      event.preventDefault();
      formStatus.textContent = '送信されました。内容を確認のうえご連絡します。';
      formStatus.classList.add('is-visible');
      contactForm.reset();
    });
  }

  function setupLoginForm() {
    if (!loginForm) return;
    loginForm.addEventListener('submit', async (event) => {
      event.preventDefault();
      const status = loginForm.querySelector('.form-status');
      const formData = new FormData(loginForm);
      setStatus(status, 'ログインしています。', true, false);

      try {
        const response = await apiFetch('/api/sessions', {
          method: 'POST',
          body: JSON.stringify({
            email: String(formData.get('email') || ''),
            password: String(formData.get('password') || '')
          }),
          skipCsrf: true
        });
        const user = await response.json();
        sessionStorage.setItem('lifeshieldUser', JSON.stringify(user));
        setStatus(status, 'ログインしました。管理ダッシュボードへ移動します。', true, false);
        window.location.href = 'admin-dashboard.html';
      } catch (error) {
        setStatus(status, error.message || 'ログインに失敗しました。', true, true);
      }
    });
  }

  function setupLogout() {
    logoutButtons.forEach((button) => {
      button.addEventListener('click', async () => {
        try {
          await apiFetch('/api/sessions/current', { method: 'DELETE' });
        } catch (error) {
          // セッション切れでも画面上はログアウト済みとして扱う。
        }
        sessionStorage.removeItem('lifeshieldUser');
        sessionStorage.removeItem(DEMO_USER_KEY);
        window.location.href = 'login.html';
      });
    });
  }

  function setupEmailMonitoringForm() {
    if (!emailMonitoringForm) return;
    emailMonitoringForm.addEventListener('submit', async (event) => {
      event.preventDefault();
      const status = emailMonitoringForm.querySelector('.email-monitoring-status');
      if (!emailMonitoringForm.reportValidity()) return;

      const formData = new FormData(emailMonitoringForm);
      const payload = {
        senderEmail: String(formData.get('senderEmail') || ''),
        subject: String(formData.get('subject') || ''),
        body: String(formData.get('body') || ''),
        hasUrl: formData.has('hasUrl'),
        hasAttachment: formData.has('hasAttachment')
      };

      setStatus(status, 'メールを判定しています。', true, false);

      try {
        const response = await apiFetch('/api/email-monitoring/check', {
          method: 'POST',
          body: JSON.stringify(payload)
        });
        const result = await response.json();
        renderEmailMonitoringResult(result);
        setStatus(status, '判定結果を保存しました。危険イベント一覧でも確認できます。', true, false);
      } catch (error) {
        showLoginPromptIfUnauthorized(error);
        setStatus(status, error.message, true, true);
      }
    });
  }

  async function setupSessionRequiredViews() {
    if (!sessionRequired) return;
    const status = sessionRequired.querySelector('[data-session-status]');
    try {
      const user = await currentUser();
      sessionStorage.setItem('lifeshieldUser', JSON.stringify(user));
      sessionRequired.dataset.authenticated = 'true';
      if (status) status.textContent = `${user.name} さんでログイン中です。`;
    } catch (error) {
      sessionRequired.dataset.authenticated = 'false';
      if (status) status.textContent = '管理機能を使うにはログインしてください。';
    }
  }

  async function loadDashboardSummary() {
    if (!dashboardSummary) return;
    try {
      await currentUser();
      const response = await apiFetch('/api/security-events?size=100');
      const data = await response.json();
      const events = data.content || [];
      dashboardSummary.querySelector('[data-total-events]').textContent = String(data.totalElements ?? events.length);
      dashboardSummary.querySelector('[data-high-risk]').textContent = String(events.filter((event) => event.riskLevel === '高').length);
      dashboardSummary.querySelector('[data-mail-events]').textContent = String(events.filter((event) => event.eventType === 'MAIL' || event.channel === 'メール').length);
    } catch (error) {
      dashboardSummary.querySelector('[data-dashboard-message]').textContent = 'ログインすると管理ダッシュボードを確認できます。';
    }
  }

  async function loadSecurityEventsTable() {
    if (!securityEventsTable) return;
    const tbody = securityEventsTable.querySelector('tbody');
    const message = document.querySelector('[data-events-message]');
    try {
      await currentUser();
      const response = await apiFetch('/api/security-events?sortBy=detectedAt&sortDir=desc&size=20');
      const data = await response.json();
      const events = data.content || [];
      tbody.innerHTML = events.map((event) => `
        <tr>
          <td>${escapeHtml(event.id)}</td>
          <td>${escapeHtml(event.detectedAt ? formatDate(event.detectedAt) : '-')}</td>
          <td>${escapeHtml(event.eventType)}</td>
          <td><span class="risk-badge risk-${riskClass(event.riskLevel)}">${escapeHtml(event.riskLevel)}</span></td>
          <td>${escapeHtml(event.status)}</td>
          <td>${escapeHtml(event.channel)}</td>
          <td>${escapeHtml(event.reason)}</td>
        </tr>
      `).join('');
      if (message) message.textContent = `${events.length}件を表示しています。`;
    } catch (error) {
      tbody.innerHTML = '';
      if (message) message.textContent = 'ログインすると危険イベント一覧を確認できます。';
    }
  }

  async function currentUser() {
    const response = await apiFetch('/api/sessions/current', { method: 'GET', skipCsrf: true });
    return response.json();
  }

  async function apiFetch(path, options = {}) {
    if (isDemoMode()) return demoFetch(path, options);

    const headers = { ...(options.headers || {}) };
    if (options.body) headers['Content-Type'] = 'application/json';
    if (!options.skipCsrf && options.method && options.method !== 'GET') {
      const csrf = await getCsrf();
      headers[csrf.headerName] = csrf.token;
    }

    try {
      const response = await fetch(`${apiBase()}${path}`, {
        method: options.method || 'GET',
        headers,
        credentials: 'include',
        body: options.body
      });

      if (!response.ok) {
        let message = response.status === 401
          ? 'ログイン後に利用できます。'
          : '処理に失敗しました。入力内容とAPIの起動状態を確認してください。';
        try {
          const error = await response.json();
          message = error.message || message;
        } catch (error) {
          // JSONではないエラーは既定メッセージを使う。
        }
        const err = new Error(message);
        err.status = response.status;
        throw err;
      }
      return response;
    } catch (error) {
      enableDemoMode();
      return demoFetch(path, options);
    }
  }

  async function getCsrf() {
    if (isDemoMode()) {
      return { headerName: 'X-XSRF-TOKEN', parameterName: '_csrf', token: 'demo-token' };
    }
    const response = await fetch(`${apiBase()}/api/csrf`, { credentials: 'include' });
    return response.json();
  }

  function demoFetch(path, options = {}) {
    const method = (options.method || 'GET').toUpperCase();
    const body = options.body ? JSON.parse(options.body) : null;

    if (path === '/api/csrf' && method === 'GET') {
      return jsonResponse({ headerName: 'X-XSRF-TOKEN', parameterName: '_csrf', token: 'demo-token' });
    }

    if (path === '/api/sessions' && method === 'POST') {
      const email = String(body?.email || '').trim();
      const password = String(body?.password || '').trim();
      if (!email || !password) return errorResponse(400, '入力内容が不正です。');
      const user = {
        id: 1,
        name: '管理者ユーザー',
        email,
        role: 'ADMIN',
        message: 'ログインしました'
      };
      sessionStorage.setItem(DEMO_USER_KEY, JSON.stringify(user));
      return jsonResponse(user);
    }

    if (path === '/api/sessions/current' && method === 'GET') {
      const user = getDemoUser();
      if (!user) return errorResponse(401, 'ログイン後に利用できます。');
      return jsonResponse({ ...user, message: 'ログイン中です' });
    }

    if (path === '/api/sessions/current' && method === 'DELETE') {
      sessionStorage.removeItem(DEMO_USER_KEY);
      return jsonResponse({});
    }

    if (path.startsWith('/api/security-events') && method === 'GET') {
      const user = getDemoUser();
      if (!user) return errorResponse(401, 'ログイン後に利用できます。');
      const events = getDemoEvents().slice().sort((a, b) => b.id - a.id);
      const content = events.slice(0, parseSize(path, 20));
      return jsonResponse({
        content,
        totalElements: events.length,
        totalPages: 1,
        number: 0,
        size: content.length,
        first: true,
        last: true
      });
    }

    if (path === '/api/email-monitoring/check' && method === 'POST') {
      const user = getDemoUser();
      if (!user) return errorResponse(401, 'ログイン後に利用できます。');

      const result = analyzeMailInDemo(body || {});
      const events = getDemoEvents();
      const nextId = events.length ? Math.max(...events.map((e) => e.id)) + 1 : 1;
      const event = {
        id: nextId,
        userId: user.id,
        eventType: 'MAIL',
        riskLevel: result.riskLevel,
        status: '未対応',
        channel: 'メール',
        detectedAt: new Date().toISOString(),
        reason: result.reason,
        recommendation: result.recommendation
      };
      events.unshift(event);
      sessionStorage.setItem(DEMO_EVENTS_KEY, JSON.stringify(events));

      return jsonResponse({
        riskLevel: result.riskLevel,
        reason: result.reason,
        recommendation: result.recommendation,
        matchedRules: result.matchedRules,
        event,
        notificationCreated: result.riskLevel === '高'
      });
    }

    return errorResponse(404, '未対応のデモAPIです。');
  }

  function analyzeMailInDemo(payload) {
    const keywords = ['至急', '確認してください', 'アカウント停止', '支払い', 'パスワード', '本人確認', '今すぐ', '期限'];
    const text = `${payload.subject || ''}\n${payload.body || ''}`.toLowerCase();
    const matched = [];
    let score = 0;

    keywords.forEach((keyword) => {
      if (text.includes(keyword.toLowerCase())) {
        matched.push(`文面キーワード「${keyword}」`);
        score += 2;
      }
    });
    if (payload.hasUrl) {
      matched.push('本文にURLが含まれている');
      score += 2;
    }
    if (payload.hasAttachment) {
      matched.push('添付ファイルが含まれている');
      score += 1;
    }

    const riskLevel = score >= 5 ? '高' : score >= 2 ? '中' : '低';
    const reason = matched.length
      ? `疑うべき項目: ${matched.join('、')}。`
      : '疑うべき項目は検出されませんでした。';
    const recommendation = riskLevel === '高'
      ? 'リンクや添付ファイルは開かず、送信元の正当性を別経路で確認してください。'
      : riskLevel === '中'
        ? '本文と送信元を再確認し、少しでも不審なら操作を中止してください。'
        : '現時点では重大なリスクは見つかっていません。';
    return { riskLevel, reason, recommendation, matchedRules: matched };
  }

  function getDemoUser() {
    try {
      return JSON.parse(sessionStorage.getItem(DEMO_USER_KEY) || 'null');
    } catch {
      return null;
    }
  }

  function getDemoEvents() {
    try {
      return JSON.parse(sessionStorage.getItem(DEMO_EVENTS_KEY) || '[]');
    } catch {
      return [];
    }
  }

  function parseSize(path, fallback) {
    try {
      const u = new URL(path, 'https://dummy.local');
      const s = Number(u.searchParams.get('size'));
      return Number.isFinite(s) && s > 0 ? s : fallback;
    } catch {
      return fallback;
    }
  }

  function jsonResponse(data) {
    return {
      ok: true,
      status: 200,
      json: async () => data
    };
  }

  function errorResponse(status, message) {
    const err = new Error(message);
    err.status = status;
    throw err;
  }

  function apiBase() {
    const explicit = document.body.dataset.apiBase || document.querySelector('[data-api-base]')?.dataset.apiBase;
    if (explicit) return explicit;
    return window.location.protocol === 'file:' ? DEFAULT_LOCAL_API : DEFAULT_REMOTE_API;
  }

  function setStatus(status, message, visible, isError) {
    if (!status) return;
    status.textContent = message;
    status.classList.toggle('is-visible', visible);
    status.classList.toggle('is-error', Boolean(isError));
  }

  function renderEmailMonitoringResult(result) {
    const risk = document.querySelector('[data-result-risk]');
    const reason = document.querySelector('[data-result-reason]');
    const recommendation = document.querySelector('[data-result-recommendation]');
    const event = document.querySelector('[data-result-event]');
    if (risk) risk.textContent = result.riskLevel || '未判定';
    if (reason) reason.textContent = result.reason || '';
    if (recommendation) recommendation.textContent = result.recommendation || '';
    if (event) {
      event.textContent = result.event?.id
        ? `危険イベントID ${result.event.id} として保存済み`
        : '保存結果を確認できませんでした';
    }
  }

  function showLoginPromptIfUnauthorized(error) {
    if (error.status !== 401) return;
    const prompt = document.querySelector('[data-login-prompt]');
    if (prompt) prompt.hidden = false;
  }

  function formatDate(value) {
    return new Date(value).toLocaleString('ja-JP', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  function riskClass(riskLevel) {
    if (riskLevel === '高') return 'high';
    if (riskLevel === '中') return 'medium';
    return 'low';
  }

  function escapeHtml(value) {
    return String(value ?? '').replace(/[&<>"']/g, (char) => ({
      '&': '&amp;',
      '<': '&lt;',
      '>': '&gt;',
      '"': '&quot;',
      "'": '&#39;'
    })[char]);
  }
})();
