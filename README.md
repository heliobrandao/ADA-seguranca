# HelpDesk — API de Chamados de Suporte

**Integrantes:**

- Edgar Guimaraes Damazio Junior
- Henrique Campanharo Seabra
- Karla Danielle Santos da Silva
- Helio Serrano Brandao Junior

Projeto do módulo **Fundamentos de Segurança para Aplicações — ADA**, desenvolvido para aplicar autenticação, proteção de senhas, controle de acesso e validação de dados em uma API REST.

No cenário proposto, colaboradores de uma empresa fictícia substituem solicitações de suporte por e-mail e planilhas por chamados. Cada pessoa pode se cadastrar, autenticar, abrir chamados e acompanhar suas próprias solicitações. **Um usuário não deve consultar nem alterar chamados de outro usuário.**

O foco é demonstrar os fundamentos de segurança, e não ampliar a quantidade de funcionalidades. Os requisitos estão na [especificação](./apichamadosespecificacao.md) e no [backlog](./backlog.md).

## Backlog priorizado e situação atual

A coluna **OK no backlog** reproduz as marcações existentes, sem modificá-las. A última coluna registra a comparação com o código disponível: marcação não equivale, por si só, a todos os critérios de aceite comprovados.

| Ordem | História | Descrição | Prioridade | Pontos | OK no backlog | Situação observada no código |
| ---: | --- | --- | --- | ---: | :---: | --- |
| 1 | US01 | Criar estrutura da aplicação | Alta | 2 | ✅ | Estrutura e dependências presentes; inicialização não revalidada nesta revisão documental. |
| 2 | US02 | Cadastrar usuário | Alta | 3 | ✅ | Cadastro com validações, BCrypt, DTO de resposta, retorno 201 e verificação de duplicidade. |
| 3 | US05 | Abrir chamado | Alta | 3 | ✅ | Implementado |
| 4 | US06 | Listar meus chamados | Alta | 3 | ✅ | Listagem restrita ao usuário autenticado e testes existentes para isolamento e lista vazia. |
| 5 | US07 | Filtrar chamados | Alta | 3 | ✅ | Filtros opcionais e combinados, consulta parametrizada e handler de parâmetro inválido com 400. |
| 6 | US08 | Consultar chamado pelo ID | Crítica | 3 | ✅ | Consulta por ID e proprietário simultaneamente; inexistente ou de outro usuário resulta em 404. |
| 7 | US09 | Atualizar status | Alta | 3 | ✅ | Atualização por proprietário, enum validado e bloqueio de FECHADO → ABERTO. |
| 8 | US10 | Validar dados de entrada | Alta | 3 | — | Validações e handlers implementados; falta consolidar evidências de todos os critérios. |
| 9 | US11 | Padronizar respostas de erro | Alta | 3 | — | Parcial: handler global existe, mas os erros de autenticação não seguem todos o mesmo formato. |
| 10 | US03 | Autenticar usuário | Crítica | 5 | ✅ | Comparação BCrypt, geração de JWT e mesma mensagem para credenciais incorretas. |
| 11 | US04 | Proteger endpoints com JWT | Crítica | 5 | ✅ | Configuração stateless, validação JWT e chave externa presentes; evidências de token adulterado/expirado ainda precisam ser consolidadas. |
| 12 | US12 | Garantir propriedade dos chamados | Crítica | 5 | — | Regra presente na criação, listagem, consulta e atualização; faltam evidências completas de acesso cruzado. |
| 13 | US13 | Proteger segredos e credenciais | Crítica | 2 | — | BCrypt, respostas sem hash e variável de ambiente presentes; revisão do histórico Git e de artefatos ainda pendente. |
| 14 | US14 | Validar cenários críticos | Alta | 3 | — | Há testes de listagem/filtros; o roteiro completo de aceite ainda precisa de evidências. |
| 15 | US16 | Criar testes automatizados | Média | 5 | — | Parcial: existem testes de listagem/filtros, mas não toda a cobertura exigida pela história. |
| 16 | US15 | Criar README | Alta | 3 | ✅ | Este documento cobre execução, decisões, integrantes e divisão das atividades; a confirmação do extra permanece pendente. |

**Estimativa total do backlog: 54 pontos.**

## MVP, arquitetura e tecnologias

O projeto foi desenvolvido como um **MVP (Produto Mínimo Viável)**, considerando a complexidade do escopo e o tempo disponível para desenvolvimento. A proposta é entregar o fluxo essencial de cadastro, autenticação e gerenciamento dos próprios chamados, priorizando os fundamentos de segurança exigidos pelo módulo.

MVP define o recorte da entrega, não um padrão arquitetural. Tecnicamente, a aplicação é um **monólito organizado em camadas**, com endpoints REST que retornam JSON e sem interface gráfica. O Spring MVC é utilizado como infraestrutura web do Spring Boot.

