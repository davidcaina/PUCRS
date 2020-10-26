#pragma once
#include "Barril.h"

/*
	Classe: Fogo
	Descri��o: Interface do Inimigo Fogo (N�o funcional)
	Autores: David Cain� e Lucas Bergmann
*/

class Fogo : public Barril
{
public:
	Fogo();
	Fogo(int, int);
	bool atBottom(); // Chega at� o fim
	void backUp();	// E depois sobe ( ERRO - ta voando pra cima) 
	void step();
	~Fogo();
};

