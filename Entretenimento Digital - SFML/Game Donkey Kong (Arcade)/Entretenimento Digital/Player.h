#pragma once
#include "GameObject.h"
#include <SFML/Graphics.hpp>
#include "Ladder.h"

/*
	Classe: Player
	Descrição: Interface do Jogador
	Autores: David Cainã e Lucas Bergmann
*/

class Player : public GameObject
{
public:
	Player(double x, double y);
	~Player();

	void input();
	void changeSprite();
	void step();
	void doubleCollide(Ladder, Ladder);
	void die();
	void justDie0();
	int getLives();
	int getScore();
	int getSpriteType();
	bool getDualCollide();
	int getJustDie();
	void setLives(int i);
	void setScore(int i);
	void setSpriteType(int i);
	void setDualCollide(bool set);
	void setJustDie(int set);

private:
	int lives, score, counter, spriteType, justDie;
	bool dualCollide, pressAgain;
	sf::Sprite right[3], left[3], up[4], jump[4], grab[2]; // ERRO na animação 
	sf::Texture texture;
};