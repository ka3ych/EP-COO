# EP Computação Orientada a Objetos
Dérick dos Santos Arriado 15636042
Júlio Arroio Silva 15466241
Karina Yang Chen 15466658

* [Shoot em UP Game](#shoot-em-up-game)
* [Derick 20/06/2025](#dérick-yo-20062025)
* [POWERUPS](#powerups)

## Shoot em UP Game
![image](https://github.com/user-attachments/assets/dc5b9c5c-729c-4e1c-8590-6297b707737d)


Embora funcione, seu código não foi elaborado seguindo bons princípios de orientação a objetos.
Apesar de escrito em Java, o código foi elaborado seguindo um estilo de programação procedural e,
mesmo considerando este estilo, não muito bem feito uma vez que há muito código redundante.
Existem portanto inúmeras oportunidades de melhoria do código. Os dois principais aspectos que
devem ser trabalhados no desenvolvimento deste projeto são:
- A **aplicação de princípios de orientação a objetos**, através da criação de uma boa estrutura de
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
feita por terceiros) e, portanto, ela não precisa ser modificada neste trabalho, apenas utilizada.


## Atualizações feitas no código
Para uma melhor organização das classes, foi criada uma nova Pasta para o pacote "game"
- classes referentes às entidades do jogo serão agrupadas nesse pacote
  - Player
  - Projectile
  - Enemy
  - BackgroundElement

Ajuda na organização das classes, além de realizar melhor o controle com os modificadores de acesso, como public e private, por exemplo

## Dérick YO 20/06/2025
Galera, não entendi se devia criar mais pacotes então fiz tudo na main, yo

A primeira coisa a se notar ao analisar o código original da main é a sua extensão, possuindo 669 linhas, por conta disso decididmos dividir em 3 partes:
1a parte: tirar conteúdo desnecessário do código original e fazer pequenas melhorias
2a parte: melhorar a base do código para colocar os power-ups, chefes de fase e fases
3a parte: implementar os power-ups, chefes de fase e fases

  1a parte:
  A 1a coisa a se notar, é que tudo está na 'public class Main' e isso é muito ruim, pois tudo está em apenas uma classe, tem vários dados espalhados e que se repetem, e melhoraria muito a coesão se a gente tentasse separar e colocar sentido em alguns números que aparecem 'jogados'.

A 1a coisa coisa feita foi atribuir constantes para algumas propriedades que se repetem para para facilitar ainda mais a coesão

    /* constantes para temporização e duração de explosões    */
    public static final long PLAYER_EXPLOSION_DURATION = 2000;
    public static final long ENEMY_EXPLOSION_DURATION = 500;
    
    public static final long PLAYER_EXPLOSION_DURATION = 2000;
    public static final long ENEMY_EXPLOSION_DURATION = 500;
    
    public static final double PLAYER_INITIAL_VELOCITY = 0.25;
    public static final double PLAYER_RADIUS = 12.0;
    public static final double ENEMY1_RADIUS = 9.0;
    public static final double ENEMY2_RADIUS = 12.0;
    public static final double PROJECTILE_RADIUS = 2.0;
    public static final double COLLISION_FACTOR = 0.8;

Por mais que alguns desses só sejam usadas uma única vez (tal como ENEMY_EXPLOSION_DURATION, que só fora utilizado na linha 295 do código original com valor 500) ajuda muito para entender rapidamente o que está acontecendo no código.

Depois foi criada classes, para facilitar a alteração e manipulação do código, no jogo, percebemos que temos esses 4 elementos:
- Player
- Inimigos
- Projeteis
- Estrelas
Considerando que o código original estava com o nome dos elementos em inglês, decidimos seguir nesse formato, para entender de forma mais fácil o estado do nosso código atual e comparar com o original

Na classe de Player(jogador), foi inserito o estado, posicao, tempo para explosao e proximo momento permitido para disparo

    // jogador (entidade controlada pelo usuário)
    static class Player {
        int state = ACTIVE; // estado inicial
        double x, y; // posicao
        double explosion_start, explosion_end; // para o tempo de explosão
        long nextShot; // proximo momento permitido para disparo
    }
Tentamos manter em todas as classes nomes de variaveis usadas e repetidas no código original

Na classe inimigo, é bem interessante notar que há 2 tipos de inimigos, as 'esferas cianas' (tipo1) e a 'cobra magenta' (tipo2)

    // inimigo
    static class Enemy {
        int type; // tipo 1 ou 2
        int state = INACTIVE; 
        double x, y; // posicao
        double v; // velocidade escalar
        double angle; // angulo da direcao
        double vr; // velocidade rotacao
        long nextShoot; // prox momento que pode disparar
        double explosionStart, explosionEnd; // temporizador
    }
Classe projetil

    // projétil (disparado pelo jogador ou inimigos)
    static class Projectile {
        int state = INACTIVE;
        double x, y; // posicao
        double vx, vy; // velocidade
    }

Classe estrelas

    // estrelas
    static class Stars {
        double x, y; // posicao
    }

No código original, havia muitas arrays, e a gente fez o possível para mudar para coleções dinamicas com encapsulamento

Outra coisa que se repetia muito, de modo desnecessario, era verificar explosão
Original 217 - 225, 233 - 241, 247 - 255, 264 - 276, que seguia essa estrutura básica, dentro de for com o tamanho da coisa atingida:

    for(int i = 0; i < COISA_states.length; i++){
    double dx = A_X - B_X;
    double dy = A_Y - B_Y;
    double dist = Math.sqrt(dx * dx + dy * dy);
    
    if(dist < (RAIO_A + RAIO_B) * 0.8) {
        // Colisão detectada!
      }
    }

Isso é muito redundante de se ficar repetindo, então foi criada esse boolean

    /* verifica colisão entre duas entidades */
    private static boolean checkCollision(double x1, double y1, double r1, double x2, double y2, double r2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        double dist = Math.sqrt(dx * dx + dy * dy);
        return dist < (r1 + r2) * COLLISION_FACTOR;
    }

No método principal foi reduzido a quantidade de variaveis e foi começado a se utilizar coleções para deixar o código melhor
No código atual, é das linhas 100 - 112 as variaveis e as coleções são das linhas 116 - 120
Foi usada, nessa linha especifica a ArrayList, agora será abordado as as 4 importações do novo código e o motivo delas

    import java.util.ArrayList;
  Implementar listas dinâmicas para melhor gerenciamento dos elementos e entidades do jogo
  
    import java.util.Iterator;
  Percorrer coleções e remover elementos de forma segura
  
    import java.util.List;
  Interface comum
  
    import java.util.Random;
  Valores mais aleatorios para inimigos, velocidades, intervalos de disparo etc

Após isso também foram inicializas as estrelas de forma randomica, nas linhas 124 - 137

Agora é a parte do loop principal
Alteramos usando algumas das constantes que colocamos no inicio e o boolean checkCollision para verificar as explosões, usando só 2 loops, um do jogador com os projeteis inimigos e outro do jogador com os inimigos
Linhas 184 - 227

Em 'Atualizações de estados', na parte de projeteis foi usado iteradores para ter remoção segura e parou deter verificações manuais de estado
Linhas 235 - 270

No de comportamento de inimigos, juntou para ter os dois tipos de inimigos (não fica tão redundante), iterador para remoção e tudo fica mais fácil de entender pois estão no mesmo fluxo.
Linhas 273 - 374

Na parte de inimigos serem 'lançados' (spawnnados), graças aos esforços passados foi substituido as partes de arrays para criação direta de objetos com uso de listas dinamicas, a forma de manutenção é por temporizadores
Linhas 378 - 422

A parte de explosão do player só mudamos para que seja if( && ) que não tem sentido ter um if dentro de outro se pode usar &&

Agora em 'Verificando entrada do usuário (teclado)', só foi deixado tudo mais centralizado com criação direta dos projeteis (não precisa mais dos indices livres pois não está sendo usada arrays) e constantes para valores (definidas no inicio).
Linhas 434 - 457

Em 'Desenhor da cena', que é a parte de renderização de tudo, foi separado as camadas de renderização, usando as classes para facilitação, uso de loops for para ser mais simples, não teve muitas alterações
Linhas 475 - 548

## PowerUps

### Adição de Métodos Utilitários ao GameLib.java

Para aprimorar as capacidades gráficas e de detecção do jogo, dois novos métodos estáticos foram implementados na classe `GameLib.java`

`drawText(String text, double x, double y)`: Este método permite que textos sejam renderizados diretamente na tela do jogo, facilitando a exibição de informações visuais como rótulos de power-ups, mensagens de feedback, ou indicadores de interface.

`checkCollision(double x1, double y1, double r1, double x2, double y2, double r2)`: Um método fundamental para a lógica de jogo, responsável por determinar se dois objetos circulares (representados por suas coordenadas centrais e raios) estão em colisão. Este método é crucial para a interação entre o jogador, inimigos, projéteis e power-ups."

### Adição de Power-ups: Aprimorando a Capacidade do Jogador
Para enriquecer a jogabilidade e introduzir elementos de recompensa e variação dinâmica, foram implementados três novos arquivos de classe relacionados aos power-ups, todos localizados sob o pacote `GameObjects.PowerUps`:

1. `PowerUp.java` (Base):

    Servir como base para todos os tipos de power-ups do jogo. Define características e comportamentos comuns como posição, raio de colisão, estado, movimento, tempo de vida na tela, e o método abstrato applyEffect() que será implementado pelas subclasses para aplicar o bônus específico ao jogador. Também inclui a lógica de detecção de colisão com o jogador e a desativação após a coleta.
  
2. `ShieldPowerUp.java`
   
    Consiste num power-up de defesa que concede ao jogador um escudo temporário. Ele, ao ser coletado, ativa um bônus de proteção por um período, absorvendo um impacto inimigo que, de outra forma, causaria dano à nave.
    
    Melhora o comportamento da nave ao coletar o ShieldPowerUp, a nave do jogador adquire uma camada de defesa temporária, representada visualmente por um círculo ciano ao seu redor. Funcionalmente, isso permite que o jogador resista a um único hit de inimigos ou projéteis inimigos sem perder pontos de vida, aumentando sua capacidade de sobrevivência.


    <figure>
      <img src="escudo.png" alt="Aparecimento do escudo para a nave" style="width:100%;">
      <figcaption style="text-align:center;">Representação visual do escudo de proteção não coletado pela nave do jogador.</figcaption>
    </figure>

3. `TripleShotPowerUp.java`
    
   Consiste num power-up ofensivo que aprimora o poder de fogo da nave do jogador, permitindo que ela dispare três projéteis simultaneamente por um período limitado.

    A coleta do TripleShotPowerUp eleva a capacidade ofensiva da nave. Em vez de um único projétil central, a nave passa a disparar um feixe de três projéteis, aumentando a área de cobertura e a probabilidade de atingir inimigos. Isso permite ao jogador atacar inimigos de forma mais eficiente.


    <figure>
      <img src="disparo-triplo.png" alt="Disparo triplo" style="width:100%;">
      <figcaption style="text-align:center;">Representação do disparo triplo da nave.</figcaption>
    </figure>

### Detalhamento das Alterações e Integração dos Power-ups
Para a implementação dos power-ups foram realizadas alterações em diversas classes, visando a modularidade e a clareza do código.

1. **Constantes Adicionadas**
   - `POWERUP_RADIUS`: raio para o desenho de todos os power-ups
   - `DEFAULT_EFFECT_DURATION` definido internamente nas classes de power-ups, permitindo que cada powerUp tenha uma duração personalizada

2. **Classe `Player.java`**
  
   - Atributos booleanos (`hasShield`, `hasTripleShot`) e temporizadores (`shieldEndTime`, `tripleShotEndTime`) controlam a ativação e a duração de cada power-up

   - métodos (`activateShield`, `activateTripleShot`) são invocados quando o jogador coleta um power-up, ligando a funcionalidade
  
   - `update()` aprimorado para monitorar o tempo e desativar os efeitos automaticamente quando ele acaba

   - `hit()` alterado para que o escudo absorva um impacto, evitando dano à nave. A lógica de `shoot()` foi atualizada para, quando o projétil triplo ativo, criar e lançar três projéteis em vez de um apenas

3. **Classe `Main.java`**
  
    - `nextPowerUpSpawn` e uma nova lógica foram adicionados para determinar o momento e o tipo de power-up a ser gerado aleatoriamente na tela

    - Uma List para PowerUps criada para guardar e processar todas as instâncias ativas. ELa é iterada para mover os power-ups, verificar suas colisões com o jogador e remover aqueles que saem da tela ou expiram sem serem coletados

     - O desenho dos PowerUps foi integrado ao processo de renderização da cena, garantindo sua visibilidade

  Mudanças:
Imports:
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

main:
Função auxiliar para carregar fases linha 58 - 70:
    // para carregar as fases
    private static List<String[]> carregarConfiguracoes(String arquivoFase) throws IOException {
    List<String[]> eventos = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new FileReader(arquivoFase))) {
        String linha;
        while ((linha = reader.readLine()) != null) {
            if (linha.trim().isEmpty()) continue;
            String[] partes = linha.split(" ");
            eventos.add(partes);
        }
    }
    return eventos;
}

Linha 92 long bossSpawnTime = 20000; tirada

Em coleções linha 103-111 para carregar as fases:
        List<String[]> eventosDaFase = new ArrayList<>();
        int proximoEvento = 0;

        try{
        eventosDaFase = carregarConfiguracoes("fase1.txt");
        }catch (IOException e){
            System.out.println("Erro ao carregar a fase: " + e.getMessage());
            e.printStackTrace();
        }

175 - 217 para programar as coisas necessarias na fase:
            while (proximoEvento < eventosDaFase.size()) {
                String[] evento = eventosDaFase.get(proximoEvento);
                String tipo = evento[0];
                int tempo = Integer.parseInt(evento[2]);

                if (currentTime < tempo) break;

                if ("INIMIGO".equalsIgnoreCase(tipo)) {
                    int tipoInimigo = Integer.parseInt(evento[1]);
                    double x = Double.parseDouble(evento[3]);
                    double y = Double.parseDouble(evento[4]);

                    if (tipoInimigo == 1) {
                        Enemy1 e = new Enemy1(x, y, 0.2, (3 * Math.PI) / 2, 0.0);
                        e.setNextShoot(currentTime + 500);
                        enemies.add(e);
                        enemies1.add(e);
                        colideComPlayer.add(e);
                    } else if (tipoInimigo == 2) {
                        Enemy2 e = new Enemy2(x, y, 0.42, (3 * Math.PI) / 2, 0.0);
                        enemies.add(e);
                        enemies2.add(e);
                        colideComPlayer.add(e);
                    }
                } else if ("CHEFE".equalsIgnoreCase(tipo)) {
                    int tipoChefe = Integer.parseInt(evento[1]);
                    int vida = Integer.parseInt(evento[2]);
                    double x = Double.parseDouble(evento[4]);
                    double y = Double.parseDouble(evento[5]);

                    if (tipoChefe == 1) {
                        Boss1 boss = new Boss1(x, y, 0.15, (3 * Math.PI) / 2, 0.0);
                        boss.setVida(vida); // Assumindo método setVida existe
                        enemies.add(boss);
                        bosses1.add(boss);
                        colideComPlayer.add(boss);
                    }
                }

                proximoEvento++;
            }

Foi necessário alterar, para conseguir dar vida em boss.setVida, em Enemy para ter getter e setter:
    // setter de vida
    public void setVida(int vida) {
        this.healthPoints = vida;
        this.initialHealth = vida;
    }

    // getter de vida
    public int getVida() {
        return this.healthPoints;
    }
E em Boss para ter o setVida:
        public void setVida(int vida) {
        this.healthPoints = vida;
        this.initialHealth = vida; // importante manter proporção correta na barra
    }

Linha 406-420 excluida:
            /*if(currentTime > bossSpawnTime && !bossHasSpawned) {
                // Spawn do boss
                Boss1 boss1 = new Boss1(
                    GameLib.WIDTH / 6,
                    GameLib.HEIGHT * 0.5,
                    0.15,
                    (3 * Math.PI) / 2,
                    0.0
                );
                
                enemies.add(boss1);
                bosses1.add(boss1);
                colideComPlayer.add(boss1);
                bossHasSpawned = true;
            }*/
/////////////////////////////////////// Dérick 06/07
## 1. Código original
O código original tinha diversos problemas:
- Toda a lógica concentrada na classe main.
- Redundância excessiva com repetição de lógica para colisões, atualização de estados e renderização de entidades
- Gestão manual de arrays.
- Sem encapsulamento.
- Ausência de hierarquia.

## 2. Nova estrutura

[IMAGEM DO FLUXOGRAMA]

fases:
  Armazena a configuração da fase, levando em consideração as especifidades dadas pelo enunciado do EP.

GameObjects:
- SpaceShips: Player, Enemy (tipos 1/2), Bosses
  Em 'SpaceShips' os inimigos e o próprios jogador foram interpretados como 'naves', que dividem caracteristicas em comum como por exemplo ter movimentação, explodir e atirar.
  Player:   [IMAGEM DO PLAYER]
  Enemy tipo 1:   [IMAGEM DO ENEMY TIPO 1]
  Enemy tipo 2:   [IMAGEM DO ENEMY TIPO 2]
  Boss 1:   [IMAGEM DO BOSS 1]
  Boss 2:   [IMAGEM DO BOSS 2]
  Boss 3:   [IMAGEM DO BOSS 3]

- Projectiles: PlayerProjectile, EnemyProjectile
  Os projeteis dividem caracteristicas em comum entre si, só que tem a diferença que o do jogador danifica as 'SpaceShips' inimigas e os inimigos danificam a 'SpaceShip' do jogador.

  [IMAGEM DOS PROJETEIS]

  
- PowerUps: ShieldPowerUp, TripleShotPowerUp
  Poderes para o jogador conseguir tomar um hit sem morrer ou para que ele atire 3 projeteis ao mesmo tempo.

  [IMAGEM DOS PODERES QUE JÁ ESTÃO NO GITHUB]

- BackgroundObjects: Stars, HealthBar
  Estrelas e barra de vida.

  [IMAGEM DA ESTRELA E BARRA DE VIDA]

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
