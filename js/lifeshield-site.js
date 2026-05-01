(() => {
  const header = document.querySelector('.site-header');
  const toggle = document.querySelector('.mobile-nav-toggle');
  const navLinks = document.querySelectorAll('.site-nav a');
  const menuToggle = document.querySelector('.brand-menu-toggle');
  const contactForm = document.querySelector('.contact-form');
  const formStatus = document.querySelector('.form-status');
  const emailMonitoringForm = document.querySelector('.email-monitoring-form');

  if (header && toggle) {
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
        if (!header.contains(event.target)) {
          header.setAttribute('data-menu', 'false');
          menuToggle.setAttribute('aria-expanded', 'false');
        }
      });

      document.addEventListener('keydown', (event) => {
        if (event.key === 'Escape') {
          header.setAttribute('data-menu', 'false');
          menuToggle.setAttribute('aria-expanded', 'false');
        }
      });
    }

    navLinks.forEach((link) => {
      link.addEventListener('click', () => {
        header.setAttribute('data-menu', 'false');
        if (menuToggle) {
          menuToggle.setAttribute('aria-expanded', 'false');
        }
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
      header.setAttribute('data-menu', 'false');
      if (menuToggle) {
        menuToggle.setAttribute('aria-expanded', 'false');
      }
    });
  }

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

  if (contactForm && formStatus) {
    contactForm.addEventListener('submit', (event) => {
      if (contactForm.classList.contains('email-monitoring-form')) return;
      event.preventDefault();
      formStatus.classList.add('is-visible');
      contactForm.reset();
    });
  }

  if (emailMonitoringForm) {
    emailMonitoringForm.addEventListener('submit', async (event) => {
      event.preventDefault();
      const status = emailMonitoringForm.querySelector('.email-monitoring-status');
      const apiBase = emailMonitoringForm.dataset.apiBase || '';
      const formData = new FormData(emailMonitoringForm);
      const payload = {
        senderEmail: String(formData.get('senderEmail') || ''),
        subject: String(formData.get('subject') || ''),
        body: String(formData.get('body') || ''),
        hasUrl: formData.has('hasUrl'),
        hasAttachment: formData.has('hasAttachment')
      };

      if (!emailMonitoringForm.reportValidity()) return;
      setStatus(status, '判定中です。しばらくお待ちください。', true);

      try {
        const response = await fetch(`${apiBase}/api/email-monitoring/check`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            ...csrfHeader()
          },
          credentials: 'include',
          body: JSON.stringify(payload)
        });

        if (!response.ok) {
          throw new Error(response.status === 401 ? 'ログイン後に利用できます。' : '判定に失敗しました。入力内容とAPIの起動状態を確認してください。');
        }

        const result = await response.json();
        renderEmailMonitoringResult(result);
        setStatus(status, '判定結果を保存しました。', true);
      } catch (error) {
        setStatus(status, error.message, true);
      }
    });
  }

  function csrfHeader() {
    const token = document.cookie
      .split('; ')
      .find((row) => row.startsWith('XSRF-TOKEN='))
      ?.split('=')[1];
    return token ? { 'X-XSRF-TOKEN': decodeURIComponent(token) } : {};
  }

  function setStatus(status, message, visible) {
    if (!status) return;
    status.textContent = message;
    status.classList.toggle('is-visible', visible);
  }

  function renderEmailMonitoringResult(result) {
    const risk = document.querySelector('[data-result-risk]');
    const reason = document.querySelector('[data-result-reason]');
    const recommendation = document.querySelector('[data-result-recommendation]');
    const event = document.querySelector('[data-result-event]');
    if (risk) risk.textContent = result.riskLevel || '未判定';
    if (reason) reason.textContent = result.reason || '';
    if (recommendation) recommendation.textContent = result.recommendation || '';
    if (event) event.textContent = result.event?.id ? `危険イベントID ${result.event.id} として保存済み` : '保存結果を確認できませんでした';
  }
})();
