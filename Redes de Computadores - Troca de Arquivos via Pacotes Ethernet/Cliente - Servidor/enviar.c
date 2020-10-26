//#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <signal.h>
#include <errno.h>
#include <sys/ioctl.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <net/if.h>
#include <arpa/inet.h>
#include <net/ethernet.h>
#include <linux/if_packet.h>
#include <errno.h>
#include <netinet/ip.h>
#include <netinet/tcp.h> 
#include <netinet/udp.h> 
#include <netinet/ip6.h> 

unsigned char macO = 0xa2;
unsigned char macD = 0x8f;

unsigned char buff[1500];
char texto[1420];
int tamanho;
int sock, i;
int total_len;
struct ifreq ifr;

struct ether_header *eth;
struct iphdr *iph;
struct ip6_hdr *ip6h;
struct udphdr *udh;
struct tcphdr *tcph;	

int usandoudp = 0;
int usandoipv4 = 0;

unsigned short checksum(unsigned short *buf, int nwords)
{
    unsigned long sum;
    for(sum=0; nwords>0; nwords--)
        sum += *buf++;
    sum = (sum >> 16) + (sum &0xffff);
    sum += (sum >> 16);
    return (unsigned short)(~sum);
}

int monta_pacote()
/*
# IPv4 (sem erro)
# UDP: (sem erro)
# TCP: testar nova versao 
#
# IPv6 (sem erro)
# UDP: (sem erro)
# TCP: testar nova versao 
*/
{	
	printf("Informe qual nivel de ethernet sera usado, 1=ipv4,0=ipv6 \n");
	scanf("%d", &usandoipv4);
	printf("Informe qual nivel de protocolo sera usado, 1=udp,0=tcp \n");
	scanf("%d", &usandoudp);
	
	// coloca o ponteiro do header ethernet apontando para a 1a. posicao do buffer. onde inicia o header do ethernet.
	//montar ethernet header comeco
	eth = (struct ether_header *) &buff[0];
	//Endereco Mac Destino
	eth->ether_dhost[0] = 0Xa4;	eth->ether_dhost[1] = 0X1f;	eth->ether_dhost[2] = 0X72;
	eth->ether_dhost[3] = 0Xf5;	eth->ether_dhost[4] = 0X90;	eth->ether_dhost[5] = 0X8f;
	//Endereco Mac Origem
	eth->ether_shost[0] = 0Xa4;	eth->ether_shost[1] = 0X1f;	eth->ether_shost[2] = 0X72;
	eth->ether_shost[3] = 0Xf5;	eth->ether_shost[4] = 0X90;	eth->ether_shost[5] = 0Xa2;
	
 	if(usandoipv4){eth->ether_type = htons(0X800);}
	else{eth->ether_type = htons(0X86dd);}
	total_len+=sizeof(struct ether_header);
	
	if(usandoipv4){
		//montar header ipv4 comeco
		iph = (struct iphdr*)&buff[14];
		iph->ihl = 5;
		iph->version = 4;
		iph->tos = 16;
		iph->id = htons(10201);
		iph->ttl = 64;
		if(usandoudp){iph->protocol = 17;}//udp = 17, tcp = 6
		else{iph->protocol = 6;}
		iph->saddr = inet_addr("10.32.143.167"); // colocar ip origem
		iph->daddr = inet_addr("10.32.143.165"); // colocar ip destino 
		total_len+=sizeof(struct iphdr);
	}
	
	else{
		//montar header ipv6 comeco
		ip6h = (struct ip6_hdr*)&buff[14];
 		ip6h->ip6_vfc = 6;
		ip6h->ip6_flow = htonl ((6 << 28) | (0 << 20) | 0);
		if(usandoudp){ip6h->ip6_nxt = 17;}//udp = 17, tcp = 6
		else{ip6h->ip6_nxt = 6;}
		ip6h->ip6_hops = 255;		
		struct sockaddr_in6 aux;
		inet_pton (AF_INET6, "fe80::a61f:72ff:fef5:90a2", &(aux.sin6_addr));
		ip6h->ip6_src = aux.sin6_addr;
		inet_pton (AF_INET6, "fe80::a61f:72ff:fef5:908f", &(aux.sin6_addr));
		ip6h->ip6_dst = aux.sin6_addr;		
		total_len+=sizeof(struct ip6_hdr*);
		
	}
	
	if(usandoudp){
		//montar header udp comeco
		if(usandoipv4){udh = (struct udphdr*) &buff[34];}
		else{udh = (struct udphdr*) &buff[22];}
		udh->source = htons(23451);
		udh->dest = htons(23452);
		udh->check = 0;	
		total_len+=sizeof(struct udphdr);
		if(usandoipv4){i=42;}
		else{i=30;}
	}
	
	else{
		//montar header tcp comeco
		if(usandoipv4){tcph = (struct tcphdr*) &buff[34];}
		else{tcph = (struct tcphdr*) &buff[22];}
		tcph->source = htons(23452); 
		tcph->dest = htons(23452);
	    tcph->seq = 22222;
		tcph->ack_seq = 0;
		tcph->fin=0;
		tcph->syn=1;
		tcph->rst=0;
		tcph->psh=0;
		tcph->ack=0;
		tcph->urg=0;
		tcph->check = 0; 
		tcph->window = htons (5840); 
		tcph->check = 0;
		tcph->urg_ptr = 0;
		total_len+=sizeof(struct tcphdr);
		if(usandoipv4){i=54;}
		else{i=42;}
	}
	
	//adicionar dados
	char *auxb = NULL;
	FILE *file = fopen("arquivo.txt", "r");
	fseek(file, 0, SEEK_END);
	tamanho = ftell(file);
	rewind(file);
	auxb = malloc((tamanho + 1) * sizeof(*auxb));
	fread(auxb, tamanho, 1, file); 
	auxb[tamanho] = '\0';
	strcpy(&buff[i], auxb);
	total_len+=tamanho;
	fclose(file);
	
	//completar headers com tamanho
	if(usandoudp){
		if(usandoipv4){udh->len = htons((total_len - sizeof(struct iphdr) - sizeof(struct ether_header)));}//udp ipv4 tamanho
		else{udh->len = htons((total_len - sizeof(struct ip6_hdr) - sizeof(struct ether_header)));}//udp ipv6 tamanho
		}
	else{
		if(usandoipv4){tcph->doff = htons((total_len - sizeof(struct iphdr) - sizeof(struct ether_header) - tamanho));}//tcp ipv4 tamanho
		else{tcph->doff = htons((total_len - sizeof(struct ip6_hdr) - sizeof(struct ether_header) - tamanho));}//tcp ipv6 tamanho
	}
	if(usandoipv4){iph->tot_len = htons(total_len - sizeof(struct ether_header));}//ipv4 tamanho
	else{ip6h->ip6_plen = htons(total_len - sizeof(struct ether_header));}//ipv6 tamanho
	
	//checksum 

	if(usandoipv4){iph->check = checksum((unsigned short*)(buff + sizeof(struct ether_header)), (sizeof(struct iphdr)/2));}//ipv4 checksum
	return 0;
}


