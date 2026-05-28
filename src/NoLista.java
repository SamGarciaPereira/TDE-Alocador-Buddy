public class NoLista {

    class No {
        Object bloco;
        No proximo;

        public No(Object bloco) {
            this.bloco = bloco;
            this.proximo = null;
        }
    }

    private No cabeca;
    private int qtd;

    public NoLista() {
        this.cabeca = null;
        this.qtd = 0;
    }

    public void inserir(Object bloco) {
        No novo = new No(bloco);
        novo.proximo = cabeca;
        cabeca = novo;
        qtd++;
    }

    public Object remover(Object bloco) {
        if (estaVazia()) {
            return null;
        }

        if (cabeca.bloco == bloco) {
            Object removido = cabeca.bloco;
            cabeca = cabeca.proximo;
            qtd--;
            return removido;
        }

        No atual = cabeca;
        while (atual.proximo != null && atual.proximo.bloco != bloco) {
            atual = atual.proximo;
        }

        if (atual.proximo != null) {
            Object removido = atual.proximo.bloco;
            atual.proximo = atual.proximo.proximo;
            qtd--;
            return removido;
        }

        return null;
    }

    public Object buscarPrimeiro() {
        if (estaVazia()) {
            return null;
        }
        return cabeca.bloco;
    }

    public boolean estaVazia() {
        return cabeca == null;
    }

    public int tamanho() {
        return qtd;
    }
}
