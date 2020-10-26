 #include <sys/stat.h>
#include <sys/types.h>
#include <fcntl.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <termios.h>
#include <fcntl.h>
#include <string.h>
#include <stdbool.h>

#define DIRECTION_MAX 35
#define VALUE_MAX 30
#define BUFFER_MAX 15

#define IN  0
#define OUT 1

#define LOW  0
#define HIGH 1

#define QUANTIDADE_PADROES 14
#define PADRAO_INICIAL 'A'

/*
******************
      Variaveis
*****************
*/

char padrao_atual = PADRAO_INICIAL;
char AUX = PADRAO_INICIAL;
int contador = 0;

int contador_de_execucoes[QUANTIDADE_PADROES];
int aux_padroes = QUANTIDADE_PADROES;
int numero_de_padroes = 2;


/*
*******************
 Codigo Fornecido
*******************
*/

int config_serial(char * device, unsigned int baudrate)
{
	struct termios options;
	int fd;

	fd = open(device, O_RDWR | O_NOCTTY | O_NDELAY );
	if (fd < 0)
	{
		//Could not open the port.
		perror("config_serial: Não pode abrir a serial - ");
		return -1;
	}

	fcntl(fd, F_SETFL, 0);

	//Get the current options for the port...
	tcgetattr(fd, &options);

	//Sets the terminal to something like the "raw" mode
	cfmakeraw(&options);

	//Set the baudrate...
	cfsetispeed(&options, baudrate);
	cfsetospeed(&options, baudrate);

	//Enable the receiver and set local mode...
	options.c_cflag |= (CLOCAL | CREAD);

	//No parit, 1 stop bit, size 8
	options.c_cflag &= ~PARENB;
	options.c_cflag &= ~CSTOPB;
	options.c_cflag &= ~CSIZE;
	options.c_cflag |= CS8;

	//Clear old settings
	options.c_cflag &= ~CRTSCTS;
	options.c_iflag &= ~(IXON | IXOFF | IXANY);

	//non-caninical mode
	options.c_lflag &= ~ICANON; 

	//Set the new options for the port...
	tcsetattr(fd, TCSANOW, &options);

	//configura a tty para escritas e leituras não bloqueantes
	//fcntl(fd, F_SETFL, fcntl(fd, F_GETFL) | O_NONBLOCK);

	return fd;
}

static int GPIOExport(int pin) 
{

	char buffer[BUFFER_MAX];
	ssize_t bytes_written;
	int fd;

	fd = open("/sys/class/gpio/export", O_WRONLY);
	if (-1 == fd) 
	{
		fprintf(stderr, "Failed to open export for writing!\n");
		return(-1);
	}

	bytes_written = snprintf(buffer, BUFFER_MAX, "%d", pin);
	write(fd, buffer, bytes_written);
	close(fd);
	return(0);
}

static int GPIOUnexport(int pin) 
{
	char buffer[BUFFER_MAX];
	ssize_t bytes_written;
	int fd;

	fd = open("/sys/class/gpio/unexport", O_WRONLY);
	if (-1 == fd) 
	{
		fprintf(stderr, "Failed to open unexport for writing!\n");
		return(-1);
	}

	bytes_written = snprintf(buffer, BUFFER_MAX, "%d", pin);
	write(fd, buffer, bytes_written);
	close(fd);
	return(0);
}

static int GPIODirection(int pin, int dir) 
{
	static const char s_directions_str[]  = "in\0out";

	char path[DIRECTION_MAX];
	int fd;

	snprintf(path, DIRECTION_MAX, "/sys/class/gpio/gpio%d/direction", pin);
	fd = open(path, O_WRONLY);
	if (-1 == fd) 
	{
		fprintf(stderr, "Failed to open gpio direction for writing!\n");
		return(-1);
	}

	if (-1 == write(fd, &s_directions_str[IN == dir ? 0 : 3], IN == dir ? 2 : 3)) 
	{
		fprintf(stderr, "Failed to set direction!\n");
		return(-1);
	}

	close(fd);
	return(0);
}

