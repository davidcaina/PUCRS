#include <stdlib.h>
#include <stdio.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/ioctl.h>
#include <string.h>
#include <unistd.h>
#include <errno.h>
#include <linux/if_packet.h>
/* Diretorios: net, netinet, linux contem os includes que descrevem */
/* as estruturas de dados do header dos protocolos   	  	        */
#include <netinet/ip.h>
#include <netinet/tcp.h> 
#include <netinet/udp.h> 
#include <netinet/ip6.h> 
#include <net/if.h>  		//estrutura ifr
#include <netinet/ether.h> 	//header ethernet
#include <netinet/in.h> 	//definicao de protocolos
#include <arpa/inet.h> 		//funcoes para manipulacao de enderecos IP
#include <netinet/in_systm.h> //tipos de dados
// Atencao!! Confira no /usr/include do seu sisop o nome correto
// das estruturas de dados dos protocolos.

  unsigned char buff[1500]; // buffer de recepcao 1500
  unsigned char macO = 0xa2;
  unsigned char macD = 0x8f;
  int sock;
  struct ifreq ifr;
  int sockE;
  struct ifreq ifrE;
  
  struct iphdr *iph;
  struct ip6_hdr *ip6h;
  struct tcphdr *tcph;
  struct udphdr *udh;

int save(int i,int j){
	
	FILE *file = fopen("arquivo.txt", "w");
    if (file == NULL) {
          printf ("Erro ao abrir o arquivo.");
	}
    while(i < j) {
    if(fwrite(&buff[i],sizeof(char), 1, file) != 1) {
        printf("Erro na escrita do arquivo");
    }
          i++;
    }  
    fclose(file);
	return 0;
}

int responder(int tam){	
	struct sockaddr_ll to; 
	socklen_t len;
	unsigned char addr[6];	
	to.sll_protocol= htons(ETH_P_ALL);
	to.sll_ifindex = ifrE.ifr_ifindex; 
	to.sll_halen = 6;
	memcpy (to.sll_addr, addr, 6);	
	len = sizeof(struct sockaddr_ll);
		
	unsigned char buff2[tam+14];
	for (int c = 0; c < tam+14; c++) buff2[c] = buff[c];	
	if(sendto(sockE, (char *) buff2, tam + 14, 0, (struct sockaddr*) &to, len)<0){
		printf("ERRO ao enviar para maquina destino.\n");}	
	return 0;
}

int receber(){	
	while(1){
		recv(sock,(char *) &buff, sizeof(buff), 0x0);
		if(buff[11] == macO && buff[5] == macD){return 0;}
	}
}

int enverter(){
	unsigned char buff2[6];
	//inverte no ethernet header
	buff2[0] = buff[0]; 
	buff2[1] = buff[1];
	buff2[2] = buff[2];
	buff2[3] = buff[3];
	buff2[4] = buff[4];
	buff2[5] = buff[5];
	buff[0] = buff[6]; 
	buff[1] = buff[7];
	buff[2] = buff[8];
	buff[3] = buff[9];
	buff[4] = buff[10];
	buff[5] = buff[11];
	buff[6] = buff2[0]; 
	buff[7] = buff2[1];
	buff[8] = buff2[2];
	buff[9] = buff2[3];
	buff[10] = buff2[4];
	buff[11] = buff2[5];
	//inverte no ipheader
	char*saux;
	
	saux = inet_ntoa(*(struct in_addr *)&iph->saddr);
	iph->saddr = iph->daddr;
	iph->daddr = inet_addr(saux);
	//inverte no tcpheader
	
	int paux;
	paux = tcph->source;
	tcph->source = tcph->dest;
	tcph->dest = paux ;
	
	return 0;
}

