const menuButton = document.querySelector('.menu-button');
const mainNav = document.querySelector('.main-nav');
const year = document.querySelector('#year');
const reduceMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;

// Visual principal de Sudolux: núcleo Sudoku + órbitas temáticas.
const heroVisual = document.querySelector('.hero-visual');
if (heroVisual) {
  heroVisual.innerHTML = `
    <div class="s-orbit s-orbit-outer" aria-hidden="true">
      <span class="s-node s-node-top"><b>JUGAR</b></span>
      <span class="s-node s-node-right"><b>LÓGICA</b></span>
      <span class="s-node s-node-bottom"><b>RETO</b></span>
      <span class="s-node s-node-left"><b>PROGRESO</b></span>
    </div>
    <div class="s-orbit s-orbit-inner" aria-hidden="true">
      <span class="s-chip s-chip-top"><b>FILA</b></span>
      <span class="s-chip s-chip-right"><b>COLUMNA</b></span>
      <span class="s-chip s-chip-left"><b>BLOQUE</b></span>
    </div>
    <div class="s-core" aria-hidden="true">
      <div class="s-core-grid">
        <span>5</span><span>3</span><span>4</span>
        <span>6</span><span>7</span><span>2</span>
        <span>1</span><span>9</span><span>8</span>
      </div>
      <small>SUDOKU 9×9</small>
    </div>`;
}

if (year) year.textContent = new Date().getFullYear();

if (menuButton && mainNav) {
  const closeMenu = () => {
    mainNav.classList.remove('open');
    menuButton.setAttribute('aria-expanded', 'false');
    menuButton.setAttribute('aria-label', 'Abrir menú de navegación');
  };

  menuButton.addEventListener('click', () => {
    const open = mainNav.classList.toggle('open');
    menuButton.setAttribute('aria-expanded', String(open));
    menuButton.setAttribute('aria-label', open ? 'Cerrar menú de navegación' : 'Abrir menú de navegación');
  });

  mainNav.querySelectorAll('a').forEach(link => link.addEventListener('click', closeMenu));
  document.addEventListener('keydown', event => {
    if (event.key === 'Escape') closeMenu();
  });
}

const revealItems = document.querySelectorAll('.reveal');
if (reduceMotion || !('IntersectionObserver' in window)) {
  revealItems.forEach(item => item.classList.add('visible'));
} else {
  const observer = new IntersectionObserver(entries => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.classList.add('visible');
        observer.unobserve(entry.target);
      }
    });
  }, {threshold: .1});
  revealItems.forEach(item => observer.observe(item));
}

// Demostración de las tres reglas
const ruleBoard = document.querySelector('#ruleBoard');
const ruleButtons = [...document.querySelectorAll('.rule-button')];
const ruleText = document.querySelector('#ruleText');
const sample = [
  5,3,4,6,7,8,9,1,2,
  6,7,2,1,9,5,3,4,8,
  1,9,8,3,4,2,5,6,7,
  8,5,9,7,6,1,4,2,3,
  4,2,6,8,5,3,7,9,1,
  7,1,3,9,2,4,8,5,6,
  9,6,1,5,3,7,2,8,4,
  2,8,7,4,1,9,6,3,5,
  3,4,5,2,8,6,1,7,9
];

function renderRule(rule = 'row') {
  if (!ruleBoard) return;
  ruleBoard.innerHTML = '';
  sample.forEach((number, index) => {
    const row = Math.floor(index / 9);
    const column = index % 9;
    const blockRow = Math.floor(row / 3);
    const blockColumn = Math.floor(column / 3);
    let highlight = false;
    if (rule === 'row') highlight = row === 4;
    if (rule === 'column') highlight = column === 4;
    if (rule === 'block') highlight = blockRow === 1 && blockColumn === 1;
    const cell = document.createElement('span');
    cell.textContent = number;
    if (highlight) cell.classList.add('highlight');
    ruleBoard.appendChild(cell);
  });

  const descriptions = {
    row: 'Una fila debe contener los números del 1 al 9 sin repetir.',
    column: 'Una columna debe contener los números del 1 al 9 sin repetir.',
    block: 'Cada bloque de 3 × 3 debe contener los números del 1 al 9 sin repetir.'
  };
  if (ruleText) ruleText.textContent = descriptions[rule];
}