```text
Cliente HTTP / Postman
        |
Spring Security — validação do Bearer JWT nas rotas protegidas
        |
Controller — endpoints, DTOs e validação de entrada
        |
Service — regras de negócio e propriedade dos chamados
        |
Repository — Spring Data JPA e JPQL parametrizado
        |
Banco H2
```

| Tecnologia | Uso |
| --- | --- |
| Java 17 e Maven | Linguagem e gerenciamento do build |
| Spring Boot 4.1.2 / Spring MVC | Inicialização e endpoints REST |
| Spring Data JPA / Hibernate | Persistência |
| Spring Security / OAuth2 Resource Server | Proteção das rotas e validação JWT |
| BCrypt, custo 12 | Hash de senhas |
| Jakarta Bean Validation | Validação de DTOs |
| H2 em arquivo | Banco local persistente |
| JUnit, Spring Boot Test e MockMvc | Testes existentes |

Os pacotes de negócio ficam em `br.gov.caixa.projeto_fundamentos_seguranca_ada`:

- `usuario`: cadastro e autenticação, com controllers, DTOs, entidade, repositório e services.
- `chamado`: abertura, listagem, filtros, consulta e atualização de status, com as mesmas divisões.
- `security`: configuração de acesso, BCrypt e geração/validação JWT.
- `exception`: tratamento global de erros da camada MVC.

## Equipe e divisão das atividades

| Integrante | Atividades realizadas |
| --- | --- |
| Edgar Guimaraes Damazio Junior | US07 — Filtrar chamados; participação nas histórias desenvolvidas em conjunto. |
| Henrique Campanharo Seabra | US08 — Consultar chamado pelo ID e US09 — Atualizar status, em dupla com Helio; participação nas histórias desenvolvidas em conjunto. |
| Karla Danielle Santos da Silva | US06 — Listar meus chamados; participação nas histórias desenvolvidas em conjunto. |
| Helio Serrano Brandao Junior | US03 — Autenticar usuário; US08 — Consultar chamado pelo ID e US09 — Atualizar status, em dupla com Henrique; participação nas histórias desenvolvidas em conjunto. |

As histórias **US01 — Criar estrutura da aplicação**, **US02 — Cadastrar usuário**, **US05 — Abrir chamado** e **US04 — Proteger endpoints com JWT** foram desenvolvidas por todos os integrantes em conjunto.

Todos os integrantes devem compreender o fluxo completo e as decisões de segurança, independentemente da divisão adotada.

## Como executar

### Pré-requisitos

- JDK 17.
- Maven instalado ou utilização do Maven Wrapper incluído.
- Porta 8888 disponível.
- Variável de ambiente com a chave de assinatura JWT.

### Configurar a chave JWT

A [configuração atual](./src/main/resources/application.properties) utiliza:

```properties
security.jwt.secret=${JWT_KEY}
security.jwt.issuer=helpdesk
security.jwt.expiration=PT1H
```

**Atenção:** o backlog chama a variável de `JWT_SECRET`, mas o nome consumido atualmente pela aplicação é **`JWT_KEY`**. Definir somente `JWT_SECRET` não atende à configuração existente. A chave precisa ter pelo menos 32 bytes em UTF-8.

No PowerShell, gere uma chave aleatória para uso local e mantenha-a apenas no ambiente do processo:

```powershell
$bytes = New-Object byte[] 32
$rng = [System.Security.Cryptography.RandomNumberGenerator]::Create()
try {
    $rng.GetBytes($bytes)
    $env:JWT_KEY = [Convert]::ToBase64String($bytes)
} finally {
    $rng.Dispose()
}
```

Não coloque o valor gerado em arquivos versionados, capturas de tela ou coleções compartilhadas. A aplicação deve ser iniciada no mesmo terminal; se executada pelo VS Code, a variável precisa estar disponível no processo de execução correspondente.

### Iniciar a aplicação

Na raiz do projeto, no terminal que contém a variável:

```powershell
mvn spring-boot:run
```

Alternativa com o wrapper:

```powershell
.\mvnw.cmd spring-boot:run
```

Endereço local: `http://localhost:8888`.

O H2 persiste os dados em `.\data\seguranca`, e o Hibernate está configurado com `ddl-auto=update`. 

O console local está habilitado em `http://localhost:8888/h2-console`, com JDBC URL `jdbc:h2:file:./data/seguranca`, usuário `sa` e senha vazia. **Esse console está liberado sem autenticação e deve ser desabilitado antes de qualquer exposição fora do ambiente local.** Não inicie duas instâncias usando o mesmo banco em arquivo ou a mesma porta.

## Como utilizar a API

Envie os corpos como JSON com `Content-Type: application/json`.

