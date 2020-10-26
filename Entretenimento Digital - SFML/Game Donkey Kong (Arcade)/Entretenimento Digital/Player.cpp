#include "Player.h"
#include <SFML/Graphics.hpp>
#include "Platform.h"
#include <typeinfo>
#include <iostream>

/*
	Classe: Player
	Descrição: Desenvolvimento do Jogador
	Autores: David Cainã e Lucas Bergmann
*/

Player::Player(double x, double y) : GameObject(x, y)
{
	texture.loadFromFile("spritesheet.png");

	for (int i = 0; i < 4; i++)
	{
		up[i].setTexture(texture);
		up[i].setScale(sf::Vector2f(2, 2));
		up[i].setColor(sf::Color::White);

		jump[i].setTexture(texture);
		jump[i].setScale(sf::Vector2f(2, 2));
		jump[i].setColor(sf::Color::White);

		if (i < 3)
		{
			right[i].setTexture(texture);
			right[i].setScale(sf::Vector2f(2, 2));
			right[i].setColor(sf::Color::White);

			left[i].setTexture(texture);
			left[i].setScale(sf::Vector2f(2, 2));
			left[i].setColor(sf::Color::White);
		}
		if (i < 2)
		{
			grab[i].setTexture(texture);
			grab[i].setScale(sf::Vector2f(2, 2));
			grab[i].setColor(sf::Color::White);
		}
	}

	//Coloca cada sprit numa posição
	right[0].setTextureRect(sf::IntRect(15, 4, -14, 17)); // Desuso (Animções com erro - Retirado)
	right[1].setTextureRect(sf::IntRect(15, 4, -14, 17));
	right[2].setTextureRect(sf::IntRect(15, 4, -14, 17));
	right[3].setTextureRect(sf::IntRect(15, 4, -14, 17));

	left[0].setTextureRect(sf::IntRect(1, 4, 14, 17)); // --
	left[1].setTextureRect(sf::IntRect(1, 4, 14, 17));
	left[2].setTextureRect(sf::IntRect(1, 4, 14, 17));
	left[3].setTextureRect(sf::IntRect(1, 4, 14, 17));

	up[0].setTextureRect(sf::IntRect(29, 28, 14, 17)); // -- 
	up[1].setTextureRect(sf::IntRect(16, 28, 14, 17));
	up[2].setTextureRect(sf::IntRect(29, 28, -14, 17));
	up[3].setTextureRect(sf::IntRect(16, 28, -14, 17));

	jump[0].setTextureRect(sf::IntRect(15, 47, 15, 17)); // --
	jump[1].setTextureRect(sf::IntRect(15, 47, 15, 17));
	jump[2].setTextureRect(sf::IntRect(15, 47, -15, 17));
	jump[3].setTextureRect(sf::IntRect(15, 47, -15, 17));

	setSprite(right[0]);
	counter = 1;
	dualCollide = false;
	spriteType = 1;
	setTeam(1);
	setLives(3);
	setScore(0);
	pressAgain = false;
	justDie = 0;
}
Player::~Player()
{
}


