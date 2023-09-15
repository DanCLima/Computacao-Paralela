#include <mpi.h>
#include <stdio.h>
#include <stdlib.h>

int main(int argc, char** argv) {
    const int PING_PONG_LIMIT = 10;

  // Initialize the MPI environment. The two arguments to MPI Init are not
  // currently used by MPI implementations, but are there in case future
  // implementations might need the arguments.
    MPI_Init(NULL, NULL);

    // Get the number of processes
    int world_size;
    MPI_Comm_size(MPI_COMM_WORLD, &world_size);

    // Get the rank of the process
    int world_rank;
    MPI_Comm_rank(MPI_COMM_WORLD, &world_rank);
  
    if (world_size != 2) {
        fprintf(stderr, "World size must be two for %s\n", argv[0]);
        MPI_Abort(MPI_COMM_WORLD, 1);
    }

    int ping_ping_count = 0;
    int partner_rank = (world_rank + 1) % 2;
    while (ping_ping_count < PING_PONG_LIMIT) {
        if (world_rank == ping_ping_count % 2) {
            ping_ping_count++;
            MPI_Send(&ping_ping_count, 1, MPI_INT, partner_rank, 0, MPI_COMM_WORLD);
            printf("%d sent and incremented ping_pong_count %d to %d\n", world_rank, ping_ping_count, partner_rank);
        } else {
            MPI_Recv(&ping_ping_count, 1, MPI_INT, partner_rank, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
            printf("%d received ping_pong_count %d to %d\n", world_rank, ping_ping_count, partner_rank);
        }
    }
        
    MPI_Finalize();
}