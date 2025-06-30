import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

// Pacotes criados por nós
import GameLib.GameLib;
import GameObjects.BackgroundObjects.*;
import GameObjects.Colliders.CollideWithPlayer;
import GameObjects.SpaceShips.*;
import GameObjects.SpaceShips.Enemies.*;
import GameObjects.Projectiles.*;
import GameObjects.SpaceShips.Enemies.Bosses.*;



/***********************************************************************/
/*                                                                     */
/* Para jogar:                                                         */
/*                                                                     */
/*    - cima, baixo, esquerda, direita: movimentação do player.        */
/*    - control: disparo de projéteis.                                 */
/*    - ESC: para sair do jogo.                                        */
/*                                                                     */
/***********************************************************************/

public class Main {
	/* Constantes relacionadas aos estados que os elementos do jogo (player, projeteis ou inimigos) podem assumir. */

    public static final int INACTIVE = 0;
    public static final int ACTIVE = 1;
    public static final int EXPLODING = 2;
    
    
    /* constantes para temporização e duração de explosões    */
    public static final long PLAYER_EXPLOSION_DURATION = 2000;
    public static final long ENEMY_EXPLOSION_DURATION = 500;
    
    /* constantes para propriedades do player, inimigos e projetil    */
    public static final double PLAYER_INITIAL_VELOCITY = 0.25;
    public static final double PLAYER_RADIUS = 12.0;
    public static final double ENEMY1_RADIUS = 9.0;
    public static final double ENEMY2_RADIUS = 12.0;
    public static final double PROJECTILE_RADIUS = 2.0;
    public static final double COLLISION_FACTOR = 0.8;
    
    
    /* Espera ativamente até o momento especificado para controle de framerate */
    public static void busyWait(long time){
        while(System.currentTimeMillis() < time) Thread.yield();
    }
    
