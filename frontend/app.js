const apiBase = '/helpdesk-app'; // adjust if deployed under different context

async function loginFormHandler(e){
  e.preventDefault();
  const form = e.target;
  const data = { username: form.username.value, password: form.password.value };
  const res = await fetch(apiBase + '/api/auth/login', {
    method:'POST', headers:{'Content-Type':'application/json'}, body:JSON.stringify(data)
  });
  if (res.ok){
    const j = await res.json();
    localStorage.setItem('token', j.token);
    window.location = 'index.html';
  } else {
    document.getElementById('msg').textContent = 'Credenciais inválidas';
  }
}

async function loadTickets(){
  const token = localStorage.getItem('token');
  if (!token) { window.location='login.html'; return; }
  const res = await fetch(apiBase + '/api/tickets', { headers: { Authorization: 'Bearer '+token } });
  if (!res.ok) { console.error(res.status); return; }
  const tickets = await res.json();
  const ul = document.getElementById('tickets');
  if (!ul) return;
  ul.innerHTML = '';
  tickets.forEach(t => {
    const li = document.createElement('li');
    li.textContent = `#${t.id} [${t.status}] ${t.title} — ${t.description}`;
    ul.appendChild(li);
  });
}

async function openTicket(e){
  e.preventDefault();
  const form = e.target;
  const data = { title: form.title.value, description: form.description.value };
  const token = localStorage.getItem('token');
  const res = await fetch(apiBase + '/api/tickets', { method:'POST', headers: { 'Content-Type':'application/json', Authorization: 'Bearer '+token }, body:JSON.stringify(data) });
  if (res.ok){
    form.reset();
    loadTickets();
  } else console.error('Erro ao criar chamado', res.status);
}

if (document.getElementById('loginForm')) document.getElementById('loginForm').addEventListener('submit', loginFormHandler);
if (document.getElementById('ticketForm')) {
  document.getElementById('ticketForm').addEventListener('submit', openTicket);
  loadTickets();
}
if (document.getElementById('logout')) document.getElementById('logout').addEventListener('click', ()=>{ localStorage.removeItem('token'); window.location='login.html'; });
