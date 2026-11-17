# HelpDesk — API de chamados

API REST de suporte desenvolvida com Spring Boot, Java 17, Spring Security, JWT, JPA e H2.
O estado abaixo foi conferido no código e nos testes existentes em 16/11/2026. “Pronto”
indica implementação presente; quando não há testes específicos, isso é indicado.

## Situação do backlog priorizado

| Ordem | História | Situação | Observações |
|---:|---|---|---|
| 1 | US01 — Estrutura da aplicação | **Pronto** | Spring Boot, Maven, Java 17 e pacotes por responsabilidade. |
| 2 | US02 — Cadastrar usuário | **Pronto no código** | Validação, BCrypt, username único, resposta `201` sem senha/hash. Sem testes específicos de cadastro. |
| 3 | US05 — Abrir chamado | **Pronto no código** | Validação de título, descrição e prioridade; proprietário vem do JWT; resposta `201`. Sem teste específico de criação. |
| 4 | US06 — Listar meus chamados | **Pronto** | Lista restrita ao usuário autenticado e lista vazia quando não há chamados; há testes. |
| 5 | US07 — Filtrar chamados | **Parcial** | Filtros opcionais e combinados usam consulta parametrizada; status inválido ainda pode gerar exceção não tratada, e o teste espera `400`. |
| 6 | US08 — Consultar chamado pelo ID | **Pronto** | Busca por ID e proprietário; chamado alheio e inexistente retornam `404`; testes direcionados passaram. |
| 7 | US09 — Atualizar status | **Pronto** | Somente o proprietário atualiza; status inválido e transição de `FECHADO` para `ABERTO` retornam `400`; testes direcionados passaram. |
| 8 | US10 — Validar dados de entrada | **Parcial** | Há Bean Validation e validação de enums, mas respostas de erro e identificação do campo ainda não são uniformes. |
| 9 | US11 — Padronizar respostas de erro | **Pendente** | O tratamento global foi desfeito; respostas ainda variam conforme a origem do erro. |
| 10 | US03 — Autenticar usuário | **Pronto no código** | Compara senha com BCrypt e emite JWT com expiração. Sem testes específicos de autenticação. |
| 11 | US04 — Proteger endpoints com JWT | **Parcial** | Rotas protegidas e sessão stateless; falta validar todos os cenários exigidos e usar a variável de ambiente nos ambientes compartilhados. |
| 12 | US12 — Garantir propriedade dos chamados | **Pronto no código** | Criação, listagem, consulta e atualização usam a identidade do token; há testes de acesso cruzado. |
| 13 | US13 — Proteger segredos e credenciais | **Parcial** | Senhas usam BCrypt. O valor JWT em `application.properties` é apenas para desenvolvimento local; nos ambientes compartilhados, a chave será configurada por variável de ambiente. Evite manter tokens válidos no arquivo de requisições. |
| 14 | US14 — Validar cenários críticos | **Parcial** | Existem testes de chamados; faltam testes para cadastro, autenticação, senha errada e tokens adulterados/expirados. |
| 15 | US16 — Testes automatizados de segurança | **Parcial** | Há testes automatizados para listagem, filtros, consulta e atualização; o conjunto não cobre todos os critérios e a suíte completa não está verde. |
| 16 | US15 — README do projeto | **Parcial** | Este documento cobre execução, API, decisões e status; nomes/divisão da equipe e extra precisam ser preenchidos pela equipe. |

No [backlog](backlog.md), somente US08 e US09 estão marcadas como concluídas.

## Como executar

Requisitos: Java 17 ou superior e conexão à internet na primeira compilação.

```bash
export SECURITY_JWT_SECRET="$(openssl rand -hex 32)"
sh mvnw spring-boot:run
```

A API fica em `http://localhost:8888`. O banco H2 é persistido localmente em `data/seguranca`.
O valor de JWT em `application.properties` deve ser tratado somente como configuração de
desenvolvimento. Nos ambientes compartilhados e de produção, configure `SECURITY_JWT_SECRET`
no ambiente de execução; essa variável sobrescreve o valor local.

Para executar os testes:

```bash
sh mvnw test
```

**Atenção:** a suíte completa ainda tem falha conhecida no teste de status inválido de US07.
Os testes direcionados de US08/US09 passaram anteriormente; isso não significa que a suíte
completa esteja aprovada.

## API e exemplos de requisição

