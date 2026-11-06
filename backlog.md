# Backlog do Projeto HelpDesk

## 1\. Visão geral

A **API de Chamados de Suporte HelpDesk** permitirá que colaboradores se cadastrem, realizem autenticação e gerenciem seus próprios chamados de suporte.

O principal objetivo do projeto é aplicar fundamentos de segurança em uma API REST desenvolvida com Spring Boot.

### Informações principais

* **Aplicação:** API REST em uma única aplicação Spring Boot
* **Equipe:** 3 a 4 pessoas
* **Entrega:** 18/11/2026
* **Apresentação:** 6 minutos e 2 minutos para perguntas
* **Autenticação:** JWT
* **Banco de dados:** H2 ou outro SGBD escolhido pela equipe
* **Extra obrigatório:** 1 extra para equipes de 3 pessoas e 2 extras para equipes de 4 pessoas

\---

## 2\. Objetivo do produto

Permitir que um colaborador:

1. Realize seu cadastro.
2. Autentique-se utilizando usuário e senha.
3. Receba um token JWT.
4. Abra chamados de suporte.
5. Liste e pesquise seus chamados.
6. Consulte um chamado específico.
7. Atualize o status de um chamado.

### Regra de segurança principal

Cada usuário pode acessar somente os chamados que ele mesmo criou.

Caso um usuário tente acessar um chamado de outra pessoa, a API deve responder com **404 Not Found**, da mesma forma que responderia para um chamado inexistente.

\---

## 3\. Épicos

|Épico|Descrição|
|-|-|
|E01|Estrutura inicial do projeto|
|E02|Cadastro de usuários|
|E03|Autenticação e segurança JWT|
|E04|Gerenciamento de chamados|
|E05|Validação e tratamento de erros|
|E06|Proteção dos dados e segredos|
|E07|Testes de segurança|
|E08|Documentação e entrega|
|E09|Funcionalidade extra|

\---

# 4\. Histórias de usuário e critérios de aceite

## E01 - Estrutura inicial do projeto

### US01 - Criar a estrutura da aplicação

**Como equipe**, queremos criar e organizar a aplicação Spring Boot para iniciar o desenvolvimento do HelpDesk.

#### Critérios de aceite

* \[ ] Projeto criado com Spring Boot.
* \[ ] Projeto configurado com Java 17.
* \[ ] Projeto utilizando Maven.
* \[ ] Dependência Spring Web adicionada.
* \[ ] Dependência Spring Data JPA adicionada.
* \[ ] Dependência Spring Security adicionada.
* \[ ] Dependência Validation adicionada.
* \[ ] Dependência H2 adicionada.
* \[ ] Aplicação inicia sem erros.
* \[ ] Pacotes separados por responsabilidade.

#### Estrutura sugerida

```text
helpdesk
├── chamado
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── repository
│   └── service
├── usuario
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── repository
│   └── service
├── security
└── exception
```

**Prioridade:** Alta  
**Estimativa:** 2 pontos

\---

## E02 - Cadastro de usuários

### US02 - Cadastrar usuário

**Como colaborador**, quero realizar meu cadastro para utilizar o sistema de chamados.

**Endpoint:** `POST /user`

#### Critérios de aceite

* \[ ] O nome é obrigatório.
* \[ ] O nome deve possuir entre 2 e 100 caracteres.
* \[ ] O username é obrigatório.
* \[ ] O username deve possuir entre 3 e 30 caracteres.
* \[ ] O username aceita somente letras, números, ponto e sublinhado.
* \[ ] O username deve ser único.
* \[ ] A senha deve possuir pelo menos 8 caracteres.
* \[ ] A senha deve ser armazenada como hash BCrypt.
* \[ ] A senha original não pode ser persistida.
* \[ ] A senha e o hash não podem aparecer na resposta.
* \[ ] Cadastro válido retorna `201 Created`.
* \[ ] Dados inválidos retornam `400 Bad Request`.
* \[ ] Username já cadastrado retorna `409 Conflict`.

#### Exemplo de entrada

```json
{
  "nome": "Ana Souza",
  "username": "ana.souza",
  "password": "senha123"
}
```

#### Exemplo de resposta

```json
{
  "id": 1,
  "nome": "Ana Souza",
  "username": "ana.souza"
}
```

**Prioridade:** Alta  
**Estimativa:** 3 pontos  
**Caso de uso:** UC1

\---

## E03 - Autenticação e segurança JWT

### US03 - Autenticar usuário

**Como usuário cadastrado**, quero me autenticar para receber um token de acesso.

