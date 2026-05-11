package lojacarrosweb;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import java.net.InetSocketAddress;
import java.io.OutputStream;
import java.util.List;

public class LojaCarrosWeb {

    private static HttpServer servidor;

    public static void main(String[] args) throws Exception {

        servidor = HttpServer.create(new InetSocketAddress(9091), 0);

        servidor.createContext("/", exchange -> {
            try {
                paginaInicial(exchange);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        servidor.createContext("/carro", exchange -> {
            try {
                paginaCarro(exchange);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        servidor.createContext("/historico", exchange -> {
            try {
                paginaHistorico(exchange);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        servidor.createContext("/voltar", exchange -> {
            try {
                voltarPagina(exchange);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        servidor.createContext("/fechar", exchange -> {
            try {
                fecharSistema(exchange);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        servidor.start();

        System.out.println("Loja de carros rodando em: http://localhost:9091");
    }

    public static void paginaInicial(HttpExchange exchange) throws Exception {

        HistoricoService.registrarPagina("Página Inicial - Loja de Carros");

        List<Carro> carros = CarroService.listarCarros();

        String html = "";

        html += HtmlUtilCarros.inicioHtml("Loja de Carros Java");

        html += "<div class='card'>";
        html += "<h1>Loja de Carros Java</h1>";
        html += "<p>Escolha um veículo para visualizar os detalhes.</p>";

        html += "<div class='menu-topo'>";
        html += "<a class='botao secundario' href='/historico'>Ver Histórico</a>";
        html += "<a class='botao alerta' href='/voltar'>Voltar Página</a>";
        html += "<a class='botao perigo' href='/fechar'>Fechar</a>";
        html += "</div>";

        html += "<div class='grid'>";

        for (Carro carro : carros) {

            html += "<div class='carro-card'>";

            html += "<div class='figura'>" + carro.getFigura() + "</div>";

            html += "<h2>" + carro.getNome() + "</h2>";

            html += "<p><strong>Ano:</strong> " + carro.getAno() + "</p>";

            html += "<p class='preco'>R$ " + String.format("%.2f", carro.getPreco()) + "</p>";

            html += "<a class='botao' href='/carro?id=" + carro.getId() + "'>Ver Detalhes</a>";

            html += "</div>";
        }

        html += "</div>";
        html += "</div>";

        html += HtmlUtilCarros.fimHtml();

        enviarResposta(exchange, html);
    }

    public static void paginaCarro(HttpExchange exchange) throws Exception {

        String query = exchange.getRequestURI().getQuery();

        int id = 0;

        if (query != null && query.startsWith("id=")) {

            id = Integer.parseInt(query.replace("id=", ""));
        }

        Carro carro = CarroService.buscarPorId(id);

        if (carro == null) {

            String html = "";

            html += HtmlUtilCarros.inicioHtml("Carro não encontrado");
            html += "<div class='card central'>";
            html += "<h1>Carro não encontrado</h1>";
            html += "<p>O veículo solicitado não existe.</p>";
            html += "<a class='botao' href='/'>Voltar</a>";
            html += "</div>";
            html += HtmlUtilCarros.fimHtml();

            enviarResposta(exchange, html);
            return;
        }

        HistoricoService.registrarPagina("Carro visitado: " + carro.getNome());

        String html = "";

        html += HtmlUtilCarros.inicioHtml(carro.getNome());

        html += "<div class='card central'>";
        html += "<div class='figura grande'>" + carro.getFigura() + "</div>";
        html += "<h1>" + carro.getNome() + "</h1>";
        html += "<p><strong>Ano:</strong> " + carro.getAno() + "</p>";
        html += "<p><strong>Preço:</strong> R$ " + String.format("%.2f", carro.getPreco()) + "</p>";
        html += "<p class='descricao'>" + carro.getDescricao() + "</p>";

        html += "<div class='menu-topo'>";
        html += "<a class='botao' href='/'>Voltar para Loja</a>";
        html += "<a class='botao secundario' href='/historico'>Ver Histórico</a>";
        html += "<a class='botao alerta' href='/voltar'>Voltar Página</a>";
        html += "</div>";

        html += "</div>";

        html += HtmlUtilCarros.fimHtml();

        enviarResposta(exchange, html);
    }

    public static void paginaHistorico(HttpExchange exchange) throws Exception {

        List<String> historico = HistoricoService.listarHistorico();

        String html = "";

        html += HtmlUtilCarros.inicioHtml("Histórico de Páginas");

        html += "<div class='card'>";
        html += "<h1>Histórico de Páginas Visitadas</h1>";

        html += "<p class='aviso'>";
        html += "O histórico abaixo funciona utilizando PILHA (LIFO).";
        html += "</p>";

        if (historico.isEmpty()) {

            html += "<p>Nenhuma página foi registrada no histórico.</p>";

        } else {

            html += "<ol class='historico'>";

            for (String pagina : historico) {

                html += "<li>" + pagina + "</li>";
            }

            html += "</ol>";
        }

        html += "<div class='menu-topo'>";
        html += "<a class='botao' href='/'>Voltar para Loja</a>";
        html += "<a class='botao alerta' href='/voltar'>Voltar Página</a>";
        html += "</div>";

        html += "</div>";

        html += HtmlUtilCarros.fimHtml();

        enviarResposta(exchange, html);
    }

    public static void voltarPagina(HttpExchange exchange) throws Exception {

        String paginaRemovida = HistoricoService.voltarPagina();

        String html = "";

        html += HtmlUtilCarros.inicioHtml("Voltar Página");

        html += "<div class='card central'>";
        html += "<h1>Simulação do Botão Voltar</h1>";

        if (paginaRemovida == null) {

            html += "<p>Não há páginas suficientes no histórico para voltar.</p>";

        } else {

            html += "<p>A seguinte página foi removida do topo da pilha:</p>";
            html += "<h2>" + paginaRemovida + "</h2>";
        }

        html += "<div class='menu-topo'>";
        html += "<a class='botao' href='/'>Voltar para Loja</a>";
        html += "<a class='botao secundario' href='/historico'>Ver Histórico</a>";
        html += "</div>";

        html += "</div>";

        html += HtmlUtilCarros.fimHtml();

        enviarResposta(exchange, html);
    }

    public static void fecharSistema(HttpExchange exchange) throws Exception {

        String html = "";

        html += HtmlUtilCarros.inicioHtml("Sistema Encerrado");
        html += "<div class='card central'>";
        html += "<h1>Sistema encerrado</h1>";
        html += "<p>Você pode fechar esta aba do navegador.</p>";
        html += "</div>";
        html += HtmlUtilCarros.fimHtml();

        enviarResposta(exchange, html);

        servidor.stop(1);
    }

    public static void enviarResposta(HttpExchange exchange, String resposta) throws Exception {

        byte[] bytes = resposta.getBytes("UTF-8");

        exchange.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");

        exchange.sendResponseHeaders(200, bytes.length);

        OutputStream os = exchange.getResponseBody();

        os.write(bytes);

        os.close();
    }
}