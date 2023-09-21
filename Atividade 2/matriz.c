/*---------------------------------------------------------------+
|            UNIFAL - Universidade Federal de Alfenas            |
|              BACHARELADO EM CIÊNCIA DA COMPUTAÇÃO              |
|       Trabalho....:   Mensagens Coletivas                      |
|       Disciplina..:   Programação Paralela                     |
|       Professor...:   Paulo Alexandre Bressan                  |
|       Aluno.......:   Daniel da Costa Lima                     |
|       Data........:   20/09/2023                               |
+----------------------------------------------------------------*/

// mpicc matriz.c -o matriz
// mpirun -np 4 --hostfile hostfile ./matriz 5 6

#include <stdio.h>
#include <stdlib.h>
#include <mpi.h>
#include <time.h>
#include <string.h>

int main(int argc, char const *argv[])
{
    MPI_Status status;
    MPI_Init(NULL, NULL);

    int world_size;
    MPI_Comm_size(MPI_COMM_WORLD, &world_size);

    int world_rank;
    MPI_Comm_rank(MPI_COMM_WORLD, &world_rank);

    /* Dimensão das estruturas */
    int colunas_A = atoi(argv[1]);
    int colunas_B = atoi(argv[2]);
    int linhas_A = world_size;      // A qtd de linhas de A é igual a qtd de processos
    int linhas_B = colunas_A;       // Para multiplicar duas matrizes, a qtd de colunas de A tem que ser igual a qtd de linhas de B

    /* Gerando as matrizes de forma estática */
    int matriz_A[linhas_A][colunas_A];
    int matriz_B[linhas_B][colunas_B];
    int matriz_C[linhas_A][colunas_B];

    if (world_rank == 0) {
        /* Iniciando a matriz A */
        for (int i = 0, k = 0; i < linhas_A; i++) {
            for (int j = 0; j < colunas_A; j++, k++) {
                matriz_A[i][j] = k;
            }
        }

        /* Iniciando a matriz B */
        for (int i = 0, k = 12; i < linhas_B; i++) {
            for (int j = 0; j < colunas_B; j++, k++) {
                matriz_B[i][j] = k;
            }
        }

        /* Exibindo a matriz A */
        printf("Matriz A: \n");
        for (int i = 0; i < linhas_A; i++) {
            for (int j = 0; j < colunas_A; j++) {
                printf("%d ", matriz_A[i][j]);
            }
            printf("\n");
        }

        /* Exibindo a matriz B */
        printf("\nMatriz B: \n");
        for (int i = 0; i < linhas_B; i++) {
            for (int j = 0; j < colunas_B; j++) {
                printf("%d ", matriz_B[i][j]);
            }
            printf("\n");
        }
    }
    
    /* Vetor no qual os dados serão recebidos pelo processo */
    int linha_local_A[colunas_A];       

    /* Contém o resultado de cada multiplicação */
    int linha_local_multiplicada[colunas_B];        

    /* Enviando as linhas para os respectivos processos */
    MPI_Scatter(matriz_A, colunas_A, MPI_INT, linha_local_A, colunas_A, MPI_INT, 0, MPI_COMM_WORLD);

    /* Transmitindo os dados da matriz B para todos os processo */
    MPI_Bcast(matriz_B, (linhas_B * colunas_B), MPI_INT, 0, MPI_COMM_WORLD);        

    /* Realizando a multiplicação das duas matrizes */
    for (int i = 0, resultado = 0; i < colunas_B; i++) {
        for (int j = 0; j < colunas_A; j++) {
            resultado += linha_local_A[j] * matriz_B[j][i];
        }
        linha_local_multiplicada[i] = resultado;
        resultado = 0;
    }
    
    /* Enviando a linha calculada para o processo root */
    MPI_Gather(linha_local_multiplicada, colunas_B, MPI_INT, matriz_C, colunas_B, MPI_INT, 0, MPI_COMM_WORLD);
    if (world_rank == 0){
        /* Exibindo o resultado: a matriz C */
        printf("\nMatriz C: \n");
        for (int i = 0; i < linhas_A; i++) {
            for (int j = 0; j < colunas_B; j++) {
                printf("%d ", matriz_C[i][j]);
            }
            printf("\n");
        }
    }
    
    MPI_Barrier(MPI_COMM_WORLD);
    MPI_Finalize();
    return 0;
}
