import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servidor HTTP para o sistema bancário.
 * Porta padrão: 8080
 *
 * Compilar (junto com todas as outras classes):
 * javac *.java
 *
 * Executar:
 * java Servidor
 */
public class Servidor {

    static ArrayList<Pessoas> pessoas;
    static final String DATA_FILE = "clientes.dat";

    // ─────────────────────────────────────────────
    //  MAIN
    // ─────────────────────────────────────────────
    public static void main(String[] args) throws Exception {
        pessoas = Persistencia.carregarDados(DATA_FILE);
        if (pessoas == null) pessoas = new ArrayList<>();

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/api/login",      Servidor::handleLogin);
        server.createContext("/api/cadastrar",   Servidor::handleCadastrar);
        server.createContext("/api/saldo",       Servidor::handleSaldo);
        server.createContext("/api/saque",       Servidor::handleSaque);
        server.createContext("/api/deposito",    Servidor::handleDeposito);
        server.createContext("/api/pagamento",   Servidor::handlePagamento);
        server.createContext("/api/transacoes",  Servidor::handleTransacoes);
        server.createContext("/api/clientes",    Servidor::handleClientes);
        server.createContext("/api/alterar-senha", Servidor::handleAlterarSenha);

        server.setExecutor(null);
        server.start();

        System.out.println("╔══════════════════════════════════╗");
        System.out.println("║  Servidor BancoApp rodando       ║");
        System.out.println("║  http://localhost:8080           ║");
        System.out.println("╚══════════════════════════════════╝");
        System.out.println("Clientes carregados: " + pessoas.size());

        // Salva ao encerrar (Ctrl+C)
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Persistencia.salvarDados(DATA_FILE, pessoas);
            System.out.println("\nDados salvos. Servidor encerrado.");
        }));
    }

    // ─────────────────────────────────────────────
    //  HANDLERS
    // ─────────────────────────────────────────────

    /** POST /api/login  { "conta": 1001, "senha": "abc" } */
    static void handleLogin(HttpExchange ex) throws IOException {
        if (preflight(ex)) return;
        if (!ex.getRequestMethod().equals("POST")) { respond(ex, 405, erro("Método não permitido")); return; }

        Map<String, String> body = parseJson(lerBody(ex));
        int numConta = intVal(body, "conta");
        String senha = body.getOrDefault("senha", "");

        Pessoas p = buscar(numConta);
        if (p == null) { respond(ex, 404, erro("Conta não encontrada")); return; }
        if (!p.getSenha().equals(senha)) { respond(ex, 401, erro("Senha incorreta")); return; }

        respond(ex, 200, pessoaJson(p));
    }

    /** POST /api/cadastrar  { nome, cpf, nascimento, escolaridade, estadoCivil,
                               tipoConta, numConta, saldo, senha,
                               limiteCheque | limiteSaque | limiteTransf | rendimento } */
    static void handleCadastrar(HttpExchange ex) throws IOException {
        if (preflight(ex)) return;
        if (!ex.getRequestMethod().equals("POST")) { respond(ex, 405, erro("Método não permitido")); return; }

        Map<String, String> b = parseJson(lerBody(ex));
        String cpf        = b.getOrDefault("cpf", "");
        String nome       = b.getOrDefault("nome", "");
        String nascimento = b.getOrDefault("nascimento", "");
        String esc        = b.getOrDefault("escolaridade", "");
        String ec         = b.getOrDefault("estadoCivil", "");
        String senha      = b.getOrDefault("senha", "");
        int    numConta   = intVal(b, "numConta");
        double saldo      = dblVal(b, "saldo");
        String tipoConta  = b.getOrDefault("tipoConta", "corrente");

        // Validações básicas
        if (nome.isEmpty() || cpf.isEmpty() || senha.isEmpty()) {
            respond(ex, 400, erro("Campos obrigatórios faltando")); return;
        }
        if (buscar(numConta) != null) {
            respond(ex, 409, erro("Número de conta já cadastrado")); return;
        }

        ContaBancaria conta;
        try {
            switch (tipoConta) {
                case "corrente":
                    double lim = dblVal(b, "limiteCheque");
                    conta = new Corrente(lim, 10, numConta, saldo, LocalDate.now().toString(), null, senha, true);
                    break;
                case "salario":
                    double ls = dblVal(b, "limiteSaque");
                    double lt = dblVal(b, "limiteTransf");
                    conta = new Salario(ls, lt, numConta, saldo, LocalDate.now().toString(), null, senha, true);
                    break;
                case "poupanca":
                    double rend = dblVal(b, "rendimento");
                    conta = new Poupança(rend, numConta, saldo, LocalDate.now().toString(), null, senha, true);
                    break;
                default:
                    respond(ex, 400, erro("Tipo de conta inválido")); return;
            }

            Endereço end = new Endereço("MG", "Uberlândia", "Centro");
            Agencia ag  = new Agencia(1001, "Tiradentes", end);
            Pessoas novo = new Cliente(cpf, nome, nascimento, end, esc, ag, ec, conta);
            pessoas.add(novo);
            Persistencia.salvarDados(DATA_FILE, pessoas);
            respond(ex, 201, pessoaJson(novo));

        } catch (CPFInvalido e) {
            respond(ex, 400, erro("CPF inválido: " + e.getMessage()));
        } catch (Exception e) {
            respond(ex, 500, erro("Erro interno: " + e.getMessage()));
        }
    }

    /** GET /api/saldo?conta=1001&senha=abc */
    static void handleSaldo(HttpExchange ex) throws IOException {
        if (preflight(ex)) return;
        Map<String, String> q = queryParams(ex);
        int numConta = intVal(q, "conta");
        String senha = q.getOrDefault("senha", "");

        Pessoas p = buscar(numConta);
        if (p == null) { respond(ex, 404, erro("Conta não encontrada")); return; }
        if (!p.getSenha().equals(senha)) { respond(ex, 401, erro("Senha incorreta")); return; }

        new ConsultaSaldo(p.getConta(), LocalDate.now(), "Web").executar();
        respond(ex, 200, "{\"saldo\":" + p.getConta().getSaldo() + "}");
    }

    /** POST /api/saque  { "conta": 1001, "senha": "abc", "valor": 200.0 } */
    static void handleSaque(HttpExchange ex) throws IOException {
        if (preflight(ex)) return;
        if (!ex.getRequestMethod().equals("POST")) { respond(ex, 405, erro("Método não permitido")); return; }

        Map<String, String> b = parseJson(lerBody(ex));
        Pessoas p = autenticar(ex, b); if (p == null) return;
        double valor = dblVal(b, "valor");

        if (valor <= 0) { respond(ex, 400, erro("Valor deve ser maior que zero")); return; }
        try {
            new Saque(p.getConta(), LocalDate.now(), valor, "Web").executar();
            Persistencia.salvarDados(DATA_FILE, pessoas);
            respond(ex, 200, "{\"saldo\":" + p.getConta().getSaldo() + "}");
        } catch (SaldoInsuficienteException e) {
            respond(ex, 422, erro(e.getMessage()));
        }
    }

    /** POST /api/deposito  { "conta": 1001, "senha": "abc", "valor": 500.0 } */
    static void handleDeposito(HttpExchange ex) throws IOException {
        if (preflight(ex)) return;
        if (!ex.getRequestMethod().equals("POST")) { respond(ex, 405, erro("Método não permitido")); return; }

        Map<String, String> b = parseJson(lerBody(ex));
        Pessoas p = autenticar(ex, b); if (p == null) return;
        double valor = dblVal(b, "valor");

        if (valor <= 0) { respond(ex, 400, erro("Valor deve ser maior que zero")); return; }
        try {
            new Deposito(p.getConta(), LocalDate.now(), valor, "Web").executar();
            Persistencia.salvarDados(DATA_FILE, pessoas);
            respond(ex, 200, "{\"saldo\":" + p.getConta().getSaldo() + "}");
        } catch (Exception e) {
            respond(ex, 422, erro(e.getMessage()));
        }
    }

    /** POST /api/pagamento  { "conta": 1001, "senha": "abc", "valor": 150.0 } */
    static void handlePagamento(HttpExchange ex) throws IOException {
        if (preflight(ex)) return;
        if (!ex.getRequestMethod().equals("POST")) { respond(ex, 405, erro("Método não permitido")); return; }

        Map<String, String> b = parseJson(lerBody(ex));
        Pessoas p = autenticar(ex, b); if (p == null) return;
        double valor = dblVal(b, "valor");

        if (valor <= 0) { respond(ex, 400, erro("Valor deve ser maior que zero")); return; }
        try {
            new Pagamento(p.getConta(), LocalDate.now(), valor, "Web").executar();
            Persistencia.salvarDados(DATA_FILE, pessoas);
            respond(ex, 200, "{\"saldo\":" + p.getConta().getSaldo() + "}");
        } catch (Exception e) {
            respond(ex, 422, erro(e.getMessage()));
        }
    }

    /** GET /api/transacoes?conta=1001&senha=abc */
    static void handleTransacoes(HttpExchange ex) throws IOException {
        if (preflight(ex)) return;
        Map<String, String> q = queryParams(ex);
        int numConta = intVal(q, "conta");
        String senha = q.getOrDefault("senha", "");

        Pessoas p = buscar(numConta);
        if (p == null) { respond(ex, 404, erro("Conta não encontrada")); return; }
        if (!p.getSenha().equals(senha)) { respond(ex, 401, erro("Senha incorreta")); return; }

        List<Transacao> hist = Transacao.getHistoricoTransacoes().stream()
                .filter(t -> t.getConta().getNumConta() == numConta)
                .collect(Collectors.toList());

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < hist.size(); i++) {
            Transacao t = hist.get(i);
            if (i > 0) sb.append(",");
            sb.append("{")
              .append("\"data\":\"").append(t.getData()).append("\",")
              .append("\"tipo\":\"").append(t.getClass().getSimpleName()).append("\",")
              .append("\"valor\":").append(t.getValor()).append(",")
              .append("\"canal\":\"").append(t.getCanal()).append("\"")
              .append("}");
        }
        sb.append("]");
        respond(ex, 200, sb.toString());
    }

    /** GET /api/clientes  (lista pública: nome + numConta) */
    static void handleClientes(HttpExchange ex) throws IOException {
        if (preflight(ex)) return;
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < pessoas.size(); i++) {
            Pessoas p = pessoas.get(i);
            if (i > 0) sb.append(",");
            sb.append("{")
              .append("\"nome\":\"").append(esc(p.getNome())).append("\",")
              .append("\"conta\":").append(p.getNumConta()).append(",")
              .append("\"tipo\":\"").append(p.getConta().getClass().getSimpleName()).append("\"")
              .append("}");
        }
        sb.append("]");
        respond(ex, 200, sb.toString());
    }

    /** POST /api/alterar-senha  { "conta": 1001, "senha": "antiga", "novaSenha": "nova" } */
    static void handleAlterarSenha(HttpExchange ex) throws IOException {
        if (preflight(ex)) return;
        if (!ex.getRequestMethod().equals("POST")) { respond(ex, 405, erro("Método não permitido")); return; }

        Map<String, String> b = parseJson(lerBody(ex));
        Pessoas p = autenticar(ex, b); if (p == null) return;
        String nova = b.getOrDefault("novaSenha", "");
        if (nova.length() < 4) { respond(ex, 400, erro("Senha muito curta (mín. 4 caracteres)")); return; }

        p.getConta().setSenha(nova);
        Persistencia.salvarDados(DATA_FILE, pessoas);
        respond(ex, 200, "{\"ok\":true}");
    }

    // ─────────────────────────────────────────────
    //  HELPERS
    // ─────────────────────────────────────────────

    static Pessoas buscar(int numConta) {
        return pessoas.stream().filter(p -> p.getNumConta() == numConta).findFirst().orElse(null);
    }

    /** Autentica conta+senha do body; responde automaticamente se falhar. */
    static Pessoas autenticar(HttpExchange ex, Map<String, String> b) throws IOException {
        int numConta = intVal(b, "conta");
        String senha = b.getOrDefault("senha", "");
        Pessoas p = buscar(numConta);
        if (p == null)                    { respond(ex, 404, erro("Conta não encontrada")); return null; }
        if (!p.getSenha().equals(senha))  { respond(ex, 401, erro("Senha incorreta"));      return null; }
        return p;
    }

    static String pessoaJson(Pessoas p) {
        ContaBancaria c = p.getConta();
        String tipo = c.getClass().getSimpleName();
        String extra = "";
        if (c instanceof Corrente) extra = ",\"limiteCheque\":" + ((Corrente)c).getLimiteCheque();
        if (c instanceof Poupança) extra = ",\"rendimento\":"   + ((Poupança)c).getRendimento();
        if (c instanceof Salario)  extra = ",\"limiteSaque\":"  + ((Salario)c).getLimiteSaque();

        return "{" +
            "\"nome\":\""    + esc(p.getNome())       + "\"," +
            "\"cpf\":\""     + esc(p.getCPF())        + "\"," +
            "\"conta\":"     + c.getNumConta()         + "," +
            "\"saldo\":"     + c.getSaldo()            + "," +
            "\"tipo\":\""    + tipo                    + "\"," +
            "\"abertura\":\"" + esc(c.getDataAbertura()) + "\"," +
            "\"status\":"    + c.getStatus()           +
            extra + "}";
    }

    static String erro(String msg) { return "{\"erro\":\"" + esc(msg) + "\"}"; }

    static String esc(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    static void respond(HttpExchange ex, int code, String json) throws IOException {
        Headers h = ex.getResponseHeaders();
        h.set("Content-Type", "application/json; charset=UTF-8");
        h.set("Access-Control-Allow-Origin", "*");
        h.set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        h.set("Access-Control-Allow-Headers", "Content-Type");
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        ex.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = ex.getResponseBody()) { os.write(bytes); }
    }

    /** Trata preflight CORS (OPTIONS) */
    static boolean preflight(HttpExchange ex) throws IOException {
        if (ex.getRequestMethod().equals("OPTIONS")) {
            respond(ex, 204, "");
            return true;
        }
        return false;
    }

    static String lerBody(HttpExchange ex) throws IOException {
        try (InputStream is = ex.getRequestBody()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    /** Parser JSON mínimo: só lida com objetos planos {"k":"v","k2":123} */
    static Map<String, String> parseJson(String json) {
        Map<String, String> m = new HashMap<>();
        if (json == null || json.isBlank()) return m;
        json = json.trim();
        if (json.startsWith("{")) json = json.substring(1);
        if (json.endsWith("}"))   json = json.substring(0, json.length() - 1);

        // Tokeniza par a par
        int i = 0;
        while (i < json.length()) {
            // Pula espaços e vírgulas
            while (i < json.length() && (json.charAt(i) == ',' || Character.isWhitespace(json.charAt(i)))) i++;
            if (i >= json.length()) break;

            // Lê chave
            String key = readToken(json, i);
            i += tokenLen(json, i);

            // Pula ':'
            while (i < json.length() && (json.charAt(i) == ':' || Character.isWhitespace(json.charAt(i)))) i++;

            // Lê valor
            String val = readToken(json, i);
            i += tokenLen(json, i);

            m.put(key, val);
        }
        return m;
    }

    static String readToken(String s, int start) {
        if (start >= s.length()) return "";
        char c = s.charAt(start);
        if (c == '"') {
            int end = start + 1;
            while (end < s.length() && s.charAt(end) != '"') {
                if (s.charAt(end) == '\\') end++;
                end++;
            }
            return s.substring(start + 1, end);
        }
        int end = start;
        while (end < s.length() && s.charAt(end) != ',' && s.charAt(end) != '}' && !Character.isWhitespace(s.charAt(end))) end++;
        return s.substring(start, end);
    }

    static int tokenLen(String s, int start) {
        if (start >= s.length()) return 0;
        char c = s.charAt(start);
        if (c == '"') {
            int end = start + 1;
            while (end < s.length() && s.charAt(end) != '"') {
                if (s.charAt(end) == '\\') end++;
                end++;
            }
            return end - start + 1;
        }
        int end = start;
        while (end < s.length() && s.charAt(end) != ',' && s.charAt(end) != '}' && !Character.isWhitespace(s.charAt(end))) end++;
        return end - start;
    }

    static Map<String, String> queryParams(HttpExchange ex) {
        Map<String, String> m = new HashMap<>();
        String query = ex.getRequestURI().getQuery();
        if (query == null) return m;
        for (String pair : query.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                try { m.put(URLDecoder.decode(kv[0], "UTF-8"), URLDecoder.decode(kv[1], "UTF-8")); }
                catch (Exception ignored) {}
            }
        }
        return m;
    }

    static int intVal(Map<String, String> m, String k) {
        try { return Integer.parseInt(m.getOrDefault(k, "0").trim()); } catch (Exception e) { return 0; }
    }
    static double dblVal(Map<String, String> m, String k) {
        try { return Double.parseDouble(m.getOrDefault(k, "0").trim()); } catch (Exception e) { return 0; }
    }
}