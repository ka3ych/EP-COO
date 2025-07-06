package GameObjects.SpaceShips.Enemies;

import java.awt.Color;
import java.util.List;
import java.util.Random;
import GameObjects.Colliders.CollideWithPlayer;
import GameLib.GameLib;
import GameObjects.Projectiles.EnemyProjectile;


public class Enemy1 extends Enemy{
        // construtor
        public Enemy1(double x, double y, double escalarVelocity, double angle, double velocityRotation){
            super(x, y, escalarVelocity, angle, velocityRotation, ENEMY1_RADIUS, 1.0, 1, 20);
            color = Color.CYAN; // cor do inimigo tipo 1
        }

        // métodos
        public void drawShape(){
            if(isStateTrue(ACTIVE)){
                GameLib.drawCircle(getX(), getY(), getRadius());
            }
        }

        public void moveAndDirection(long time){
            x += getEscalarVelocity() * Math.cos(getAngle()) * time;
            y += getEscalarVelocity() * Math.sin(getAngle()) * time * (-1.0);
            angle += getRotationVelocity() * time;
            outOfBounds();
        }

        public void shoot(List<EnemyProjectile> enemyProjectiles, List<CollideWithPlayer> colideComPlayer) {
            // disparo de projetil
            if(System.currentTimeMillis() > getNextShoot()){
                Random rand = new Random();
                EnemyProjectile proj = new EnemyProjectile(
                    getX(),
                    getY(),
                    Math.cos(getAngle()) * 0.45,
                    Math.sin(getAngle()) * 0.45 * (-1.0)
                );
                
                enemyProjectiles.add(proj);
                colideComPlayer.add(proj);
                setNextShoot((long)(System.currentTimeMillis() + 200 + rand.nextDouble() * 500));
            }
        }
    }