**Endpoint:** `POST /authenticate`

#### Critérios de aceite

* \[ ] Username e senha válidos retornam `200 OK`.
* \[ ] A API gera um token JWT.
* \[ ] O token possui tempo de expiração definido.
* \[ ] O token não contém senha ou informação sensível.
* \[ ] A senha é comparada com o hash BCrypt armazenado.
* \[ ] Usuário inexistente retorna `401 Unauthorized`.
* \[ ] Senha incorreta retorna `401 Unauthorized`.
* \[ ] Usuário inexistente e senha incorreta retornam a mesma mensagem.
* \[ ] A mensagem esperada é `Usuário ou senha inválidos.`.

#### Exemplo de entrada

```json
{
  "username": "ana.souza",
  "password": "senha123"
}
```

#### Exemplo de resposta

```json
{
  "token": "<jwt>"
}
```

**Prioridade:** Crítica  
**Estimativa:** 5 pontos  
**Caso de uso:** UC2

\---

### US04 - Proteger endpoints com JWT

**Como sistema**, quero validar o token JWT nas rotas protegidas para impedir acessos não autenticados.

#### Critérios de aceite

* \[ ] `POST /user` permanece público.
* \[ ] `POST /authenticate` permanece público.
* \[ ] Todas as rotas `/chamados/\*\*` exigem autenticação.
* \[ ] Token válido permite acesso.
* \[ ] Token ausente retorna `401 Unauthorized`.
* \[ ] Token adulterado retorna `401 Unauthorized`.
* \[ ] Token expirado retorna `401 Unauthorized`.
* \[ ] A assinatura do token é validada.
* \[ ] A identidade do usuário é obtida pelo token.
* \[ ] A chave JWT fica fora do código-fonte.
* \[ ] A chave JWT é carregada por variável de ambiente.
* \[ ] A aplicação utiliza sessão stateless.
* \[ ] A decisão de desabilitar CSRF está documentada no README.

**Prioridade:** Crítica  
**Estimativa:** 5 pontos

\---

## E04 - Gerenciamento de chamados

### US05 - Abrir chamado

**Como usuário autenticado**, quero abrir um chamado para registrar um problema de suporte.

**Endpoint:** `POST /chamados`

#### Critérios de aceite

* \[ ] O usuário deve estar autenticado.
* \[ ] O título é obrigatório.
* \[ ] O título deve possuir entre 5 e 100 caracteres.
* \[ ] A descrição é obrigatória.
* \[ ] A descrição deve possuir no máximo 2.000 caracteres.
* \[ ] A prioridade aceita `BAIXA`, `MEDIA` ou `ALTA`.
* \[ ] O status inicial é definido automaticamente como `ABERTO`.
* \[ ] A data de criação é preenchida automaticamente.
* \[ ] O proprietário é identificado pelo token.
* \[ ] A API não recebe um ID de usuário para definir o proprietário.
* \[ ] Chamado válido retorna `201 Created`.
* \[ ] Dados inválidos retornam `400 Bad Request`.
* \[ ] Requisição sem autenticação retorna `401 Unauthorized`.

#### Exemplo de entrada

```json
{
  "titulo": "Notebook lento",
  "descricao": "O computador está demorando muito para iniciar.",
  "prioridade": "ALTA"
}
```

**Prioridade:** Alta  
**Estimativa:** 3 pontos  
**Caso de uso:** UC3

\---

### US06 - Listar meus chamados

**Como usuário autenticado**, quero listar meus chamados para acompanhar minhas solicitações.

**Endpoint:** `GET /chamados`

#### Critérios de aceite

* \[ ] O usuário deve estar autenticado.
* \[ ] Somente chamados do usuário autenticado são retornados.
* \[ ] Chamados de outros usuários nunca aparecem na resposta.
* \[ ] Quando não houver chamados, a API retorna uma lista vazia.
* \[ ] A listagem válida retorna `200 OK`.
* \[ ] Requisição sem token válido retorna `401 Unauthorized`.

**Prioridade:** Alta  
**Estimativa:** 3 pontos  
**Caso de uso:** UC4

\---

### US07 - Filtrar chamados

**Como usuário autenticado**, quero filtrar meus chamados por status e título para encontrar solicitações com facilidade.

#### Exemplos

```http
GET /chamados?status=ABERTO
GET /chamados?titulo=impressora
GET /chamados?status=ABERTO\&titulo=impressora
```

#### Critérios de aceite

