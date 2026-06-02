package visualizacao;

import buddy.Alocador;

import java.io.BufferedReader;
import java.io.FileReader;

public class DatasetProcessor {

    public static void carregar(
            String caminho,
            Alocador alocador
    ) {

        try {

            BufferedReader br =
                    new BufferedReader(
                            new FileReader(caminho)
                    );

            String linha;

            while ((linha = br.readLine()) != null) {

                System.out.println("\n>> " + linha);

                String[] partes = linha.split(" ");

                if (partes[0].equals("ALOCAR")) {

                    String id = partes[1];
                    int tamanho =
                            Integer.parseInt(partes[2]);

                    alocador.alocar(id, tamanho);

                } else if (partes[0].equals("LIBERAR")) {

                    String id = partes[1];

                    alocador.liberar(id);
                }

                System.out.println("\nESTADO DA MEMÓRIA:");

                ArvorePrinter.imprimir(
                        alocador.getRaiz()
                );

                BuddyInfoPrinter.imprimir(alocador);

                alocador.getFilaPendentes()
                        .imprimirFila();
            }

            br.close();

        } catch (Exception e) {

            System.out.println(
                    "Erro ao carregar dataset: " +
                            e.getMessage()
            );
        }
    }
}