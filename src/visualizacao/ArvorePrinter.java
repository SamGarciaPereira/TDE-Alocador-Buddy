package visualizacao;

import buddy.NoArvore;

public class ArvorePrinter {

    public static void imprimir(NoArvore no) {
        imprimirRecursivo(no, "", true);
    }

    private static void imprimirRecursivo(
            NoArvore no,
            String prefixo,
            boolean ultimo
    ) {

        if (no == null) {
            return;
        }

        System.out.println(
                prefixo +
                        (ultimo ? "└── " : "├── ") +
                        formatarNo(no)
        );

        String novoPrefixo =
                prefixo + (ultimo ? "    " : "│   ");

        imprimirRecursivo(
                no.getFilhoEsquerdo(),
                novoPrefixo,
                false
        );

        imprimirRecursivo(
                no.getFilhoDireito(),
                novoPrefixo,
                true
        );
    }

    private static String formatarNo(NoArvore no) {

        String tamanho;

        if (no.getTamanho() >= 1024) {
            tamanho = (no.getTamanho() / 1024) + "MB";
        } else {
            tamanho = no.getTamanho() + "KB";
        }

        String estado = no.getEstado().toString();

        if (no.getId() != null) {
            return "[" + tamanho + " " + estado + " " + no.getId() + "]";
        }

        return "[" + tamanho + " " + estado + "]";
    }
}