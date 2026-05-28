package estruturas;

public class NoPilha {

    class No {
        Object operacao;
        No proximo;

        public No(Object operacao) {
            this.operacao = operacao;
            this.proximo = null;
        }
    }

    private No topo;

    public NoPilha() {
        this.topo = null;
    }

    public void empilhar(Object operacao) {
        No novo = new No(operacao);
        novo.proximo = topo;
        topo = novo;
    }

    public Object desempilhar() {
        if (estaVazia()) {
            return null;
        }
        Object removido = topo.operacao;
        topo = topo.proximo;
        return removido;
    }

    public Object topo() {
        if (estaVazia()) {
            return null;
        }
        return topo.operacao;
    }

    public boolean estaVazia() {
        return topo == null;
    }
}
