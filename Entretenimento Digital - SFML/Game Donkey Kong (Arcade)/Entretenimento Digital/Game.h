#pragma once
#include <SFML/Graphics.hpp>
#include "Player.h"
#include "Platform.h"
#include "Ladder.h"
#include "Barril.h"
#include "Fogo.h"
#include <iostream>
#include <vector>
#include <Windows.h>
#include <string>
#include <SFML\Audio.hpp>
#include <time.h>

/*
	Classe: Game
	Descrição: Interface para o controle dos objetos e variabeis (Game)
	Autores: David Cainã e Lucas Bergmann
*/

class Game
{
public:
	Game();
	~Game();
	void play(); // Start
	void nextStep();
	void platformCheck();
	void vineCheck();
	void barrilCheck();
	void playerCheck();
	void gameOver();
	void buildLevelOne();
	void buildBarril();
	void buildLevelTwo();
	/*

	void buildLevelThree(); // não feito
	void buildLevelFour();  // não feito 
	*/
	void clearAll();
private:
	Player* p;
	sf::RenderWindow window;
	std::vector<Platform*> plats;
	std::vector<Ladder*> ladders;
	std::vector<Barril*> barrils;
	std::vector<Fogo*> fogos;
	sf::Font f; // Erro
	sf::Text lives, score;
	sf::Event event;
	sf::Sprite peach;
	sf::Sprite kong;
	sf::Texture t;
	bool deathWait;
	int level, levelsPlayed;
};