ruleButtons.forEach(button => {
  button.addEventListener('click', () => {
    ruleButtons.forEach(other => {
      const active = other === button;
      other.classList.toggle('active', active);
      other.setAttribute('aria-pressed', String(active));
    });
    renderRule(button.dataset.rule);
  });
});
renderRule('row');

// Sudoku de demostración.
// En cada carga se transforma aleatoriamente una pareja tablero-solución válida.
// Las transformaciones preservan la estructura y la solución del Sudoku.
const basePuzzle = [
  5,3,0,0,7,0,0,0,0,
  6,0,0,1,9,5,0,0,0,
  0,9,8,0,0,0,0,6,0,
  8,0,0,0,6,0,0,0,3,
  4,0,0,8,0,3,0,0,1,
  7,0,0,0,2,0,0,0,6,
  0,6,0,0,0,0,2,8,0,
  0,0,0,4,1,9,0,0,5,
  0,0,0,0,8,0,0,7,9
];

const baseSolution = [
  5,3,4,6,7,8,9,1,2,
  6,7,2,1,9,5,3,4,8,
  1,9,8,3,4,2,5,6,7,
  8,5,9,7,6,1,4,2,3,
  4,2,6,8,5,3,7,9,1,
  7,1,3,9,2,4,8,5,6,
  9,6,1,5,3,7,2,8,4,
  2,8,7,4,1,9,6,3,5,
  3,4,5,2,8,6,1,7,9
];

function shuffle(array) {
  const copy = [...array];
  for (let i = copy.length - 1; i > 0; i -= 1) {
    const j = Math.floor(Math.random() * (i + 1));
    [copy[i], copy[j]] = [copy[j], copy[i]];
  }
  return copy;
}

function toMatrix(flat) {
  return Array.from({length: 9}, (_, row) => flat.slice(row * 9, row * 9 + 9));
}

function randomGroupOrder() {
  const groups = shuffle([0, 1, 2]);
  return groups.flatMap(group => shuffle([0, 1, 2]).map(offset => group * 3 + offset));
}

function buildRandomDemo(puzzleSource, solutionSource) {
  const digits = shuffle([1,2,3,4,5,6,7,8,9]);
  const digitMap = new Map([1,2,3,4,5,6,7,8,9].map((number, index) => [number, digits[index]]));
  const rowOrder = randomGroupOrder();
  const columnOrder = randomGroupOrder();
  const transpose = Math.random() < 0.5;

  const transform = source => {
    const matrix = toMatrix(source);
    const reordered = rowOrder.map(row => columnOrder.map(column => matrix[row][column]));
    const oriented = transpose
      ? Array.from({length: 9}, (_, row) => Array.from({length: 9}, (_, column) => reordered[column][row]))
      : reordered;

    return oriented.flat().map(value => value === 0 ? 0 : digitMap.get(value));
  };

  return {
    puzzle: transform(puzzleSource),
    solution: transform(solutionSource)
  };
}

const randomizedDemo = buildRandomDemo(basePuzzle, baseSolution);
const puzzle = randomizedDemo.puzzle;
const solution = randomizedDemo.solution;

const sudokuBoard = document.querySelector('#sudokuBoard');
const numberPad = document.querySelector('#numberPad');
const eraseButton = document.querySelector('#eraseButton');
const checkButton = document.querySelector('#checkButton');
const resetButton = document.querySelector('#resetButton');
const gameStatus = document.querySelector('#gameStatus');
const progressValue = document.querySelector('#progressValue');
const errorValue = document.querySelector('#errorValue');

