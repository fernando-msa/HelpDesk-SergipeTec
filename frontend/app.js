const apiBase = '/helpdesk-app'; // adjust if deployed under different context

function formatTimestamp(value){
  if (!value) return 'Agora';
  return new Date(value).toLocaleString('pt-BR');
}

function getUnreadNotifications(notifications){
  return notifications.filter(notification => !notification.readAt).length;
}

function updateNotificationBadge(notifications){
  const badge = document.getElementById('notificationCount');
  if (!badge) return;
  const unreadCount = getUnreadNotifications(notifications);
  badge.textContent = String(unreadCount);
  badge.hidden = unreadCount === 0;
}

function renderNotifications(notifications){
  const list = document.getElementById('notifications');
  if (!list) return;

  list.innerHTML = '';

  if (!notifications.length) {
    const emptyItem = document.createElement('li');
    emptyItem.className = 'empty-state';
    emptyItem.textContent = 'Nenhuma notificação ainda.';
    list.appendChild(emptyItem);
    updateNotificationBadge(notifications);
    return;
  }

  notifications.slice(0, 10).forEach(notification => {
    const item = document.createElement('li');
    item.className = notification.readAt ? 'notification-item' : 'notification-item unread';

    const button = document.createElement('button');
    button.type = 'button';
    button.className = 'notification-button';
    button.addEventListener('click', () => markNotificationAsRead(notification.id));

    const type = document.createElement('strong');
    type.textContent = notification.type.replace(/_/g, ' ');

    const message = document.createElement('span');
    message.textContent = notification.message;

    const meta = document.createElement('small');
    meta.textContent = `${formatTimestamp(notification.createdAt)}${notification.readAt ? ' · lida' : ' · não lida'}`;

    button.append(type, message, meta);
    item.appendChild(button);
    list.appendChild(item);
  });

  updateNotificationBadge(notifications);
}

async function loadNotifications(){
  const token = localStorage.getItem('token');
  if (!token) return;

  const res = await fetch(apiBase + '/api/notifications', { headers: { Authorization: 'Bearer ' + token } });
  if (!res.ok) { console.error(res.status); return; }

  const notifications = await res.json();
  renderNotifications(notifications);
}

async function markNotificationAsRead(id){
  const token = localStorage.getItem('token');
  if (!token) return;

  const res = await fetch(apiBase + '/api/notifications/' + id + '/read', {
    method: 'POST',
    headers: { Authorization: 'Bearer ' + token },
  });

  if (res.ok) {
    loadNotifications();
  }
}

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
    loadNotifications();
  } else console.error('Erro ao criar chamado', res.status);
}

if (document.getElementById('loginForm')) document.getElementById('loginForm').addEventListener('submit', loginFormHandler);
if (document.getElementById('ticketForm')) {
  document.getElementById('ticketForm').addEventListener('submit', openTicket);
  loadTickets();
  loadNotifications();
}
if (document.getElementById('logout')) document.getElementById('logout').addEventListener('click', ()=>{ localStorage.removeItem('token'); window.location='login.html'; });
