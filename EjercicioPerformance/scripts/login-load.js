import http from 'k6/http';
import exec from 'k6/execution';
import { check } from 'k6';
import { SharedArray } from 'k6/data';
import { Trend } from 'k6/metrics';

// Criterios del reto:
//   - al menos 20 TPS sobre POST /auth/login
//   - tiempo de respuesta maximo 1,5 s
//   - tasa de error menor al 3%
const TARGET_TPS = 20;
const MAX_RESPONSE_MS = 1500;
const MAX_ERROR_RATE = 0.03;
const LOGIN_URL = 'https://fakestoreapi.com/auth/login';
const SCENARIO_SECONDS = parseDurationSeconds(__ENV.DURATION || '2m');

function parseDurationSeconds(value) {
  const match = /^(\d+)(s|m)$/.exec(String(value).trim());
  if (!match) {
    return 120;
  }
  const amount = Number(match[1]);
  return match[2] === 'm' ? amount * 60 : amount;
}

const loginDuration = new Trend('login_duration', true);

const users = new SharedArray('users', function () {
  return parseUsers(open('../data/users.csv'));
});

export const options = {
  scenarios: {
    login_load: {
      executor: 'constant-arrival-rate',
      rate: TARGET_TPS,
      timeUnit: '1s',
      duration: __ENV.DURATION || '2m',
      preAllocatedVUs: Number(__ENV.PRE_VUS || 40),
      maxVUs: Number(__ENV.MAX_VUS || 200),
      gracefulStop: '15s',
    },
  },
  thresholds: {
    http_req_failed: [`rate<${MAX_ERROR_RATE}`],
    http_req_duration: [`p(95)<${MAX_RESPONSE_MS}`, `max<${MAX_RESPONSE_MS}`],
    login_duration: [`p(95)<${MAX_RESPONSE_MS}`],
  },
  summaryTrendStats: ['avg', 'min', 'med', 'p(90)', 'p(95)', 'p(99)', 'max'],
};

export function setup() {
  if (users.length === 0) {
    exec.test.abort('data/users.csv no tiene usuarios');
  }
}

export default function () {
  const account = users[exec.scenario.iterationInTest % users.length];
  const response = http.post(
    LOGIN_URL,
    JSON.stringify({
      username: account.user,
      password: account.passwd,
    }),
    {
      headers: { 'Content-Type': 'application/json' },
      timeout: '60s',
      tags: { name: 'POST /auth/login', endpoint: 'login' },
    }
  );

  loginDuration.add(response.timings.duration);

  const token = readToken(response);
  check(response, {
    'status is 201': (res) => res.status === 201,
    'token is present': () => token.length > 0,
    'response time <= 1.5s': (res) => res.timings.duration <= MAX_RESPONSE_MS,
  });
}

export function handleSummary(data) {
  const report = buildReport(data);
  const text = renderText(report);
  return {
    'reports/resumen.txt': text,
    'reports/summary.json': JSON.stringify(report, null, 2),
    'reports/summary.html': renderHtml(report),
    stdout: text,
  };
}

function parseUsers(content) {
  const lines = content
    .replace(/^\uFEFF/, '')
    .split(/\r?\n/)
    .map((line) => line.trim())
    .filter((line) => line.length > 0);

  const header = splitCsv(lines.shift() || '');
  const userIndex = header.indexOf('user');
  const passwordIndex = header.indexOf('passwd');
  if (userIndex < 0 || passwordIndex < 0) {
    throw new Error('El CSV debe tener las columnas user,passwd');
  }

  return lines
    .map((line) => {
      const columns = splitCsv(line);
      return {
        user: columns[userIndex] || '',
        passwd: columns[passwordIndex] || '',
      };
    })
    .filter((row) => row.user.length > 0 && row.passwd.length > 0);
}

