// Project archive shared scripts

function resolvePaths(navLevel) {
  const paths = {
    rootPath: './',
    docsPath: './docs/',
    designPath: './design/',
    promptsPath: './prompts/',
  };

  if (navLevel === 'docs') {
    return {
      rootPath: '../',
      docsPath: './',
      designPath: '../design/',
      promptsPath: '../prompts/',
    };
  }

  if (navLevel === 'design') {
    return {
      rootPath: '../',
      docsPath: '../docs/',
      designPath: './',
      promptsPath: '../prompts/',
    };
  }

  if (navLevel === 'prompts') {
    return {
      rootPath: '../',
      docsPath: '../docs/',
      designPath: '../design/',
      promptsPath: './',
    };
  }

  return paths;
}

function generateNavigation() {
  const sidebar = document.querySelector('.sidebar');
  if (!sidebar) return;

  const navLevel = sidebar.dataset.navLevel || 'root';
  const { rootPath, docsPath, designPath, promptsPath } = resolvePaths(navLevel);

  sidebar.innerHTML = `
    <div class="sidebar-header">
      <div class="logo">PROJECT ARCHIVE</div>
      <div class="project-name">プロジェクトアーカイブ</div>
    </div>
    <nav class="sidebar-nav">
      <div class="nav-group">
        <div class="nav-group-title">Project</div>
        <a href="${rootPath}index.html"><span class="material-symbols-outlined icon-sm">home</span> トップページ</a>
        <a href="${rootPath}about.html"><span class="material-symbols-outlined icon-sm">person</span> 自己紹介</a>
        <a href="${rootPath}works.html"><span class="material-symbols-outlined icon-sm">work</span> 成果物一覧</a>
        <a href="${rootPath}process.html"><span class="material-symbols-outlined icon-sm">assignment</span> 開発プロセス</a>
        <a href="${rootPath}skills.html"><span class="material-symbols-outlined icon-sm">bolt</span> スキルシート</a>
        <a href="${rootPath}contact.html"><span class="material-symbols-outlined icon-sm">mail</span> お問い合わせ</a>
      </div>
      <div class="nav-group">
        <div class="nav-group-title">Documents</div>
        <a href="${docsPath}01-proposal.html"><span class="nav-number">01</span> 企画提案書</a>
        <a href="${docsPath}02-market-research.html"><span class="nav-number">02</span> マーケットリサーチ</a>
        <a href="${docsPath}03-persona.html"><span class="nav-number">03</span> ペルソナシート</a>
        <a href="${docsPath}04-sitemap.html"><span class="nav-number">04</span> サイトマップ</a>
        <a href="${docsPath}05-wireframe.html"><span class="nav-number">05</span> ワイヤーフレーム</a>
        <a href="${docsPath}06-design-guide.html"><span class="nav-number">06</span> デザインガイドライン</a>
        <a href="${docsPath}07-specification.html"><span class="nav-number">07</span> 仕様書</a>
        <a href="${docsPath}08-db-design.html"><span class="nav-number">08</span> DB設計書</a>
        <a href="${docsPath}09-test-report.html"><span class="nav-number">09</span> テスト報告書</a>
        <a href="${docsPath}10-retrospective.html"><span class="nav-number">10</span> 振り返り・改善案</a>
      </div>
      <div class="nav-group">
        <div class="nav-group-title">Design</div>
        <a href="${designPath}system-flow.html"><span class="material-symbols-outlined icon-sm">account_tree</span> システムフロー図</a>
        <a href="${designPath}class-diagram.html"><span class="material-symbols-outlined icon-sm">lan</span> クラス図</a>
        <a href="${designPath}method-list.html"><span class="material-symbols-outlined icon-sm">list_alt</span> メソッド一覧</a>
        <a href="${designPath}logic-explanation.html"><span class="material-symbols-outlined icon-sm">search</span> ロジック解説</a>
      </div>
      <div class="nav-group">
        <div class="nav-group-title">Prompts</div>
        <a href="${promptsPath}prompt-step.html"><span class="material-symbols-outlined icon-sm">format_list_numbered</span> ステップ別プロンプト</a>
        <a href="${promptsPath}prompt-function.html"><span class="material-symbols-outlined icon-sm">extension</span> 機能実装用プロンプト</a>
        <a href="${promptsPath}prompt-log.html"><span class="material-symbols-outlined icon-sm">history</span> 実行ログ</a>
      </div>
    </nav>
    <div class="sidebar-footer">&copy; Project Archive 2026</div>
  `;
}

function bindSidebarToggle() {
  const hamburger = document.querySelector('.hamburger');
  const sidebar = document.querySelector('.sidebar');
  if (!hamburger || !sidebar) return;

  hamburger.addEventListener('click', () => {
    sidebar.classList.toggle('open');
  });

  document.addEventListener('click', (event) => {
    if (
      sidebar.classList.contains('open') &&
      !sidebar.contains(event.target) &&
      !hamburger.contains(event.target)
    ) {
      sidebar.classList.remove('open');
    }
  });

  sidebar.querySelectorAll('a').forEach((link) => {
    link.addEventListener('click', () => {
      if (window.innerWidth <= 768) {
        sidebar.classList.remove('open');
      }
    });
  });
}

function markActiveLink() {
  const currentPage = window.location.pathname.split('/').pop() || 'index.html';
  document.querySelectorAll('.sidebar-nav a').forEach((link) => {
    const href = link.getAttribute('href');
    if (!href) return;
    if (href.endsWith(currentPage) || (currentPage === 'index.html' && href === './index.html')) {
      link.classList.add('active');
    }
  });
}

function bindPromptCopy() {
  document.querySelectorAll('.prompt-box').forEach((box) => {
    const promptText = box.querySelector('.prompt-text');
    if (!promptText) return;

    promptText.style.cursor = 'pointer';
    promptText.title = 'クリックしてコピー';

    promptText.addEventListener('click', async () => {
      try {
        await navigator.clipboard.writeText(promptText.textContent);
        const originalBg = promptText.style.background;
        promptText.style.background = 'rgba(255,255,255,0.28)';
        setTimeout(() => {
          promptText.style.background = originalBg || 'rgba(0, 0, 0, 0.35)';
        }, 500);
      } catch (error) {
        // Clipboard may be unavailable under file:// preview.
      }
    });
  });
}

function bindTocSmoothScroll() {
  document.querySelectorAll('.toc-list a').forEach((link) => {
    link.addEventListener('click', (event) => {
      const href = link.getAttribute('href');
      if (!href || !href.startsWith('#')) return;
      event.preventDefault();

      const target = document.querySelector(href);
      if (target) {
        target.scrollIntoView({ behavior: 'smooth', block: 'start' });
      }
    });
  });
}

document.addEventListener('DOMContentLoaded', () => {
  generateNavigation();
  bindSidebarToggle();
  markActiveLink();
  bindPromptCopy();
  bindTocSmoothScroll();
});
