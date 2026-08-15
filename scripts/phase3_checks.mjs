import { chromium } from 'playwright';
import AxeBuilder from '@axe-core/playwright';
import { mkdir, writeFile } from 'node:fs/promises';

const baseURL = process.env.SMOKE_BASE_URL || 'http://127.0.0.1:4173/';
const artifactsDir = 'artifacts/phase3';
const blockingImpacts = new Set(['critical', 'serious']);
const browser = await chromium.launch({ headless: true });
const failures = [];

function assert(condition, message) { if (!condition) throw new Error(message); }
async function saveFailure(page, label, details) {
  await mkdir(artifactsDir, { recursive: true });
  await page.screenshot({ path: `${artifactsDir}/${label}.png`, fullPage: true }).catch(() => {});
  await writeFile(`${artifactsDir}/${label}.json`, JSON.stringify(details, null, 2), 'utf8').catch(() => {});
}
function summarizeViolation(v) { return { id:v.id, impact:v.impact, help:v.help, helpUrl:v.helpUrl, nodes:v.nodes.map(n => ({target:n.target,failureSummary:n.failureSummary})) }; }

async function runAxe(label, viewport, setup) {
  const context = await browser.newContext({ viewport, reducedMotion:'reduce' });
  const page = await context.newPage();
  try {
    const response = await page.goto(baseURL, { waitUntil:'domcontentloaded', timeout:30_000 });
    assert(response && response.status() < 400, `${label}: la página no cargó correctamente.`);
    await page.locator('#sudokuBoard [role="gridcell"]').first().waitFor({state:'attached',timeout:10_000});
    await page.waitForTimeout(250);
    if (setup) await setup(page);
    const results = await new AxeBuilder({ page }).analyze();
    const blocking = results.violations.filter(v => blockingImpacts.has(v.impact));
    const advisory = results.violations.filter(v => !blockingImpacts.has(v.impact));
    console.log(`✓ axe ${label}: ${blocking.length} bloqueantes, ${advisory.length} informativas`);
    advisory.forEach(v => console.log(`  · ${v.impact || 'sin impacto'} ${v.id}: ${v.help}`));
    if (blocking.length) {
      const details = blocking.map(summarizeViolation);
      await saveFailure(page, `axe-${label}`, details);
      failures.push({label:`axe-${label}`,errors:details.map(v => `${v.impact} ${v.id}: ${v.help}`)});
      details.forEach(v => console.error(`  - ${v.impact} ${v.id}: ${v.help}`));
    }
  } catch (error) {
    await saveFailure(page, `axe-${label}-exception`, {error:error.message});
    failures.push({label:`axe-${label}`,errors:[error.message]});
  } finally { await context.close(); }
}

async function runFunctionalFlow() {
  const context = await browser.newContext({ viewport:{width:1280,height:900}, reducedMotion:'reduce' });
  const page = await context.newPage();
  try {
    await page.goto(baseURL, { waitUntil:'domcontentloaded', timeout:30_000 });
    await page.locator('#sudokuBoard [role="gridcell"]').first().waitFor({state:'attached',timeout:10_000});

    const columnRule = page.locator('.rule-button[data-rule="column"]');
    await columnRule.click();
    assert(await columnRule.getAttribute('aria-pressed') === 'true', 'Cambiar a la regla Columna no actualizó aria-pressed.');
    assert(((await page.locator('#ruleText').textContent()) || '').toLowerCase().includes('columna'), 'La explicación de la regla no cambió a Columna.');

    const editable = page.locator('#sudokuBoard [role="gridcell"]:not(.given)').first();
    await editable.click();
    const notes = page.locator('#notesButton');
    await notes.click();
    assert(await notes.getAttribute('aria-pressed') === 'true', 'El botón Notas no activó el modo candidatos.');
    await page.locator('.number-pad [data-number="1"]').click();
    assert(((await editable.getAttribute('aria-label')) || '').toLowerCase().includes('candidatos 1'), 'El número en modo Notas no se registró como candidato.');
    await page.locator('#eraseButton').click();
    assert(!((await editable.getAttribute('aria-label')) || '').toLowerCase().includes('candidatos 1'), 'Borrar no eliminó el candidato de la casilla.');

    const beforeMessage = (await page.locator('#gameMessage').textContent()) || '';
    await page.locator('#checkButton').click();
    await page.waitForTimeout(100);
    assert(((await page.locator('#gameMessage').textContent()) || '') !== beforeMessage, 'Comprobar partida no actualizó el estado del juego.');
    console.log('✓ funcional Sudolux: reglas, notas, candidatos, borrado y comprobación responden');
  } catch (error) {
    await saveFailure(page, 'functional-sudolux', {error:error.message});
    failures.push({label:'functional-sudolux',errors:[error.message]});
    console.error(`✗ funcional Sudolux: ${error.message}`);
  } finally { await context.close(); }
}

await runAxe('home-desktop', {width:1440,height:900});
await runAxe('home-mobile-menu', {width:390,height:844}, async page => {
  const menu = page.locator('header .menu-button, header .menu').first();
  if ((await menu.count()) && (await menu.isVisible())) await menu.click();
});
await runFunctionalFlow();
await browser.close();
if (failures.length) { console.error(`\nFase 3 falló en ${failures.length} comprobación(es).`); process.exit(1); }
console.log('\nFase 3 superada: accesibilidad automática y flujo funcional principal verificados.');
