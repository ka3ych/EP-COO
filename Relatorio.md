## 1. Código original
O código original tinha diversos problemas:
- Toda a lógica concentrada na classe main.
- Redundância excessiva com repetição de lógica para colisões, atualização de estados e renderização de entidades
- Gestão manual de arrays.
- Sem encapsulamento.
- Ausência de hierarquia.

## 2. Nova estrutura

![My First Board](https://github.com/user-attachments/assets/38c9fd82-f7df-4c1b-b19f-184c01a0cdd6)
![My First Board(1)](https://github.com/user-attachments/assets/042b16b8-acce-4665-935d-d94d0cc0f04f)


fases:
  Armazena a configuração da fase, levando em consideração as especifidades dadas pelo enunciado do EP.

GameObjects:
- SpaceShips: Player, Enemy (tipos 1/2), Bosses <br />
  Em 'SpaceShips' os inimigos e o próprios jogador foram interpretados como 'naves', que dividem caracteristicas em comum como por exemplo ter movimentação, explodir e atirar. <br />
  Player:   ![Captura de tela de 2025-07-06 18-55-49](https://github.com/user-attachments/assets/c54cb5a4-9adf-49d9-a696-fed0b02b739d) <br />
  Enemy tipo 1:   ![Captura de tela de 2025-07-06 18-57-24](https://github.com/user-attachments/assets/5a755098-a662-45e7-95bf-e2a261dec34b) <br />
  Enemy tipo 2:   ![Captura de tela de 2025-07-06 18-57-30](https://github.com/user-attachments/assets/1b884aea-320b-4efa-846a-f5b78a0e1630) <br />
  Boss 1:   ![Captura de tela de 2025-07-06 18-59-23](https://github.com/user-attachments/assets/239443fe-c8e2-4759-a591-a93e091ab736) <br />
  Boss 2:   ![Captura de tela de 2025-07-06 19-00-08](https://github.com/user-attachments/assets/85d13133-e062-49b8-85aa-d3b1174c26db) <br />
  Boss 3:   ![Captura de tela de 2025-07-06 19-00-41](https://github.com/user-attachments/assets/6390ae59-10fb-4a0e-8a8b-e4db2dd9f215) <br />
<br />

- Projectiles: PlayerProjectile, EnemyProjectile
  Os projeteis dividem caracteristicas em comum entre si, só que tem a diferença que o do jogador danifica as 'SpaceShips' inimigas e os inimigos danificam a 'SpaceShip' do jogador.


  
- PowerUps: ShieldPowerUp, TripleShotPowerUp
  Poderes para o jogador conseguir tomar um hit sem morrer ou para que ele atire 3 projeteis ao mesmo tempo.

  ![Captura de tela de 2025-07-06 19-02-56](https://github.com/user-attachments/assets/e5c2b824-8b92-4c75-8202-1a0e97378920)
  ![Captura de tela de 2025-07-06 19-02-08](https://github.com/user-attachments/assets/32be2c53-5087-4069-b42e-4d240bafe84d)


- BackgroundObjects: Stars, HealthBar
  Estrelas e barra de vida.

![Captura de tela de 2025-07-06 19-03-44](https://github.com/user-attachments/assets/5af78ca2-6792-4755-8659-2ca037220521)


- Colliders: Interface para colisão
  Para as colisões.

GameLib (não modificada)

## 3. Coleções
  Foi usada coleções para substituir as arrays originais que eram estáticas, assim consegue melhorar e muito a qualidade do código, com exclusões mais seguras e gerenciamento seguro.
  Exemplo do código original:
  // Declaração de arrays
  int [] projectile_states = new int[10];
  double [] projectile_X = new double[10];
  double [] projectile_Y = new double[10];
  double [] projectile_VX = new double[10];
  double [] projectile_VY = new double[10];
  
  // Inicialização
  for(int i = 0; i < projectile_states.length; i++) 
      projectile_states[i] = INACTIVE;
  
  // Atualização de estado
  for(int i = 0; i < projectile_states.length; i++) {
      if(projectile_states[i] == ACTIVE) {
          if(projectile_Y[i] < 0) {
              projectile_states[i] = INACTIVE;
          } else {
              projectile_X[i] += projectile_VX[i] * delta;
              projectile_Y[i] += projectile_VY[i] * delta;
          }
      }
  }
  
  // Adição de novo projétil
  if(currentTime > player_nextShot) {
      int free = findFreeIndex(projectile_states);
      if(free < projectile_states.length) {
          projectile_X[free] = player_X;
          projectile_Y[free] = player_Y - 2 * player_radius;
          projectile_VX[free] = 0.0;
          projectile_VY[free] = -1.0;
          projectile_states[free] = ACTIVE;
          player_nextShot = currentTime + 100;
      }
  }

  Esse mesmo exemplo atualmente:
  List<PlayerProjectile> playerProjectiles = new ArrayList<>();
  
  // Atualização com iterator
  Iterator<PlayerProjectile> iter = playerProjectiles.iterator();
  while(iter.hasNext()) {
      PlayerProjectile p = iter.next();
      if(p.isStateTrue(ACTIVE)) {
          if(p.getY() < 0) {
              iter.remove(); // Remoção segura
          } else {
              p.move(delta); // Comportamento encapsulado
          }
      }
  }
  
  // Adição de novo projétil
  if(currentTime > player.getNextShoot()) {
      playerProjectiles.add(new PlayerProjectile(
          player.getX(),
          player.getY() - 2 * Player.PLAYER_RADIUS,
          0.0,
          -1.0
      ));
      player.setNextShoot(currentTime + 100);
  }

## 4. Novas funcionalidades e fases
  Power-ups:
- ShieldPowerUp:	Escudo protetor (absorve 1 hit)	| Duração: até receber um hit | Aparência: círculo ciano
- TripleShotPowerUp:	Disparo triplo simultâneo | Duração	15s | Aparência:	3 projéteis alinhados
  Bosses:
  Todos os chefes compartilham uma HealthBar.
  Boss 1: Aparência: circulo vermelho | Movimento: segue pontos pré definidos e tem uma chance de ir para cada um | Ataque: 3 tiros que vão para baixo
  Boss 2: Aparência: Quadrado da cor magenta | Movimento: ele se 'teleporta' aleatoriamente, o player tem uma representação visual antes dele se eleportar| Ataque: 3 tiros que vão para baixo.
  Boss 3: Aparência: Um triangulo de 3 lados com a ponta para baixa, amarelo | Movimento: Circular que altera entre horário e antihorário aleatoriamente| Ataque: Ataca em todas as direções ao mesmo tempo

  Fases:
  Em todas as fases, após matar o chefe o jogador é redirecionado para a próxima fase.
- Fase 1 à 3: Com inimigos tipo 1 e 2 até aparecer o Boss, quando o boss é morto vai para a próxima fase. 
- Freeplay: após matar o último boss é praticamente 'infinita' (muito grande) e tem apenas inimigos tipo 1 e 2.

## 5. Conclusão, resultados e reflexão
Após finalizar, concluimos que no código original em comparação ao nosso conseguimos:
- Redução da complexidade e dos ciclos na classe main original.
- Maior coesão de código.
- Adição de 3 novas mecânicas (power-ups, chefes e fases).

Com o uso de Orientação à Objetos:
- Extensibilidade com possíveis adições de novos power-ups/inimigos via herança.
- Modificações localizadas em classes específicas.
- Componentes reutilizaveis (como HealthBar e Collider).
- Código organizado com as classes.
- Classes isoladas permitem testes unitários.

Desafios enfrentados:
  No início, um dos maiores desafios foi encontrar as redundâncias e como as substituir para que o código fosse mais eficiente, separamos com essas 4 classes no início:
- Player
- Inimigos
- Projeteis
- Estrelas
Considerando que o código original estava com o nome dos elementos em inglês, decidimos seguir nesse formato, para entender de forma mais fácil o estado do nosso código atual e comparar com o original.
Só depois, nós percebemos que 'Player' e 'Inimigos' dividem muitas caracteristicas em comum, depois foi criada a 'SpaceShip' e em 'Stars' percebemos que seria mais fácil utilizar como 'BackgroundObjects' para caso fossemos decidir no futuro mudar algo e interpretar como backgroud.
No método principal foi reduzido a quantidade de variaveis e foi começado a se utilizar coleções para deixar o código melhor.

Depois da estruturação inicial, foi adicionado sem muitas dificuldades a classe de 'PowerUps', entretanto, em algum momento o jogo começou a congelar sozinho, até que percebemos que nós deveriamos colocar um limite pro delta das explosões, o limitando entre 0 e 1.

Após isso, colocamos para carregar as fases e fizemos o 'Boss1' para testar, entretanto o 'Boss1' não estava nascendo, ai percebemos que não colocamos o tempo dos inimigos e do chefe separados, estavamos tratando como todos iguais.
