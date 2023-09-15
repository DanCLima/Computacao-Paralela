#include <mpi.h>
#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <limits.h>
#define TAM 1000

/* 
Exercícios
    1 - Gerar um vetor de 1000 elementos, dividir em P processos e encontrar o menor valor do vetor.
    2 - Gerar 2 matrizes e realizar a soma entre elas em uma 3 matriz, onde cada linha será calculada em um processo.
    Ao final, enviar a matriz para a raiz.


    Obs: a raiz deve receber o tamanho e divir e trabalhar

    Compilar: mpicc -o hello hello.c
    Executar: mpiexec -n 4 hello
 */

int main(int argc, char const *argv[])
{
    MPI_Init(NULL, NULL);

    int world_size;
    MPI_Comm_size(MPI_COMM_WORLD, &world_size);

    int world_rank;
    MPI_Comm_rank(MPI_COMM_WORLD, &world_rank);
    
    int numbers[TAM];
    int number_amount;
    int number_task = 0;

    number_task = TAM/world_size;
    // printf("Numero de tarefas: %d\n", number_task);
    int numbers_local[number_task];

    srand(time(NULL));
    /* Preencher o vetor com 1000 números aleatórios */
    for (int i = 0; i < TAM; i++) {
        number_amount = rand();      // Gerando números menores que 1000
        numbers[i] = number_amount;
    }

    MPI_Scatter(numbers, number_task, MPI_INT, numbers_local, number_task, MPI_INT, 0, MPI_COMM_WORLD);

    int menor = INT_MAX;        // Iniciando a variável menor com o maior valor INT possível
    for (int i = 0; i < number_task; i++) {
        if (menor > numbers_local[i]) {
            menor = numbers_local[i];
        } 
    }
    printf("O menor valor encontrado no processo %d foi: %d\n", world_rank, menor);

    int* returned_number = NULL;
    if (world_rank == 0) {
        returned_number = (int*)malloc(sizeof(int) * world_size);
    }

    // printf("Menor eh: %d\n", menor);
    MPI_Gather(&menor, 1, MPI_INT, returned_number, 1, MPI_INT, 0, MPI_COMM_WORLD);

    if (world_rank == 0) {
    int menor = INT_MAX;

        for (int i = 0; i < world_size; i++) {
            if (menor > returned_number[i]) {
                menor = returned_number[i];
            }
        }
        printf("O menor valor encontrado foi: %d\n", menor);
    }

    MPI_Barrier(MPI_COMM_WORLD);
    MPI_Finalize();
    return 0;
}
