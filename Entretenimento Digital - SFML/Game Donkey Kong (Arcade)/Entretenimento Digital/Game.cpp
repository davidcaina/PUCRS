#include "Game.h"


/*
	Classe: Game
	Descrição: Desenvolvimento do  p/ o controle dos objetos e variabeis.
	Autores: David Cainã e Lucas Bergmann
*/


Game::Game()
{
	srand(time(NULL));

	 window.create(sf::VideoMode(520, 600), "Donkey Kong Arcade");
	
	p = new Player(10, 500);

	window.setFramerateLimit(60);
	//ShowWindow(GetConsoleWindow(), SW_HIDE);

	t.loadFromFile("spritesheet.png");
	// C:/Users/david/source/repos/Entretenimento Digital/Entretenimento Digital/
	peach.setTexture(t);
	kong.setTexture(t);

	deathWait = false;
	level = 1;

	lives.setFont(f);
	score.setFont(f);
	levelsPlayed = 1;
}


Game::~Game()
{
	clearAll();

	delete p;
}

/*
-------------------------
Clear board - arrays que guardam objetos
-------------------------
*/

void Game::clearAll()
{
	//Deletes all pointers in Plats
	for (int i = 0; i < plats.size(); i++)
		delete (plats[i]);
	plats.clear();

	//Deletes all pointers in Ladders
	for (int i = 0; i < ladders.size(); i++)
		delete (ladders[i]);
	ladders.clear();

	//Deletes all pointers in Chomps
	for (int i = 0; i < barrils.size(); i++)
		delete (barrils[i]);
	barrils.clear();

	//Deletes all pointers in Chomps
	for (int i = 0; i < fogos.size(); i++)
		delete (fogos[i]);
	fogos.clear();
}


/*
-------------------------
Roda o jogo
-------------------------
*/

void Game::play()
{
	buildLevelOne();

	while (window.isOpen())
	{
		if (p->getLives() >= 0)
		{
			if (deathWait)
			{
				Sleep(2000);
				deathWait = false;
			}

			nextStep();
			platformCheck();
			vineCheck();
			barrilCheck();
			playerCheck();
			window.display();
			window.clear(); // clear com a cor sf::Color(0,105,105,1 ) para tirar fundo das sprits

			window.draw(peach);
			window.draw(kong);

		}
		else
		{
			gameOver();
		}
		while (window.pollEvent(event))
		{
			if (event.type == sf::Event::Closed)
			{
				window.close();
				break;
			}
		}
	}
}


/*
-------------------------
Seta as novas variaveis
-------------------------
*/

void Game::nextStep()
{
	//Reset
	p->input();
	p->setOnPlat(false);
	p->setTouchingEnemy(false);

	for (int i = 0; i < barrils.size(); i++)
		barrils.at(i)->setOnLadder(false);
	for (int i = 0; i < fogos.size(); i++)
		fogos.at(i)->setOnLadder(false);

}

/*
-------------------------
Colisão plataforma
-------------------------
*/

void Game::platformCheck()
{
	for (int i = 0; i < plats.size(); i++)
	{
		if (plats.at(i)->collision(p))
			p->setOnPlat(true);

		for (int j = 0; j < barrils.size(); j++)
		{
			if (!barrils.at(j)->getOnLadderAgain())
				plats.at(i)->collision(barrils.at(j));
		}
		for (int j = 0; j < fogos.size(); j++)
		{
			if (!fogos.at(j)->getOnLadderAgain())
				plats.at(i)->collision(fogos.at(j));
		}
		plats.at(i)->drawPlat(window);
	}
}

/*
-------------------------
Colisão escada - meio bugado
-------------------------
*/

void Game::vineCheck()
{
	if (ladders.at(0)->allLaders(ladders, p) == 0)
	{
		p->setOnLadder(false);
		p->setDualCollide(false);
	}
	else if (ladders.at(0)->allLaders(ladders, p) == 1)
	{
		p->setOnLadder(true);
		p->setDualCollide(false);
	}
	else if (ladders.at(0)->allLaders(ladders, p) == 2)
	{
		p->setDualCollide(true);
		p->setOnLadder(true);
	}
	for (int i = 0; i < ladders.size(); i++)
	{
		ladders.at(i)->drawLadder(window);
	}

}


/*
-------------------------
Colisão Inimigo
-------------------------
*/

void Game::barrilCheck()
{
	//Check collision with the chomps
	for (int i = 0; i < barrils.size(); i++)
	{
		ladders.at(0)->allLaders(ladders, barrils.at(i));
		barrils.at(i)->step();
		if (barrils.at(i)->collision(p))
			p->setTouchingEnemy(true);
		window.draw(barrils.at(i)->getSprite());
	}

	/*
	for (int i = 0; i < ochomps.size(); i++)
	{
		vines.at(0)->allVines(vines, ochomps.at(i));
		ochomps.at(i)->step();
		if (ochomps.at(i)->collision(p))
			p->setTouchingEnemy(true);
		window.draw(ochomps.at(i)->getSprite());
	}
	*/
}