int tcpconection(int usandoipv4,int tam){	
	//printar coisas na primeira recepcao
	if(usandoipv4){tcph = (struct tcphdr*)&buff[34];}
	else{tcph = (struct tcphdr*)&buff[22];}
	
	printf("-->TCP \n");
	printf("Source port : %d \n", tcph->source);
	printf("Destination port : %d \n", tcph->dest);
	printf("Sequence number : %d \n", tcph->seq);
	printf("Acknowledgment number : %d \n", tcph->ack_seq);
	printf("Window : %d \n", tcph->window);
	printf("Checksum : %d \n", tcph->check);
	printf("Urgent pointer : %d \n", tcph->urg_ptr);
	
	
	//responde conexao	
	tcph->ack_seq = 33333;
	tcph->seq = tcph->seq+1; 
	tcph->ack=1;
	enverter(); //enverter endereços
	responder(tam); // enviar devolta 
	printf("responde pedido de conexao. \n");
	
	// recebe que a conexao foi estabelecida	
	receber();
	printf("Conexao feita. \n");
	
	// recebe dados e salva apartir de tcp, buff[54] em diante possui dados	
	receber();
	if(usandoipv4){save(54,ntohs(tam)+14);}
	else{save(42,ntohs(tam)+14);}
	printf("Arquivo recebido e salvo. \n");
	
	// responde que recebeu dados	
	enverter(); 
	tcph->seq = tcph->seq+1; 
	tcph->ack_seq = tcph->ack_seq+1; 
	tcph->ack=1;
	if(usandoipv4){tcph->doff = htons(tam - sizeof(struct iphdr))}
	else{tcph->doff = htons(tam - sizeof(struct ip6_hdr))}
	if(usandoipv4){iph = (struct iphdr*)&buff[14];
	iph->tot_len = htons(tam);
	}
	else{ip6h = (struct ip6_hdr*)&buff[14];
	ip6h->ip6_plen = htons(tam);
	}	
	responder(tam);
	printf("Respondendo que recebeu arquivos com ACK. \n");
	
	// encerar conexao	
	receber();
	
	// responde pedido de enceramento
	enverter();
	tcph->seq = tcph->seq+1; 
	tcph->ack_seq = tcph->ack_seq+1; 
	tcph->fin=1;
	tcph->ack=1;
	responder(tam);
	printf("Recebido o pedido de fim de conexao! Respondendo com FIN e ACK. \n");
	
	// receber ack
	receber();
	printf("Recebido ACK de fim de conexao. \n");
	printf("\n");
	printf("Printando informacaos do ultimo header tcp recebido: \n");
	printf("Sequence number : %d \n", tcph->seq);
	printf("Acknowledgment number : %d \n", tcph->ack_seq);
	printf("flag syn : %d \n", tcph->syn);
	printf("flag fin : %d \n", tcph->fin);
	printf("flag ack : %d \n", tcph->ack);
}