* \[ ] O filtro por status é opcional.
* \[ ] O filtro por parte do título é opcional.
* \[ ] Os dois filtros podem ser utilizados juntos.
* \[ ] A busca retorna somente chamados do usuário autenticado.
* \[ ] Status inválido retorna `400 Bad Request`.
* \[ ] A consulta utiliza Spring Data ou JPQL parametrizado.
* \[ ] Não existe concatenação de SQL com valores recebidos do cliente.

**Prioridade:** Alta  
**Estimativa:** 3 pontos  
**Caso de uso:** UC4

\---

### US08 - Consultar chamado pelo ID

**Como usuário autenticado**, quero consultar um chamado específico que pertença a mim.

**Endpoint:** `GET /chamados/{id}`

#### Critérios de aceite

* \[ ] Chamado pertencente ao usuário retorna `200 OK`.
* \[ ] Chamado inexistente retorna `404 Not Found`.
* \[ ] Chamado pertencente a outro usuário retorna `404 Not Found`.
* \[ ] As duas situações de 404 utilizam o mesmo formato de resposta.
* \[ ] A consulta filtra simultaneamente pelo ID e pelo proprietário.
* \[ ] A consulta não busca apenas pelo ID para depois revelar o proprietário.
* \[ ] Requisição sem token válido retorna `401 Unauthorized`.

**Prioridade:** Crítica  
**Estimativa:** 3 pontos  
**Caso de uso:** UC5

\---

### US09 - Atualizar status do chamado

**Como proprietário de um chamado**, quero alterar seu status para registrar sua evolução.

**Endpoint:** `PATCH /chamados/{id}/status`

#### Critérios de aceite

* \[ ] O novo status aceita `ABERTO`, `EM\_ANDAMENTO` ou `FECHADO`.
* \[ ] Somente o proprietário pode atualizar o chamado.
* \[ ] Chamado inexistente retorna `404 Not Found`.
* \[ ] Chamado pertencente a outro usuário retorna `404 Not Found`.
* \[ ] Status inválido retorna `400 Bad Request`.
* \[ ] Transição não permitida retorna `400 Bad Request`.
* \[ ] Um chamado `FECHADO` não pode voltar para `ABERTO`.
* \[ ] Atualização válida retorna `200 OK`.
* \[ ] Requisição sem autenticação retorna `401 Unauthorized`.

#### Exemplo de entrada

```json
{
  "status": "EM\_ANDAMENTO"
}
```

**Prioridade:** Alta  
**Estimativa:** 3 pontos  
**Caso de uso:** UC6

\---

## E05 - Validação e tratamento de erros

### US10 - Validar dados de entrada

**Como sistema**, quero validar todos os dados recebidos para impedir entradas inválidas.

#### Critérios de aceite

* \[ ] A aplicação utiliza Bean Validation.
* \[ ] Campos obrigatórios não aceitam valores vazios.
* \[ ] Limites mínimos e máximos são validados.
* \[ ] Enums aceitam somente valores definidos.
* \[ ] O username segue o formato definido.
* \[ ] Dados inválidos retornam `400 Bad Request`.
* \[ ] A mensagem identifica o campo inválido.
* \[ ] A resposta não expõe detalhes internos.

**Prioridade:** Alta  
**Estimativa:** 3 pontos

\---

### US11 - Padronizar respostas de erro

**Como consumidor da API**, quero receber respostas de erro padronizadas para entender o problema sem ter acesso a informações internas.

#### Formato esperado

```json
{
  "status": 404,
  "erro": "Not Found",
  "mensagem": "Chamado 10 não encontrado.",
  "timestamp": "2026-11-17T10:45:02"
}
```

#### Critérios de aceite

* \[ ] Todas as respostas de erro seguem o mesmo formato.
* \[ ] Entrada inválida retorna `400`.
* \[ ] Problema de autenticação retorna `401`.
* \[ ] Chamado inexistente ou de outro usuário retorna `404`.
* \[ ] Username já cadastrado retorna `409`.
* \[ ] Erro inesperado retorna `500`.
* \[ ] Stack trace nunca aparece na resposta.
* \[ ] SQL nunca aparece na resposta.
* \[ ] Senhas, hashes e tokens nunca aparecem em logs.
* \[ ] Detalhes técnicos ficam somente no log interno.

**Prioridade:** Alta  
**Estimativa:** 3 pontos

\---

## E06 - Proteção dos dados e segredos

### US12 - Garantir a propriedade dos chamados

**Como usuário**, quero acessar somente meus próprios chamados para impedir o vazamento de dados de outros colaboradores.

