# BancoApp API

Uma API simples para um sistema bancário construída inteiramente em **Java puro**, utilizando o `HttpServer` nativo (`com.sun.net.httpserver`). Este projeto gerencia clientes, contas bancárias (Corrente, Poupança e Salário) e processa transações financeiras básicas sem a necessidade de frameworks externos.

---

## Funcionalidades

O servidor roda na porta `8080` e expõe os seguintes endpoints:

### Gerenciamento de Usuários
* `POST /api/cadastrar`: Cria uma nova conta bancária para um cliente.
* `POST /api/login`: Autentica um usuário usando número da conta e senha.
* `POST /api/alterar-senha`: Atualiza a senha da conta.
* `GET /api/clientes`: Lista todos os clientes cadastrados.

### Operações Financeiras
* `GET /api/saldo`: Retorna o saldo atual da conta.
* `POST /api/saque`: Realiza um saque na conta.
* `POST /api/deposito`: Adiciona fundos à conta.
* `POST /api/pagamento`: Efetua pagamentos a partir do saldo.
* `GET /api/transacoes`: Lista o histórico de transações da conta.

---

## Tecnologias Utilizadas

* **Java (JDK 11+)**: Linguagem principal.
* **API Nativa do Java**: Parsing de JSON e requisições HTTP feitas manualmente.
* **Persistência em Arquivo**: Os dados dos clientes são salvos e carregados automaticamente através do arquivo `clientes.dat`.

---

## Como executar o projeto (Tutorial)
Certifique-se de ter o **Java Development Kit (JDK)** instalado na sua máquina.
```bash
javac -d bin src/*.java
java -cp bin Servidor
```
Saida Esperada:
```bash
╔══════════════════════════════════╗
║  Servidor BancoApp rodando       ║
║  http://localhost:8080           ║
╚══════════════════════════════════╝
Clientes carregados: 0
```
Após isso, acesse o "banco_frontend".


