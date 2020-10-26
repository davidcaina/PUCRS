#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <ctype.h>
#include <string.h>
#include <time.h>
#include <omp.h>

/**

  # Pattern Matching: https://www.geeksforgeeks.org/wildcard-character-matching/

  # Case sense: https://stackoverflow.com/questions/1841758/how-to-remove-punctuation-from-a-string-in-c?noredirect=1&lq=1
 */

// The main function that checks if two given strings
// match. The first string may contain wildcard characters
bool match(char *first, char * second) {
  // If we reach at the end of both strings, we are done
  if (*first == '\0' && *second == '\0')
    return true;

  // Make sure that the characters after '*' are present
  // in second string. This function assumes that the first
  // string will not contain two consecutive '*'
  if (*first == '*' && *(first+1) != '\0' && *second == '\0')
    return false;

  // If the first string contains '?', or current characters
  // of both strings match
  if (*first == '?' || *first == *second)
    return match(first+1, second+1);

  // If there is *, then there are two possibilities
  // a) We consider current character of second string
  // b) We ignore current character of second string.
  if (*first == '*')
    return match(first+1, second) || match(first, second+1);
  return false;
}

void transforma_linha(char *p) {
  char *src = p, *dst = p;

  while (*src) {
    if (ispunct((unsigned char)*src)) {
      /* Skip this character */
      src++;
    } else if (isupper((unsigned char)*src)) {
      /* Make it lowercase */
      *dst++ = tolower((unsigned char)*src);
      src++;
    } else if (src == dst) {
      /* Increment both pointers without copying */
      src++;
      dst++;
    } else {
      /* Copy character */
      *dst++ = *src++;
    }
  }

  *dst = 0;
}

