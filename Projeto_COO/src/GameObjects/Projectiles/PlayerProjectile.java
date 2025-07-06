package GameObjects.Projectiles;

import java.awt.Color;
import GameLib.GameLib;
import GameObjects.SpaceShips.Enemies.Enemy;

public class PlayerProjectile extends Projectile{

    // construtor
    public PlayerProjectile(double x, double y, double vx, double vy){
        super(x, y, vx, vy);
        color = Color.GREEN; // cor do projetil do player
    }

    // métodos
    public void drawShape(){
        if(isStateTrue(ACTIVE)){
            GameLib.drawLine(getX(), getY() - 5, getX(), getY() + 5);
            GameLib.drawLine(getX() - 1, getY() - 3, getX() - 1, getY() + 3);
            GameLib.drawLine(getX() + 1, getY() - 3, getX() + 1, getY() + 3);
        }
    }

    public void collideEnemy(Enemy enemy) {
        if(isStateTrue(ACTIVE) && checkerCollider.checkCollision(getX(), getY(), radius, enemy.getX(), enemy.getY(), enemy.getRadius())) {
            enemy.hit(ENEMY_EXPLOSION_DURATION);
            state = INACTIVE; // Desativa o projetil após colidir   
        }
    }
}
