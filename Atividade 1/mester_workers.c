/*---------------------------------------------------------------+
|            UNIFAL - Universidade Federal de Alfenas            |
|              BACHARELADO EM CIÊNCIA DA COMPUTAÇÃO              |
|       Trabalho....:   Mester/Workers                           |
|       Disciplina..:   Programação Paralela                     |
|       Professor...:   Paulo Alexandre Bressan                  |
|       Aluno.......:   Daniel da Costa Lima                     |
|       Data........:   15/09/2023                               |
+----------------------------------------------------------------*/

    // Compilar: mpicc -o hello hello.c
    // Executar: mpiexec -n 4 hello

#include <mpi.h>
#include <stdio.h>
#include <time.h>
#include <windows.h>
#define MIN 1000
#define MAX 2000

int comparar(const void *a, const void *b) {
    return (*(int *)a - *(int *)b);
}

int mediana(int vetor[], int tamanho) {
    qsort(vetor, tamanho, sizeof(int), comparar);

    if (tamanho % 2 != 0) {
        return vetor[tamanho / 2];
    } else {
        int meio1 = vetor[tamanho / 2 - 1];
        int meio2 = vetor[tamanho / 2];
        return (meio1 + meio2) / 2;
    }
}

/* 
    Criar um modelo master/worker onde o processo 0 é o master que "dispara" tarefas diversificadas para os workers. 
    Os workers saberão qual a tarefa realizar pelo valor da TAG recebida. Assim que um worker terminar a tarefa, o master
    deverá atribuir uma nova tarefa 

    Tarefas pelas TAGs:
        TAG 0 =  soma dos valores
        TAG 1 = média dos valores
        TAG 2 = maior valor
        TAG 3 = mediana dos valores
        TAG 10 = finalizar

    MESTER
        INICIO
        Envia tarefas
        Recebe resultados e envia novas tarefas
        Quando termina as tarefas envia mensagem de término

    WORKER
        ESPERA UMA MSG COM TAG
        Realiza a operação
        Devolve a resultado
        Finaliza

    Compilar:mpicc -o hello hello.c
    Executar: mpiexec -n 4 hello
*/

int main(int argc, char const *argv[])
{
    MPI_Status status;
    MPI_Init(NULL, NULL);

    int world_size;
    MPI_Comm_size(MPI_COMM_WORLD, &world_size);

    int world_rank;
    MPI_Comm_rank(MPI_COMM_WORLD, &world_rank);

    int tag;
    int tarefas;
    int tamanho;
    int* numeros = NULL;

    srand(time(NULL));
    tarefas = 1 + rand() % (10 - 1); 

    while (tarefas > 0){
        if (world_rank == 0) {
            // Mester
            printf("+---------------------------------------------------+\n");
            printf("|                     Tarefa: %d                     |\n", tarefas);
            printf("|                                                   |\n");

            for (int i = 1; i < world_size; i++) {
                tamanho = MIN + rand() % (MAX - MIN);       // Quantidade de números que cada worker vai receber
                numeros = (int*)malloc(sizeof(int) * tamanho);

                for (int j = 0; j < tamanho; j++) {
                    int valor = 1 + rand() % 100;
                    numeros[j] = valor;
                }

                tag = rand() % 4;       // Gera um valor de 0 a 3 para a TAG

                MPI_Send(&tamanho, 1, MPI_INT, i, 0, MPI_COMM_WORLD);       // Envia o tamanho do buffer
                MPI_Send(numeros, tamanho, MPI_INT, i, 1, MPI_COMM_WORLD);      // Envia os dados
                MPI_Send(&tag, 1, MPI_INT, i, 2, MPI_COMM_WORLD);       // Envia a TAG
                printf("|   O processo %d recebeu %d numeros com a tag %d   |\n", i, tamanho, tag);
            }

            double resultado = 0.0;
            for (int i = 1; i < world_size; i++) {
                MPI_Recv(&resultado, 1, MPI_DOUBLE, i, 4, MPI_COMM_WORLD, &status);     // Recebe o resultado
                MPI_Recv(&tag, 1, MPI_INT, i, 5, MPI_COMM_WORLD, &status);      // Recebe a TAG
                printf("|   O processo %d com a tag %d retornou %10.3lF    |\n", i, tag, resultado);
            }
            printf("+---------------------------------------------------+\n");

            free(numeros);
        } else {
            // Worker
            MPI_Recv(&tamanho, 1, MPI_INT, 0, 0, MPI_COMM_WORLD, &status); // Recebe o tamanho (a quantidade de números entre 1000 a 2000)

            numeros = (int*)malloc(sizeof(int) * tamanho);
            
            MPI_Recv(numeros, tamanho, MPI_INT, 0, 1, MPI_COMM_WORLD, &status); // Receber os N números gerados aleatoriamente de 1 a 100 
            MPI_Recv(&tag, 1, MPI_INT, 0, 2, MPI_COMM_WORLD, &status); // Recebe a TAG que vai determinar a tarefa a ser realizada

            double resultado = 0.0;
            
            switch (tag) {
            case 0:     // Calcula a soma dos valores
                for (int j = 0; j < tamanho; j++) {
                    resultado += numeros[j];
                }
                break;
            case 1:     // Calcula a média dos valores
                for (int j = 0; j < tamanho; j++) {
                    resultado += numeros[j];
                }
                resultado = resultado/tamanho;
                break;
            case 2:     // Retorna o maior valor
                for (int j = 0; j < tamanho; j++) {
                    if (resultado < numeros[j]) {
                        resultado = numeros[j];
                    }
                }
                break;
            case 3:     // Calcula a mediana
                resultado = mediana(numeros, tamanho);
                break;
            default:
                break;
            }

            MPI_Send(&resultado, 1, MPI_DOUBLE, 0, 4, MPI_COMM_WORLD);      // Envia o resultado para o mester
            MPI_Send(&tag, 1, MPI_INT, 0, 5, MPI_COMM_WORLD);       // Envia a TAG de volta para o mester
            Sleep(10);      // Realiza uma pausa após cada tarefa
            free(numeros);
        }
        tarefas--;
    }

    MPI_Barrier(MPI_COMM_WORLD);
    MPI_Finalize();
    return 0;
}
