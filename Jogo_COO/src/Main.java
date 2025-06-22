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
        private int state; // estado inicial
        private double x, y; // posicao
        private double explosion_start, explosion_end; // para o tempo de explosão
        private long nextShoot; // proximo momento permitido para disparo

        // construtor do Player
        public Player(double x, double y, long nextShoot){
            this.x = x;
            this.y = y;
            this.nextShoot = nextShoot;
            state = ACTIVE;
        }

        public void moveX(double varX){this.x += varX;}

        public void moveY(double varY){this.y += varY;}

        public void explode(double timeExplosionStart, double timeExplosionEnd){
            this.state = EXPLODING;
            this.explosion_start = timeExplosionStart;
            this.explosion_end = timeExplosionEnd;
        }

        public void shoot(long time){nextShoot = time + 100;}

        public void activate(){state = ACTIVE;}

        public boolean isStateTrue(int state){
            if(this.state == state) return true;
            else return false;
        }

        public void outOfBounds(){
            if(this.getX() < 0.0) this.x = 0.0;
            if(this.getX() >= GameLib.WIDTH) this.x = GameLib.WIDTH - 1;
            if(this.getY() < 25.0) this.y = 25.0;
            if(this.getY() >= GameLib.HEIGHT) this.y = GameLib.HEIGHT - 1;
        }
        
        // getters
        public double getX() {return x;}
        public double getY() {return y;}
        public int getState() {return state;}
        public double getExplosionStart() {return explosion_start;}
        public double getExplosionEnd() {return explosion_end;}
        public long getNextShoot() {return nextShoot;}
    }
    
    // inimigo
    static class Enemy {
        private int type; // tipo 1 ou 2
        private int state = INACTIVE; 
        private double x, y; // posicao
        private double v; // velocidade escalar
        private double angle; // angulo da direcao
        private double vr; // velocidade rotacao
        private long nextShoot; // prox momento que pode disparar
        private double explosionStart, explosionEnd; // temporizador

        // construtor
        public Enemy(int type, double x, double y, double escalarVelocity, double angle, double velocityRotation){
            this.type = type;
            this.x = x;
            this.y = y;
            this.v = escalarVelocity;
            this.angle = angle;
            this.vr = velocityRotation;
            this.state = ACTIVE;
        }

        public boolean isStateTrue(int state){
            if(state == this.state) return true;
            else return false;
        }

        public void explode(double timeExplosionStart, double timeExplosionEnd){
            this.state = EXPLODING;
            this.explosionStart = timeExplosionStart;
            this.explosionEnd = timeExplosionEnd;
        }

        public void moveAndDirection(long time){
            x += v * Math.cos(angle) * time;
            y += v * Math.sin(angle) * time * (-1.0);
            angle += vr * time;
        }

        public void changeDirection(){
            if(x < GameLib.WIDTH / 2) vr = 0.003;
            else vr = -0.003;
        }

        // setters
        public void setNextShoot(long cooldown) {nextShoot = cooldown;}
        public void setAngle(double angle) {this.angle = angle;}
        public void setVelocityRotation(double vr) {this.vr = vr;}

        // getters
        public double getX(){return x;}
        public double getY(){return y;}
        public double getEscalarVelocity(){return v;}
        public double getAngle(){return angle;}
        public double getRotationVelocity(){return vr;}
        public int getType(){return type;}
        public int getState() {return state;}
        public double getExplosionStart() {return explosionStart;}
        public double getExplosionEnd() {return explosionEnd;}
        public long getNextShot() {return nextShoot;}
        
    }

    // projétil (disparado pelo jogador ou inimigos)
    static class Projectile {
        private int state = INACTIVE;
        private double x, y; // posicao
        private double vx, vy; // velocidade

        public Projectile(double x, double y, double vx, double vy){
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.state = ACTIVE; 
        }

        public boolean isStateTrue(int state){
            if(state == this.state) return true;
            else return false;
        }

        public void move(long delta){
            x += vx * delta;
            y += vy * delta;
        } 

        // getters
        public int getState() {return this.state;}
        public double getX() {return this.x;}
        public double getY() {return this.y;}
        public double getVelocityX() {return this.vx;}
        public double getVelocityY() {return this.vy;}
    }
    
    // estrelas
    static class Stars {
        private double x, y; // posicao

        // construtor
        public Stars(double x, double y){
            this.x = x;
            this.y = y;
        }

        // getters
        public double getX(){return this.x;}
        public double getY(){return this.y;}
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

        Player player = new Player(GameLib.WIDTH / 2, GameLib.HEIGHT * 0.90, currentTime);

        /* variaveis para controle do jogo */
        long nextEnemy1 = currentTime + 2000;    // Próxima geração de inimigo tipo 1
        long nextEnemy2 = currentTime + 7000;     // Próxima geração de inimigo tipo 2
        double enemy2_spawnX = GameLib.WIDTH * 0.20; // Posição X de spawn do tipo 2
        int enemy2_count = 0;                    // Contador para formação de inimigos
        double background1_count = 0.0;           // Contador de animação do fundo 1
        double background2_count = 0.0;           // Contador de animação do fundo 2
       	double background1_speed = 0.070; // velocidade do background

        /* coleções */

        List<Projectile> playerProjectiles = new ArrayList<>(); // Projéteis do jogador
        List<Projectile> enemyProjectiles = new ArrayList<>(); // Projéteis inimigos
        List<Enemy> enemies = new ArrayList<>(); // Todos os inimigos
        List<Stars> background1 = new ArrayList<>(); // Estrelas de fundo próximo
        List<Stars> background2 = new ArrayList<>(); // Estrelas de fundo distante
        
		/* inicializações das estrelas que formam os fundos*/

        Random rand = new Random();
        for(int i = 0; i < 20; i++){
            Stars star = new Stars(rand.nextDouble() * GameLib.WIDTH, rand.nextDouble() * GameLib.HEIGHT);
            background1.add(star);
        }
        
        for(int i = 0; i < 50; i++){
            Stars star = new Stars(rand.nextDouble() * GameLib.WIDTH, rand.nextDouble() * GameLib.HEIGHT);
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
            
            if(player.isStateTrue(ACTIVE)){

				/* colisões player - projeteis (inimigo) */

                for(Projectile p : enemyProjectiles){
                    if(p.isStateTrue(ACTIVE) && checkCollision(p.getX(), p.getY(), PROJECTILE_RADIUS, player.getX(), player.getY(), PLAYER_RADIUS)) {
                        player.explode(currentTime, currentTime + PLAYER_EXPLOSION_DURATION);
                    }
                }
            
				/* colisões player - inimigos */

                for(Enemy e : enemies){
                    if(e.isStateTrue(ACTIVE)){
                        double radius = (e.getType() == 1) ? ENEMY1_RADIUS : ENEMY2_RADIUS;

                        if(checkCollision(e.getX(), e.getY(), radius, player.getX(), player.getY(), PLAYER_RADIUS)) {
                            player.explode(currentTime, currentTime + PLAYER_EXPLOSION_DURATION);
                        }
                    }
                }
            }
            
			/* colisões projeteis (player) - inimigos */

            for(Projectile p : playerProjectiles) {
                if(!p.isStateTrue(ACTIVE)) continue;
                
                for(Enemy e : enemies) {
                    if(!e.isStateTrue(ACTIVE)) continue;
                    
                    double radius = (e.getType() == 1) ? ENEMY1_RADIUS : ENEMY2_RADIUS;
                    double dx = e.getX() - p.getX();
                    double dy = e.getY() - p.getY();
                    double dist = Math.sqrt(dx * dx + dy * dy);
                    
                    if(dist < radius){
                        e.explode(currentTime, currentTime + ENEMY_EXPLOSION_DURATION);
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

                if(p.isStateTrue(ACTIVE)) {
                    if(p.getY() < 0){
                        playerProjIter.remove();
                    }else{
                        p.move(delta);
                    }
                }
            }
            
			/* projeteis (inimigos) */

            Iterator<Projectile> enemyProjIter = enemyProjectiles.iterator();
            
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
                
                // pra inimigos tipo 1 e tipo 2
                if(e.isStateTrue(ACTIVE)) {

                    /* inimigos tipo 1 */

                    if(e.getType() == 1) {

                        // posicao e direcao
                        e.moveAndDirection(delta);
                        
					    /* verificando se inimigo saiu da tela */
                        if(e.getY() > GameLib.HEIGHT + 10) {
                            enemyIter.remove();
                        } 

                        // disparo de projetil
                        else if(currentTime > e.getNextShot() && e.getY() < player.getY()){

                            Projectile proj = new Projectile(
                                e.getX(),
                                e.getY(),
                                Math.cos(e.getAngle()) * 0.45,
                                Math.sin(e.getAngle()) * 0.45 * (-1.0)
                            );
                            
                            enemyProjectiles.add(proj);
                            e.setNextShoot((long)(currentTime + 200 + rand.nextDouble() * 500));
                        }
                    } 

			        /* inimigos tipo 2 */
                    else{ 
                        boolean shootNow = false;
                        double previousY = e.getY();
                        
                        // posicao e direcao
                        e.moveAndDirection(delta);
                        
                        // verifica se vai mudar de direcao
                        double threshold = GameLib.HEIGHT * 0.30;
                        if(previousY < threshold && e.getY() >= threshold) {
                            e.changeDirection();
                        }
                        
                        // verifica os pontos de disparo
                        if(e.getRotationVelocity() > 0 && Math.abs(e.getAngle() - 3 * Math.PI) < 0.05) {
                            e.setVelocityRotation(0.0);
                            e.setAngle(3 * Math.PI);
                            shootNow = true;
                        }
                        
                        if(e.getRotationVelocity() < 0 && Math.abs(e.getAngle()) < 0.05) {
                            e.setVelocityRotation(0.0);
                            e.setAngle(0);
                            shootNow = true;
                        }
                        
					    /* verificando se inimigo saiu da tela */
                        if(e.getX() < -10 || e.getX() > GameLib.WIDTH + 10){
                            enemyIter.remove();
                        } 

                        else if(shootNow){
                            double[] angles = { 
                                Math.PI/2 + Math.PI/8, 
                                Math.PI/2, 
                                Math.PI/2 - Math.PI/8 
                            };
                            for(double angle : angles){
                                double a = angle + rand.nextDouble() * Math.PI/6 - Math.PI/12;

                                Projectile proj = new Projectile(
                                    e.getX(),
                                    e.getY(),
                                    Math.cos(a) * 0.30,
                                    Math.sin(a) * 0.30
                                );

                                enemyProjectiles.add(proj);
                            }
                        }
                    }
                }
            }
            
			/* verificando se novos inimigos (tipo 1) devem ser "lançados" */

            if(currentTime > nextEnemy1){

                Enemy e = new Enemy(
                    1,
                    rand.nextDouble() * (GameLib.WIDTH - 20.0) + 10.0,
                    -10.0,
                    0.20 + rand.nextDouble() * 0.15,
                    (3 * Math.PI) / 2,
                    0.0
                );
                e.setNextShoot(currentTime + 500);

                enemies.add(e);
                nextEnemy1 = currentTime + 500;
            }
            
			/* verificando se novos inimigos (tipo 2) devem ser "lançados" */

            if(currentTime > nextEnemy2){

                Enemy e = new Enemy(
                    2,
                    enemy2_spawnX,
                    -10.0,
                    0.42,
                    (3 * Math.PI) / 2,
                    0.0
                );

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
            if(player.isStateTrue(EXPLODING) && currentTime > player.getExplosionEnd()){
                player.activate();
            }
            
			/********************************************/
			/* Verificando entrada do usuário (teclado) */
			/********************************************/
			
            if(player.isStateTrue(ACTIVE)) {
                // movimentação do player
                if(GameLib.iskeyPressed(GameLib.KEY_UP)) player.moveY(-1 * delta * PLAYER_INITIAL_VELOCITY); ;
                if(GameLib.iskeyPressed(GameLib.KEY_DOWN)) player.moveY(delta * PLAYER_INITIAL_VELOCITY);
                if(GameLib.iskeyPressed(GameLib.KEY_LEFT)) player.moveX(-1 * delta * PLAYER_INITIAL_VELOCITY);
                if(GameLib.iskeyPressed(GameLib.KEY_RIGHT)) player.moveX(delta * PLAYER_INITIAL_VELOCITY);
                
                // disparo
                if(GameLib.iskeyPressed(GameLib.KEY_CONTROL)) {

                    if(currentTime > player.nextShoot){

                        Projectile p = new Projectile(
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
			
            GameLib.setColor(Color.DARK_GRAY);
            background2_count += 0.045 * delta;

            for(Stars star : background2){
                double yPos = (star.getY() + background2_count) % GameLib.HEIGHT;

                GameLib.fillRect(star.getX(), yPos, 2, 2);
            }
			
			/* desenhando plano de fundo próximo */
			
            GameLib.setColor(Color.GRAY);
            background1_count += background1_speed * delta;

            for(Stars star : background1){
                double yPos = (star.getY() + background1_count) % GameLib.HEIGHT;
                GameLib.fillRect(star.getX(), yPos, 3, 3);
            }
                        
            /* desenhando player */

            if(player.isStateTrue(EXPLODING)){
                double alpha = (currentTime - player.getExplosionStart()) / (player.getExplosionEnd() - player.getExplosionStart());
                GameLib.drawExplosion(player.getX(), player.getY(), alpha);
            }
			else{
				
                GameLib.setColor(Color.BLUE);
                GameLib.drawPlayer(player.getX(), player.getY(), PLAYER_RADIUS);
            }
                
            /* desenhando projeteis (player) */
            for(Projectile p : playerProjectiles){

                if(p.isStateTrue(ACTIVE)) {

                    GameLib.setColor(Color.GREEN);
                    GameLib.drawLine(p.getX(), p.getY() - 5, p.getX(), p.getY() + 5);
                    GameLib.drawLine(p.getX() - 1, p.getY() - 3, p.getX() - 1, p.getY() + 3);
                    GameLib.drawLine(p.getX() + 1, p.getY() - 3, p.getX() + 1, p.getY() + 3);
                }
            }
            
            /* desenhando projeteis (inimigos) */
            for(Projectile p : enemyProjectiles) {
                if(p.isStateTrue(ACTIVE)) {
                    GameLib.setColor(Color.RED);
                    GameLib.drawCircle(p.getX(), p.getY(), PROJECTILE_RADIUS);
                }
            }
            
            /* desenhando inimigos tipo 1 e 2 */
            for(Enemy e : enemies){

                if(e.isStateTrue(EXPLODING)){

                    double alpha = (currentTime - e.getExplosionStart()) / (e.getExplosionEnd() - e.getExplosionStart());
                    GameLib.drawExplosion(e.getX(), e.getY(), alpha);
                }else if(e.isStateTrue(ACTIVE)){

                    // inimigo tipo 1 (esfera ciana)
                    if(e.getType() == 1){
                        GameLib.setColor(Color.CYAN);
                        GameLib.drawCircle(e.getX(), e.getY(), ENEMY1_RADIUS);
                    } 
					
                    // inimigo tipo 2 (cobra magenta)
                    else{
                        GameLib.setColor(Color.MAGENTA);
                        GameLib.drawDiamond(e.getX(), e.getY(), ENEMY2_RADIUS);
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