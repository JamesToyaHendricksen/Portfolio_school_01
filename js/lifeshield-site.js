(() => {
  const DEFAULT_REMOTE_API = 'https://portfolio-school-01.onrender.com';
  const DEFAULT_LOCAL_API = 'http://localhost:8080';
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

  setupHeader();
  setupSmoothScroll();
  setupContactForm();
  setupLoginForm();
  setupLogout();
  setupEmailMonitoringForm();
  setupSessionRequiredViews();
  loadDashboardSummary();
  loadSecurityEventsTable();

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
    const headers = { ...(options.headers || {}) };
    if (options.body) headers['Content-Type'] = 'application/json';
    if (!options.skipCsrf && options.method && options.method !== 'GET') {
      const csrf = await getCsrf();
      headers[csrf.headerName] = csrf.token;
    }

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
  }

  async function getCsrf() {
    const response = await fetch(`${apiBase()}/api/csrf`, { credentials: 'include' });
    return response.json();
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