As rotas `/user` e `/authenticate` são públicas. As rotas de chamados exigem
`Authorization: Bearer <token>`. O JWT expira em uma hora.

### Cadastrar e autenticar

Escolha um `username` ainda não cadastrado:

```bash
curl -i -X POST http://localhost:8888/user \
  -H 'Content-Type: application/json' \
  -d '{"nome":"Usuário Demo","username":"usuario.demo","password":"SenhaDemo123"}'
```

```bash
curl -i -X POST http://localhost:8888/authenticate \
  -H 'Content-Type: application/json' \
  -d '{"username":"usuario.demo","password":"SenhaDemo123"}'
```

Copie o campo `token` da resposta e use-o nas chamadas protegidas:

```bash
TOKEN='COLE_AQUI_O_TOKEN_RECEBIDO'
```

### Criar chamado

O ID é gerado pelo banco e aparece na resposta `201 Created`; não envie ID ou proprietário
no corpo.

```bash
curl -i -X POST http://localhost:8888/chamados \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"titulo":"Notebook lento","descricao":"Demora para iniciar.","prioridade":"ALTA"}'
```

### Listar e filtrar chamados

```bash
curl -i "http://localhost:8888/chamados?status=ABERTO&titulo=notebook" \
  -H "Authorization: Bearer $TOKEN"
```

Filtros aceitos:

- `status`: `ABERTO`, `EM_ANDAMENTO` ou `FECHADO`.
- `titulo`: busca por trecho do título, sem diferenciar maiúsculas/minúsculas.

### Consultar por ID

Use o ID devolvido na criação:

```bash
curl -i http://localhost:8888/chamados/1 \
  -H "Authorization: Bearer $TOKEN"
```

Chamado inexistente ou pertencente a outro usuário retorna `404`.

### Atualizar status

```bash
curl -i -X PATCH http://localhost:8888/chamados/1/status \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"status":"EM_ANDAMENTO"}'
```

O arquivo [requisicoes.http](requisicoes.http) também contém exemplos, mas seu token fixo
deve ser substituído por um token recém-obtido. Não compartilhe nem versione tokens válidos.

## Decisões e notas de segurança

- **Propriedade:** a identidade vem do `sub` do JWT. Consultas e atualizações usam ID e
  username do proprietário; acesso cruzado responde `404` para não confirmar a existência.
- **Senha:** armazenada com BCrypt; a resposta de cadastro não inclui senha nem hash.
- **SQL Injection:** filtros usam JPQL parametrizado; não concatenam valores fornecidos pelo cliente.
- **CSRF:** desabilitado porque a API usa JWT no cabeçalho Bearer e não autenticação baseada
  em sessão/cookie. Se o mecanismo mudar para cookies, a decisão precisa ser revista.
- **XSS:** a API recebe texto, mas não o codifica para uma página. O front-end deve escapar
  o conteúdo conforme o contexto em que o exibe.
- **HTTPS:** obrigatório em produção; sem TLS, um token Bearer pode ser interceptado.
- **H2 Console:** habilitado para desenvolvimento; deve ser desabilitado ou protegido em produção.
- **Erros:** ainda não há formato global padronizado (US11 pendente). Não interprete mensagens
  de erro como contrato estável.

## Mapa resumido de ameaças

| Ameaça | Defesa atual / pendência |
|---|---|
| Acesso sem autenticação | Rotas de chamados exigem JWT; `401` para ausência ou rejeição do token. |
| Acesso ao chamado de outra pessoa | Consultas por ID também filtram pelo proprietário; resposta `404`. |
| Injeção em filtros | JPQL parametrizado e busca de título como texto. |
| Senha exposta no banco | BCrypt. |
| Chave JWT ou token exposto | O valor local é somente para desenvolvimento; definir `SECURITY_JWT_SECRET` nos ambientes compartilhados e não manter tokens válidos no arquivo de requisições. |
| Erros revelam detalhes internos | **Pendente:** implementar US11 com tratamento global e formato uniforme. |
| XSS no cliente | O front-end deve codificar/escapar o texto ao renderizar. |
| Interceptação do token | Usar HTTPS em produção e evitar compartilhar tokens. |

## Equipe e entrega

- **Integrantes:** preencher pela equipe.
- **Divisão das atividades:** preencher pela equipe.
- **Funcionalidade extra:** não informada; US16 (testes de segurança) está parcial.