static int GPIORead(int pin) 
{
	char path[VALUE_MAX];
	char value_str[3];
	int fd;

	snprintf(path, VALUE_MAX, "/sys/class/gpio/gpio%d/value", pin);
	fd = open(path, O_RDONLY);
	if (-1 == fd) 
	{
		fprintf(stderr, "Failed to open gpio value for reading!\n");
		return(-1);
	}

	if (-1 == read(fd, value_str, 3)) 
	{
		fprintf(stderr, "Failed to read value!\n");
		return(-1);
	}

	close(fd);

	return(atoi(value_str));
}

static int GPIOWrite(int pin, int value) 
{
	static const char s_values_str[] = "01";

	char path[VALUE_MAX];
	int fd;

	snprintf(path, VALUE_MAX, "/sys/class/gpio/gpio%d/value", pin);
	fd = open(path, O_WRONLY);
	if (-1 == fd) 
	{
		fprintf(stderr, "Failed to open gpio value for writing!\n");
		return(-1);
	}

	if (1 != write(fd, &s_values_str[LOW == value ? 0 : 1], 1)) 
	{
		fprintf(stderr, "Failed to write value!\n");
		return(-1);
	}

	close(fd);
	return(0);
}

/*
*******************
Codigo Desenvolvido
*******************
*/


/*-------------------------------------------------
   Função que envia padrões para as LEDS.
-------------------------------------------------*/

void escrever_padrao()
{
	int fd;

	fd = config_serial((char*) "/dev/ttyAMA0", B9600);

	if(fd < 0)
	{
		printf("ERRO AO TENTAR ESCREVER NO SERIAL!!!\n");

		return;
	}

	printf("PADRAO ATUAL = %c\n", padrao_atual);

	write(fd, &padrao_atual, 1);
}


/*-------------------------------------------------
   Função que "movimenta" o padrão atual
   	    (SEGUNDA EXECUÇÃO).
-------------------------------------------------*/

void proximo_padrao()
{
	if(contador < (aux_padroes - 1))
	{
		padrao_atual++;
		contador++;
	}
	else
	{
		padrao_atual = PADRAO_INICIAL;
		contador = 0;
	}

	escrever_padrao();
}


/*-------------------------------------------------
   Função que "movimenta" o padrão atual
   	    (SEGUNDA EXECUÇÃO).
-------------------------------------------------*/

void padrao_anterior()
{
	if(contador > 0)
	{
		padrao_atual--;
		contador--;
	}
	else
	{
		padrao_atual = PADRAO_INICIAL + aux_padroes - 1;
		contador = aux_padroes - 1;
	}

	escrever_padrao();
}

void ler_mudancas_de_cada_padrao() 
{
	int fd;
	int i;

	fd = config_serial((char*) "/dev/ttyAMA0", B9600);

	if(fd < 0)
	{
		printf("ERRO AO TENTAR LER DO SERIAL!!!\n");

		return;
	}

	char valor = '0';

	write(fd, &valor, 1);	

	char padrao_aux = PADRAO_INICIAL;

	printf("\nAplicando padrão [%c] nos LEDS.\n", padrao_atual);
	//printf("MUDANCAS DE CADA PADRAO -> [");
}

void aplica_mudanca_nas_LEDS()
{
    int fd;
	int i;

	fd = config_serial((char*) "/dev/ttyAMA0", B9600);

	if(fd < 0)
	{
		printf("ERRO AO TENTAR LER DO SERIAL!!!\n");

		return;
	}

	char valor = '0';

	write(fd, &valor, 1);
 
	//char padrao_aux = PADRAO_INICIAL;
}


/*-------------------------------------------------
   Função que converte a organização numerica 
   para alfabetica.
-------------------------------------------------*/

char printa_nas_leds()
{
    if(numero_de_padroes == 2){return 'B';}
    else if(numero_de_padroes == 3){return 'C';}
    else if(numero_de_padroes == 4){return 'D';}
    else if(numero_de_padroes == 5){return 'E';}
    else if(numero_de_padroes == 6){return 'F';}
    else if(numero_de_padroes == 7){return 'G';}
    else if(numero_de_padroes == 8){return 'H';}
    else if(numero_de_padroes == 9){return 'I';}
    else if(numero_de_padroes == 10){return 'J';}
    else if(numero_de_padroes == 11){return 'K';}
    else if(numero_de_padroes == 12){return 'L';}
    else if(numero_de_padroes == 13){return 'M';}
    else if(numero_de_padroes == 14){return 'N';}
    else if(numero_de_padroes == 15){return 'O';}
}


