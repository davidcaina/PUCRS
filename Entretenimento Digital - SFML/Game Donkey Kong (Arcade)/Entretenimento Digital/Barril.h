#pragma once
#include "GameObject.h"
#include "Ladder.h"


/*
	Classe: Barril
	Descrição: Interface do Inimigo Barril
	Autores: David Cainã e Lucas Bergmann
*/

class Barril : public GameObject
{
public:
	Barril();
	Barril(int, int);

	void step();
	void changeRotation(double); // quando estiver na escada vai mudar
	~Barril();
	sf::FloatRect getLadderBox();
	bool ladderIntersect(Ladder *); // vai para a escada
	bool getOnLadderAgain();		// detecta


protected:
	int spriteType;
	sf::Sprite open, closed;
	sf::Texture texture;
	int counter;
	sf::FloatRect boundingBox, ladderBox;
	bool onLadder;
};