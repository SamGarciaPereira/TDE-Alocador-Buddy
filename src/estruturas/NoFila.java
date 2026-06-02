package estruturas;

public class NoFila {

    public class No {
        public String id;
        public int tamanho;
        No proximo;

        public No(String id, int tamanho) {
            this.id = id;
            this.tamanho = tamanho;
            this.proximo = null;
        }
    }

    private No inicio;
    private No fim;
    private int qtd;

    public NoFila() {
        this.inicio = null;
        this.fim = null;
        this.qtd = 0;
    }

    public void enfileirar(String id, int tamanho) {
        No novo = new No(id, tamanho);
        if (estaVazia()) {
            inicio = novo;
        } else {
            fim.proximo = novo;
        }
        fim = novo;
        qtd++;
    }

    public No desenfileirar() {
        if (estaVazia()) {
            return null;
        }
        No removido = inicio;
        inicio = inicio.proximo;
        if (inicio == null) {
            fim = null;
        }
        qtd--;
        return removido;
    }

    public No espiar() {
        return inicio;
    }

    public boolean estaVazia() {
        return qtd == 0;
    }

    public int tamanho() {
        return qtd;
    }

    public void imprimirFila() {

        No atual = inicio;

        System.out.println("\n===== FILA =====");

        if (atual == null) {
            System.out.println("Fila vazia");
        }

        while (atual != null) {

            System.out.println(
                    atual.id +
                            " - " +
                            atual.tamanho +
                            "KB"
            );

            atual = atual.proximo;
        }

        System.out.println("================\n");
    }
}