int main(int argc,char *argv[])
{
	int tamanho;
    /* Criacao do socket. Todos os pacotes devem ser construidos a partir do protocolo Ethernet. */
    /* De um "man" para ver os parametros.*/
    /* htons: converte um short (2-byte) integer para standard network byte order. */
    if((sock = socket(PF_PACKET, SOCK_RAW, htons(ETH_P_ALL))) < 0) {
       printf("Erro na criacao do socket.\n");
       exit(1);
    }
	
	// O procedimento abaixo eh utilizado para "setar" a interface em modo promiscuo
	strcpy(ifr.ifr_name, "enp4s0");
	if(ioctl(sock, SIOCGIFINDEX, &ifr) < 0)
		printf("erro no ioctl!");
		
	ioctl(sock, SIOCGIFFLAGS, &ifr);
	ifr.ifr_flags |= IFF_PROMISC;
	ioctl(sock, SIOCSIFFLAGS, &ifr);
	
	
	//==============Criamos um segundo socket para enviar
	memset(&ifrE, 0, sizeof(ifrE));
	if((sockE = socket(PF_PACKET, SOCK_RAW, htons(ETH_P_ALL))) < 0)  {
		printf("Erro na criacao do socket.\n");
        exit(1);
 	}
	strcpy(ifrE.ifr_name, "enp4s0");
	if(ioctl(sockE, SIOCGIFINDEX, &ifrE) < 0)
		printf("erro no ioctl!");
	//==============Criamos um segundo socket para enviar
	
	
	// recepcao de pacotes 
	while(1){
	// impress�o do conteudo - exemplo Endereco Destino e Endereco Origem
	
		recv(sock,(char *) &buff, sizeof(buff), 0x0);
		
		if(buff[11] == macO && buff[5] == macD){
								
		printf("MAC Destino: %x:%x:%x:%x:%x:%x \n", buff[0],buff[1],buff[2],buff[3],buff[4],buff[5]);
		printf("MAC Origem:  %x:%x:%x:%x:%x:%x \n", buff[6],buff[7],buff[8],buff[9],buff[10],buff[11]);
		printf("Type : %x%x \n", buff[12],buff[13]);	
		printf(" \n");
		
			if(buff[12] == 0x08 && buff[13] == 0x00) {
				iph = (struct iphdr*)&buff[14];
				printf("-->IPv4 \n");
				printf("Version : %d \n", iph->version);
				printf("IHL : %d \n", iph->ihl);
				printf("Type of service %d \n", iph->tos);
				printf("Total length : %d\n", iph->tot_len);
				printf("Identification : %d \n", iph->id);
				printf("Fragment Offset : %d \n", iph->frag_off);
				printf("Ttl : %d\n", iph->ttl);
				printf("Protocol : %d\n", iph->protocol);
				printf("Checksum : %d\n", iph->check);			
				printf("Source address : %s\n", inet_ntoa(*(struct in_addr *)&iph->saddr));	
				printf("Destination address : %s\n", inet_ntoa(*(struct in_addr *)&iph->daddr));		
				printf(" \n");
				tamanho = ntohs(iph->tot_len);
				
				switch(iph->protocol){
					case 6 : //tcp
						tcpconection(1,tamanho);						
						break;
						
					case 17 : //udp
						udh = (struct udphdr*)&buff[34];
						printf("-->UDP \n");
						printf("Source port : %d \n", udh->source);
						printf("Destination port : %d \n", udh->dest);
						printf("Length : %d \n", udh->len);					
						printf("Checksum : %d \n", udh->check);
						//salva apartir de udp, buff[42] em diante possui dados						
						save(42,ntohs(tamanho)+14);
						break;
				}
			}
			else if(buff[12] == 0x86 && buff[13] == 0xdd){//ipv6
				
				ip6h = (struct ip6_hdr*)&buff[14];
				char str[INET6_ADDRSTRLEN];
				struct sockaddr_in6 sa;
				printf("-->IPv6 \n");
				printf("Version : %d \n", ip6h->ip6_vfc);
				printf("Flow label : %d \n", ip6h->ip6_flow);
				printf("Payload length : %d \n", ip6h->ip6_plen);
				printf("Next header : %d \n", ip6h->ip6_nxt);
				printf("Hoop limit : %d \n", ip6h->ip6_hops);
				inet_ntop(AF_INET6, &ip6h->ip6_src, str, INET6_ADDRSTRLEN);
				printf("Source adress : %s \n", str );
				inet_ntop(AF_INET6, &ip6h->ip6_dst, str, INET6_ADDRSTRLEN);
				printf("Destination adress : %s \n", str);
				printf(" \n");
				tamanho = ntohs(ip6h->ip6_plen);	
			
				switch(ip6h->ip6_nxt){
					
					case 6 : //tcp
						tcpconection(0,tamanho);	
						break;
						
					case 17 : //udp
						udh = (struct udphdr*)&buff[22];
						printf("-->UDP \n");
						printf("Source port : %d \n", udh->source);
						printf("Destination port : %d \n", udh->dest);
						printf("Length : %d \n", udh->len);						
						printf("Checksum : %d \n", udh->check);
						//salva apartir de udp buff[30], em diante possui dados
						save(30,ntohs(tamanho)+14);		
						break;
				}			
			}
		 } // if
	}//while	
} // main
