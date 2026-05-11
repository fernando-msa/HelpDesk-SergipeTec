# 🎫 HelpDesk-SergipeTec

![Java 25](https://img.shields.io/badge/Java-25_LTS-007396?logo=openjdk&logoColor=white)
![Maven 3.9.15](https://img.shields.io/badge/Maven-3.9.15-C71A36?logo=apachemaven&logoColor=white)
![Tests Passing](https://img.shields.io/badge/Tests-100%25_Passing-2E8B57?logo=checkmarx&logoColor=white)
![License MIT](https://img.shields.io/badge/License-MIT-4C1?logo=opensourceinitiative&logoColor=white)

Um sistema de gerenciamento de tickets de suporte técnico desenvolvido em Java com autenticação JWT, persistência JPA e interface web responsiva.

**Status**: ✅ Java 25 LTS | Maven 3.9.15 | 100% Testes Passando

---

## 📋 Índice

- [Visão Geral](#visão-geral)
- [Pré-requisitos](#pré-requisitos)
- [Instalação](#instalação)
- [Configuração](#configuração)
- [Como Usar](#como-usar)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Testes](#testes)
- [API Endpoints](#api-endpoints)
- [Upgrade Java 25](#upgrade-java-25)
- [Segurança](#segurança)
- [Troubleshooting](#troubleshooting)
- [Contribuindo](#contribuindo)
- [Documentação do Projeto](#documentação-do-projeto)
- [Licença](#licença)

---

## 🎯 Visão Geral

HelpDesk-SergipeTec é uma aplicação web para gerenciamento centralizado de tickets de suporte técnico com:

✅ **Autenticação JWT** - Tokens seguros para acesso à API  
✅ **Persistência com JPA/Hibernate** - Integração com PostgreSQL  
✅ **Interface Web Responsiva** - HTML5 + CSS3 + JavaScript  
✅ **REST API** - Endpoints para gerenciar tickets  
✅ **Testes Automatizados** - JUnit 5 com cobertura de funcionalidades críticas  

### Tecnologias

| Componente | Versão |
| --- | --- |
| **Java** | 25 LTS (Eclipse Adoptium) |
| **Maven** | 3.9.15 |
| **Jakarta EE** | 10.0.0 |
| **Hibernate ORM** | 6.2.7.Final |
| **PostgreSQL JDBC** | 42.7.2 |
| **JUnit Jupiter** | 5.10.0 |
| **JJWT** | 0.11.5 |

---

## 📦 Pré-requisitos

Antes de começar, certifique-se de ter instalado:

### Obrigatório
- **JDK 25** ou superior
  ```bash
  java -version
  # Expected: Java 25.x.x
  ```

- **Maven 3.9+**
  ```bash
  mvn -version
  # Expected: Maven 3.9.x
  ```

- **PostgreSQL 12+** (para produção)
  ```bash
  psql --version
  ```

- **Git**
  ```bash
  git --version
  ```

### Opcional (Desenvolvimento)
- IDE: IntelliJ IDEA, VS Code, ou Eclipse
- Docker (para containerização)

---

## 🚀 Instalação

### 1. Clonar o Repositório

```bash
git clone https://github.com/fernando-msa/HelpDesk-SergipeTec.git
cd HelpDesk-SergipeTec
```

### 2. Compilar o Projeto

```bash
# Compilação completa com testes
mvn clean install

# Compilação sem testes (mais rápido)
mvn clean install -DskipTests
```

### 3. Executar Testes Locais

```bash
# Todos os testes
mvn clean test

# Com output detalhado
mvn clean test -X
```

---

## ⚙️ Configuração

### Variáveis de Ambiente

Crie um arquivo `.env` na raiz do projeto com as seguintes variáveis:

```env
# Segurança JWT
JWT_SECRET=sua-chave-secreta-super-segura-aqui-minimo-32-caracteres

# Banco de Dados (Produção)
DATABASE_URL=jdbc:postgresql://localhost:5432/helpdesk
DATABASE_USER=postgres
DATABASE_PASSWORD=sua_senha

# Servidor
SERVER_PORT=8080
SERVER_HOST=localhost

# Hibernate
HIBERNATE_DIALECT=org.hibernate.dialect.PostgreSQL10Dialect
HIBERNATE_HBM2DDL_AUTO=validate
```

### Arquivo de Persistência (JPA)

O arquivo `backend/src/main/resources/META-INF/persistence.xml` contém a configuração de conexão:

```xml
<persistence-unit name="helpdeskPU" transaction-type="RESOURCE_LOCAL">
    <class>com.helpdesk.model.Ticket</class>
    <properties>
        <property name="jakarta.persistence.jdbc.driver" value="org.h2.Driver"/>
        <property name="jakarta.persistence.jdbc.url" value="jdbc:h2:mem:helpdesk"/>
        <property name="jakarta.persistence.schema-generation.database.action" value="drop-and-create"/>
    </properties>
</persistence-unit>
```

---

## 💻 Como Usar

### Build e Deploy

```bash
# Gerar WAR para deploy em servidor aplicações
mvn clean package

# Arquivo gerado: target/helpdesk-app-0.1.0.war
```

### Executar Testes

```bash
# Testes de JWT
mvn test -Dtest=JwtUtilTest

# Testes de JPA
mvn test -Dtest=TicketJpaTest

# Todos os testes com relatório
mvn clean test site
```

### Usar Docker (Opcional)

```bash
# Build da imagem
docker build -t helpdesk-sergipetec .

# Executar container
docker run -p 8080:8080 helpdesk-sergipetec
```

---

## 📁 Estrutura do Projeto

```
HelpDesk-SergipeTec/
├── backend/
│   └── src/
│       ├── main/
│       │   ├── java/com/helpdesk/
│       │   │   ├── api/
│       │   │   │   ├── AuthResource.java       # Autenticação & JWT
│       │   │   │   └── TicketResource.java     # CRUD de Tickets
│       │   │   ├── model/
│       │   │   │   └── Ticket.java             # Entidade JPA
│       │   │   └── security/
│       │   │       └── JwtUtil.java            # Utilitários JWT
│       │   └── resources/
│       │       └── META-INF/persistence.xml    # Config JPA
│       └── test/
│           ├── java/com/helpdesk/test/
│           │   ├── JwtUtilTest.java            # Testes JWT
│           │   └── TicketJpaTest.java          # Testes JPA
│           └── resources/
│               └── META-INF/persistence.xml    # Config Teste
├── frontend/
│   ├── index.html                              # Dashboard
│   ├── login.html                              # Login
│   ├── app.js                                  # Lógica frontend
│   └── styles.css                              # Estilos
├── .github/
│   ├── workflows/
│   │   └── ci.yml                              # GitHub Actions CI/CD
│   └── java-upgrade/
│       └── 20260511115348/                     # Upgrade docs
├── pom.xml                                     # Configuração Maven
├── README.md                                   # Este arquivo
└── run-tests-docker.ps1                        # Script testes Docker
```

---

## ✅ Testes

### Suites de Testes

#### 1. **JwtUtilTest** - Validação de Tokens JWT

```java
// Testa geração e parsing de tokens JWT
- Geração de token com claims corretos
- Validação de expiração
- Parsing e extração de dados
```

**Executar:**
```bash
mvn test -Dtest=JwtUtilTest
```

#### 2. **TicketJpaTest** - Persistência em Banco de Dados

```java
// Testa operações JPA com Hibernate + H2
- Criação de ticket
- Persistência em banco
- Recuperação de dados
- Transações
```

**Executar:**
```bash
mvn test -Dtest=TicketJpaTest
```

### Relatório de Testes

```bash
# Gerar relatório Surefire
mvn surefire-report:report

# Abrir relatório
target/site/surefire-report.html
```

---

## 🔌 API Endpoints

### Autenticação

```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "usuario",
  "password": "senha"
}

Response: { "token": "eyJhbGciOiJIUzI1NiJ9..." }
```

### Tickets

#### Listar Tickets

```http
GET /api/tickets
Authorization: Bearer {token}
```

#### Criar Ticket

```http
POST /api/tickets
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "Bug na autenticação",
  "description": "Não consigo fazer login",
  "status": "OPEN"
}
```

#### Atualizar Ticket

```http
PUT /api/tickets/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "Bug na autenticação - RESOLVIDO",
  "status": "CLOSED"
}
```

#### Deletar Ticket

```http
DELETE /api/tickets/{id}
Authorization: Bearer {token}
```

---

## 🚀 Upgrade Java 25

Este projeto foi recentemente atualizado de **Java 11** para **Java 25 LTS**. Veja [UPGRADE.md](.github/java-upgrade/20260511115348/summary.md) para detalhes completos.

### Mudanças Principais

✅ Java 11 → **Java 25 LTS** (Suporte até 2033)  
✅ Maven 3.x → **Maven 3.9.15**  
✅ maven-compiler-plugin → **3.11.0**  
✅ Jakarta EE 9.1 → **Jakarta EE 10.0**  
✅ PostgreSQL JDBC 42.5.4 → **42.7.2** (CVE fixes)  

### Verificar Versão

```bash
java -version
# Expected: Java 25.0.3

mvn -version
# Expected: Maven 3.9.15
```

---

## 🔒 Segurança

### Boas Práticas Implementadas

✅ **JWT com HMAC-SHA256** - Tokens assinados e verificáveis  
✅ **Variáveis de Ambiente** - Secrets não commitados  
✅ **HTTPS Ready** - Compatível com TLS/SSL  
✅ **CORS Configurável** - Proteção contra requisições não autorizadas  

### CVEs Conhecidas

| CVE | Versão | Status | Ação |
| --- | --- | --- | --- |
| CVE-2024-1597 | PostgreSQL 42.5.4 | ✅ FIXADO | Upgrading para 42.7.2 |
| CVE-2026-42198 | PostgreSQL 42.7.2 | ⚠️ RESIDUAL | Usar trusted connections |

**Mitigação**: Conecte apenas a servidores PostgreSQL confiáveis com verificação TLS.

---

## 🐛 Troubleshooting

### Problema: "Java version not found"

```bash
# Solução 1: Definir JAVA_HOME
export JAVA_HOME=/path/to/jdk-25
echo $JAVA_HOME

# Solução 2: Verificar PATH
java -version
```

### Problema: "Maven compilation failed"

```bash
# Limpar cache Maven
mvn clean install -U

# Verificar pom.xml
mvn validate
```

### Problema: "Tests failing - Database connection"

```bash
# Verificar H2 (usado em testes)
# O projeto usa H2 in-memory por padrão
mvn test -X  # Debug mode
```

### Problema: "Port 8080 already in use"

```bash
# Mudar porta no application.properties
server.port=8081
```

---

## 📚 Documentação do Projeto

- [CONTRIBUTING.md](CONTRIBUTING.md) - Guia para contribuir com o projeto
- [SECURITY.md](SECURITY.md) - Política de segurança e reporte de vulnerabilidades
- [CHANGELOG.md](CHANGELOG.md) - Histórico resumido das mudanças
- [Upgrade Java 25](.github/java-upgrade/20260511115348/summary.md) - Detalhes do upgrade mais recente

---

## 📝 Contribuindo

1. **Fork** o repositório
2. **Crie** uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. **Commit** suas mudanças (`git commit -m 'Add AmazingFeature'`)
4. **Push** para a branch (`git push origin feature/AmazingFeature`)
5. **Abra** um Pull Request

### Padrões de Código

- **Java**: seguir convenções Oracle Java
- **Nomes**: camelCase para variáveis, PascalCase para classes
- **Comments**: Documentar métodos públicos com Javadoc
- **Testes**: Mínimo 1 teste por classe

---

## 📄 Licença

Este projeto está sob a licença **MIT**. Veja [LICENSE](LICENSE) para detalhes.

---

## 👥 Autores

- **Fernando Admin** - Upgrade Java 25 LTS
- **SergipeTec Team** - Desenvolvimento inicial

---

## 📞 Suporte

- 📧 Email: support@helpdesk-sergipetec.local
- 🐛 Issues: [GitHub Issues](https://github.com/fernando-msa/HelpDesk-SergipeTec/issues)
- 📚 Documentação: [Wiki](https://github.com/fernando-msa/HelpDesk-SergipeTec/wiki)

---

## 🎓 Recursos Adicionais

- [Java 25 Documentation](https://docs.oracle.com/en/java/javase/25/)
- [Maven Official Guide](https://maven.apache.org/guides/)
- [Jakarta EE Specification](https://jakarta.ee/)
- [Hibernate ORM Reference](https://hibernate.org/orm/)
- [JWT.io](https://jwt.io/) - JWT Explanation

---

**Last Updated**: 11 de maio de 2026 | **Version**: 0.1.0 | **Java**: 25 LTS
