#pragma once
#include <SFML/Graphics.hpp>
#include <vector>
#include "GameObject.h"


/*
	Classe: Barril
	Descrição: Interface das plataformas
	Autores: David Cainã e Lucas Bergmann
*/

class Platform : public GameObject
{
public:
	Platform(int l, double x, double y, int type);
	~Platform();
	bool collision(GameObject * other);
	void drawPlat(sf::RenderWindow& window); // desenha
	void setSprite(sf::Sprite sprite);		 // set sprit da plat
	void step();
private:
	std::vector<sf::Sprite> sprites;
	sf::Texture texture;
	int length;
};