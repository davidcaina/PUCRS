#include "Platform.h"
#include "Barril.h"
#include <iostream>

/*
	Classe: Plats
	Descrição: Interface das plataformas
	Autores: David Cainã e Lucas Bergmann
*/

Platform::Platform(int l, double x, double y, int type) : GameObject(x, y)
{
	length = l;
	texture.loadFromFile("spritesheet.png");
	// C:/Users/david/source/repos/Entretenimento Digital/Entretenimento Digital/
	for (int i = 0; i < l; i++)
	{
		sprites.push_back(sf::Sprite());
		sprites.at(i).setTexture(texture);
		if (type == 1)
		{
			if (i == 0)
				sprites.at(i).setTextureRect(sf::IntRect(142, 153, 8, 10));
			else if (i == l - 1)
				sprites.at(i).setTextureRect(sf::IntRect(153, 153, 8, 10));
			else
				sprites.at(i).setTextureRect(sf::IntRect(142, 153, 8, 10));
		}
		else if (type == 2)
		{
			if (i == 0)
				sprites.at(i).setTextureRect(sf::IntRect(142, 153, 8, 8));
			else if (i == l - 1)
				sprites.at(i).setTextureRect(sf::IntRect(153, 153, 8, 8));
			else
				sprites.at(i).setTextureRect(sf::IntRect(142, 153, 8, 8));
		}

		sprites.at(i).setScale(sf::Vector2f(3, 3));
		sprites.at(i).setColor(sf::Color::White);
		sprites.at(i).setPosition(getX() + 8 * 3 * i, getY());
	}

	setBB(sf::FloatRect(sf::Vector2f(getX(), getY()),
		sf::Vector2f(8 * length * 3, 8 * 3)));
}

/*
 --------------------------------
*/

void Platform::step()
{
	setBB(sf::FloatRect(sf::Vector2f(getX(), getY()),
		sf::Vector2f(8 * length * 3, 8 * 3)));
}

/*
 --------------------------------
*/

Platform::~Platform()
{
}

/*
 --------------------------------
*/

// Verifica colisão com a plataforma
bool Platform::collision(GameObject * other)
{
	setBB(sf::FloatRect(sf::Vector2f(getX(), getY()),
		sf::Vector2f(8 * length * 3, 8 * 3)));

	//Check top.
	if (other->getY() <= (getY() - other->getBB().height)
		&& (other->getY() + other->getVY()) >= (getY() - other->getBB().height)
		&& (other->getX() + other->getBB().width) > getX()
		&& other->getX() < (getX() + getBB().width))
	{
		if (Barril* c = dynamic_cast<Barril*>(other))
			dynamic_cast<Barril*>(other)->changeRotation(0);
		other->setVY(0);
		other->setY(getY() - other->getBB().height);
		if (!other->getOnLadder())
			return true;
	}
	else if (other->getY() >= (getY() + 24)
		&& (other->getY() + other->getVY()) <= (getY() + 24)
		&& (other->getX() + other->getBB().width) > getX()
		&& other->getX() < (getX() + getBB().width))
	{
		other->setVY(0);
		other->setY(getY() + 28);
	}
	else if ((other->getX() + other->getBB().width <= getX()
		&& (other->getX() + other->getVX() + other->getBB().width) >= getX())
		|| other->getBB().intersects(this->getBB())
		&& ((other->getY() + other->getBB().height) > getY())
		&& (other->getY() < getY() + 24))
	{
		other->setX(getX() - other->getBB().width);
	}
	else if ((other->getX() >= getX() + getBB().width
		&& (other->getX() + other->getVX()) <= getX() + getBB().width)
		&& ((other->getY() + other->getBB().height) > getY())
		&& (other->getY() < getY() + 24))
	{
		other->setX(getX() + getBB().width + 5);
	}

	return false;
}

/*
 --------------------------------
*/


// Desenha plataforma
void Platform::drawPlat(sf::RenderWindow & window)
{
	for (int i = 0; i < length; i++)
	{
		window.draw(sprites.at(i)); // desenha
	}
}


/*
 --------------------------------
*/

// pegar a sprit
void Platform::setSprite(sf::Sprite sprite)
{
	setBB(sf::FloatRect(sf::Vector2f(getX(), getY()),
		sf::Vector2f(8 * length * 3, 8 * 3)));
}


