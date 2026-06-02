package visualizacao;

import buddy.Alocador;
import estruturas.NoLista;

public class BuddyInfoPrinter {

    public static void imprimir(Alocador alocador) {

        NoLista[] listas = alocador.getListasLivres();

        int tamanho = 4;

        System.out.println("\n===== BUDDYINFO =====");

        for (int i = 0; i < listas.length; i++) {

            String label;

            if (tamanho >= 1024) {
                label = (tamanho / 1024) + "MB";
            } else {
                label = tamanho + "KB";
            }

            System.out.println(
                    label + " : " +
                            listas[i].tamanho() +
                            " blocos livres"
            );

            tamanho *= 2;
        }

        System.out.println("=====================\n");
    }
}