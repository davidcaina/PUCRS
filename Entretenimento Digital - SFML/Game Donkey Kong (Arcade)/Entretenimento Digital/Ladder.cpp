#include "Ladder.h"
#include "Barril.h"
#include <iostream>
#include <vector>

/*
	Classe: Escadas
	Descrição: Desenvolvimento das escadas (BUG na colisão do mario - Tentar arrumar)
	Autores: David Cainã e Lucas Bergmann
*/

// Inimigo desce as escadas tb.
bool isChomp(GameObject *thing)
{
	Barril *test = dynamic_cast<Barril*>(thing);
	if (test == NULL)
		return false;
	return true;
}


// Construtor - Set as sprits e outras configs
Ladder::Ladder(int l, double x, double y) : GameObject(x, y)
{
	length = l;
	texture.loadFromFile("spritesheet.png");
	// C:/Users/david/source/repos/Entretenimento Digital/Entretenimento Digital/
	for (int i = 0; i < l; i++)
	{
		sprites.push_back(sf::Sprite());
		sprites.at(i).setTexture(texture);
		if (i == l - 1)
			sprites.at(i).setTextureRect(sf::IntRect(130, 143, 8, 8));
		else
			sprites.at(i).setTextureRect(sf::IntRect(130, 143, 8, 8));
		sprites.at(i).setScale(sf::Vector2f(3, 3));
		sprites.at(i).setColor(sf::Color::White);
		sprites.at(i).setPosition(getX(), getY() + 8 * 3 * i);
	}


}

Ladder::~Ladder()
{
}

// Array de ladders
int Ladder::allLaders(std::vector<Ladder*> vines, GameObject *other)
{
	int numVines = 0;

	for (int i = 0; i < vines.size(); i++)
	{
		if (vines.at(i)->collision(other))
		{
			numVines++;
		}
	}
	return numVines;
}

// Colisão das escadas.
bool Ladder::collision(GameObject *other)
{
	bool x = false;
	setBB(sf::FloatRect(sf::Vector2f(getX() + 10, getY()),
		sf::Vector2f(1.75 * 3, 8 * length * 3)));

	if (!isChomp(other) && getBB().intersects(other->getBB()))
	{
		if (!other->getOnLadder() && other->getTeam() == 1)
		{
			other->setX(getX() - 20);
		}
		x = true;
		other->setOnLadder(true);
	}
	else if (Barril* c = dynamic_cast<Barril*>(other))
	{
		if (getBB().intersects(other->getBB())
			&& dynamic_cast<Barril*>(other)->getOnLadderAgain() == true)
		{
			other->setOnLadder(true);
		}

		if (dynamic_cast<Barril*>(other)->getLadderBox().intersects(getBB()))
		{
			dynamic_cast<Barril*>(other)->ladderIntersect(this);
			x = true;
		}
	}
	return x;
}

// Desenha escadas.
void Ladder::drawLadder(sf::RenderWindow &window)
{
	for (int i = 0; i < length; i++)
	{
		window.draw(sprites.at(i));
	}
}

// conf sprit.
void Ladder::setSprite(sf::Sprite sprite)
{
	setBB(sf::FloatRect(sf::Vector2f(getX(), getY()),
		sf::Vector2f(8 * 3, 8 * length * 3)));
}