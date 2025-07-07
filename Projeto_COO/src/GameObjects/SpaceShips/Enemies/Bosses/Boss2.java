package GameObjects.SpaceShips.Enemies.Bosses;

import java.awt.Color;
import java.util.List;
import java.util.Random;

import GameObjects.GameManager;
import GameObjects.Colliders.CollideWithPlayer;
import GameLib.GameLib;
import GameObjects.Projectiles.EnemyProjectile;
import GameObjects.SpaceShips.Player;


public class Boss2 extends Boss{
    public static final double HEALTH_BAR_SIZE = 20.0;
    public static final int INITIAL_HEALTH = 10;
    public static final double BOSS2_RADIUS = 30.0;

    // atributos
    long timePassed = 0;
    long changeDirectionTime = 1500; // tempo para mudar direção
    long showTpTime = changeDirectionTime/2;
    double newX, newY;
    int xOrientation = 1; // 1 para direita, -1 para esquerda
    int yOrientation = 1; // 1 para baixo, -1 para cima
    double size = getRadius() * 2;

    // construtor
    public Boss2(double x, double y, double escalarVelocity, double angle, double velocityRotation){
        super(x, y, escalarVelocity, angle, velocityRotation, BOSS2_RADIUS, 1.0, INITIAL_HEALTH, HEALTH_BAR_SIZE);
        color = Color.MAGENTA;
    }

    // métodos
    public void drawShape(){
        if(isStateTrue(ACTIVE)){
            // Define a cor antes de desenhar
            GameLib.setColor(color);
            double size = getRadius() * 2;
            // Desenha o quadrado (boss)
            GameLib.fillRect(getX(), getY(), size, size);
            // Desenha a barra de vida
            healthBar.drawShape();
        }

        
    }

    public void moveAndDirection(long time){
        Random rand = new Random();

        // Fator de redução de velocidade (60% da velocidade original)
        //double speedFactor = 0.6;
        
        if(timePassed > changeDirectionTime){
            x = newX;
            y = newY;
            timePassed = 0;
        }else if(timePassed <= showTpTime){
            newX = rand.nextDouble(0+size/2, GameLib.WIDTH-size/2);
            newY = rand.nextDouble(0+size/2, GameLib.HEIGHT-size/2);
        }
        else{ 
            GameLib.setColor(color);
            

            // Desenha o quadrado (boss)
            GameLib.drawLine(newX-size/2, newY+size/2, newX+size/2, newY+size/2);
            GameLib.drawLine(newX-size/2, newY-size/2, newX+size/2, newY-size/2);
            GameLib.drawLine(newX-size/2, newY-size/2, newX-size/2, newY+size/2);
            GameLib.drawLine(newX+size/2, newY-size/2, newX+size/2, newY+size/2);
        }

        timePassed += time;
    }

    public void shoot(List<EnemyProjectile> enemyProjectiles, List<CollideWithPlayer> colideComPlayer, Player player) {
        if(System.currentTimeMillis() > getNextShoot()){
            // Disparo direcionado para baixo com pequeno espalhamento
            for(int i = -1; i <= 1; i++) {
                // Componente horizontal (pequeno espalhamento)
                double vx = i * 0.05; // 5% de velocidade lateral
                // Componente vertical principal (para baixo)
                double vy = 0.30; // Velocidade fixa para baixo
                
                EnemyProjectile proj = new EnemyProjectile(
                    getX(),
                    getY(),
                    vx,
                    vy
                );
                proj.setColor(Color.CYAN);
                enemyProjectiles.add(proj);
                colideComPlayer.add(proj);
            }
            
            setNextShoot((long)(System.currentTimeMillis() + 1500));
        }
    }

    @Override
    public void explode(double timeExplosionStart, double timeExplosionEnd){
        state = EXPLODING;
        this.explosionStart = timeExplosionStart;
        this.explosionEnd = timeExplosionEnd;
        GameManager.loadLevel("./src/fases/fase3.txt");
    }
}