function splitCsv(line) {
  const fields = [];
  let current = '';
  let quoted = false;
  for (let i = 0; i < line.length; i++) {
    const character = line[i];
    if (character === '"') {
      quoted = !quoted;
    } else if (character === ',' && !quoted) {
      fields.push(current.trim());
      current = '';
    } else {
      current += character;
    }
  }
  fields.push(current.trim());
  return fields;
}

function readToken(response) {
  if (!response.body) {
    return '';
  }
  try {
    const body = response.json();
    return body && typeof body.token === 'string' ? body.token : '';
  } catch (error) {
    return '';
  }
}

function metricValue(data, name, field) {
  const metric = data.metrics[name];
  if (!metric || !metric.values || metric.values[field] === undefined) {
    return null;
  }
  return metric.values[field];
}

function thresholdPassed(data, name) {
  const metric = data.metrics[name];
  if (!metric || !metric.thresholds) {
    return null;
  }
  return Object.values(metric.thresholds).every((item) => item.ok);
}

function collectChecks(group, found) {
  if (!group) {
    return found;
  }
  (group.checks || []).forEach((item) => found.push(item));
  (group.groups || []).forEach((child) => collectChecks(child, found));
  return found;
}

function buildReport(data) {
  const wallClockMs = data.state.testRunDurationMs;
  const requests = metricValue(data, 'http_reqs', 'count') || 0;
  // http_reqs/s includes the graceful stop, when k6 is no longer starting
  // the 20 arrivals per second. The arrival rate of the scenario is
  // requests divided by the configured duration. dropped_iterations == 0
  // means k6 never had to skip an arrival for lack of VUs.
  const arrivalTps = SCENARIO_SECONDS > 0 ? requests / SCENARIO_SECONDS : 0;
  const wallClockTps = metricValue(data, 'http_reqs', 'rate');
  const failedRate = metricValue(data, 'http_req_failed', 'rate');
  const dropped = metricValue(data, 'dropped_iterations', 'count') || 0;
  const p95 = metricValue(data, 'http_req_duration', 'p(95)');
  const max = metricValue(data, 'http_req_duration', 'max');
  const reached20Tps = dropped === 0 && arrivalTps >= TARGET_TPS * 0.98;

  return {
    tool: 'k6',
    url: LOGIN_URL,
    method: 'POST',
    usersFile: 'data/users.csv',
    userCount: users.length,
    targetTps: TARGET_TPS,
    maxResponseMs: MAX_RESPONSE_MS,
    maxErrorRate: MAX_ERROR_RATE,
    scenarioSeconds: SCENARIO_SECONDS,
    wallClockMs: wallClockMs,
    requests: requests,
    arrivalTps: arrivalTps,
    wallClockTps: wallClockTps,
    droppedIterations: dropped,
    errorRate: failedRate,
    responseMs: {
      avg: metricValue(data, 'http_req_duration', 'avg'),
      min: metricValue(data, 'http_req_duration', 'min'),
      med: metricValue(data, 'http_req_duration', 'med'),
      p90: metricValue(data, 'http_req_duration', 'p(90)'),
      p95: p95,
      p99: metricValue(data, 'http_req_duration', 'p(99)'),
      max: max,
    },
    checks: collectChecks(data.root_group, []).map((item) => ({
      name: item.name,
      passes: item.passes,
      fails: item.fails,
    })),
    criteria: {
      reached20Tps: reached20Tps,
      p95Under1_5s: p95 !== null && p95 < MAX_RESPONSE_MS,
      maxUnder1_5s: max !== null && max < MAX_RESPONSE_MS,
      errorRateUnder3Percent: failedRate !== null && failedRate < MAX_ERROR_RATE,
      thresholds: {
        http_req_failed: thresholdPassed(data, 'http_req_failed'),
        http_req_duration: thresholdPassed(data, 'http_req_duration'),
        login_duration: thresholdPassed(data, 'login_duration'),
      },
    },
  };
}

function fmt(value, digits) {
  if (value === null || value === undefined || Number.isNaN(value)) {
    return 'n/d';
  }
  return Number(value).toFixed(digits);
}

