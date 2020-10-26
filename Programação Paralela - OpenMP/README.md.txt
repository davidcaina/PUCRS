# Pattern Matching em OpenMP

## Aplicação:
- A fim de avaliar o ganho obtido em paralelização através de OpenMP, foi solicitado o desenvolvimento de uma solução para o problema Pattern Matching (Correspondência de Padrões). O problema em questão trata-se do ato de verificação da presença de um padrão em um determinado conjunto de dados. Esse problema trabalha com a utilização de dois parâmetros inicias, sendo eles: o padrão a ser procurado e o conjunto de dados, a onde as funções de pesquisa serão aplicadas. Explicando melhor o problema solicitado, existem, dentre as condições de desenvolvimento, dois casos especiais (Curingas), sendo eles: o “?”, que representa uma casa com qualquer caractere único e “*”, que representa uma sequência de caracteres (incluindo sequencias vazias). Ou seja, tendo-se o conjunto de dados “baaabab”, por exemplo, e os padrões: “*ba*ab”, “*baaa?ab”, “ba*a?” e “a*ab”, teríamos, respectivamente, os resultados: padrão encontrado, encontrado, encontrado e não encontrado. 

- Foi desenvolvimento como projeto final para a cadeira de Programação Paralela, no curso de Ciências da Computação.


