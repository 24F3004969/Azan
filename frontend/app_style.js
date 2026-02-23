/* ----------------------- Endpoint -----------------------
Use current device hostname so it works on phone, tablets, etc.
If you deploy the HTML under the same origin as the API, switch to:
const ENDPOINT = "/azan_time";
--------------------------------------------------------- */
const ENDPOINT =
    (location.port && location.port !== "8080")
        ? `http://${location.hostname}:8080/azan_time`   // frontend on 8081 → backend on 8080
        : `/azan_time`;                                   // same-origin case (served from 8080)

//  document.getElementById('endpointLabel').textContent = ENDPOINT;

/* ----------------------- Config ----------------------- */
const ORDER = ["Fajir", "Duhur", "Asr", "Maghrib", "Isha", "Tahajjud"];
const REFRESH_MS = 60_000;

/* ----------------------- Helpers ----------------------- */
const $ = (sel, root = document) => root.querySelector(sel);
const pad = n => n.toString().padStart(2, "0");
const fmtDateLong = (d = new Date()) => d.toLocaleDateString(undefined, {
    weekday: 'long',
    year: 'numeric',
    month: 'long',
    day: 'numeric'
});

function parse12hToDate(timeStr, base = new Date()) {
    const m = timeStr.trim().match(/^(\d{1,2}):(\d{2})\s*([ap])m$/i);
    if (!m) return null;
    let [_, hh, mm, ap] = m;
    hh = parseInt(hh, 10);
    mm = parseInt(mm, 10);
    if (ap.toLowerCase() === 'p' && hh !== 12) hh += 12;
    if (ap.toLowerCase() === 'a' && hh === 12) hh = 0;
    const d = new Date(base);
    d.setHours(hh, mm, 0, 0);
    if (d.getTime() <= base.getTime()) d.setDate(d.getDate() + 1);
    return d;
}

function diffAsHMS(to, from = new Date()) {
    let ms = Math.max(0, to - from);
    const h = Math.floor(ms / 3_600_000);
    ms -= h * 3_600_000;
    const m = Math.floor(ms / 60_000);
    ms -= m * 60_000;
    const s = Math.floor(ms / 1_000);
    return `${pad(h)}:${pad(m)}:${pad(s)}`;
}

/* ----------------------- State ----------------------- */
let currentData = null, nextPrayerKey = null, nextPrayerTime = null, tickTimer = null, refreshTimer = null;

/* ----------------------- UI builders ----------------------- */
function makeCard(name, time, isNext = false) {
    const card = document.createElement('article');
    card.className = 'card' + (isNext ? ' next' : '');
    card.setAttribute('role', 'group');
    card.innerHTML = `
    <div class="name">${name}</div>
    <div class="time">${time}</div>
    <div class="note">${isNext ? 'Upcoming prayer' : 'Scheduled time'}</div>
    ${isNext ? '<div class="badge">Next</div>' : ''}
  `;
    return card;
}

function renderCards(data) {
    const container = $('#cards');
    container.innerHTML = '';
    ORDER.filter(k => k in data).forEach(k => {
        container.appendChild(makeCard(k, data[k], k === nextPrayerKey));
    });
    container.setAttribute('aria-busy', 'false');
}

function setStatus(ok, msg) {
    const pill = $('#statusPill'), txt = $('#statusText');
    txt.textContent = msg || (ok ? 'Online' : 'Offline');
    pill.classList.toggle('bad', !ok);
}

function setError(message) {
    const el = $('#errorMsg');
    el.classList.remove('hidden');
    el.textContent = message;
    setStatus(false, 'Offline');
}

/* ----------------------- Logic ----------------------- */
function computeNextPrayer(data) {
    const now = new Date();
    let bestKey = null, bestDate = null;
    for (const [key, timeStr] of Object.entries(data)) {
        const d = parse12hToDate(timeStr, now);
        if (!d) continue;
        if (bestDate === null || d < bestDate) {
            bestDate = d;
            bestKey = key;
        }
    }
    nextPrayerKey = bestKey;
    nextPrayerTime = bestDate;
}

function updateHeader() {
    $('#todayLabel').textContent = fmtDateLong(new Date());
    $('#nextName').textContent = nextPrayerKey || '—';
    $('#nextTime').textContent = (nextPrayerKey && currentData) ? currentData[nextPrayerKey] : '—';
}

function startTick() {
    if (tickTimer) clearInterval(tickTimer);
    tickTimer = setInterval(() => {
        if (nextPrayerTime) {
            $('#countdown').textContent = diffAsHMS(nextPrayerTime);
            if (nextPrayerTime - new Date() <= 1000) {
                fetchAndRender({soft: true});
            }
        }
    }, 1000);
}

function startAutoRefresh() {
    if (refreshTimer) clearInterval(refreshTimer);
    refreshTimer = setInterval(() => fetchAndRender({soft: true}), REFRESH_MS);
}

async function fetchAndRender({soft = false} = {}) {
    try {
        $('#errorMsg').classList.add('hidden');
        if (!soft) setStatus(true, 'Refreshing…');
        const res = await fetch(ENDPOINT, {cache: "no-store"});
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        const data = await res.json();
        currentData = data;
        computeNextPrayer(data);
        renderCards(data);
        updateHeader();
        startTick();
        setStatus(true, 'Online');
    } catch (err) {
        console.error(err);
        setError('Could not load times. Check server/CORS.');
    }
}

/* ----------------------- Events & boot ----------------------- */
$('#refreshBtn').addEventListener('click', () => fetchAndRender());
(function init() {
    $('#todayLabel').textContent = fmtDateLong(new Date());
    setStatus(true, 'Connecting…');
    fetchAndRender();
    startAutoRefresh();
})();