function yesNo(value) {
  if (value === null || value === undefined) {
    return 'n/d';
  }
  return value ? 'SI' : 'NO';
}

function renderText(report) {
  const checkLines = report.checks.map(
    (item) => `  ${item.name}: ${item.passes} ok, ${item.fails} fallo`
  );
  const lines = [
    '================================================================================',
    'PRUEBA DE CARGA — POST /auth/login — k6',
    '================================================================================',
    `URL:                 ${report.url}`,
    `Usuarios CSV:        ${report.userCount} (${report.usersFile})`,
    `Objetivo:            ${report.targetTps} TPS durante ${report.scenarioSeconds} s`,
    `Peticiones:          ${report.requests}`,
    `TPS de llegada:      ${fmt(report.arrivalTps, 2)}`,
    `TPS con el cierre:   ${fmt(report.wallClockTps, 2)} (incluye graceful stop, ${fmt(report.wallClockMs / 1000, 1)} s)`,
    `Iteraciones perdidas: ${report.droppedIterations}`,
    '',
    'Tiempo de respuesta (ms)',
    `  avg ${fmt(report.responseMs.avg, 1)}   min ${fmt(report.responseMs.min, 1)}   med ${fmt(report.responseMs.med, 1)}`,
    `  p90 ${fmt(report.responseMs.p90, 1)}   p95 ${fmt(report.responseMs.p95, 1)}   p99 ${fmt(report.responseMs.p99, 1)}   max ${fmt(report.responseMs.max, 1)}`,
    '',
    `Tasa de error:       ${fmt((report.errorRate || 0) * 100, 2)} %`,
    'Checks',
    ...checkLines,
    '',
    'Criterios',
    `  Alcanzo 20 TPS:              ${yesNo(report.criteria.reached20Tps)}`,
    `  p95 menor a 1,5 s:           ${yesNo(report.criteria.p95Under1_5s)}`,
    `  maximo menor a 1,5 s:        ${yesNo(report.criteria.maxUnder1_5s)}`,
    `  Error menor al 3 %:          ${yesNo(report.criteria.errorRateUnder3Percent)}`,
    '================================================================================',
    '',
  ];
  return lines.join('\n');
}

function renderHtml(report) {
  const row = (label, value) => `<tr><th>${label}</th><td>${value}</td></tr>`;
  return `<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="utf-8">
  <title>k6 login load test</title>
  <style>
    body { font-family: Segoe UI, sans-serif; margin: 2rem; color: #1a1a1a; }
    table { border-collapse: collapse; min-width: 32rem; }
    th, td { border: 1px solid #ccc; padding: 0.4rem 0.7rem; text-align: left; }
    th { background: #f4f4f4; }
  </style>
</head>
<body>
  <h1>Prueba de carga — login Fake Store API</h1>
  <p>POST ${report.url}. Usuarios leidos de ${report.usersFile}.</p>
  <table>
    ${row('TPS objetivo', report.targetTps)}
    ${row('TPS de llegada', fmt(report.arrivalTps, 2))}
    ${row('TPS incluyendo el cierre', fmt(report.wallClockTps, 2))}
    ${row('Peticiones', report.requests)}
    ${row('Escenario (s)', report.scenarioSeconds)}
    ${row('Error (%)', fmt((report.errorRate || 0) * 100, 2))}
    ${row('p95 (ms)', fmt(report.responseMs.p95, 1))}
    ${row('max (ms)', fmt(report.responseMs.max, 1))}
    ${row('avg (ms)', fmt(report.responseMs.avg, 1))}
    ${row('Alcanzo 20 TPS', yesNo(report.criteria.reached20Tps))}
    ${row('p95 dentro de 1,5 s', yesNo(report.criteria.p95Under1_5s))}
    ${row('Maximo dentro de 1,5 s', yesNo(report.criteria.maxUnder1_5s))}
    ${row('Error menor al 3%', yesNo(report.criteria.errorRateUnder3Percent))}
  </table>
</body>
</html>
`;
}