### 1. Cadastrar um usuário

`POST http://localhost:8888/user`

```json
{
  "nome": "Usuario Exemplo",
  "username": "usuario.exemplo",
  "password": "senha-apenas-para-teste"
}
```

Resposta esperada: **201 Created**, com `id`, `nome` e `username`, sem senha ou hash. O username deve ter de 3 a 30 caracteres e aceitar somente letras sem acentos, números, ponto e sublinhado. A senha deve ter pelo menos 8 caracteres e no máximo 72 bytes em UTF-8.

### 2. Autenticar

`POST http://localhost:8888/authenticate`

```json
{
  "username": "usuario.exemplo",
  "password": "senha-apenas-para-teste"
}
```

Resposta de sucesso: **200 OK**.

```json
{
  "token": "<jwt>"
}
```

Usuário inexistente e senha incorreta retornam **401** com a mesma mensagem: `Usuário ou senha inválidos.`

### 3. Enviar o token

Nas requisições protegidas, utilize:

```http
Authorization: Bearer <jwt>
```

Esse é o cabeçalho configurado para autenticação; não se utiliza token em query string ou em cookie. No Postman, selecione **Authorization → Bearer Token** e informe o token retornado no login.

### 4. Abrir um chamado

`POST http://localhost:8888/chamados`, com o Bearer token:

```json
{
  "titulo": "Impressora sem papel",
  "descricao": "A impressora do setor precisa de reposicao de papel.",
  "prioridade": "MEDIA"
}
```

O título deve ter de 5 a 100 caracteres; a descrição é obrigatória e limitada a 2.000 caracteres. Prioridades: `BAIXA`, `MEDIA` e `ALTA`.

O status inicial é `ABERTO`; data e proprietário são definidos pelo servidor. Não envie um ID de usuário. **Atualmente retorna 200; o contrato do backlog exige 201.**

### 5. Listar, filtrar e consultar

| Método | Rota | Objetivo |
| --- | --- | --- |
| GET | `/chamados` | Listar somente os chamados do usuário autenticado |
| GET | `/chamados?status=ABERTO` | Filtrar por status |
| GET | `/chamados?titulo=impressora` | Buscar por parte do título, sem diferenciar maiúsculas e minúsculas |
| GET | `/chamados?status=ABERTO&titulo=impressora` | Combinar os filtros |
| GET | `/chamados/{id}` | Consultar o próprio chamado |

Listagens retornam **200** e uma lista, inclusive vazia. Status não reconhecido retorna **400**. Consulta por ID inexistente ou pertencente a outro usuário retorna **404**.

### 6. Atualizar status

`PATCH http://localhost:8888/chamados/{id}/status`

```json
{
  "status": "EM_ANDAMENTO"
}
```

Valores aceitos: `ABERTO`, `EM_ANDAMENTO` e `FECHADO`, com a grafia indicada. Atualização válida retorna **200**. A transição **FECHADO → ABERTO** retorna **400**; o backlog não proíbe FECHADO → EM_ANDAMENTO, e o código mantém essa possibilidade.

### Respostas de erro

O handler global produz o formato:

```json
{
  "status": 404,
  "erro": "Not Found",
  "mensagem": "Chamado 10 não encontrado.",
  "timestamp": "2026-11-17T17:00:00Z"
}
```

São tratados dados inválidos (**400**), chamado não encontrado (**404**), username duplicado (**409**) e falhas inesperadas (**500**). A padronização ainda não abrange todos os **401**, conforme as pendências descritas anteriormente.

## Collection e validação

A [collection do Postman](./API%20Projeto%20Backlog%20Seguranca%20ADA.postman_collection.json) está disponível na raiz e contém requisições de cadastro, autenticação e gerenciamento de chamados.

1. Importe o arquivo JSON no Postman.
2. Confira a URL base e utilize `http://localhost:8888`.
3. **Na requisição de autenticação da collection, selecione POST:** o arquivo atual utiliza GET, mas o endpoint implementado aceita POST.
4. Cadastre e autentique um usuário de teste.
5. Substitua qualquer token salvo pelo token da execução atual nas requisições protegidas.

A presença da collection não comprova a execução dos cenários nem substitui testes com asserções. Antes de compartilhar uma exportação, remova tokens, senhas reais e outros dados sensíveis.

### Roteiro de aceite

- Cadastrar dois usuários distintos e conferir 201; repetir username e conferir 409.
- Autenticar e conferir 200 com JWT; usar senha incorreta e conferir 401.
- Listar chamados sem token, com token adulterado e com token expirado: conferir 401.
- Criar um chamado do usuário A; conferir a pendência atual de 200 em vez de 201.
- Listar, filtrar e consultar esse chamado com o token de A: conferir 200.
- Consultar e tentar atualizar o mesmo chamado com o token de B: conferir 404 nas duas operações.
- Enviar título curto, prioridade inválida e status inválido no filtro e no PATCH: conferir 400.
- Fechar o chamado e tentar voltar para ABERTO: conferir 400.
- Conferir que respostas não incluem senha, hash, SQL ou stack trace.

