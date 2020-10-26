# King's Valley com RMI

- Foi desenvolvimento como projeto final para a cadeira de Programação Distribuida, no curso de Ciências da Computação.

## Funcionamento da Aplicação:
O servidor deverá funcionar de modo que:
- Sejam suportadas 500 partidas, simultâneas, entre 2 jogadores registrados no servidor.
- Ao se registrar, deverá esperar que outro jogador também se registre (formando uma dupla para jogar).
- Primeiro jogador a se registrar inicia jogando.
- Responda a invocações remotas de métodos realizadas pelos clientes.
- Limites de tempo para determinados eventos.
  - 2 minutos pelo registro do segundo jogador.
  - 60 segundos pelas jogadas de cada jogador.
  - 60 segundos para “destruir” após um vendedor.
      
O cliente será responsável: pela interface com o usuário (que poderá ser tanto em modo texto quanto
em modo gráfico); e por executar as invocações remotas de métodos disponíveis no servidor, de
modo que os usuários possam jogar partidas consistentes.


### Entrega: `04/10/2018`.


----------------------------------------------------------------------------------

# Speculate com RMI

## Funcionamento da Aplicação:

O jogo consiste de 33 bolas, 1 dado e um tabuleiro (tal como o tabuleiro mostrado na Figura 1). Na
parte central do tabuleiro há espaço armazenar bolas e há também casas numeradas de 1 até 5 que
podem conter alguma das bolas e uma canaleta numerada com 6, que permite que bolas largadas nela
caiam na parte central do tabuleiro (juntamente com as demais bolas).


Uma partida de Speculate pode ser disputada por dois ou mais jogadores, mas para simplificar o
desenvolvimento será considerado que a partida ocorrerá sempre entre 2 jogadores. Inicialmente 3
bolas são colocadas nas casas 1, 3 e 5 do tabuleiro. As demais 30 bolas são divididas entre os
participantes (15 bolas para cada jogador).
O primeiro jogador a se habilitar (registrar) para a partida, inicia jogando. E o objetivo dos jogadores
será eliminar as bolas que cada um tem nas mãos. Antes de realizar suas jogadas, o jogador deve dizer
quantas vezes quer lançar o dado. O número de lançamentos deve variar de 1 até o número de bolas que
este jogador tem em suas mãos. Para cada lançamento de dado, o número obtido indica em que casa a
bola deve ser colocada. Se a casa estiver livre, o jogador coloca uma de suas bolas ali. Se a casa estiver
ocupada, o jogador deve pegar esta bola e colocá-la junto com as bolas que tem em suas mãos,
deixando a respectiva casa livre. Se o jogador tiver obtido o número 6, ele coloca uma de suas bolas na
respectiva cavidade e ela rola definitivamente para o centro do tabuleiro.
Depois de executar todos os seus lançamentos, o jogador passa a vez para o adversário, que procede
exatamente da mesma forma (definindo o número de lançamentos e executando estes lançamentos).
O vencedor será o primeiro jogador a ficar sem nenhuma bola.

### Entrega: `2 de maio de 2019`.