let values = [...puzzle];
let selectedIndex = puzzle.findIndex(value => value === 0);
let errors = 0;
let cells = [];
let hasProgress = false;
let gameCompleted = false;

function coordinates(index) {
  return {row: Math.floor(index / 9), column: index % 9};
}

function sameBlock(a, b) {
  const ca = coordinates(a);
  const cb = coordinates(b);
  return Math.floor(ca.row / 3) === Math.floor(cb.row / 3) && Math.floor(ca.column / 3) === Math.floor(cb.column / 3);
}

function cellLabel(index) {
  const {row, column} = coordinates(index);
  const value = values[index];
  const type = puzzle[index] ? 'pista fija' : 'casilla editable';
  return `Fila ${row + 1}, columna ${column + 1}, ${value || 'vacía'}, ${type}`;
}

function updateProgressState() {
  hasProgress = values.some((value, index) => puzzle[index] === 0 && value !== 0);
}

function updateProgress() {
  const editable = puzzle.reduce((total, value) => total + (value === 0 ? 1 : 0), 0);
  const correct = values.reduce((total, value, index) => total + (puzzle[index] === 0 && value === solution[index] ? 1 : 0), 0);
  const percent = Math.round((correct / editable) * 100);
  gameCompleted = correct === editable;

  if (progressValue) progressValue.textContent = `${percent}%`;
  if (errorValue) errorValue.textContent = String(errors);

  if (gameCompleted && gameStatus) {
    gameStatus.textContent = '¡Demo completada! Has resuelto correctamente el tablero.';
  }
}

function paintBoard() {
  cells.forEach((cell, index) => {
    const {row, column} = coordinates(index);
    const selected = coordinates(selectedIndex);
    const related = row === selected.row || column === selected.column || sameBlock(index, selectedIndex);
    const same = values[selectedIndex] !== 0 && values[index] === values[selectedIndex];

    cell.textContent = values[index] || '';
    cell.classList.toggle('selected', index === selectedIndex);
    cell.classList.toggle('related', index !== selectedIndex && related);
    cell.classList.toggle('same', index !== selectedIndex && same);
    cell.classList.toggle('given', puzzle[index] !== 0);
    cell.setAttribute('aria-label', cellLabel(index));
    cell.tabIndex = index === selectedIndex ? 0 : -1;
  });
  updateProgressState();
  updateProgress();
}

function selectCell(index, focus = false) {
  if (index < 0 || index > 80) return;
  selectedIndex = index;
  paintBoard();
  if (focus) cells[index]?.focus();
  if (gameStatus) {
    const {row, column} = coordinates(index);
    gameStatus.textContent = puzzle[index]
      ? `Fila ${row + 1}, columna ${column + 1}: número fijo ${puzzle[index]}.`
      : `Fila ${row + 1}, columna ${column + 1}: casilla editable.`;
  }
}

function enterNumber(number) {
  if (selectedIndex < 0 || puzzle[selectedIndex] !== 0) {
    if (gameStatus) gameStatus.textContent = 'Selecciona una casilla vacía antes de ingresar un número.';
    return;
  }

  if (values[selectedIndex] === number) {
    if (gameStatus) gameStatus.textContent = `El número ${number} ya está colocado en esta casilla.`;
    return;
  }

  const cell = cells[selectedIndex];
  values[selectedIndex] = number;
  cell?.classList.remove('invalid');

  if (number !== solution[selectedIndex]) {
    errors += 1;
    cell?.classList.add('invalid');
    if (gameStatus) gameStatus.textContent = 'Ese número no corresponde a esta casilla. Puedes probar otra opción.';
  } else if (gameStatus) {
    gameStatus.textContent = `Número ${number} colocado correctamente.`;
  }

  paintBoard();
}