### Testes automatizados existentes

Há testes para listagem por proprietário, lista vazia, filtros opcionais/combinados, busca textual, status inválido e acesso à listagem sem token. A suíte de filtros usa H2 em memória e chave exclusiva de teste.

Para executar somente essa suíte:

```powershell
mvn "-Dtest=ChamadoFiltroIntegrationTests" test
```

Para executar todos os testes sem usar o banco em arquivo, com `JWT_KEY` disponível no ambiente:

```powershell
mvn "-Dspring.datasource.url=jdbc:h2:mem:helpdesk-tests;DB_CLOSE_DELAY=-1" "-Dspring.flyway.enabled=false" test
```

Ainda faltam testes automatizados de todos os cenários exigidos pela US16, como cadastro, login, criação e acesso cruzado por ID/PATCH. Portanto, o extra de testes automatizados está **parcialmente implementado**, não concluído.

## Decisões de segurança e mapa de ameaças

O JWT é assinado com **HS256** e inclui emissor, usuário no `sub`, emissão e expiração, sem senha no payload. O prazo configurado é **uma hora** (`PT1H`): limita a janela de uso de um token roubado sem exigir autenticação a cada operação. Não há revogação individual implementada; durante sua validade, quem possuir o token pode agir como o usuário correspondente.

A chave fica no ambiente para não acompanhar o código ou o histórico Git. Deve ser aleatória, protegida e compartilhada somente pelos componentes que precisam assinar ou validar os tokens. Trocar a chave invalida os tokens assinados com a anterior na configuração atual.

| Ameaça | Defesa implementada ou decisão / limitação |
| --- | --- |
| SQL Injection | Spring Data e JPQL com parâmetros. A pesquisa por título usa `locate` com valor parametrizado, sem concatenar entrada do usuário na consulta. |
| XSS armazenado | DTOs limitam tamanho e validam campos, mas isso não sanitiza HTML. Título e descrição são texto livre; o front-end deve escapar a saída e não inserir conteúdo recebido como HTML executável. JSON, por si só, não elimina XSS no cliente. |
| CSRF | Desabilitado porque a API é stateless e recebe Bearer token em cabeçalho definido explicitamente pelo cliente, sem autenticação por cookie. Navegadores não anexam esse cabeçalho automaticamente como fazem com cookies. A decisão deve ser revista se a autenticação passar a usar cookies. |
| DoS / força bruta | Não há rate limiting implementado. Tentativas repetidas de login e consumo excessivo de recursos continuam sendo riscos; a proteção deve ser adicionada na aplicação ou infraestrutura. |
| Token roubado | Expiração de uma hora e payload sem senha. O cliente deve proteger o token; não existe logout com revogação individual no servidor. |
| Chave JWT exposta | Configuração atual lê `JWT_KEY` do ambiente. A ausência de segredos em todo o histórico Git e nas exportações ainda precisa ser verificada. |
| Senha vazada | Persistência com BCrypt de custo 12 e respostas por DTO, sem senha/hash. Isso reduz o impacto de vazamento do banco, mas não substitui proteção de acesso ao banco e revisão dos logs. |
| Vazamento por mensagem de erro | Handler global não devolve stack trace nem SQL nos erros tratados. Ainda faltam uniformizar os 401 e registrar internamente as falhas inesperadas de forma segura. |
| Acesso a dados de outro usuário | Identidade vem do JWT; consultas por ID e alterações filtram também pelo proprietário. Retornar 404 tanto para inexistente quanto para outro usuário evita confirmar a existência de recursos alheios. |
| Enumeração de usuários | Login usa a mesma mensagem para usuário inexistente e senha incorreta. O cadastro retorna 409 para username repetido, conforme o contrato; não há garantia de tempo de resposta uniforme no login. |
| Tráfego em claro | HTTP é utilizado somente no ambiente local. Em produção, HTTPS é obrigatório para impedir interceptação de senhas e Bearer tokens; assinatura JWT não cifra o tráfego nem o payload. TLS deve ser configurado no servidor ou proxy de entrada. |
| Dependências vulneráveis | Não há evidência registrada de execução de ferramenta de análise de dependências nesta revisão. Esse extra não é declarado como entregue. |

Além de HTTPS, a publicação exige rever a configuração de desenvolvimento: console H2 público, credenciais locais do banco, exibição de SQL e atualização automática do esquema. O estado atual não deve ser interpretado como configuração pronta para produção.