package buddy;

public class NoArvore {
    public enum Estado {LIVRE, OCUPADO, DIVIDIDO}

    private int tamanho;
    private Estado estado;
    private String id;

    private NoArvore filhoEsquerdo;
    private NoArvore filhoDireito;
    private NoArvore pai;

    public NoArvore(int tamanho, NoArvore pai){
        this.tamanho = tamanho;
        this.estado = Estado.LIVRE;
        this.pai = pai;
    }

    public int getTamanho(){
        return tamanho;
    }

    public Estado getEstado(){
        return estado;
    }
    public void setEstado(Estado estado){
        this.estado = estado;
    }

    public String getId(){
        return id;
    }
    public void setId(String id){
        this.id = id;
    }

    public NoArvore getFilhoEsquerdo() {
        return filhoEsquerdo;
    }
    public void setFilhoEsquerdo(NoArvore filhoEsquerdo){
        this.filhoEsquerdo = filhoEsquerdo;
    }

    public NoArvore getFilhoDireito() {
        return filhoDireito;
    }
    public void setFilhoDireito(NoArvore filhoDireito){
        this.filhoDireito = filhoDireito;
    }

    public NoArvore getPai() {
        return pai;
    }
}
