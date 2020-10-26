#pragma once
#include <SFML/Graphics.hpp>
#include <vector>
#include "GameObject.h"

/*
	Classe: Escada
	Descrição: Interface das escadas
	Autores: David Cainã e Lucas Bergmann
*/

class Ladder : public GameObject
{
public:
	Ladder(int l, double x, double y);
	~Ladder();
	bool collision(GameObject * other);
	void drawLadder(sf::RenderWindow & window);
	void setSprite(sf::Sprite sprite);
	int allLaders(std::vector<Ladder*> vines, GameObject * other);
private:
	std::vector<sf::Sprite> sprites;
	sf::Texture texture;
	int length;
	sf::FloatRect boundingBox;
};