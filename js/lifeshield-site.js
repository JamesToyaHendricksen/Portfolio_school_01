(() => {
  const header = document.querySelector('.site-header');
  const toggle = document.querySelector('.mobile-nav-toggle');
  const navLinks = document.querySelectorAll('.site-nav a');
  const menuToggle = document.querySelector('.brand-menu-toggle');
  const contactForm = document.querySelector('.contact-form');
  const formStatus = document.querySelector('.form-status');

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
      event.preventDefault();
      formStatus.classList.add('is-visible');
      contactForm.reset();
    });
  }
})();