int main(int argc,char *argv[])
{
	
	struct sockaddr_ll to;
	socklen_t len;
	unsigned char addr[6];

    /* Inicializa com 0 os bytes de memoria apontados por ifr. */
	memset(&ifr, 0, sizeof(ifr));

    /* Criacao do socket. Uso do protocolo Ethernet em todos os pacotes. D� um "man" para ver os par�metros.*/
    /* htons: converte um short (2-byte) integer para standard network byte order. */
	if((sock = socket(PF_PACKET, SOCK_RAW, htons(ETH_P_ALL))) < 0)  {
		printf("Erro na criacao do socket.\n");
        exit(1);
 	}

	strcpy(ifr.ifr_name, "enp4s0");
	if(ioctl(sock, SIOCGIFINDEX, &ifr) < 0)
		printf("erro no ioctl!");
	
	/* Identicacao de qual maquina (MAC) deve receber a mensagem enviada no socket. */
	to.sll_protocol= htons(ETH_P_ALL);
	to.sll_ifindex = ifr.ifr_ifindex; /* indice da interface pela qual os pacotes serao enviados */
	to.sll_halen = 6;

	memcpy (to.sll_addr, addr, 6);
	len = sizeof(struct sockaddr_ll);

	monta_pacote();
	int c = 0;
	unsigned char buff2[total_len];//enviar
	
	//===========================UDP===============================
	if(usandoudp){
	//passa informacoes para segundo buffer com tamanho exato dos dados
	for (int c; c < total_len; c++) {buff2[c] = buff[c];}
	if(sendto(sock, (char *) buff2, total_len, 0, (struct sockaddr*) &to, len)<0){
		printf("sendto maquina destino.\n");
		}
	}	
	//===========================TCP===============================
	else{
	unsigned char buff3[total_len-tamanho];//receber
	if(usandoipv4){iph = (struct iphdr*)&buff[14];
	iph->tot_len = htons(total_len - sizeof(struct ether_header)-tamanho);
	}
	else{ip6h = (struct ip6_hdr*)&buff[14];
	ip6h->ip6_plen = htons(total_len - sizeof(struct ether_header)-tamanho);
	}

	//==============Criamos um segundo socket para receber 
	int sockR;
	struct ifreq ifrR;
	if((sockR = socket(PF_PACKET, SOCK_RAW, htons(ETH_P_ALL))) < 0) {
       printf("Erro na criacao do socket.\n");
       exit(1);
    }
	strcpy(ifrR.ifr_name, "enp4s0");
	if(ioctl(sockR, SIOCGIFINDEX, &ifrR) < 0)
		printf("erro no ioctl!");		
	ioctl(sockR, SIOCGIFFLAGS, &ifrR);
	ifrR.ifr_flags |= IFF_PROMISC;
	ioctl(sockR, SIOCSIFFLAGS, &ifrR);
	//==============Criamos um segundo socket para receber 
	
	//inicia conexao
	for ( c; c < total_len-tamanho; c++) buff2[c] = buff[c];
	if(sendto(sock, (char *) buff2, total_len, 0, (struct sockaddr*) &to, len)<0){
		printf("sendto maquina destino.\n");
		}
	printf("iniciando conexao de tcp enviando syn \n");

	//recebe syn ack
	while(1){
	recv(sockR,(char *) &buff3, sizeof(buff3), 0x0);
	if(buff3[5] == macD && buff3[11] == macO){break;}
	}
	if(usandoipv4){tcph = (struct tcphdr*)&buff3[34];}
	else{tcph = (struct tcphdr*)&buff3[22];}	
	int aux1 = tcph->seq+1;
	int aux2 = tcph->ack_seq+1;	
	if(usandoipv4){tcph = (struct tcphdr*)&buff[34];}
	else{tcph = (struct tcphdr*)&buff[22];}	
	tcph->seq = aux1;
	tcph->ack_seq = aux2;
	tcph->ack=1;
	tcph->syn=0;
	printf("recebendo confirmacao da conexao syn ack \n");
	
	//responde que a conexao foi estabelecida			
	c = 0;	
	for ( c; c < total_len-tamanho; c++) buff2[c] = buff[c];
	if(sendto(sock, (char *) buff2, total_len, 0, (struct sockaddr*) &to, len)<0){
		printf("sendto maquina destino.\n");
		}
	printf("respondendo conexao ativa \n");

	//envia arquivo
	tcph->seq = tcph->seq +1;
	tcph->ack_seq = tcph->ack_seq+1;
	tcph->ack=0;
	tcph->doff = htons((total_len - sizeof(struct iphdr) - sizeof(struct ether_header)));
	if(usandoipv4){iph->tot_len = htons(total_len - sizeof(struct ether_header));}
	else{ip6h->ip6_plen = htons(total_len - sizeof(struct ether_header));}	
	c=0;
	for ( c; c < total_len; c++) buff2[c] = buff[c];
	if(sendto(sock, (char *) buff2, total_len, 0, (struct sockaddr*) &to, len)<0){
		printf("sendto maquina destino.\n");
		}
	printf("enviado arquivo \n");
		
	//recebe ack que arquivo foi entrege
	while(1){
	recv(sockR,(char *) &buff3, sizeof(buff3), 0x0);
	if(buff3[5] == macD && buff3[11] == macO){break;}
	}
	if(usandoipv4){tcph = (struct tcphdr*)&buff3[34];}
	else{tcph = (struct tcphdr*)&buff3[22];}	
	aux1 = tcph->seq+1;
	aux2 = tcph->ack_seq+1;	
	if(usandoipv4){tcph = (struct tcphdr*)&buff[34];}
	else{tcph = (struct tcphdr*)&buff[22];}	
	if(usandoipv4){iph->tot_len = htons(total_len - sizeof(struct ether_header)-tamanho);}
	else{ip6h->ip6_plen = htons(total_len - sizeof(struct ether_header)-tamanho);}	
	tcph->seq = aux1;
	tcph->ack_seq = aux2;
	printf("recebendo ack para o envio do arquivo \n");
	
	//encerar conecao		
	tcph->fin=1;
	tcph->doff = htons((total_len - sizeof(struct iphdr) - sizeof(struct ether_header))-tamanho);
	c=0;	
	for ( c; c < total_len-tamanho; c++) buff2[c] = buff[c];
	if(sendto(sock, (char *) buff2, total_len, 0, (struct sockaddr*) &to, len)<0){
		printf("sendto maquina destino.\n");
		}
	printf("enviado perido de fim de conexao \n");	
		
	//recebe fin e ack para encerar conexao
	while(1){
	recv(sockR,(char *) &buff3, sizeof(buff3), 0x0);
	if(buff3[5] == macD && buff3[11] == macO){break;}
	}
	if(usandoipv4){tcph = (struct tcphdr*)&buff3[34];}
	else{tcph = (struct tcphdr*)&buff3[22];}	
	aux1 = tcph->seq+1;
	aux2 = tcph->ack_seq+1;	
	if(usandoipv4){tcph = (struct tcphdr*)&buff[34];}
	else{tcph = (struct tcphdr*)&buff[22];}	
	tcph->seq = aux1;
	tcph->ack_seq = aux2;
	printf("recebendo fin e ack para encerar conexao \n");	
		
	//enviando ack pra fim de conexao		
	tcph->fin=0;	
	tcph->ack=1;
	c = 0;
	for ( c; c < total_len-tamanho; c++) buff2[c] = buff[c];
	if(sendto(sock, (char *) buff2, total_len, 0, (struct sockaddr*) &to, len)<0){
		printf("sendto maquina destino.\n");
	printf("enviado ack para confirmar conexao encerada \n");
		}			
	}
}
