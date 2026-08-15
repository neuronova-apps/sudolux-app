(() => {
  const menuButton = document.querySelector('.menu-button');
  const mainNav = document.querySelector('.main-nav');
  const board = document.querySelector('#sudokuBoard');
  const numberPad = document.querySelector('#numberPad');
  const eraseButton = document.querySelector('#eraseButton');
  const checkButton = document.querySelector('#checkButton');
  const gameStatus = document.querySelector('#gameStatus');

  function createInstructions() {
    if (!board || document.querySelector('#sudokuInstructions')) return;
    const instructions = document.createElement('p');
    instructions.id = 'sudokuInstructions';
    instructions.textContent = 'Tablero de 9 filas y 9 columnas. Usa las flechas para moverte entre casillas. En una casilla editable, usa las teclas 1 a 9 para introducir un valor. Pulsa N para alternar el modo Notas y Backspace o Delete para borrar.';
    Object.assign(instructions.style, {
      position: 'absolute',
      width: '1px',
      height: '1px',
      padding: '0',
      margin: '-1px',
      overflow: 'hidden',
      clip: 'rect(0, 0, 0, 0)',
      whiteSpace: 'nowrap',
      border: '0'
    });
    board.before(instructions);
  }

  function syncGridAccessibility() {
    if (!board) return;
    const cells = [...board.querySelectorAll('[role="gridcell"]')];
    board.setAttribute('aria-rowcount', '9');
    board.setAttribute('aria-colcount', '9');
    board.setAttribute('aria-describedby', 'sudokuInstructions gameStatus');

    cells.forEach((cell, index) => {
      const row = Math.floor(index / 9) + 1;
      const column = (index % 9) + 1;
      cell.setAttribute('aria-rowindex', String(row));
      cell.setAttribute('aria-colindex', String(column));
      cell.setAttribute('aria-selected', String(cell.classList.contains('selected')));
      cell.setAttribute('aria-readonly', String(cell.classList.contains('given')));
      cell.setAttribute('aria-invalid', String(cell.classList.contains('invalid')));
    });
  }

  function focusSelectedCell() {
    const selected = board?.querySelector('[role="gridcell"][tabindex="0"]');
    selected?.focus({preventScroll: true});
  }

  function focusStatus() {
    if (!gameStatus) return;
    gameStatus.tabIndex = -1;
    gameStatus.focus({preventScroll: true});
  }

  function normalizeFooterContact() {
    const columns = [...document.querySelectorAll('.site-footer .footer-column')];
    const contactColumn = columns.find(column => column.querySelector('h2')?.textContent.trim() === 'Proyecto');
    if (!contactColumn) return;

    contactColumn.replaceChildren();

    const heading = document.createElement('h2');
    heading.textContent = 'Contacto';

    const email = document.createElement('a');
    email.href = 'mailto:berm_km@hotmail.com';
    email.textContent = 'berm_km@hotmail.com';

    const location = document.createElement('span');
    location.textContent = 'Pucallpa, Ucayali · Perú';

    const project = document.createElement('span');
    project.textContent = 'Proyecto independiente';

    contactColumn.append(heading, email, location, project);
  }

  createInstructions();
  syncGridAccessibility();
  normalizeFooterContact();

  if (board && 'MutationObserver' in window) {
    const observer = new MutationObserver(syncGridAccessibility);
    observer.observe(board, {
      subtree: true,
      childList: true,
      attributes: true,
      attributeFilter: ['class', 'tabindex', 'aria-label']
    });
  }

  document.addEventListener('keydown', event => {
    if (event.key !== 'Escape' || !menuButton || !mainNav?.classList.contains('open')) return;
    queueMicrotask(() => menuButton.focus({preventScroll: true}));
  }, true);

  numberPad?.addEventListener('click', event => {
    if (!event.target.closest('[data-number]')) return;
    queueMicrotask(focusSelectedCell);
  });

  eraseButton?.addEventListener('click', () => queueMicrotask(focusSelectedCell));
  checkButton?.addEventListener('click', () => queueMicrotask(focusStatus));
})();