function eraseSelected() {
  if (selectedIndex < 0 || puzzle[selectedIndex] !== 0 || values[selectedIndex] === 0) return;
  values[selectedIndex] = 0;
  cells[selectedIndex]?.classList.remove('invalid');
  if (gameStatus) gameStatus.textContent = 'Casilla borrada.';
  paintBoard();
}

function checkBoard() {
  let incorrect = 0;
  let empty = 0;

  values.forEach((value, index) => {
    cells[index]?.classList.remove('invalid');
    if (puzzle[index] !== 0) return;
    if (value === 0) empty += 1;
    else if (value !== solution[index]) {
      incorrect += 1;
      cells[index]?.classList.add('invalid');
    }
  });

  if (incorrect === 0 && empty === 0) {
    gameCompleted = true;
    gameStatus.textContent = '¡Tablero completo y correcto! Has terminado la demostración.';
  } else if (incorrect > 0) {
    gameStatus.textContent = `Hay ${incorrect} ${incorrect === 1 ? 'casilla incorrecta' : 'casillas incorrectas'} y ${empty} por completar.`;
  } else {
    gameStatus.textContent = `Todo lo colocado es correcto. Faltan ${empty} ${empty === 1 ? 'casilla' : 'casillas'} por completar.`;
  }
}

function resetGame() {
  if (hasProgress && !gameCompleted) {
    const confirmed = window.confirm('Tienes una partida en progreso. ¿Deseas reiniciar este mismo Sudoku y perder los movimientos realizados?');
    if (!confirmed) return;
  }

  values = [...puzzle];
  errors = 0;
  hasProgress = false;
  gameCompleted = false;
  selectedIndex = puzzle.findIndex(value => value === 0);
  cells.forEach(cell => cell.classList.remove('invalid'));
  paintBoard();
  if (gameStatus) gameStatus.textContent = 'La partida de demostración se reinició. El tablero actual se mantiene.';
}

function moveSelection(key) {
  const {row, column} = coordinates(selectedIndex);
  let nextRow = row;
  let nextColumn = column;
  if (key === 'ArrowUp') nextRow = Math.max(0, row - 1);
  if (key === 'ArrowDown') nextRow = Math.min(8, row + 1);
  if (key === 'ArrowLeft') nextColumn = Math.max(0, column - 1);
  if (key === 'ArrowRight') nextColumn = Math.min(8, column + 1);
  selectCell(nextRow * 9 + nextColumn, true);
}

// Protección de la partida: al recargar, cerrar o abandonar la página,
// el navegador solicita confirmación si existen movimientos sin terminar.
window.addEventListener('beforeunload', event => {
  if (!hasProgress || gameCompleted) return;
  event.preventDefault();
  event.returnValue = '';
});

if (sudokuBoard) {
  values.forEach((value, index) => {
    const cell = document.createElement('button');
    cell.type = 'button';
    cell.className = 'sudoku-cell';
    cell.setAttribute('role', 'gridcell');
    cell.addEventListener('click', () => selectCell(index));
    cell.addEventListener('keydown', event => {
      if (['ArrowUp','ArrowDown','ArrowLeft','ArrowRight'].includes(event.key)) {
        event.preventDefault();
        moveSelection(event.key);
        return;
      }
      if (/^[1-9]$/.test(event.key)) {
        event.preventDefault();
        enterNumber(Number(event.key));
      }
      if (event.key === 'Backspace' || event.key === 'Delete') {
        event.preventDefault();
        eraseSelected();
      }
    });
    sudokuBoard.appendChild(cell);
    cells.push(cell);
  });
  paintBoard();
}

numberPad?.querySelectorAll('[data-number]').forEach(button => {
  button.addEventListener('click', () => enterNumber(Number(button.dataset.number)));
});
eraseButton?.addEventListener('click', eraseSelected);
checkButton?.addEventListener('click', checkBoard);
resetButton?.addEventListener('click', resetGame);
