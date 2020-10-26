#include "Barril.h"
#include <iostream>
#include <cstdlib>

/*
	Classe: Barril
	Descrição: Desenvolvimento dos metodos barril. Ações e colisões
	Autores: David Cainã e Lucas Bergmann
*/


Barril::Barril()
{
}

Barril::~Barril()
{
}

Barril::Barril(int x, int y) : GameObject(x, y)
{
	texture.loadFromFile("spritesheet.png");

	onLadder = false;

	int chance = rand() % 2;
	if (chance == 1)
		setVX(1);
	else
		setVX(-1);
	setTeam(2);

	open.setTexture(texture);
	open.setScale(sf::Vector2f(2, 2));
	open.setColor(sf::Color::White);

	closed.setTexture(texture);
	closed.setScale(sf::Vector2f(2, 2));
	closed.setColor(sf::Color::White);

	open.setTextureRect(sf::IntRect(0, 110, 15, 8));
	closed.setTextureRect(sf::IntRect(0, 110, 15, 8));

	open.setPosition(x, y);
	closed.setPosition(x, y);

	setSprite(open);
	spriteType = 0;
	setTouchingEnemy(false);
	counter = 0;
}


//Method to overide GameObject step.
void Barril::step()
{
	setX(getX() + getVX());
	setY(getY() + getVY());
	open.setPosition(getX(), getY());
	closed.setPosition(getX(), getY());

	//Update the bounding box.
	setBB(getSprite().getGlobalBounds());
	ladderBox = sf::FloatRect(getX() + 12, getY() + 48, 1, 1);

	if (!getOnLadder())
	{
		useGravity();
	}
	else
	{
		setVY(1);
		setVX(0);
	}

	counter++;
	if (counter % 25 == 0)
	{
		if (counter % 50 == 0)
			spriteType = 1;
		else
			spriteType = 0;
	}

	if (getY() > 800)
	{
		setY(120);
		onLadder = false;
		setX(80);

		int chance = rand() % 2;
		if (chance == 1)
			setVX(1);
		else
			setVX(-1);
	}

	if (getVX() > 0)
	{
		open.setTextureRect(sf::IntRect(0, 110, 15, 8)); // inverte lado ta pegando outra skin
		closed.setTextureRect(sf::IntRect(0, 110, 15, 8));
	}
	else if (getVX() < 0)
	{
		open.setTextureRect(sf::IntRect(0, 110, 15, 8));
		closed.setTextureRect(sf::IntRect(0, 111, 15, 7));
	}

	if (spriteType)
		setSprite(closed);
	else
		setSprite(open);
}


// barril Descendo a escada
bool Barril::ladderIntersect(Ladder * v)
{
	if (ladderBox.intersects(v->getBB()) && !onLadder && rand() % 10 == 0)
	{
		onLadder = true;
		setY(getY() + 50);
		open.setRotation(90);
		closed.setRotation(90);
		setX(getX() + 30);
		setVX(0);
		return true;
	}

	return false;
}


// Roda narril na escada
void Barril::changeRotation(double angle)
{
	open.setRotation(angle);
	closed.setRotation(angle);
	if (onLadder)
	{
		setX(getX() - 25);
		onLadder = false;
	}
}


//Bounding box.
sf::FloatRect Barril::getLadderBox()
{
	return ladderBox;
}


//nao deixa dois na mesma escada
bool Barril::getOnLadderAgain()
{
	return onLadder;
}