/*-------------------------------------------------
   Função para conversão local.
   Converte a organizacao alfabetica para numerica.
-------------------------------------------------*/
int descobre_padrao()
{
    if(padrao_atual == 'A'){return 1;}
    else if(padrao_atual == 'B'){return 2;}
    else if(padrao_atual == 'C'){return 3;}
    else if(padrao_atual == 'D'){return 4;}
    else if(padrao_atual == 'E'){return 5;}
    else if(padrao_atual == 'F'){return 6;}
    else if(padrao_atual == 'G'){return 7;}
    else if(padrao_atual == 'H'){return 8;}
    else if(padrao_atual == 'I'){return 9;}
    else if(padrao_atual == 'J'){return 10;}
    else if(padrao_atual == 'K'){return 11;}
    else if(padrao_atual == 'L'){return 12;}
    else if(padrao_atual == 'M'){return 13;}
    else if(padrao_atual == 'N'){return 14;}
    else if(padrao_atual == 'O'){ return 15;}
}

/*-------------------------------------------------
   Aumenta o numero de padrões na primeira execução.
-------------------------------------------------*/

void aumenta_numero_padroes()
{
    if(numero_de_padroes <15){

/*-------------------------------------------------
  # Aumenta o numero de padrões.
  # Pega o padrão de led correspondente.
  # Manda o padrão_atual (que foi alterado localmente)
    para as LEDS.
-------------------------------------------------*/

        numero_de_padroes++;
	char aux = printa_nas_leds();
	padrao_atual =aux;
	// aplica_mudanca_nas_LEDS();
	printf("Numero de padroes atuais: %d\n", numero_de_padroes);
    }
    else{
    	numero_de_padroes = 2;
    }

	//escrever_padrao();
}


/*-------------------------------------------------
  Diminui o numero de padrões na primeira execução.
-------------------------------------------------*/

void diminui_numero_padroes()
{
    if(numero_de_padroes > 2){

/*-------------------------------------------------
  # Diminui o numero de padrões.
  # Pega o padrão de led correspondente.
  # Manda o padrão_atual (que foi alterado localmente)
    para as LEDS.
-------------------------------------------------*/

        numero_de_padroes--;
	char aux2 = printa_nas_leds();
	padrao_atual = aux2;
       // aplica_mudanca_nas_LEDS();
	printf("Numero de padroes atuais: %d\n", numero_de_padroes);
    }
    else{
    	numero_de_padroes = aux_padroes -1;
    }
	//escrever_padrao();
}



/*-------------------------------------------------
   Passa os valores setados para a segunda execução.
   Reseta o padrao atual para o valor B (1).
-------------------------------------------------*/

void confirma_numero_de_padroes()
{
    aux_padroes = numero_de_padroes;
    padrao_atual = 'B';
}



void aux(){
	char inicial = 'A';
	int i = 0;
	for(i = 0; i< numero_de_padroes; i++){
	printf("O padrao %c executou: %d\n",inicial, contador_de_execucoes[i]);
	inicial++;
	}
}


/*-------------------------------------------------
   Retorna a quantidade de execuções que os padrões
   rodaram (segunda execução).
-------------------------------------------------*/

void printa_quantidade_de_execucoes()
{
    int res = descobre_padrao(); // descobre atual
    contador_de_execucoes[res-1]++;
}


/*********************
   	MAIN
**********************/

