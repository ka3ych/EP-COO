# EP Computação Orientada a Objetos

## Shoot em UP Game
![image](https://github.com/user-attachments/assets/dc5b9c5c-729c-4e1c-8590-6297b707737d)


Embora funcione, seu código não foi elaborado seguindo bons princípios de orientação a objetos.
Apesar de escrito em Java, o código foi elaborado seguindo um estilo de programação procedural e,
mesmo considerando este estilo, não muito bem feito uma vez que há muito código redundante.
Existem portanto inúmeras oportunidades de melhoria do código. Os dois principais aspectos que
devem ser trabalhados no desenvolvimento deste projeto são:
- A aplicação de princípios de orientação a objetos, através da criação de uma boa estrutura de
interfaces e classes bem encapsuladas, definição de uma hierarquia de classes/interfaces
adequada, e uso de recursos como herança/composição.
- O uso das coleções Java ao invés de arrays para manter/gerenciar as entidades do jogo que
aparecem em multiplicidade (inimigos, projéteis, etc).

O código do jogo é composto por dois arquivos fonte: Main.java e GameLib.java. No primeiro
arquivo está implementada toda a lógica do jogo, enquanto o segundo implementa uma mini biblioteca
com recursos úteis no desenvolvimento de jogos: inicialização da interface gráfica, desenho de figuras
geométricas simples e verificação de entrada através do teclado.

O foco da reescrita de código deve ser a classe Main. Pode-se assumir que a classe GameLib é uma
biblioteca fechada à qual não se tem acesso ao código-fonte (como se realmente fosse uma biblioteca
feita por terceiros) e portanto ela não precisa ser modificada neste trabalho, apenas utilizada.