int main(void) {
 /* ================== VARIAVEIS ========================= */
  FILE *aux;
  char *buffer;
  long numero_bytes;

  // SEPARA A STRING EM 'N' TOKENS.
  char *str = "\n";
  char padrao[100];

  int cont_padroes_sequencial = 0;
  int cont_padroes_paralelo = 0;
  int n_linha = 0;
  int n_execucoes = 0;
  int n_threads = 0;

  char *array[300000];
  int i = 0;           // VARIAVEL PARA CONTAR O N DE LINHAS.


  /* ================== CRIAÇÃO BUFFER ========================= */

       // Abre arquivo para leitura.
      aux = fopen("Texto1.txt", "r");

      // Erro: Arquivo não disponivel.
      if(aux == NULL)
        return 1;

      // vai para final do arquivo - Get tamanho.
      fseek(aux, 0L, SEEK_END);
      numero_bytes = ftell(aux);

      // Volta para o começo do arquivo.
      fseek(aux, 0L, SEEK_SET);

      // Cria buffer com tamanho necessario.
      buffer = (char*)calloc(numero_bytes, sizeof(char));

      // Erro: alocação de memoria.
      if(buffer == NULL)
        return 1;

      // Passa conteudo do arquivo para o buffer.
      fread(buffer, sizeof(char), numero_bytes, aux);
      fclose(aux);


  /* ================== WHILE SEQUENCIAL ========================= */

    printf("\n\n === EXECUCAO SEQUENCIAL === \n");

    printf("\n Insira padrao a ser procurado: ");
    scanf("%s", &padrao);

    do{
    printf("\n Insira o numero de threads desejadas: ");
    scanf("%d", &n_threads);

    if(n_threads > 0)
        break;

    printf("\n ENTRADA INVALIDA (O numero de threads nao pode ser zero ou menor).\n");

    }while(1);

    printf("\n Insira a quantidade de vezes da execucao: ");
    scanf("%d", &n_execucoes);

    /* VARIAVEIS DE EXECUÇÃO: */
    int execucao_atual = 1;
    int buffer_numeroPadroes_sequencial[n_execucoes];
    int buffer_numeroPadroes_paralelo  [n_execucoes];

    double buffer_startTempo_sequencial[n_execucoes];
    double buffer_endTempo_sequencial  [n_execucoes];
    double buffer_startTempo_paralelo  [n_execucoes];
    double buffer_endTempo_paralelo    [n_execucoes];

    clock_t end_SEQUENCIAL;
    clock_t start_SEQUENCIAL;
    clock_t start_PARALELO;
    clock_t end_PARALELO;

    /*LOOP DE EXECUÇÕES: */
    while (execucao_atual <= n_execucoes){

        i = 0;
        n_linha = 0;
        cont_padroes_sequencial = 0;
        cont_padroes_paralelo = 0;

     printf("\n\n------------------------------------------------------\n\n ");
     printf("\n\n=== EXECUCAO SEQUENCIAL === \n");

        /* CRIA PONTEIRO PARA PERCORRER BUFFER - SEQUENCIAL*/
        char * string_sequencial = strtok(strdup(buffer), str);

        /* CONTABILIZA O TEMPO DE INICIO DO SEQUENCIAL: */
        clock_t start_SEQUENCIAL = clock();
        buffer_startTempo_sequencial[execucao_atual] = start_SEQUENCIAL;

        while(string_sequencial != NULL) {

            // TRANSFORMA TUDO P/ MINUSCULO.
            transforma_linha(string_sequencial);

            // SALVA CADA LINHA NO ARRAY.
            array[i] = string_sequencial;
            // CONTA O NUMERO DE LINHAS.
            i++;

            // VERIFICA SE OCORREU UM PADRAO.
            if(match(padrao, string_sequencial))
                {
                  printf("Ocorrecia encontrada na linha: %d \n" , i);

                  // INCREMENTA NUMERO DE PADROES.
                  cont_padroes_sequencial++;
                }

            /*ITERA PARA PROXIMA LINHA*/
            string_sequencial  = strtok(NULL, str);

            /*CONTA QUANTIDADE DE LINHAS*/
            n_linha++;
        }

        if(cont_padroes_sequencial == 0){ printf("\nNENHUM PADRAO FOI ENCONTRADO. \n");}

        // SALVA O NUMERO DE PADROES.
        buffer_numeroPadroes_sequencial[execucao_atual] = cont_padroes_sequencial;

        /* SALVA TEMPO FINAL DA EXECUCAO ATUAL:*/
        clock_t end_SEQUENCIAL = clock();
        buffer_endTempo_sequencial[execucao_atual] = end_SEQUENCIAL;



     /* ================== WHILE PARALELO ========================= */

        printf("\n\n------------------------------------------------------\n\n ");

        printf("\n\n=== EXECUCAO PARALELA === \n");

        // SALVA TEMPO ATUAL DA EXECUCAO:
        clock_t start_PARALELO = clock();
        buffer_startTempo_paralelo[execucao_atual] = start_PARALELO;

        // SET NUMERO THREADS PARA EXECUCAO
        omp_set_num_threads(n_threads);

        //INICIA PARALELIZACAO:
          #pragma omp parallel
          {
            // UTILIZA REDUCTION PARA NAO SOBRESCREVER VALORES.
            #pragma omp for reduction(+:cont_padroes_paralelo)
            for(int i = 0; i < n_linha; i++)
                {
                 if(match(padrao, array[i]))  // SE ACHOU UM PADRÃO, CONTABILIZA.
                    {
                      printf("Ocorrecia encontrada na linha: %d \n" , i);

                      // INCREMENTA NUMERO DE PADROES.
                      cont_padroes_paralelo++;
                    }
                }
          }

        if(cont_padroes_paralelo == 0){ printf("\nNENHUM PADRAO FOI ENCONTRADO. \n");}
        // SALVA NUMERO DE PADROES DA EXECUCAO ATUAL
        buffer_numeroPadroes_paralelo[execucao_atual] = cont_padroes_paralelo;

        // SALVA TEMPO FINAL DA EXECUCAO ATUAL.
        clock_t end_PARALELO = clock();
        buffer_endTempo_paralelo[execucao_atual] = end_PARALELO;

        execucao_atual++;
    }


     /* ================== RELATORIO ========================= */

    printf("\n\n------------------------------------------------------\n\n ");

    free(buffer);

    printf("\n\n=== RELATORIO DA EXECUCAO === \n");

    // VARIAVEIS DE AUXILIO.
    double res_proc_ganho;
    double res_tempo_sequencial = 0.0;
    double res_tempo_paralelo = 0.0;
    double res_RES = 0.0;


    // EXIBICAO DOS RESULTADOS
    if(n_execucoes > 0){
        for(int i = 0 ; i < n_execucoes; i++){


            printf("\n ### Execucao %d  ###\n\n", i+1);

            printf(" Sequencial: \n");
            printf(" # Numero de padroes encontrados: %d \n", buffer_numeroPadroes_sequencial[i+1]);

            double res_SEQ = (double)(buffer_endTempo_sequencial[i+1] - buffer_startTempo_sequencial[i+1])/CLOCKS_PER_SEC;
            res_tempo_sequencial = res_tempo_sequencial + res_SEQ;

            printf(" # Tempo de execucao: %lf seg \n\n", res_SEQ);

            printf(" Paralela:  \n");
            printf(" # Numero de padroes encontrados: %d \n", buffer_numeroPadroes_paralelo[i+1]);

            double res_PAR     = (double)(buffer_endTempo_paralelo[i+1] - buffer_startTempo_paralelo[i+1])/CLOCKS_PER_SEC;
            res_tempo_paralelo = res_tempo_paralelo + res_PAR;

            printf(" # Tempo de execucao: %lf seg\n\n", res_PAR);

            double res = (100*(res_SEQ-res_PAR))/res_SEQ;
            res_RES = res_RES + res;

            printf("    Ganho de %.2lf%% \n\n", res);

            printf("-----------------------------------------");
        }


        printf("\n\n Resultado final (MEDIA): \n");
        printf("      # Tempo de execucao sequencial - %lf seg\n", (res_tempo_sequencial/n_execucoes));
        printf("      # Tempo de execucao paralela   - %lf seg\n", (res_tempo_paralelo/n_execucoes));
        printf("      # Quanto foi ganho de execucao - %.2lf%% \n\n\n", res_RES/n_execucoes );
    }

    else
    {
            printf(" \nNenhuma execucao ocorreu. \n\n\n");
    }
    return 0;
}
