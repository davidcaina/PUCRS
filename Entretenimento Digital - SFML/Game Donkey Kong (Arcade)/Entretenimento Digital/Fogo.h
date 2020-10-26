#pragma once
#include "Barril.h"

/*
	Classe: Fogo
	Descrição: Interface do Inimigo Fogo (Não funcional)
	Autores: David Cainã e Lucas Bergmann
*/

class Fogo : public Barril
{
public:
	Fogo();
	Fogo(int, int);
	bool atBottom(); // Chega até o fim
	void backUp();	// E depois sobe ( ERRO - ta voando pra cima) 
	void step();
	~Fogo();
};