	/* Método principal */
    public static void main(String [] args) {

        /* Indica que o jogo está em execução */

        boolean running = true;
        
		/* variáveis usadas no controle de tempo efetuado no main loop */

        long delta;
        long currentTime = System.currentTimeMillis();

		/* variáveis do player */

        Player player = new Player(GameLib.WIDTH / 2, GameLib.HEIGHT * 0.90, currentTime);

        /* variaveis para controle do jogo */
        long nextEnemy1 = currentTime + 2000;    // Próxima geração de inimigo tipo 1
        long nextEnemy2 = currentTime + 7000;     // Próxima geração de inimigo tipo 2
        boolean bossHasSpawned = false; // Flag para controle de boss
        long bossSpawnTime = 20000; // Tempo de spawn do boss
        double enemy2_spawnX = GameLib.WIDTH * 0.20; // Posição X de spawn do tipo 2
        int enemy2_count = 0;                    // Contador para formação de inimigos
        double background1_count = 0.0;           // Contador de animação do fundo 1
        double background2_count = 0.0;           // Contador de animação do fundo 2
       	double background1_speed = 0.070; // velocidade do background
        double background2_speed = 0.045; // velocidade do background distante

        /* coleções */

        List<PlayerProjectile> playerProjectiles = new ArrayList<>(); // Projéteis do jogador
        List<EnemyProjectile> enemyProjectiles = new ArrayList<>(); // Projéteis inimigos
        List<Enemy> enemies = new ArrayList<>(); // Todos os inimigos>
        List<Enemy1> enemies1 = new ArrayList<>(); // Inimigos tipo 1
        List<Enemy2> enemies2 = new ArrayList<>(); // Inimigos
        List<Boss1> bosses1 = new ArrayList<>(); // Lista de bosses
        List<CollideWithPlayer> colideComPlayer = new ArrayList<>(); // Lista de objetos que colidem com o player
        List<Stars> background1 = new ArrayList<>(); // Estrelas de fundo próximo
        List<Stars> background2 = new ArrayList<>(); // Estrelas de fundo distante
        
		/* inicializações das estrelas que formam os fundos*/

        Random rand = new Random();
        for(int i = 0; i < 20; i++){
            Stars star = new Stars(rand.nextDouble() * GameLib.WIDTH, rand.nextDouble() * GameLib.HEIGHT, Color.GRAY);
            background1.add(star);
        }
        
        for(int i = 0; i < 50; i++){
            Stars star = new Stars(rand.nextDouble() * GameLib.WIDTH, rand.nextDouble() * GameLib.HEIGHT, Color.DARK_GRAY);
            background2.add(star);
        }
                        
		/* iniciado interface gráfica */

        GameLib.initGraphics();
		//GameLib.initGraphics_SAFE_MODE();  // chame esta versão do método caso nada seja desenhado na janela do jogo.
		
		/*************************************************************************************************/
		/*                                                                                               */
		/* Main loop do jogo                                                                             */
		/* -----------------                                                                             */
		/*                                                                                               */
		/* O main loop do jogo executa as seguintes operações:                                           */
		/*                                                                                               */
		/* 1) Verifica se há colisões e atualiza estados dos elementos conforme a necessidade.           */
		/*                                                                                               */
		/* 2) Atualiza estados dos elementos baseados no tempo que correu entre a última atualização     */
		/*    e o timestamp atual: posição e orientação, execução de disparos de projéteis, etc.         */
		/*                                                                                               */
		/* 3) Processa entrada do usuário (teclado) e atualiza estados do player conforme a necessidade. */
		/*                                                                                               */
		/* 4) Desenha a cena, a partir dos estados dos elementos.                                        */
		/*                                                                                               */
		/* 5) Espera um período de tempo (de modo que delta seja aproximadamente sempre constante).      */
		/*                                                                                               */
		/*************************************************************************************************/
        
        while(running){

			/* Usada para atualizar o estado dos elementos do jogo    */
			/* (player, projéteis e inimigos) "delta" indica quantos  */
			/* ms se passaram desde a última atualização.             */

            delta = System.currentTimeMillis() - currentTime;

            /* Já a variável "currentTime" nos dá o timestamp atual.  */

            currentTime = System.currentTimeMillis();
            
            /***************************/
            /* Verificação de colisões */
            /***************************/
            
            /* colisões player */
            if(player.isStateTrue(ACTIVE)){
                for(CollideWithPlayer ccp : colideComPlayer){
                    ccp.colideWithPlayer(player);
                }
            }
            
			/* colisões projeteis (player) - inimigos */

            for(PlayerProjectile p : playerProjectiles) {
                if(!p.isStateTrue(ACTIVE)) continue;
                
                for(Enemy e : enemies) {
                    if(!e.isStateTrue(ACTIVE)) continue;
                    p.collideEnemy(e);
                }
            }
                
            /****************************/
			/* Atualizações de estados */
            /****************************/
            
			/* projeteis (player) */

            Iterator<PlayerProjectile> playerProjIter = playerProjectiles.iterator();
            while(playerProjIter.hasNext()){

                Projectile p = playerProjIter.next();
                
                /* verificando se projétil saiu da tela */

                if(p.isStateTrue(ACTIVE)) {
                    if(p.getY() < 0){
                        playerProjIter.remove();
                    }else{
                        p.move(delta);
                    }
                }
            }
            
			/* projeteis (inimigos) */

            Iterator<EnemyProjectile> enemyProjIter = enemyProjectiles.iterator();
            
            while(enemyProjIter.hasNext()){
                Projectile p = enemyProjIter.next();
                
                /* verificando se projétil saiu da tela */

                if(p.isStateTrue(ACTIVE)) {
                    if(p.getY() > GameLib.HEIGHT) {
                        enemyProjIter.remove(); // Remove se saiu da tela
                    }
                    else{
                        p.move(delta);
                    }
                }
            }
            
            // parte dos inimigos com comportamentos especificos
            Iterator<Enemy> enemyIter = enemies.iterator();
            while(enemyIter.hasNext()) {
                Enemy e = enemyIter.next();
                
                // para o final da explosão e remover ao final
                if(e.isStateTrue(EXPLODING)) {
                    if(currentTime > e.getExplosionEnd()) {
                        enemyIter.remove();
                    }
                }
                else if(e.isStateTrue(ACTIVE)) e.moveAndDirection(delta);

                /* verificando se inimigo saiu da tela */
                if(e.getY() > GameLib.HEIGHT + 10 || (e.getX() < -10 || e.getX() > GameLib.WIDTH + 10)) {
                    enemyIter.remove();
                } 
            }

            Iterator<Enemy1> enemy1Iter = enemies1.iterator();
            while(enemy1Iter.hasNext()){
                Enemy1 e = enemy1Iter.next();

                if(e.isStateTrue(ACTIVE)){                     
                    e.shoot(enemyProjectiles, colideComPlayer);
                }
            }
            

            Iterator<Enemy2> enemy2Iter = enemies2.iterator();
            while(enemy2Iter.hasNext()){
                Enemy2 e = enemy2Iter.next();
                
                if(e.isStateTrue(ACTIVE)) {
                    e.shoot(enemyProjectiles, colideComPlayer);
                }
            }

            Iterator<Boss1> boss1Iter = bosses1.iterator();
            while(boss1Iter.hasNext()){
                Boss1 b = boss1Iter.next();
                
                if(b.isStateTrue(ACTIVE)) {
                    b.shoot(enemyProjectiles, colideComPlayer, player);
                }
            }
            
			/* verificando se novos inimigos (tipo 1) devem ser "lançados" */

            if(currentTime > nextEnemy1){

                Enemy1 e = new Enemy1(
                    rand.nextDouble() * (GameLib.WIDTH - 20.0) + 10.0,
                    -10.0,
                    0.20 + rand.nextDouble() * 0.15,
                    (3 * Math.PI) / 2,
                    0.0
                );
                e.setNextShoot(currentTime + 500);

                enemies.add(e);
                enemies1.add(e);
                colideComPlayer.add(e);
                nextEnemy1 = currentTime + 500;
            }
            
			/* verificando se novos inimigos (tipo 2) devem ser "lançados" */

            if(currentTime > nextEnemy2){

                Enemy2 e = new Enemy2(
                    enemy2_spawnX,
                    -10.0,
                    0.42,
                    (3 * Math.PI) / 2,
                    0.0
                );

                enemies.add(e);
                enemies2.add(e);
                colideComPlayer.add(e);

                enemy2_count++;
                
                // Controle de formação
                if(enemy2_count < 10){

                    nextEnemy2 = currentTime + 120;
                }
                else{

                    enemy2_count = 0;
                    enemy2_spawnX = rand.nextDouble() > 0.5 ? GameLib.WIDTH * 0.2 : GameLib.WIDTH * 0.8;
                    nextEnemy2 = (long) (currentTime + 3000 + rand.nextDouble() * 3000);
                }
            }

            if(currentTime > bossSpawnTime && !bossHasSpawned) {
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
            }

            

			/* Verificando se a explosão do player já acabou.         */
			/* Ao final da explosão, o player volta a ser controlável */
            if(!player.isStateTrue(ACTIVE) && currentTime > player.getExplosionEnd()){
                player.activate(delta);
            }
            
			/********************************************/
			/* Verificando entrada do usuário (teclado) */
			/********************************************/
			
            if(player.isStateTrue(ACTIVE)){
                // movimentação do player
                if(GameLib.iskeyPressed(GameLib.KEY_UP)) player.moveY(-1 * delta * PLAYER_INITIAL_VELOCITY); ;
                if(GameLib.iskeyPressed(GameLib.KEY_DOWN)) player.moveY(delta * PLAYER_INITIAL_VELOCITY);
                if(GameLib.iskeyPressed(GameLib.KEY_LEFT)) player.moveX(-1 * delta * PLAYER_INITIAL_VELOCITY);
                if(GameLib.iskeyPressed(GameLib.KEY_RIGHT)) player.moveX(delta * PLAYER_INITIAL_VELOCITY);
                
                // disparo
                if(GameLib.iskeyPressed(GameLib.KEY_CONTROL)) {

                    if(currentTime > player.getNextShoot()){

                        PlayerProjectile p = new PlayerProjectile(
                            player.getX(),
                            player.getY() - 2 * PLAYER_RADIUS,
                            0.0,
                            -1.0
                        );

                        playerProjectiles.add(p);
                        player.shoot(currentTime);
                    }    
                }
            }
            
            if(GameLib.iskeyPressed(GameLib.KEY_ESCAPE)) running = false;
            
			/* Verificando se coordenadas do player ainda estão dentro */
			/* da tela de jogo após processar entrada do usuário.      */

            player.outOfBounds();

			/*******************/
			/* Desenho da cena */
			/*******************/
			
			/* desenhando plano fundo distante */
            background2_count += background2_speed * delta;

            for(Stars star : background2){
                star.draw(background2_count);
            }
			
			/* desenhando plano de fundo próximo */
			
            background1_count += background1_speed * delta;
            for(Stars star : background1){
                star.draw(background1_count);
            }
                        
            /* desenhando player */
            player.draw();
                
            /* desenhando projeteis (player) */
            for(Projectile p : playerProjectiles){
                p.draw();
            }
            
            /* desenhando projeteis (inimigos) */
            for(Projectile p : enemyProjectiles) {
                p.draw();
            }
            
            /* desenhando inimigos*/
            for(Enemy e : enemies){
                e.draw();
            }
            
			/* chamada a display() da classe GameLib atualiza o desenho exibido pela interface do jogo. */

			GameLib.display();
            
			/* faz uma pausa de modo que cada execução do laço do main loop demore aproximadamente 3 ms. */

			busyWait(currentTime + 3);
        }
        
        System.exit(0);
    }
}