#### Critérios de aceite

* \[ ] A criação associa o chamado ao usuário autenticado.
* \[ ] A identidade do proprietário vem do token.
* \[ ] A listagem filtra pelo usuário autenticado.
* \[ ] A consulta por ID filtra pelo usuário autenticado.
* \[ ] A atualização de status filtra pelo usuário autenticado.
* \[ ] O usuário A nunca consegue obter dados do usuário B.
* \[ ] Tentativa de acesso cruzado retorna `404`, e não `403`.
* \[ ] A API não confirma se um chamado pertence a outro usuário.

**Prioridade:** Crítica  
**Estimativa:** 5 pontos

\---

### US13 - Proteger segredos e credenciais

**Como equipe**, queremos proteger senhas, tokens e chaves para evitar exposição de informações sensíveis.

#### Critérios de aceite

* \[ ] Senhas são persistidas somente como BCrypt.
* \[ ] Senha nunca aparece nas respostas.
* \[ ] Hash nunca aparece nas respostas.
* \[ ] Senha não aparece nos logs.
* \[ ] Token não aparece nos logs.
* \[ ] A chave JWT fica fora do código-fonte.
* \[ ] A chave JWT é informada por variável de ambiente.
* \[ ] Nenhum segredo real é commitado no Git.
* \[ ] O `.gitignore` está configurado.

**Prioridade:** Crítica  
**Estimativa:** 2 pontos

\---

## E07 - Testes de segurança

### US14 - Validar cenários críticos

**Como equipe**, queremos validar os principais cenários funcionais e de segurança antes da entrega.

#### Critérios de aceite

* \[ ] Cadastrar usuário retorna `201`.
* \[ ] Autenticar usuário retorna `200` e um JWT.
* \[ ] Senha incorreta retorna `401`.
* \[ ] Acesso a chamados sem token retorna `401`.
* \[ ] Acesso com token válido retorna `200`.
* \[ ] Token adulterado retorna `401`.
* \[ ] Token expirado retorna `401`.
* \[ ] Criar chamado retorna `201`.
* \[ ] Consultar o próprio chamado retorna `200`.
* \[ ] Consultar chamado de outro usuário retorna `404`.
* \[ ] Atualizar chamado de outro usuário retorna `404`.
* \[ ] Username repetido retorna `409`.
* \[ ] Entrada inválida retorna `400`.

**Prioridade:** Alta  
**Estimativa:** 3 pontos

\---

## E08 - Documentação e entrega

### US15 - Criar o README do projeto

**Como avaliador ou desenvolvedor**, quero encontrar instruções e decisões documentadas para executar, testar e compreender a segurança da aplicação.

#### Critérios de aceite

* \[ ] README informa os integrantes da equipe.
* \[ ] README apresenta a divisão das atividades.
* \[ ] README ensina como executar a aplicação.
* \[ ] README explica como configurar a variável `JWT\_SECRET`.
* \[ ] README ensina como cadastrar um usuário.
* \[ ] README ensina como autenticar.
* \[ ] README explica como utilizar o token.
* \[ ] README informa qual cabeçalho de token é aceito.
* \[ ] README explica o tempo de expiração do JWT.
* \[ ] README explica por que a chave fica fora do código.
* \[ ] README justifica por que o CSRF foi desabilitado.
* \[ ] README explica por que HTTPS é obrigatório em produção.
* \[ ] README explica a responsabilidade do front-end em relação ao XSS.
* \[ ] README explica como SQL Injection é evitada.
* \[ ] README explica por que chamados de outros usuários retornam 404.
* \[ ] README contém o mapa de ameaças preenchido.
* \[ ] README descreve o extra implementado.
* \[ ] README contém exemplos ou uma coleção de requisições.

**Prioridade:** Alta  
**Estimativa:** 3 pontos

\---

## E09 - Funcionalidade extra

### US16 - Criar testes automatizados de segurança

**Como equipe**, queremos automatizar os principais cenários de segurança para evitar regressões.

#### Critérios de aceite

* \[ ] Existe teste automatizado para cadastro.
* \[ ] Existe teste automatizado para autenticação.
* \[ ] Existe teste para senha incorreta.
* \[ ] Existe teste para acesso sem JWT.
* \[ ] Existe teste para acesso com JWT válido.
* \[ ] Existe teste para criação de chamado.
* \[ ] Existe teste para consulta do próprio chamado.
* \[ ] Existe teste para tentativa de acesso ao chamado de outro usuário.
* \[ ] O acesso ao chamado de outro usuário é validado como `404`.
* \[ ] Os testes podem ser executados com um único comando Maven.

