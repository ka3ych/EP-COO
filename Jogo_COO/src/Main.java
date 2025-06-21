import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

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
    
    /* classes */
    
    // jogador (entidade controlada pelo usuário)
    static class Player {
        int state = ACTIVE; // estado inicial
        double x, y; // posicao
        double explosion_start, explosion_end; // para o tempo de explosão
        long nextShot; // proximo momento permitido para disparo
    }
    
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

    // projétil (disparado pelo jogador ou inimigos)
    static class Projectile {
        int state = INACTIVE;
        double x, y; // posicao
        double vx, vy; // velocidade
    }
    
    // estrelas
    static class Stars {
        double x, y; // posicao
    }
    
    /* Espera ativamente até o momento especificado para controle de framerate */
    public static void busyWait(long time){

        while(System.currentTimeMillis() < time) Thread.yield();
    }
    
    /* verifica colisão entre duas entidades */
    private static boolean checkCollision(double x1, double y1, double r1, double x2, double y2, double r2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        double dist = Math.sqrt(dx * dx + dy * dy);
        return dist < (r1 + r2) * COLLISION_FACTOR;
    }
    
	/* Método principal */

    public static void main(String [] args) {

        /* Indica que o jogo está em execução */

        boolean running = true;
        
		/* variáveis usadas no controle de tempo efetuado no main loop */

        long delta;
        long currentTime = System.currentTimeMillis();

		/* variáveis do player */

        Player player = new Player();
        player.x = GameLib.WIDTH / 2;
        player.y = GameLib.HEIGHT * 0.90;
        player.nextShot = currentTime;

        /* variaveis para controle do jogo */
        long nextEnemy1 = currentTime + 2000;    // Próxima geração de inimigo tipo 1
        long nextEnemy2 = currentTime + 7000;     // Próxima geração de inimigo tipo 2
        double enemy2_spawnX = GameLib.WIDTH * 0.20; // Posição X de spawn do tipo 2
        int enemy2_count = 0;                    // Contador para formação de inimigos
        double background1_count = 0.0;           // Contador de animação do fundo 1
        double background2_count = 0.0;           // Contador de animação do fundo 2
       	double background1_speed = 0.070; // velocidade ddo background

        /* coleções */

        List<Projectile> playerProjectiles = new ArrayList<>(); // Projéteis do jogador
        List<Projectile> enemyProjectiles = new ArrayList<>(); // Projéteis inimigos
        List<Enemy> enemies = new ArrayList<>(); // Todos os inimigos
        List<Stars> background1 = new ArrayList<>(); // Estrelas de fundo próximo
        List<Stars> background2 = new ArrayList<>(); // Estrelas de fundo distante
        
		/* inicializações das estrelas que formam os fundos*/

        Random rand = new Random();
        for(int i = 0; i < 20; i++){
            Stars star = new Stars();
            star.x = rand.nextDouble() * GameLib.WIDTH;
            star.y = rand.nextDouble() * GameLib.HEIGHT;
            background1.add(star);
        }
        
        for(int i = 0; i < 50; i++){
            Stars star = new Stars();
            star.x = rand.nextDouble() * GameLib.WIDTH;
            star.y = rand.nextDouble() * GameLib.HEIGHT;
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
            
            if(player.state == ACTIVE){

				/* colisões player - projeteis (inimigo) */

                for(Projectile p : enemyProjectiles){
                    if(p.state == ACTIVE && checkCollision(p.x, p.y, PROJECTILE_RADIUS, player.x, player.y, PLAYER_RADIUS)) {
                        player.state = EXPLODING;
                        player.explosion_start = currentTime;
                        player.explosion_end = currentTime + PLAYER_EXPLOSION_DURATION;
                    }
                }
            
				/* colisões player - inimigos */

                for(Enemy e : enemies){
                    if(e.state == ACTIVE){
                        double radius = (e.type == 1) ? ENEMY1_RADIUS : ENEMY2_RADIUS;

                        if(checkCollision(e.x, e.y, radius, player.x, player.y, PLAYER_RADIUS)) {
                            player.state = EXPLODING;
                            player.explosion_start = currentTime;
                            player.explosion_end = currentTime + PLAYER_EXPLOSION_DURATION;
                        }
                    }
                }
            }
            
			/* colisões projeteis (player) - inimigos */

            for(Projectile p : playerProjectiles) {
                if(p.state != ACTIVE) continue;
                
                for(Enemy e : enemies) {
                    if(e.state != ACTIVE) continue;
                    
                    double radius = (e.type == 1) ? ENEMY1_RADIUS : ENEMY2_RADIUS;
                    double dx = e.x - p.x;
                    double dy = e.y - p.y;
                    double dist = Math.sqrt(dx * dx + dy * dy);
                    
                    if(dist < radius){
                        
                        e.state = EXPLODING;
                        e.explosionStart = currentTime;
                        e.explosionEnd = currentTime + ENEMY_EXPLOSION_DURATION;
                    }
                }
            }
                
            /****************************/
			/* Atualizações de estados */
            /****************************/
            
			/* projeteis (player) */

            Iterator<Projectile> playerProjIter = playerProjectiles.iterator();
            while(playerProjIter.hasNext()){

                Projectile p = playerProjIter.next();
                
                /* verificando se projétil saiu da tela */

                if(p.state == ACTIVE) {
                    if(p.y < 0){
                        playerProjIter.remove();
                    }else{
                        p.x += p.vx * delta;
                        p.y += p.vy * delta;
                    }
                }
            }
            
			/* projeteis (inimigos) */

            Iterator<Projectile> enemyProjIter = enemyProjectiles.iterator();
            
            while(enemyProjIter.hasNext()){
                Projectile p = enemyProjIter.next();
                
                /* verificando se projétil saiu da tela */

                if(p.state == ACTIVE) {
                    if(p.y > GameLib.HEIGHT) {
                        enemyProjIter.remove(); // Remove se saiu da tela
                    }
                    else{
                        p.x += p.vx * delta;
                        p.y += p.vy * delta;
                    }
                }
            }
            
            // parte dos inimigos com comportamentos especificos
            Iterator<Enemy> enemyIter = enemies.iterator();
            while(enemyIter.hasNext()) {
                Enemy e = enemyIter.next();
                
                // para o final da explosão e remover ao final
                if(e.state == EXPLODING) {
                    if(currentTime > e.explosionEnd) {
                        enemyIter.remove();
                    }
                }
                
                // pra inimigos tipo 1 e tipo 2
                if(e.state == ACTIVE) {

                    /* inimigos tipo 1 */

                    if(e.type == 1) {

                        // posicao e direcao
                        e.x += e.v * Math.cos(e.angle) * delta;
                        e.y += e.v * Math.sin(e.angle) * delta * (-1.0);
                        e.angle += e.vr * delta;
                        
					    /* verificando se inimigo saiu da tela */
                        if(e.y > GameLib.HEIGHT + 10) {
                            enemyIter.remove();
                        } 

                        // disparo de projetil
                        else if(currentTime > e.nextShoot && e.y < player.y){

                            Projectile proj = new Projectile();

                            proj.x = e.x;
                            proj.y = e.y;
                            proj.vx = Math.cos(e.angle) * 0.45;
                            proj.vy = Math.sin(e.angle) * 0.45 * (-1.0);
                            proj.state = ACTIVE;
                            
                            enemyProjectiles.add(proj);
                            e.nextShoot = (long) (currentTime + 200 + rand.nextDouble() * 500);
                        }
                    } 

			        /* inimigos tipo 2 */
                    else{ 
                        boolean shootNow = false;
                        double previousY = e.y;
                        
                        // posicao e direcao
                        e.x += e.v * Math.cos(e.angle) * delta;
                        e.y += e.v * Math.sin(e.angle) * delta * (-1.0);
                        e.angle += e.vr * delta;
                        
                        // verifica se vai mudar de direcao
                        double threshold = GameLib.HEIGHT * 0.30;
                        if(previousY < threshold && e.y >= threshold) {
                            if(e.x < GameLib.WIDTH / 2) e.vr = 0.003;
                            else e.vr = -0.003;
                        }
                        
                        // verifica os pontos de disparo
                        if(e.vr > 0 && Math.abs(e.angle - 3 * Math.PI) < 0.05) {
                            e.vr = 0.0;
                            e.angle = 3 * Math.PI;
                            shootNow = true;
                        }
                        
                        if(e.vr < 0 && Math.abs(e.angle) < 0.05) {
                            e.vr = 0.0;
                            e.angle = 0.0;
                            shootNow = true;
                        }
                        
					    /* verificando se inimigo saiu da tela */
                        if(e.x < -10 || e.x > GameLib.WIDTH + 10){
                            enemyIter.remove();
                        } 

                        else if(shootNow){
                            double[] angles = { 
                                Math.PI/2 + Math.PI/8, 
                                Math.PI/2, 
                                Math.PI/2 - Math.PI/8 
                            };
                            for(double angle : angles){

                                Projectile proj = new Projectile();
                                double a = angle + rand.nextDouble() * Math.PI/6 - Math.PI/12;
                                
                                proj.x = e.x;
                                proj.y = e.y;
                                proj.vx = Math.cos(a) * 0.30;
                                proj.vy = Math.sin(a) * 0.30;
                                proj.state = ACTIVE;

                                enemyProjectiles.add(proj);
                            }
                        }
                    }
                }
            }
            
			/* verificando se novos inimigos (tipo 1) devem ser "lançados" */

            if(currentTime > nextEnemy1){

                Enemy e = new Enemy();

                e.type = 1;
                e.x = rand.nextDouble() * (GameLib.WIDTH - 20.0) + 10.0;
                e.y = -10.0;
                e.v = 0.20 + rand.nextDouble() * 0.15;
                e.angle = (3 * Math.PI) / 2;
                e.vr = 0.0;
                e.state = ACTIVE;
                e.nextShoot = currentTime + 500;
                enemies.add(e);
                nextEnemy1 = currentTime + 500;
            }
            
			/* verificando se novos inimigos (tipo 2) devem ser "lançados" */

            if(currentTime > nextEnemy2){

                Enemy e = new Enemy();

                e.type = 2;
                e.x = enemy2_spawnX;
                e.y = -10.0;
                e.v = 0.42;
                e.angle = (3 * Math.PI) / 2;
                e.vr = 0.0;
                e.state = ACTIVE;
                enemies.add(e);

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

			/* Verificando se a explosão do player já acabou.         */
			/* Ao final da explosão, o player volta a ser controlável */
            if(player.state == EXPLODING && currentTime > player.explosion_end){
                player.state = ACTIVE;
            }
            
			/********************************************/
			/* Verificando entrada do usuário (teclado) */
			/********************************************/
			
            if(player.state == ACTIVE) {
                // movimentação do player
                if(GameLib.iskeyPressed(GameLib.KEY_UP)) player.y -= delta * PLAYER_INITIAL_VELOCITY;
                if(GameLib.iskeyPressed(GameLib.KEY_DOWN)) player.y += delta * PLAYER_INITIAL_VELOCITY;
                if(GameLib.iskeyPressed(GameLib.KEY_LEFT)) player.x -= delta * PLAYER_INITIAL_VELOCITY;
                if(GameLib.iskeyPressed(GameLib.KEY_RIGHT)) player.x += delta * PLAYER_INITIAL_VELOCITY;
                
                // disparo
                if(GameLib.iskeyPressed(GameLib.KEY_CONTROL)) {

                    if(currentTime > player.nextShot){

                        Projectile p = new Projectile();

                        p.x = player.x;
                        p.y = player.y - 2 * PLAYER_RADIUS;
                        p.vx = 0.0;
                        p.vy = -1.0;
                        p.state = ACTIVE;
                        playerProjectiles.add(p);
                        player.nextShot = currentTime + 100;
                    }    
                }
            }
            
            if(GameLib.iskeyPressed(GameLib.KEY_ESCAPE)) running = false;
            
			/* Verificando se coordenadas do player ainda estão dentro */
			/* da tela de jogo após processar entrada do usuário.      */

            if(player.x < 0.0) player.x = 0.0;
            if(player.x >= GameLib.WIDTH) player.x = GameLib.WIDTH - 1;
            if(player.y < 25.0) player.y = 25.0;
            if(player.y >= GameLib.HEIGHT) player.y = GameLib.HEIGHT - 1;

			/*******************/
			/* Desenho da cena */
			/*******************/
			
			/* desenhando plano fundo distante */
			
            GameLib.setColor(Color.DARK_GRAY);
            background2_count += 0.045 * delta;

            for(Stars star : background2){
                double yPos = (star.y + background2_count) % GameLib.HEIGHT;

                GameLib.fillRect(star.x, yPos, 2, 2);
            }
			
			/* desenhando plano de fundo próximo */
			
            GameLib.setColor(Color.GRAY);
            background1_count += background1_speed * delta;

            for(Stars star : background1){
                double yPos = (star.y + background1_count) % GameLib.HEIGHT;
                GameLib.fillRect(star.x, yPos, 3, 3);
            }
                        
            /* desenhando player */

            if(player.state == EXPLODING){

                double alpha = (currentTime - player.explosion_start) / (player.explosion_end - player.explosion_start);
                GameLib.drawExplosion(player.x, player.y, alpha);
            }
			else{
				
                GameLib.setColor(Color.BLUE);
                GameLib.drawPlayer(player.x, player.y, PLAYER_RADIUS);
            }
                
            /* desenhando projeteis (player) */
            for(Projectile p : playerProjectiles){

                if(p.state == ACTIVE) {

                    GameLib.setColor(Color.GREEN);
                    GameLib.drawLine(p.x, p.y - 5, p.x, p.y + 5);
                    GameLib.drawLine(p.x - 1, p.y - 3, p.x - 1, p.y + 3);
                    GameLib.drawLine(p.x + 1, p.y - 3, p.x + 1, p.y + 3);
                }
            }
            
            /* desenhando projeteis (inimigos) */
            for(Projectile p : enemyProjectiles) {
                if(p.state == ACTIVE) {
                    GameLib.setColor(Color.RED);
                    GameLib.drawCircle(p.x, p.y, PROJECTILE_RADIUS);
                }
            }
            
            /* desenhando inimigos tipo 1 e 2 */
            for(Enemy e : enemies){

                if(e.state == EXPLODING){

                    double alpha = (currentTime - e.explosionStart) / (e.explosionEnd - e.explosionStart);
                    GameLib.drawExplosion(e.x, e.y, alpha);
                }else if(e.state == ACTIVE){

                    // inimigo tipo 1 (esfera ciana)
                    if(e.type == 1){
                        GameLib.setColor(Color.CYAN);
                        GameLib.drawCircle(e.x, e.y, ENEMY1_RADIUS);
                    } 
					
                    // inimigo tipo 2 (cobra magenta)
                    else{
                        GameLib.setColor(Color.MAGENTA);
                        GameLib.drawDiamond(e.x, e.y, ENEMY2_RADIUS);
                    }
                }
            }
            
			/* chamada a display() da classe GameLib atualiza o desenho exibido pela interface do jogo. */

			GameLib.display();
            
			/* faz uma pausa de modo que cada execução do laço do main loop demore aproximadamente 3 ms. */

			busyWait(currentTime + 3);
        }
        
        System.exit(0);
    }
}