// Pega os inputs do usuario
void Player::input()
{
	if (sf::Keyboard::isKeyPressed(sf::Keyboard::Right))
	{
		if (!getOnLadder())
		{
			setVX(2);
			counter++;
			if (counter % 4 == 0 && getOnPlat())
				spriteType = (counter % 3) + 1;
			else if (!getOnPlat())
				spriteType = 11;
		}
		else if (!pressAgain)
		{
			if (spriteType != 16)
			{
				if (spriteType == 15)
				{
					spriteType = 7;
					setX(getX() + 26);
				}
				else if (spriteType == 7 || spriteType == 8)
				{
					spriteType = 9;
					setX(getX() + 20);
				}
				else if (spriteType == 9 || spriteType == 10)
				{
					spriteType = 16;
					setX(getX() + 6);
				}
			}
			else if (!dualCollide && spriteType == 16)
			{
				spriteType = 11;
				setX(getX() + 15);
			}
			else if (dualCollide && spriteType == 16)
			{
				spriteType = 7;
				setX(getX() + 30);
			}
			pressAgain = true;
		}
	}
	else if (sf::Keyboard::isKeyPressed(sf::Keyboard::Left))
	{

		if (!getOnLadder())
		{
			setVX(-2);
			counter++;
			if (counter % 4 == 0 && getOnPlat())
				spriteType = (counter % 3) + 4;
			else if (!getOnPlat())
				spriteType = 13;
		}
		else if (!pressAgain)
		{
			if (spriteType != 15)
			{
				if (spriteType == 16)
				{
					spriteType = 9;
					setX(getX() - 6);
				}
				else if (spriteType == 9 || spriteType == 10)
				{
					spriteType = 7;
					setX(getX() - 20);
				}
				else if (spriteType == 7 || spriteType == 8)
				{
					spriteType = 15;
					setX(getX() - 26);
				}
			}
			else if (!dualCollide && spriteType == 15)
			{
				spriteType = 13;
				setX(getX() - 15);
			}
			else if (dualCollide && spriteType == 15)
			{
				spriteType = 9;
				setX(getX() - 10);
			}
			pressAgain = true;
		}
	}
	else
	{
		pressAgain = false;
	}

	if (sf::Keyboard::isKeyPressed(sf::Keyboard::Up))
	{
		if (getOnLadder())
		{
			setVY(-1);
			if (dualCollide)
				setVY(-3);
			counter++;
			if (counter % 3 == 0)
			{
				if (!dualCollide)
					if (spriteType == 7 || spriteType == 8)
						spriteType = (counter % 2) + 7;
					else
						spriteType = (counter % 2) + 9;
				else
					spriteType = (counter % 2) + 15;
			}
		}
	}
	else if (sf::Keyboard::isKeyPressed(sf::Keyboard::Down))
	{
		if (getOnLadder())
		{
			setVY(3);
			if (dualCollide)
				setVY(3);
			counter++;
			if (counter % 3 == 0)
			{
				if (dualCollide == false)
					if (spriteType == 7 || spriteType == 8)
						spriteType = (counter % 2) + 7;
					else
						spriteType = (counter % 2) + 9;
				else
					spriteType = (counter % 2) + 15;
			}
		}
	}

	if (sf::Keyboard::isKeyPressed(sf::Keyboard::Space))
	{
		if (getOnPlat())
		{
			setVY(-13); // Tamanho do pulo
			if (spriteType <= 3)
				spriteType = 11;
			else if (spriteType >= 4) // -Tentativa de mudar a sprit quando chegasse no chao-
				spriteType = 13;
		}
	}
	changeSprite();
}


void Player::changeSprite()
{
	switch (spriteType)
	{
	case 1:
		setSprite(right[0]);
		break;
	case 2:
		setSprite(right[1]);
		break;
	case 3:
		setSprite(right[2]);
		break;
	case 4:
		setSprite(left[0]);
		break;
	case 5:
		setSprite(left[1]);
		break;
	case 6:
		setSprite(left[2]);
		break;
	case 7:
		setSprite(up[0]);
		break;
	case 8:
		setSprite(up[1]);
		break;
	case 9:
		setSprite(up[2]);
		break;
	case 10:
		setSprite(up[3]);
		break;
	case 11:
		setSprite(jump[0]);
		break;
	case 12:
		setSprite(jump[1]);
		break;
	case 13:
		setSprite(jump[2]);
		break;
	case 14:
		setSprite(jump[3]);
		break;
	default:
		std::cout << "BUG - Sprit nao especificada." << std::endl;
	}
}


void Player::step()
{
	if (getOnLadder() && (spriteType != 15
		&& spriteType != 8 && spriteType != 7
		&& spriteType != 10 && spriteType != 9 && spriteType != 16))
		spriteType = 7;
	else if (getOnLadder() && (spriteType != 15 && spriteType != 8
		&& spriteType != 7 && spriteType != 10
		&& spriteType != 9 && spriteType != 16)
		&& (spriteType == 0 || spriteType == 1 || spriteType == 2))
		spriteType = 9;

	if (getOnPlat() && (spriteType >= 11 && spriteType <= 14))
	{
		if (spriteType == 11 || spriteType == 12)
			spriteType = 1;
		else if (spriteType == 13 || spriteType == 14)
			spriteType = 4;
	}

	GameObject::step();
}


void Player::doubleCollide(Ladder v, Ladder vv)
{
	dualCollide = true;

	if (counter % 3 == 0)
		spriteType = (counter % 2) + 13;
}


void Player::die()
{
	setLives(getLives() - 1);
	setX(0);
	setY(500);
}


//Getters
int Player::getLives()
{
	return lives;
}
int Player::getScore()
{
	return score;
}
int Player::getSpriteType()
{
	return spriteType;
}
bool Player::getDualCollide()
{
	return dualCollide;
}
int Player::getJustDie()
{
	return justDie;
}

//Setters
void Player::setLives(int i)
{
	lives = i;
}
void Player::setScore(int i)
{
	score = i;
}
void Player::setSpriteType(int i)
{
	spriteType = i;
}
void Player::setDualCollide(bool set)
{
	dualCollide = set;
}
void Player::setJustDie(int set)
{
	justDie = set;
}