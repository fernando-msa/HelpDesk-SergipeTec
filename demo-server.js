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

  if (pathname === '/helpdesk-app/api/tickets') {
    const authorization = req.headers.authorization || '';
    if (authorization !== `Bearer ${demoToken}`) {
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
          id: tickets.length + 1,
          title: ticket.title || 'Sem titulo',
          description: ticket.description || '',
          status: ticket.status || 'OPEN',
        };

        tickets.push(createdTicket);
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
