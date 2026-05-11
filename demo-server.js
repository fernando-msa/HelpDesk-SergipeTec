const http = require('http');
const fs = require('fs');
const path = require('path');
const url = require('url');

const port = Number(process.env.PORT || 3000);
const projectRoot = __dirname;
const frontendRoot = path.join(projectRoot, 'frontend');
const demoToken = 'demo-token';

let tickets = [
  {
    id: 1,
    title: 'Chamado de exemplo',
    description: 'Ticket inicial da demo',
    status: 'OPEN',
  },
];

let notifications = [
  {
    id: 1,
    type: 'SYSTEM_READY',
    message: 'Demo local pronta para testes.',
    createdAt: Date.now(),
    readAt: null,
  },
];

let nextTicketId = 2;
let nextNotificationId = 2;

const mimeTypes = {
  '.html': 'text/html; charset=utf-8',
  '.js': 'application/javascript; charset=utf-8',
  '.css': 'text/css; charset=utf-8',
  '.json': 'application/json; charset=utf-8',
  '.svg': 'image/svg+xml',
  '.png': 'image/png',
  '.ico': 'image/x-icon',
};

function send(res, statusCode, body, headers = {}) {
  res.writeHead(statusCode, {
    'Access-Control-Allow-Origin': '*',
    'Access-Control-Allow-Headers': 'Content-Type, Authorization',
    'Access-Control-Allow-Methods': 'GET,POST,PUT,DELETE,OPTIONS',
    ...headers,
  });
  res.end(body);
}

function authOk(req) {
  const authorization = req.headers.authorization || '';
  return authorization === `Bearer ${demoToken}`;
}

function addNotification(type, message) {
  notifications.unshift({
    id: nextNotificationId++,
    type,
    message,
    createdAt: Date.now(),
    readAt: null,
  });
}

function readJsonBody(req, callback) {
  let body = '';
  req.on('data', chunk => {
    body += chunk;
  });
  req.on('end', () => {
    try {
      callback(null, JSON.parse(body || '{}'));
    } catch (error) {
      callback(error);
    }
  });
}

