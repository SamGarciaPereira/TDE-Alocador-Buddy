# Alocador Buddy Binário

Simulador do algoritmo **Buddy System** para gerenciamento de memória, implementado em Java com estruturas de dados manuais.

A ideia central é simples: toda a memória é dividida em blocos cujo tamanho é sempre uma potência de 2. Quando um processo pede memória, o alocador encontra o menor bloco que caiba — dividindo blocos maiores se necessário. Quando libera, tenta fundir o bloco com o vizinho se ele também estiver livre, subindo até a raiz.

---

## Integrantes

- **Abílio Pedro Alcântara Mota Batista**
- **Beatriz Ceciliato Robaskievicz da Cunha**
- **Samuel Garcia Pereira**

---

## Estrutura do Projeto

```
.
├── Main.java                          # Ponto de entrada, menu interativo
├── buddy/
│   ├── NoArvore.java                  # Nó da árvore binária (bloco de memória)
│   └── Alocador.java                  # Lógica principal: split, merge, undo
├── estruturas/
│   ├── NoFila.java                    # Fila FIFO de requisições pendentes
│   ├── NoPilha.java                   # Pilha LIFO para histórico (undo)
│   └── NoLista.java                   # Lista encadeada de blocos livres
└── visualizacao/
    ├── ArvorePrinter.java             # Imprime a árvore hierarquicamente
    ├── BuddyInfoPrinter.java          # Exibe blocos livres por tamanho
    └── DatasetProcessor.java          # Lê e processa o arquivo dataset.txt
```

---

## Classes e Responsabilidades

### `NoArvore`
Representa um bloco de memória na árvore binária. Cada nó guarda:
- `tamanho` — tamanho do bloco em KB (sempre potência de 2)
- `estado` — `LIVRE`, `OCUPADO` ou `DIVIDIDO`
- `id` — identificador do processo dono (só existe quando `OCUPADO`)
- `filhoEsquerdo`, `filhoDireito` — filhos na árvore (presentes quando `DIVIDIDO`)
- `pai` — referência para o pai, essencial para o merge subir pela árvore

### `Alocador`
O coração do sistema. Gerencia:
- A árvore binária com raiz de 32 MB
- O array de 14 listas de blocos livres (uma por tamanho, de 4 KB a 32 MB)
- A fila de pendentes e a pilha de histórico

### `NoFila`
Fila encadeada manual. Guarda requisições que falharam por falta de memória. A cada liberação, o alocador tenta atender os pendentes na ordem de chegada.

### `NoPilha`
Pilha encadeada manual. Registra cada operação bem-sucedida como uma string (`"ALOCAR:p1"` ou `"LIBERAR:p1:1024"`). O `desfazer()` desempilha e aplica a operação inversa.

### `NoLista`
Lista encadeada genérica. O alocador mantém 14 instâncias — uma para cada tamanho possível de bloco. Permite encontrar um bloco livre de tamanho específico sem percorrer a árvore inteira.

---

## Como Funciona

### Split 

Quando um processo solicita memória, o tamanho pedido é arredondado para a próxima potência de 2 (mínimo 4 KB). O alocador percorre a árvore recursivamente buscando um bloco livre do tamanho exato. Se só houver blocos maiores, divide pela metade até chegar no tamanho certo.

Exemplo: pedido de 100 KB → arredonda para 128 KB. Se o menor bloco livre for 1 MB, ele é dividido: 1 MB → 512 KB + 512 KB → 256 KB + 256 KB → 128 KB + 128 KB. O primeiro 128 KB é alocado; o segundo fica livre.

A cada divisão:
- O nó pai passa para estado `DIVIDIDO`
- É removido da lista de livres do seu tamanho
- Os dois filhos criados entram na lista de livres do tamanho menor

### Merge 

Ao liberar um bloco, o alocador verifica se o "buddy" (o irmão na árvore) também está livre. Se sim, os dois são fundidos de volta no pai — que volta a ser `LIVRE`. Esse processo se repete subindo até a raiz, criando um efeito de cascata.

A cada fusão:
- Os dois filhos são removidos das listas de livres
- O pai volta a ser `LIVRE` e entra na lista de livres do tamanho maior
- Os filhos têm suas referências anuladas

