package buddy;

public class Alocador {
    private NoArvore raiz;

    public Alocador(int tamanhoTotalMemoria){
        //inicializa a arvore com bloco inteiro (32768 KB)
        this.raiz = new NoArvore(tamanhoTotalMemoria, null);
    }

    public boolean alocar(String idProcesso, int tamanhoSolicitado){
        int tamanhoNecessario = calcularProximaPotencia(tamanhoSolicitado);
        //inicia a busca recursiva a partir do topo da arvore
        return realizarSplit(raiz, tamanhoNecessario, idProcesso);
    }

    //split
    public boolean realizarSplit(NoArvore noAtual, int tamanhoNecessario, String idProcesso){
        //falha se o nó não existe ou já está ocupado por outro processo
        if(noAtual == null || noAtual.getEstado() == NoArvore.Estado.OCUPADO){
            return false;
        }

        //sucesso (caso base) se achou um bloco livre do tamanho exato
        if (noAtual.getEstado() == NoArvore.Estado.LIVRE && noAtual.getTamanho() == tamanhoNecessario) {
            noAtual.setEstado(NoArvore.Estado.OCUPADO);
            noAtual.setId(idProcesso);
            return true;
        }

        //falha se o nó atual é mt pequeno
        if(noAtual.getTamanho() < tamanhoNecessario){
            return false;
        }

        //se nó é maior que o necessário, dividimos
        if(noAtual.getEstado() == NoArvore.Estado.LIVRE){
            //previne divisão abaixo do limite mínimo (4 KB)
            if(noAtual.getTamanho() <= 4){
                return false;
            }

            noAtual.setEstado(NoArvore.Estado.DIVIDIDO);
            int metade = noAtual.getTamanho() / 2;

            //cria os filhos esquerdos e direitos, passando o nó atual como pai
            noAtual.setFilhoEsquerdo(new NoArvore(metade, noAtual));
            noAtual.setFilhoDireito(new NoArvore(metade, noAtual));
        }

        //recursividade: tenta alocar no galho da esquerda primeiro
        if(realizarSplit(noAtual.getFilhoDireito(), tamanhoNecessario, idProcesso)){
            return true;
        }

        //se a esquerda falhar, tenta no galho da direita
        return realizarSplit(noAtual.getFilhoDireito(), tamanhoNecessario, idProcesso);
    }

    private int calcularProximaPotencia(int tamanho){
        int potencia = 4;
        while (potencia < tamanho){
            potencia = potencia * 2;
        }
        return potencia;
    }

    public boolean liberar(String idProcesso){
        //busca o nó da árvore
        NoArvore noParaLiberar = buscarNoPorId(raiz, idProcesso);

        if(noParaLiberar == null){
            return false;
        }

        //libera o bloco
        noParaLiberar.setEstado(NoArvore.Estado.LIVRE);
        noParaLiberar.setId(null);

        //inicia a verificação de fusão subindo a arvore
        realizarMerge(noParaLiberar);

        return true;
    }

    //recursão que "sobe" a árvore
    public void realizarMerge(NoArvore noAtual){
        //se chegar na raiz, para
        if(noAtual.getPai() == null){
            return;
        }

        NoArvore pai = noAtual.getPai();
        NoArvore esquerdo = pai.getFilhoEsquerdo();
        NoArvore direito = pai.getFilhoDireito();

        //verifica se ambos os irmãos existem e ambos estão livres
        if (esquerdo != null && direito != null &&
                esquerdo.getEstado() == NoArvore.Estado.LIVRE &&
                direito.getEstado() == NoArvore.Estado.LIVRE) {
            //faz merge nos blocos, o pai volta a ser um bloco inteiro livre
            pai.setEstado(NoArvore.Estado.LIVRE);

            //"mata" os filhos apontando pra null
            pai.setFilhoEsquerdo(null);
            pai.setFilhoDireito(null);

            //recursão: passa o pai para verificar se ele pode "mergear" com o irmão dele no nível de cima
            realizarMerge(pai);
        }
    }


    private NoArvore buscarNoPorId(NoArvore noAtual, String idBuscado){
        if (noAtual == null){
            return null;
        }

        //encontrou
        if(idBuscado.equals(noAtual.getId())){
            return noAtual;
        }

        //procura na esquerda
        NoArvore encontradoEsquerda = buscarNoPorId(noAtual.getFilhoEsquerdo(), idBuscado);
        if (encontradoEsquerda != null) return encontradoEsquerda;

        //se não achou na esquerda, procura na direita
        return buscarNoPorId(noAtual.getFilhoDireito(), idBuscado);
    }
}
