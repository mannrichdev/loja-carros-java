package lojacarrosweb;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class HistoricoService {

    /*
     ============================================================
     IMPLEMENTAÇÃO UTILIZANDO PILHA (LIFO)
     ============================================================

     LIFO = Last In, First Out
     Último que entra, primeiro que sai.

     Estrutura utilizada:
     Deque<String>
    */

    // Pilha responsável pelo histórico de páginas.
    private static Deque<String> historico = new ArrayDeque<>();

    // Registra uma nova página visitada.
    public static void registrarPagina(String pagina) {

        // Verifica se já existe uma página no topo da pilha.
        if (!historico.isEmpty()) {

            // Consulta a página atual.
            String paginaAtual = historico.peek();

            // Evita páginas repetidas consecutivas.
            if (paginaAtual.equals(pagina)) {
                return;
            }
        }

        // Adiciona a página no topo da pilha.
        historico.push(pagina);
    }

    // Retorna o histórico completo.
    public static List<String> listarHistorico() {

        // Converte a pilha em lista para exibição.
        return new ArrayList<>(historico);
    }

    // Remove a última página visitada.
    public static String voltarPagina() {

        // Verifica se a pilha está vazia.
        if (historico.isEmpty()) {

            return null;
        }

        // Remove e retorna o topo da pilha.
        return historico.pop();
    }

    // Consulta a página atual sem remover.
    public static String paginaAtual() {

        // Verifica se existe página no histórico.
        if (historico.isEmpty()) {

            return null;
        }

        // Retorna a página atual.
        return historico.peek();
    }
}