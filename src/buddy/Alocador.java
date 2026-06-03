package buddy;

import estruturas.NoFila;
import estruturas.NoLista;
import estruturas.NoPilha;

public class
Alocador {
    private NoArvore raiz;

    private NoFila filaPendentes;
    private NoPilha pilhaHistorico;
    private NoLista[] listasLivres;

    public NoArvore getRaiz() {
        return this.raiz;
    }

    public Alocador(int tamanhoTotalMemoria){
        // inicializa a arvore com bloco inteiro (32768 KB)
        this.raiz = new NoArvore(tamanhoTotalMemoria, null);

        // inicializa estruturas
        this.filaPendentes = new NoFila();
        this.pilhaHistorico = new NoPilha();
        this.listasLivres = new NoLista[14];
        for (int i = 0; i < 14; i++) {
            this.listasLivres[i] = new NoLista();
        }

        // insere o bloco inicial inteiro na lista correta
        this.listasLivres[calcularIndiceLista(tamanhoTotalMemoria)].inserir(this.raiz);
    }

    public boolean alocar(String idProcesso, int tamanhoSolicitado){
        int tamanhoNecessario = calcularProximaPotencia(tamanhoSolicitado);

        // captura o resultado da recursão
        boolean sucesso = realizarSplit(raiz, tamanhoNecessario, idProcesso);

        if (!sucesso) {
            // insere na fila de pendentes
            filaPendentes.enfileirar(idProcesso, tamanhoSolicitado);
        } else {
            // empilha historico de alocacao
            pilhaHistorico.empilhar("ALOCAR:" + idProcesso);
        }

        return sucesso;
    }

    // split
    public boolean realizarSplit(NoArvore noAtual, int tamanhoNecessario, String idProcesso){
        // falha se o nó não existe ou já está ocupado por outro processo
        if(noAtual == null || noAtual.getEstado() == NoArvore.Estado.OCUPADO){
            return false;
        }

        // sucesso (caso base) se achou um bloco livre do tamanho exato
        if (noAtual.getEstado() == NoArvore.Estado.LIVRE && noAtual.getTamanho() == tamanhoNecessario) {
            noAtual.setEstado(NoArvore.Estado.OCUPADO);
            noAtual.setId(idProcesso);
            return true;
        }

        // falha se o nó atual é mt pequeno
        if(noAtual.getTamanho() < tamanhoNecessario){
            return false;
        }

        // se nó é maior que o necessário, dividimos
        if(noAtual.getEstado() == NoArvore.Estado.LIVRE){
            // previne divisão abaixo do limite mínimo (4 KB)
            if(noAtual.getTamanho() <= 4){
                return false;
            }

            noAtual.setEstado(NoArvore.Estado.DIVIDIDO);

            // remove no atual da lista de livres
            listasLivres[calcularIndiceLista(noAtual.getTamanho())].remover(noAtual);

            int metade = noAtual.getTamanho() / 2;

            // cria os filhos esquerdos e direitos, passando o nó atual como pai
            noAtual.setFilhoEsquerdo(new NoArvore(metade, noAtual));
            noAtual.setFilhoDireito(new NoArvore(metade, noAtual));

            // adiciona filhos na lista de livres menor
            int indiceMetade = calcularIndiceLista(metade);
            listasLivres[indiceMetade].inserir(noAtual.getFilhoEsquerdo());
            listasLivres[indiceMetade].inserir(noAtual.getFilhoDireito());
        }

        // recursividade: tenta alocar no galho da esquerda primeiro
        if(realizarSplit(noAtual.getFilhoEsquerdo(), tamanhoNecessario, idProcesso)){
            return true;
        }

        // se a esquerda falhar, tenta no galho da direita
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
        // busca o nó da árvore
        NoArvore noParaLiberar = buscarNoPorId(raiz, idProcesso);

        if(noParaLiberar == null){
            return false;
        }

        //salva tamanho antes de liberar
        int tamanhoBloco = noParaLiberar.getTamanho();

        // libera o bloco
        noParaLiberar.setEstado(NoArvore.Estado.LIVRE);
        noParaLiberar.setId(null);

        // empilha historico de liberacao
        pilhaHistorico.empilhar("LIBERAR:" + idProcesso + ":" + tamanhoBloco);

        // inicia a verificação de fusão subindo a arvore
        realizarMerge(noParaLiberar);

        // tenta alocar pendentes da fila (FIFO)
        int qtdNaFila = filaPendentes.tamanho();
        for (int i = 0; i < qtdNaFila; i++) {
            // nota para Abilio: a classe No dentro de NoFila precisa ser public para isso funcionar
            estruturas.NoFila.No req = filaPendentes.desenfileirar();
            if (req != null) {
                // o proprio metodo alocar se encarrega de reenfileirar no final se falhar novamente
                alocar(req.id, req.tamanho);
            }
        }

        return true;
    }


    // merge: recursão que "sobe" a árvore
    public void realizarMerge(NoArvore noAtual){
        // se chegar na raiz, para
        if(noAtual.getPai() == null){
            return;
        }

        NoArvore pai = noAtual.getPai();
        NoArvore esquerdo = pai.getFilhoEsquerdo();
        NoArvore direito = pai.getFilhoDireito();

        // verifica se ambos os irmãos existem e ambos estão livres
        if (esquerdo != null && direito != null &&
                esquerdo.getEstado() == NoArvore.Estado.LIVRE &&
                direito.getEstado() == NoArvore.Estado.LIVRE) {

            // faz merge nos blocos, o pai volta a ser um bloco inteiro livre
            pai.setEstado(NoArvore.Estado.LIVRE);

            // remove filhos da lista de livres
            int indiceFilhos = calcularIndiceLista(esquerdo.getTamanho());
            listasLivres[indiceFilhos].remover(esquerdo);
            listasLivres[indiceFilhos].remover(direito);

            // "mata" os filhos apontando pra null
            pai.setFilhoEsquerdo(null);
            pai.setFilhoDireito(null);

            // adiciona pai fundido na lista de livres maior
            listasLivres[calcularIndiceLista(pai.getTamanho())].inserir(pai);

            // recursão: passa o pai para verificar se ele pode "mergear" com o irmão dele no nível de cima
            realizarMerge(pai);
        }
    }

    private NoArvore buscarNoPorId(NoArvore noAtual, String idBuscado){
        if (noAtual == null){
            return null;
        }

        // encontrou
        if(idBuscado.equals(noAtual.getId())){
            return noAtual;
        }

        // procura na esquerda
        NoArvore encontradoEsquerda = buscarNoPorId(noAtual.getFilhoEsquerdo(), idBuscado);
        if (encontradoEsquerda != null) return encontradoEsquerda;

        // se não achou na esquerda, procura na direita
        return buscarNoPorId(noAtual.getFilhoDireito(), idBuscado);
    }

    // calcula o indice correto para o array de listas (0 para 4KB, 1 para 8KB, etc.)
    private int calcularIndiceLista(int tamanho) {
        int indice = 0;
        int temp = 4;
        while (temp < tamanho) {
            temp *= 2;
            indice++;
        }
        return indice;
    }

    public boolean desfazer() {
        // impede erro ao tentar reverter o estado se o historico estiver vazio
        if (pilhaHistorico.estaVazia()) {
            return false;
        }

        // extrai a ultima acao e quebra a string para recuperar os dados
        String operacao = (String) pilhaHistorico.desempilhar();
        String[] partes = operacao.split(":");
        String comando = partes[0];
        String idProcesso = partes[1];

        // a logica central do desfazer e aplicar a operacao oposta
        if (comando.equals("ALOCAR")) {
            System.out.println("Revertendo alocação: liberando " + idProcesso);
            // se a ultima acao foi alocar, libera o bloco sem gerar historico extra
            return liberarSilencioso(idProcesso);

        } else if (comando.equals("LIBERAR")) {
            int tamanho = Integer.parseInt(partes[2]);
            System.out.println("Revertendo liberação: alocando " + idProcesso + " (" + tamanho + "KB)");
            // se a ultima acao foi liberar, aloca restaurando o tamanho original
            return alocarSilencioso(idProcesso, tamanho);
        }

        return false;
    }

    // executa a alocacao e gerencia a fila de pendentes sem registrar na pilha.
    // usado pelo desfazer e pela fila para reverter ou atualizar o estado
    // sem criar logs indesejados e poluir o historico de operacoes.
    private boolean alocarSilencioso(String idProcesso, int tamanhoSolicitado) {
        int tamanhoNecessario = calcularProximaPotencia(tamanhoSolicitado);
        boolean sucesso = realizarSplit(raiz, tamanhoNecessario, idProcesso);

        // mantem a logica de enfileirar caso falte espaco mesmo durante a reversao
        if (!sucesso) {
            filaPendentes.enfileirar(idProcesso, tamanhoSolicitado);
        }

        return sucesso;
    }


    // executa a liberacao, o merge e tenta atender a fila, mas omite o registro no historico.
    // essencial para que o ato de desfazer uma alocacao nao seja interpretado
    // pelo sistema como uma nova liberacao feita pelo usuario.
    private boolean liberarSilencioso(String idProcesso) {
        NoArvore noParaLiberar = buscarNoPorId(raiz, idProcesso);
        if (noParaLiberar == null) {
            return false;
        }

        // devolve o bloco para a memoria e tira o dono
        noParaLiberar.setEstado(NoArvore.Estado.LIVRE);
        noParaLiberar.setId(null);

        // tenta reagrupar os pedacos que ficaram livres para evitar fragmentacao
        realizarMerge(noParaLiberar);

        // ao liberar espaco, varre a fila de pendentes para tentar alocar quem estava esperando
        int qtdNaFila = filaPendentes.tamanho();
        for (int i = 0; i < qtdNaFila; i++) {
            estruturas.NoFila.No req = filaPendentes.desenfileirar();
            if (req != null) {
                // usa o metodo silencioso para que as tentativas da fila nao criem falso historico
                alocarSilencioso(req.id, req.tamanho);
            }
        }
        return true;
    }

    public NoLista[] getListasLivres() {
        return listasLivres;
    }

    public NoFila getFilaPendentes() {
        return filaPendentes;
    }
}