/*
-------------------------
Colisão player
-------------------------
*/
void Game::playerCheck()
{
	if (p->getTouchingEnemy() || p->getY() > window.getSize().y)
	{
		p->die();
		//Deletes all pointers in Chomps
		for (int i = 0; i < barrils.size(); i++)
			delete (barrils[i]);
		barrils.clear();

		//Deletes all pointers in Chomps
		for (int i = 0; i < fogos.size(); i++)
			delete (fogos[i]);
		fogos.clear();

		buildBarril();

		deathWait = true;
	}
	else
	{
		p->step();
		window.draw(p->getSprite());
	}

	if (p->getBB().intersects(peach.getGlobalBounds()))
	{
		switch (level)
		{
		case 1:
			levelsPlayed++;
			buildLevelTwo();
			Sleep(2000);
			break;
		case 2:
			levelsPlayed++;
			buildLevelOne();
			Sleep(2000);
			break;
		}
		p->setScore(p->getScore() + 500);
	}

	lives.setPosition(5, 0);
	lives.setString("Lives: " + std::to_string(p->getLives()));
	window.draw(lives);
	score.setPosition(5, 15);
	score.setString("Score:  " + std::to_string(p->getScore()));
	window.draw(score);
}


/*
-------------------------
Seta fim do jogo
-------------------------
*/
void Game::gameOver()
{
	lives.setString("Game Over");
	lives.setScale(5, 5);
	lives.setPosition(window.getSize().x / 4 - 25, window.getSize().y / 4);
	window.draw(lives);
	lives.setString("Space to Play Again.");
	lives.setScale(3, 3);
	lives.setPosition(window.getSize().x / 8 - 26, window.getSize().y / 4 + 100);
	window.draw(lives);
	window.display();
	window.clear();

	if (sf::Keyboard::isKeyPressed(sf::Keyboard::Space))
	{
		p->setLives(3);
		p->setScore(0);
		lives.setScale(1, 1);
		clearAll();
		window.clear();
		levelsPlayed = 1;
		buildLevelOne();
		p->setX(10);
		p->setY(500);
	}
}


//Quantidade inimigos
void Game::buildBarril()
{
	//blue bois
	for (int i = 0; i < levelsPlayed; i++)
	{
		barrils.push_back(new Barril(40, 50));  // add barril no mapa
		barrils.push_back(new Barril(40, 50));
		barrils.push_back(new Barril(40, 50));
		//ochomps.push_back(new OChomp(200, 100));
	}
}


//Lev 1
void Game::buildLevelOne()
{
	clearAll();

	plats.push_back(new Platform(10, 12, 560, 1)); // 1 andar
	plats.push_back(new Platform(15, 15, 100, 1)); // andar peach
	plats.push_back(new Platform(5, 300, 450, 1)); // 1.5 andar
	plats.push_back(new Platform(10, 12, 400, 1)); // 2 andar
	plats.push_back(new Platform(15, 250, 300, 1)); // 3 andar
	plats.push_back(new Platform(6, 1, 300, 1)); // 3 andar
	plats.push_back(new Platform(15, 130, 200, 1)); // 4 andar

	// ------------ Escadas
	ladders.push_back(new Ladder(60, 265, 200));
	ladders.push_back(new Ladder(8, 165, 230));
	ladders.push_back(new Ladder(5, 100, 180));
	ladders.push_back(new Ladder(7, 390, 50));

	buildBarril(); // inimigos

	// mario
	p->setX(15);
	p->setY(500);

	
	peach.setTextureRect(sf::IntRect(0, 242, 20, 22)); // peach
	peach.setScale(2, 2);
	peach.setPosition(180, 58);

	kong.setTextureRect(sf::IntRect(110, 227, 45, 45)); // kong
	kong.setScale(2, 2);
	kong.setPosition(50, 33);

	level = 1;
}


//Lev 2
void Game::buildLevelTwo()
{
	clearAll();

	plats.push_back(new Platform(5, 0, 562, 1));
	plats.push_back(new Platform(2, 185, 524, 1));
	plats.push_back(new Platform(2, 278, 543, 1));
	plats.push_back(new Platform(2, 352, 524, 1));
	plats.push_back(new Platform(2, 445, 506, 1));
	plats.push_back(new Platform(4, 80, 393, 2));
	plats.push_back(new Platform(3, 80, 280, 2));
	plats.push_back(new Platform(5, 415, 337, 2));
	plats.push_back(new Platform(5, 305, 188, 2));
	plats.push_back(new Platform(13, 0, 168, 2));
	plats.push_back(new Platform(2, 80, 93, 2));

	ladders.push_back(new Ladder(13, 1, 189));
	ladders.push_back(new Ladder(13, 57, 189));

	ladders.push_back(new Ladder(2, 112, 301));
	ladders.push_back(new Ladder(4, 112, 413));

	ladders.push_back(new Ladder(9, 200, 189));
	ladders.push_back(new Ladder(6, 280, 189));

	ladders.push_back(new Ladder(10, 335, 206));
	ladders.push_back(new Ladder(8, 391, 206));

	ladders.push_back(new Ladder(7, 447, 112));
	ladders.push_back(new Ladder(7, 503, 112));

	ladders.push_back(new Ladder(3, 447, 356));
	ladders.push_back(new Ladder(3, 503, 356));

	ladders.push_back(new Ladder(2, 180, 80));

	buildBarril();

	peach.setTextureRect(sf::IntRect(189, 182, 16, 16)); // peach
	peach.setScale(2, 2);
	peach.setPosition(87, 58);
	buildBarril();

	kong.setTextureRect(sf::IntRect(0, 242, 20, 22)); // kong
	kong.setScale(2, 2);
	kong.setPosition(180, 58);

	p->setX(10);
	p->setY(500);

	level = 2;
}