### Undo 

Cada operação bem-sucedida é registrada na pilha como uma string serializada:
- `"ALOCAR:p1"` para alocações
- `"LIBERAR:p1:128"` para liberações (inclui o tamanho para poder realocar corretamente)

O `desfazer()` desempilha a última operação e aplica o inverso: se foi `ALOCAR`, libera; se foi `LIBERAR`, realoca. As operações de reversão são "silenciosas" — não geram novos registros na pilha, para não poluir o histórico.

---

## Compilação e Execução

### Compilar

```bash
javac -d out Main.java buddy/*.java estruturas/*.java visualizacao/*.java
```

### Executar

```bash
java -cp out Main
```

### Carregar dataset automaticamente

Coloque o arquivo `dataset.txt` na raiz do projeto e use a opção `7` do menu. Formato do arquivo:

```
ALOCAR p1 100
ALOCAR p2 5000
LIBERAR p1
ALOCAR p3 200
```

---

## Menu

```
===== MENU =====
1 - Alocar
2 - Liberar
3 - Desfazer
4 - Exibir memória
5 - Exibir fila
6 - Listas de livres
7 - Carregar dataset
8 - Sair
```

---

## Exemplos com Saída

### Alocar p1 com 100 KB e p2 com 5000 KB

```
>> ALOCAR p1 100

ESTADO DA MEMÓRIA:
└── [32MB DIVIDIDO]
    ├── [16MB DIVIDIDO]
    │   ├── [8MB DIVIDIDO]
    │   │   ├── [4MB DIVIDIDO]
    │   │   │   ├── [2MB DIVIDIDO]
    │   │   │   │   ├── [1MB DIVIDIDO]
    │   │   │   │   │   ├── [512KB DIVIDIDO]
    │   │   │   │   │   │   ├── [256KB DIVIDIDO]
    │   │   │   │   │   │   │   ├── [128KB OCUPADO p1]
    │   │   │   │   │   │   │   └── [128KB LIVRE]
    │   │   │   │   │   │   └── [256KB LIVRE]
    │   │   │   │   │   └── [512KB LIVRE]
    │   │   │   │   └── [1MB LIVRE]
    │   │   │   └── [2MB LIVRE]
    │   │   └── [4MB LIVRE]
    │   └── [8MB LIVRE]
    └── [16MB LIVRE]

===== BUDDYINFO =====
4KB : 0 blocos livres
8KB : 0 blocos livres
...
128KB : 1 blocos livres
256KB : 1 blocos livres
512KB : 1 blocos livres
1MB : 1 blocos livres
2MB : 1 blocos livres
4MB : 1 blocos livres
8MB : 1 blocos livres
16MB : 1 blocos livres
32MB : 0 blocos livres
=====================
```

### Liberar p1 — merge em cascata

Após liberar `p1` (128 KB), o buddy (128 KB ao lado) também está livre → fundem em 256 KB. O buddy de 256 KB também está livre → fundem em 512 KB. Isso se repete até onde houver buddies livres.

```
>> LIBERAR p1

ESTADO DA MEMÓRIA:
└── [32MB LIVRE]
```

---

## Análise de Fragmentação

O Buddy System elimina fragmentação externa — nunca sobram "buracos" entre blocos que somam memória suficiente mas individualmente são pequenos demais. O merge em cascata garante que blocos adjacentes livres sempre sejam reunidos.

A contrapartida é a **fragmentação interna**: como os blocos têm tamanho fixo em potências de 2, qualquer pedido que não seja exatamente uma potência de 2 desperdiça o espaço excedente dentro do bloco alocado.

| Solicitado | Alocado | Desperdiçado | 
|------------|---------|--------------|
| 100 KB     | 128 KB  | 28 KB        | 
| 3 MB       | 4 MB    | 1 MB         | 
| 5 MB       | 8 MB    | 3 MB         | 
| 10 MB      | 16 MB   | 6 MB         | 
| 16 MB      | 16 MB   | 0            | 
| 4 KB       | 4 KB    | 0            |
| < 4 KB     | 4 KB    | até 3 KB     | 

