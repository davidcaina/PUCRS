#pragma once
#include <SFML\Graphics.hpp>
#include <typeinfo>

/*
	Classe: GameObj
	Descrição: Interface para o controle de colisões e objetos
	Autores: David Cainã e Lucas Bergmann
*/

class GameObject
{
public:
	GameObject(double, double);
	GameObject();
	virtual ~GameObject();
	void step();
	bool collision(GameObject *);
	void die();
	void useGravity();

	//sets
	void setSprite(sf::Sprite s);

	void setSpritePos(); // Coloca no start - fix
	void setType(int i);
	void setBB(sf::FloatRect);
	void setOnLadder(bool i);
	void setOnPlat(bool i);
	void setTouchingEnemy(bool i); // para morrer
	void setTeam(int i);

	void setX(double i);
	void setY(double i);
	void setVX(double  i);
	void setVY(double i);



	//gets
	double getGravity();
	int getType();
	sf::Sprite getSprite();
	sf::FloatRect getBB();
	bool getOnLadder();
	bool getOnPlat();
	bool getTouchingEnemy();
	int getTeam();

	double getX();
	double getY();
	double getVX();
	double getVY();


private:
	double x, y, vX, vY, gravity;
	int team, type;
	bool onLadder, onPlat, touchingEnemy;
	sf::Sprite sprite;
	sf::FloatRect boundingBox;
};