const server = http.createServer((req, res) => {
  const parsed = url.parse(req.url || '/', true);
  const pathname = parsed.pathname || '/';

  if (req.method === 'OPTIONS') {
    send(res, 204, '');
    return;
  }

  if (pathname === '/helpdesk-app/api/auth/login' && req.method === 'POST') {
    readJsonBody(req, (error, credentials) => {
      if (error) {
        send(res, 400, JSON.stringify({ message: 'JSON invalido' }), {
          'Content-Type': 'application/json',
        });
        return;
      }

      if (credentials.username === 'admin' && credentials.password === 'password') {
        send(res, 200, JSON.stringify({ token: demoToken }), {
          'Content-Type': 'application/json',
        });
        return;
      }

      send(res, 401, JSON.stringify({ message: 'Credenciais invalidas' }), {
        'Content-Type': 'application/json',
      });
    });
    return;
  }

  if (pathname === '/helpdesk-app/api/notifications' && req.method === 'GET') {
    if (!authOk(req)) {
      send(res, 401, JSON.stringify({ message: 'Nao autorizado' }), {
        'Content-Type': 'application/json',
      });
      return;
    }

    send(res, 200, JSON.stringify(notifications), {
      'Content-Type': 'application/json',
    });
    return;
  }

  const notificationReadMatch = pathname.match(/^\/helpdesk-app\/api\/notifications\/(\d+)\/read$/);
  if (notificationReadMatch && req.method === 'POST') {
    if (!authOk(req)) {
      send(res, 401, JSON.stringify({ message: 'Nao autorizado' }), {
        'Content-Type': 'application/json',
      });
      return;
    }

    const notificationId = Number(notificationReadMatch[1]);
    const notification = notifications.find(item => item.id === notificationId);
    if (!notification) {
      send(res, 404, JSON.stringify({ message: 'Notificacao nao encontrada' }), {
        'Content-Type': 'application/json',
      });
      return;
    }

    notification.readAt = Date.now();
    send(res, 200, JSON.stringify(notification), {
      'Content-Type': 'application/json',
    });
    return;
  }

  if (pathname === '/helpdesk-app/api/tickets') {
    if (!authOk(req)) {
      send(res, 401, JSON.stringify({ message: 'Nao autorizado' }), {
        'Content-Type': 'application/json',
      });
      return;
    }

    if (req.method === 'GET') {
      send(res, 200, JSON.stringify(tickets), {
        'Content-Type': 'application/json',
      });
      return;
    }

    if (req.method === 'POST') {
      readJsonBody(req, (error, ticket) => {
        if (error) {
          send(res, 400, JSON.stringify({ message: 'JSON invalido' }), {
            'Content-Type': 'application/json',
          });
          return;
        }

        const createdTicket = {
          id: nextTicketId++,
          title: ticket.title || 'Sem titulo',
          description: ticket.description || '',
          status: ticket.status || 'OPEN',
        };

        tickets.push(createdTicket);
        addNotification('TICKET_CREATED', `Novo chamado #${createdTicket.id} aberto: ${createdTicket.title}`);
        send(res, 201, JSON.stringify(createdTicket), {
          'Content-Type': 'application/json',
        });
      });
      return;
    }

    send(res, 405, JSON.stringify({ message: 'Metodo nao suportado' }), {
      'Content-Type': 'application/json',
    });
    return;
  }

  const ticketStatusMatch = pathname.match(/^\/helpdesk-app\/api\/tickets\/(\d+)\/status$/);
  if (ticketStatusMatch && req.method === 'PUT') {
    if (!authOk(req)) {
      send(res, 401, JSON.stringify({ message: 'Nao autorizado' }), {
        'Content-Type': 'application/json',
      });
      return;
    }

    const ticketId = Number(ticketStatusMatch[1]);
    readJsonBody(req, (error, update) => {
      if (error || !update || !update.status) {
        send(res, 400, JSON.stringify({ message: 'JSON invalido' }), {
          'Content-Type': 'application/json',
        });
        return;
      }

      const ticket = tickets.find(item => item.id === ticketId);
      if (!ticket) {
        send(res, 404, JSON.stringify({ message: 'Chamado nao encontrado' }), {
          'Content-Type': 'application/json',
        });
        return;
      }

      ticket.status = update.status;
      addNotification('TICKET_UPDATED', `Chamado #${ticket.id} atualizado para ${update.status}`);
      send(res, 200, JSON.stringify(ticket), {
        'Content-Type': 'application/json',
      });
    });
    return;
  }

  const ticketCloseMatch = pathname.match(/^\/helpdesk-app\/api\/tickets\/(\d+)\/close$/);
  if (ticketCloseMatch && req.method === 'POST') {
    if (!authOk(req)) {
      send(res, 401, JSON.stringify({ message: 'Nao autorizado' }), {
        'Content-Type': 'application/json',
      });
      return;
    }

    const ticketId = Number(ticketCloseMatch[1]);
    const ticket = tickets.find(item => item.id === ticketId);
    if (!ticket) {
      send(res, 404, JSON.stringify({ message: 'Chamado nao encontrado' }), {
        'Content-Type': 'application/json',
      });
      return;
    }

    ticket.status = 'CLOSED';
    addNotification('TICKET_CLOSED', `Chamado #${ticket.id} foi encerrado`);
    send(res, 200, JSON.stringify(ticket), {
      'Content-Type': 'application/json',
    });
    return;
  }

  let filePath = pathname === '/' ? path.join(frontendRoot, 'index.html') : path.join(frontendRoot, pathname);
  filePath = path.normalize(filePath);

  const relativePath = path.relative(frontendRoot, filePath);
  if (relativePath.startsWith('..') || path.isAbsolute(relativePath)) {
    send(res, 403, 'Forbidden');
    return;
  }

  fs.readFile(filePath, (error, data) => {
    if (error) {
      send(res, 404, 'Not found');
      return;
    }

    send(res, 200, data, {
      'Content-Type': mimeTypes[path.extname(filePath).toLowerCase()] || 'application/octet-stream',
    });
  });
});

server.listen(port, () => {
  console.log(`Demo backend + frontend running at http://localhost:${port}`);
  console.log('Demo credentials: admin / password');
});
