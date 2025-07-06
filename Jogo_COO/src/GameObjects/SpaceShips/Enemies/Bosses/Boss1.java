package GameObjects.SpaceShips.Enemies.Bosses;

import java.awt.Color;
import java.util.List;
import java.util.Random;

import GameObjects.Colliders.CollideWithPlayer;
import GameLib.GameLib;
import GameObjects.Projectiles.EnemyProjectile;
import GameObjects.SpaceShips.Player;
import GameObjects.GameManager;


public class Boss1 extends Boss{
    public static final double HEALTH_BAR_SIZE = 20.0;
    public static final int INITIAL_HEALTH = 10;
    public static final double BOSS1_RADIUS = 20.0;

    // atributos
    long timePassed = 0;
    long changeDirectionTime = 1500; // tempo para mudar direção
    double newX, newY;
    int xOrientation = 1; // 1 para direita, -1 para esquerda
    int yOrientation = 1; // 1 para baixo, -1 para cima

    // construtor
    public Boss1(double x, double y, double escalarVelocity, double angle, double velocityRotation){
        super(x, y, escalarVelocity, angle, velocityRotation, BOSS1_RADIUS, 1.0, INITIAL_HEALTH, HEALTH_BAR_SIZE);
        color = Color.RED; // cor do inimigo tipo 1
    }

    // métodos
    public void drawShape(){
        GameLib.drawCircle(getX(), getY(), getRadius());
        healthBar.drawShape();
    }

    public void moveAndDirection(long time){
        double positionsX[] = {
            GameLib.WIDTH / 6,
            GameLib.WIDTH / 6 * 2,
            GameLib.WIDTH / 6 * 3,
            GameLib.WIDTH / 6 * 4,
            GameLib.WIDTH / 6 * 5
        };
        double positionsY[] = {
            GameLib.HEIGHT / 10,
            GameLib.HEIGHT / 10 * 2,
            GameLib.HEIGHT / 10 * 3,
            GameLib.HEIGHT / 10 * 4,
            GameLib.HEIGHT / 10 * 5
        };

        Random rand = new Random();

        if(timePassed > changeDirectionTime){
            if(((xOrientation == 1 && x < newX) || (xOrientation == -1 && x > newX)) && ((yOrientation == 1 && y < newY) || (yOrientation == -1 && y > newY))){
                x += getEscalarVelocity() * time * xOrientation;
                y += getEscalarVelocity() * time * yOrientation;
                angle += getRotationVelocity() * time;
            }
            else if((xOrientation == 1 && x < newX) || (xOrientation == -1 && x > newX)){
                x += getEscalarVelocity()  * time * xOrientation;
                angle += getRotationVelocity() * time;
            }
            else if((yOrientation == 1 && y < newY) || (yOrientation == -1 && y > newY)){
                y += getEscalarVelocity() * time * yOrientation;
                angle += getRotationVelocity() * time;
            }
            else{
                timePassed = 0;
            }
        }
        else{ 
            // Escolhe uma posição inicial aleatória
            newX = positionsX[rand.nextInt(positionsX.length)];
            newY = positionsY[rand.nextInt(positionsY.length)];

            xOrientation = newX<x ? -1 : 1;
            yOrientation = newY<y ? -1 : 1;
        }

        timePassed += time;
        
    }

    public void shoot(List<EnemyProjectile> enemyProjectiles, List<CollideWithPlayer> colideComPlayer, Player player) {
        // disparo de projetil
        if(System.currentTimeMillis() > getNextShoot()){
            //Random rand = new Random();
            EnemyProjectile proj = new EnemyProjectile(
                getX(),
                getY(),
                (player.getX() - getX())/1500,
                (player.getY() - getY())/1500
                // Math.cos(getAngle()) * 0.45,
                // Math.sin(getAngle()) * 0.45 * (-1.0)
            );
            
            enemyProjectiles.add(proj);
            colideComPlayer.add(proj);
            setNextShoot((long)(System.currentTimeMillis() + 1500));
            //System.out.println(proj.getX() + " " + proj.getY() + " " + getX() + " " + getY());
        }
    }

    @Override
    public void explode(double timeExplosionStart, double timeExplosionEnd){
        state = EXPLODING;
        this.explosionStart = timeExplosionStart;
        this.explosionEnd = timeExplosionEnd;
        GameManager.loadLevel("Jogo_COO/src/fases/fase2.txt");
    }
}