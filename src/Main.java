import buddy.Alocador;
import visualizacao.ArvorePrinter;
import visualizacao.BuddyInfoPrinter;
import visualizacao.DatasetProcessor;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        Alocador alocador =
                new Alocador(32768);

        int opcao = 0;

        while (opcao != 8) {

            System.out.println("\n===== MENU =====");

            System.out.println("1 - Alocar");
            System.out.println("2 - Liberar");
            System.out.println("3 - Desfazer");
            System.out.println("4 - Exibir memória");
            System.out.println("5 - Exibir fila");
            System.out.println("6 - Listas de livres");
            System.out.println("7 - Carregar dataset");
            System.out.println("8 - Sair");

            System.out.print("Escolha: ");

            opcao = sc.nextInt();

            switch (opcao) {

                case 1:

                    System.out.print("ID: ");
                    String id = sc.next();

                    System.out.print("Tamanho KB: ");
                    int tamanho = sc.nextInt();

                    boolean sucesso =
                            alocador.alocar(id, tamanho);

                    if (sucesso) {
                        System.out.println("Alocado com sucesso!");
                    } else {
                        System.out.println(
                                "Falha ao alocar. Foi para fila."
                        );
                    }

                    break;

                case 2:

                    System.out.print("ID: ");

                    String liberar = sc.next();

                    boolean liberou =
                            alocador.liberar(liberar);

                    if (liberou) {
                        System.out.println("Liberado!");
                    } else {
                        System.out.println("ID não encontrado.");
                    }

                    break;

                case 4:

                    ArvorePrinter.imprimir(
                            alocador.getRaiz()
                    );

                    break;


                case 5:

                    alocador.getFilaPendentes()
                            .imprimirFila();

                    break;

                case 7:

                    DatasetProcessor.carregar(
                            "dataset.txt",
                            alocador
                    );

                    break;



                case 8:

                    System.out.println("Encerrando...");
                    break;

                default:

                    System.out.println("Opção inválida.");
            }
        }

        sc.close();
    }
}