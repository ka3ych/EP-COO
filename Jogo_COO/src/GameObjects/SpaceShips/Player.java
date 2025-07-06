package GameObjects.SpaceShips;
import GameLib.GameLib;
import GameObjects.GameObject;
import GameObjects.BackgroundObjects.HealthBar;
import GameObjects.Colliders.CollideWithPlayer;
import GameObjects.Projectiles.PlayerProjectile;
import GameObjects.GameManager;

import java.awt.Color;
import java.util.List;



public class Player extends SpaceShip{
    private static final int INITIAL_HEALTH = 5;

    protected HealthBar healthBar = new HealthBar(this);
    
    // Atributos para o escudo
    private boolean hasShield;

    // Atributos para o disparo triplo
    private boolean hasTripleShot;
    private long TripleShotEndTime;

    // Atributos para reinicio da fase
    private List<CollideWithPlayer> collideWithPlayersList;

    // construtor do Player
    public Player(double x, double y, long nextShoot, List<CollideWithPlayer> list){
        this.x = x;
        this.y = y;
        this.nextShoot = nextShoot;
        collideWithPlayersList = list;

        state = ACTIVE;
        this.radius = PLAYER_RADIUS;
        this.damage = 1.0;
        this.healthPoints = 1;
        initialHealth = healthPoints;
        color = Color.BLUE; // cor do player
        this.healthBarSize = 20; // tamanho da barra de vida do player
        this.hasShield = false;
      
        // Inicializa o novo atributo de disparo duplo
        this.hasTripleShot = false;
        this.TripleShotEndTime = 0;
        healthBar.setSpaceShip(this);
        this.healthPoints = INITIAL_HEALTH;
        this.healthBarSize = 20;
        this.initialHealth = INITIAL_HEALTH;
    }   

    @Override // Sobrescreva o método hit para considerar o escudo
    public void hit(long explosionDuration) {
        if (hasShield) {
            hasShield = false; // Escudo absorve um hit
            System.out.println("Escudo ativou e foi desativado!");
        } else {
            healthPoints--;
            if(healthPoints <= 0) {
                explode(System.currentTimeMillis(), System.currentTimeMillis() + explosionDuration);
            }
        }
    }

    public void moveX(double varX){ this.x += varX; }

    public void moveY(double varY){ this.y += varY; }

    public void shoot(long time){nextShoot = time + 100;}

    public void activate(long time){
        state = ACTIVE;
        healthPoints = initialHealth;
        GameManager.resetLevel(collideWithPlayersList);
        //activateShield(1000);
    }

    public void outOfBounds(){
        if(this.getX() < 0.0) this.x = 0.0;
        if(this.getX() >= GameLib.WIDTH) this.x = GameLib.WIDTH - 1;
        if(this.getY() < 25.0) this.y = 25.0;
        if(this.getY() >= GameLib.HEIGHT) this.y = GameLib.HEIGHT - 1;
    }

    // método para ativar o escudo
    public void activateShield() {
        this.hasShield = true;
    }

    // Método para ativar disparo triplo
    public void activateDoubleShot(long duration) {
        this.hasTripleShot = true;
        this.TripleShotEndTime = System.currentTimeMillis() + duration;
    }

    // Método para atualizar o estado do player (chame no main loop)
    public void update(long currentTime) {
        // Verifica se o disparo duplo acabou
        if (hasTripleShot && currentTime > TripleShotEndTime) {
            this.hasTripleShot = false;
            this.TripleShotEndTime = 0;
            System.out.println("Disparo triplo acabou.");
        }
    }

    public void shoot(long currentTime, List<PlayerProjectile> projectilesList) {
        if (currentTime > nextShoot) {
            if (hasTripleShot) {

                double dist = GameObject.PLAYER_RADIUS;

                PlayerProjectile p1 = new PlayerProjectile(
                    getX() - dist, // Esse projétil ficará no meio
                    getY() - 2 * dist,
                    0.0,
                    -1.0
                );
                PlayerProjectile p2 = new PlayerProjectile(
                    getX(), // Espaçamento do projétil 2 para a esquerda
                    getY() - 2 * dist,
                    0.0,
                    -1.0
                );
                PlayerProjectile p3 = new PlayerProjectile(
                    getX() + dist, // Espaçamento do projétil 3 para a direita
                    getY() - 2 * dist,
                    0.0,
                    -1.0
                );
                projectilesList.add(p1);
                projectilesList.add(p2);
                projectilesList.add(p3);
            } else {
                // Disparo normal
                PlayerProjectile p = new PlayerProjectile(
                    getX(),
                    getY() - 2 * GameObject.PLAYER_RADIUS,
                    0.0,
                    -1.0
                );
                projectilesList.add(p);
            }
            nextShoot = currentTime + 100; // Define o cooldown do disparo
        }
    }
    
    @Override
    public Color getColor() { // Implementa o método abstrato de GameObject/SpaceShip
        return Color.BLUE;
    }
    
    @Override
    public void drawShape(){
        GameLib.drawPlayer(getX(), getY(), getRadius()); 
        healthBar.drawShape();
    }
    
    @Override
    public void draw(){
        super.draw(); // Desenha o player ou explosão
        if(isStateTrue(GameObject.ACTIVE) && hasShield){
            GameLib.setColor(Color.CYAN.brighter()); 
            GameLib.drawCircle(getX(), getY(), radius + 5); // Desenha um círculo ao redor para o escudo
        }
    }
}