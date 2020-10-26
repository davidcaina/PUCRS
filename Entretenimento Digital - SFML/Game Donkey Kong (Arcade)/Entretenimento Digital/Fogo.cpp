#include "Fogo.h"

/*
	Classe: Fogo
	Descrição: Desenvolvimento do Inimigo Fogo (Não funcional)
	Autores: David Cainã e Lucas Bergmann
*/

Fogo::Fogo()
{
}

// Construtor
Fogo::Fogo(int x, int y) : Barril(x, y)
{
	open.setTextureRect(sf::IntRect(31, 173, 15, 8));
	closed.setTextureRect(sf::IntRect(31, 173, 15, 8));
}


// Metodo para inverter a sprit decorrente da visao do objeto
void Fogo::step()
{
	Barril::step();

	if (getVX() > 0)
	{
		open.setTextureRect(sf::IntRect(31, 173, -17, 12));
		closed.setTextureRect(sf::IntRect(31, 173, -18, 12));
	}
	else if (getVX() < 0)
	{
		open.setTextureRect(sf::IntRect(31, 173, 17, 12));
		closed.setTextureRect(sf::IntRect(31, 173, 18, 12));
	}
	if (spriteType)
		setSprite(closed);
	else
		setSprite(open);
}

Fogo::~Fogo()
{
}
