# 📘 HardVision — Aplicação Java

Monitoramento e envio de alertas via Slack

## 📌 Pré-requisitos

* **Java 17+**
* **IntelliJ IDEA** (Community ou Ultimate)
* **MySQL** (local ou remoto)

---

# 📂 1. Clonar o repositório

```sh
git clone https://github.com/HardVision/java-appclient.git
cd java-appclient
```

---

# 📦 2. Instalar dependências

O projeto usa **Maven**, então o IntelliJ baixa tudo automaticamente.

Caso queira forçar manualmente:

```sh
mvn clean install
```

---

# 🔐 3. Configurar variáveis de ambiente

A aplicação **não usa arquivo `.env`** — tudo deve ser configurado direto nas variáveis do sistema ou no IntelliJ.

Use o seguinte formato:

```
DB_PASS=sua senha
DB_URL=jdbc:mysql://127.0.0.1:3306/hardvision?autoReconnect=true&useSSL=false&allowPublicKeyRetrieval=true
DB_USER=seu user
SLACK_BOT=xoxb-xxxxx...
```

> **⚠️ Não inclua aspas nos valores.**

---

# 🧩 4. Configurar variáveis no IntelliJ IDEA

1. Abra o IntelliJ
2. Vá em **Run → Edit Configurations...**
3. Clique na sua aplicação (ou crie uma nova Application)
4. Vá até o campo **Environment Variables**
5. Cole as variáveis

6. Clique em **OK**

Pronto, o Java poderá ler todas com:

```java
System.getenv("DB_PASS");
```

---

# 🗃️ 5. Verificar o banco MySQL

A aplicação espera o banco `hardvision` existir.

Exemplo para testar a conexão:

```sh
mysql -u root -p -h 127.0.0.1 -P 3306
```

---

# ▶️ 6. Rodar a aplicação

No IntelliJ:

* Clique em **Run > Run 'Main'**
* Ou use **Shift + F10**

O programa iniciará e exibirá os logs no console.

---

# 💬 7. Envio de mensagens no Slack

O token é lido da variável:

```
SLACK_BOT
```

Se a mensagem não aparecer no Slack:

* Verifique se o bot está no canal (`/invite @SeuBot`)
* Verifique se o token é do tipo **xoxb-** (bot token)
* Certifique-se de que o **Scope** tem `chat:write`

---

# 🛠️ 8. Erros comuns

| Erro                          | Causa provável                     |
| ----------------------------- | ---------------------------------- |
| `channel_not_found`           | Usou nome ao invés do ID           |
| `invalid_auth`                | Token errado ou ausente            |
| `Communications link failure` | MySQL fora do ar                   |
| Variáveis vazias              | Esqueceu de configurar no IntelliJ |

---

# ✔️ 9. Estrutura do projeto (exemplo)

```
src/
  main/java/
    com/hardvision/
      Main.java
      database/
      slack/
      monitoramento/
pom.xml
README.md
```

---