**Prioridade:** Média  
**Estimativa:** 5 pontos  
**Classificação:** Extra sugerido

\---

# 5\. Backlog priorizado

|Ordem|História|Descrição|Prioridade|Pontos|
|-:|-|-|-|-:|
|1|US01|Criar estrutura da aplicação|Alta|2|
|2|US02|Cadastrar usuário|Alta|3|
|3|US05|Abrir chamado|Alta|3|
|4|US06|Listar meus chamados|Alta|3|
|5|US07|Filtrar chamados|Alta|3|
|6|US08|Consultar chamado pelo ID|Crítica|3|
|7|US09|Atualizar status|Alta|3|
|8|US10|Validar dados de entrada|Alta|3|
|9|US11|Padronizar respostas de erro|Alta|3|
|10|US03|Autenticar usuário|Crítica|5|
|11|US04|Proteger endpoints com JWT|Crítica|5|
|12|US12|Garantir propriedade dos chamados|Crítica|5|
|13|US13|Proteger segredos e credenciais|Crítica|2|
|14|US14|Validar cenários críticos|Alta|3|
|15|US16|Criar testes automatizados|Média|5|
|16|US15|Criar README|Alta|3|

**Estimativa total:** 54 pontos.

\---

# 6\. Etapas de desenvolvimento

## Etapa 1 - Base funcional

1. US01 - Estrutura da aplicação.
2. US02 - Cadastro de usuário.
3. US05 - Abertura de chamado.
4. US06 - Listagem de chamados.
5. US07 - Filtros de chamados.
6. US08 - Consulta por ID.
7. US09 - Atualização de status.

## Etapa 2 - Segurança

1. US10 - Validação de entrada.
2. US11 - Tratamento global de erros.
3. US03 - Autenticação e geração do JWT.
4. US04 - Proteção das rotas.
5. US12 - Regra de propriedade.
6. US13 - Proteção de segredos.

## Etapa 3 - Testes e entrega

1. US14 - Validação dos cenários críticos.
2. US16 - Testes automatizados.
3. US15 - README e documentação.
4. Preparação da demonstração.
5. Ensaio da apresentação.

\---

# 7\. Definition of Done

Uma história somente pode ser movida para **Concluída** quando:

* \[ ] A aplicação compila.
* \[ ] A funcionalidade está implementada.
* \[ ] Todos os critérios de aceite foram verificados.
* \[ ] Os códigos HTTP estão corretos.
* \[ ] As validações necessárias estão implementadas.
* \[ ] Nenhuma senha, hash, token ou segredo é exposto.
* \[ ] A regra de propriedade é respeitada quando aplicável.
* \[ ] O código foi revisado por outro integrante.
* \[ ] O código foi commitado no repositório.
* \[ ] Pelo menos outro integrante consegue explicar a funcionalidade.

\---

# 8\. Roteiro mínimo para demonstração

1. Cadastrar o usuário A.
2. Cadastrar o usuário B.
3. Autenticar o usuário A.
4. Tentar acessar `/chamados` sem token e receber `401`.
5. Criar um chamado com o token do usuário A.
6. Listar os chamados do usuário A.
7. Consultar o chamado do usuário A e receber `200`.
8. Autenticar o usuário B.
9. Tentar consultar o chamado do usuário A usando o token do usuário B.
10. Receber `404 Not Found`.
11. Mostrar que senha, hash e detalhes internos não aparecem nas respostas.

\---

# 9\. Resumo dos endpoints

|Método|Endpoint|Função|Acesso|
|-|-|-|-|
|POST|`/user`|Cadastrar usuário|Público|
|POST|`/authenticate`|Autenticar e gerar JWT|Público|
|POST|`/chamados`|Abrir chamado|Protegido|
|GET|`/chamados`|Listar e filtrar chamados|Protegido|
|GET|`/chamados/{id}`|Consultar chamado|Protegido|
|PATCH|`/chamados/{id}/status`|Atualizar status|Protegido|

\---

# 10\. Observações importantes

* A identidade do usuário sempre vem da autenticação, nunca do corpo da requisição.
* Toda consulta de chamado deve filtrar pelo usuário autenticado.
* Um chamado de outro usuário deve ser tratado como inexistente.
* A chave do JWT não pode ficar no código nem no repositório.
* O foco do trabalho é segurança, e não quantidade de funcionalidades.
* Todos os integrantes devem compreender o projeto completo.

