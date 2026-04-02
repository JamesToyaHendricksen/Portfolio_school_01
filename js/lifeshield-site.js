(() => {
  const header = document.querySelector('.site-header');
  const toggle = document.querySelector('.mobile-nav-toggle');
  const navLinks = document.querySelectorAll('.site-nav a');
  const contactForm = document.querySelector('.contact-form');
  const formStatus = document.querySelector('.form-status');

  if (header && toggle) {
    toggle.addEventListener('click', () => {
      const isOpen = header.getAttribute('data-open') === 'true';
      header.setAttribute('data-open', String(!isOpen));
      toggle.setAttribute('aria-expanded', String(!isOpen));
    });

    navLinks.forEach((link) => {
      link.addEventListener('click', () => {
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