int main(int argc, char *argv[])
{
	
	bool confirmou = false;
	//BOTAO DA ESQUERDA (SW1)
	int pushbutton25 = 25;
	//BOTAO DO MEIO (SW2)
	int pushbutton24 = 24;
	//BOTAO DA DIREITA (SW3)
	int pushbutton23 = 23;

	//Enable GPIO pins
	GPIOExport(pushbutton25);
	GPIOExport(pushbutton24);
	GPIOExport(pushbutton23);

	printf("\t ---- Programa Rodando ---- \n\n");
	

	printf("Digite a quatidade de padroes:\n");


while(1)
	{

    // 25
    //Set GPIO directions
    if (-1 == GPIODirection(pushbutton25, OUT)){return(2);}

    //Write GPIO value
    if (-1 == GPIOWrite(pushbutton25, 1)){return(3);}

    //Set GPIO directions
    if (-1 == GPIODirection(pushbutton25, IN)){return(2);}

    //23
    if (-1 == GPIODirection(pushbutton23, OUT)){return(2);}
    if (-1 == GPIOWrite(pushbutton23, 1)){return(3);}
    if (-1 == GPIODirection(pushbutton23, IN)){return(2);}

    //24
    if (-1 == GPIODirection(pushbutton24, OUT)){return(2);}
    if (-1 == GPIOWrite(pushbutton24, 1)){return(3);}
    if (-1 == GPIODirection(pushbutton24, IN)){return(2);}

	/*-------------------------------------------------
   		CHAMDA DE FUNÇÃO PARA OS BOTÕES 
		      (PRIMEIRA EXECUÇÃO)
	--------------------------------------------------*/

		//SW1:
		if (GPIORead(pushbutton25) == 0)
		{
			confirmou = false;
			diminui_numero_padroes();
		}
		//SW2:
		else if (GPIORead(pushbutton24) == 0)
		{
			confirmou = false;
			aumenta_numero_padroes();
		}
		//SW3:
		else if (GPIORead(pushbutton23) == 0 && confirmou)
		{
			confirma_numero_de_padroes();
			break;
		}

 		else if(GPIORead(pushbutton23) == 0){
			printf("\nGostira de confirmar? \n");
			confirmou = true;
			//aplica_mudanca_nas_LEDS();

			escrever_padrao();
		}


		//Read GPIO value
		usleep(500 * 1000);

}
	// *****************************
	



	printf("\nSumario (por questoes de bom funcionamento): \n");
	printf("\t A = 1   | B = 2   | C = 3   | D = 4\n");
	printf("\t E = 5   | F = 6   | G = 7   | H = 8\n");
	printf("\t I = 9   | J = 10  | K = 11  | L = 12\n");
	printf("\t M = 13  | N = 14  | O = 15\n");

	
	while(1)
	{

    // 25
    //Set GPI O directions
    if (-1 == GPIODirection(pushbutton25, OUT)){return(2);}
    //Write GPIO value
    if (-1 == GPIOWrite(pushbutton25, 1)){return(3);}
    //Set GPIO directions
    if (-1 == GPIODirection(pushbutton25, IN)){return(2);}

    //23
    if (-1 == GPIODirection(pushbutton23, OUT)){return(2);}
    if (-1 == GPIOWrite(pushbutton23, 1)){return(3);}
    if (-1 == GPIODirection(pushbutton23, IN)){return(2);}

    //24
    if (-1 == GPIODirection(pushbutton24, OUT)){return(2);}
    if (-1 == GPIOWrite(pushbutton24, 1)){return(3);}
    if (-1 == GPIODirection(pushbutton24, IN)){return(2);}

	/*-------------------------------------------------
   		CHAMDA DE FUNÇÃO PARA OS BOTÕES 
		      (SEGUNA EXECUÇÃO).
	-------------------------------------------------*/
	
		if (GPIORead(pushbutton25) == 0)
		{
			padrao_anterior();
		}
		else if (GPIORead(pushbutton24) == 0)
		{

			proximo_padrao();
		}
		else if (GPIORead(pushbutton23) == 0)
		{
			ler_mudancas_de_cada_padrao();	
			printa_quantidade_de_execucoes();
			aux();
		}


		//Read GPIO value
		usleep(500 * 1000);
	}

	//Disable GPIO pins
	if (-1 == GPIOUnexport(pushbutton25))
	{
		return(4);
	}
	if (-1 == GPIOUnexport(pushbutton24))
	{
		return(4);
	}
	if (-1 == GPIOUnexport(pushbutton23))
	{
		return(4);
	}

	return(0);
}


