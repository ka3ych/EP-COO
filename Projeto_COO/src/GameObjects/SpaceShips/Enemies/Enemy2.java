package GameObjects.SpaceShips.Enemies;

import java.awt.Color;
import java.util.List;
import java.util.Random;
import GameObjects.Colliders.CollideWithPlayer;
import GameLib.GameLib;
import GameObjects.Projectiles.EnemyProjectile;

public class Enemy2 extends Enemy{
        boolean shootNow = false;

        // construtor
        public Enemy2(double x, double y, double escalarVelocity, double angle, double velocityRotation){
            super(x, y, escalarVelocity, angle, velocityRotation, ENEMY2_RADIUS, 1.0, 1, 20);
            color = Color.MAGENTA; // cor do inimigo tipo 2
        }

        // métodos
        public void drawShape(){
            if(isStateTrue(ACTIVE)){
                GameLib.drawDiamond(getX(), getY(), getRadius());
            }
        }

        public void moveAndDirection(long time){
            double previousY = getY();

            x += getEscalarVelocity() * Math.cos(getAngle()) * time;
            y += getEscalarVelocity() * Math.sin(getAngle()) * time * (-1.0);
            angle += getRotationVelocity() * time;


            double threshold = GameLib.HEIGHT * 0.30;
            if(previousY < threshold && getY() >= threshold) {
                changeDirection();
            }

            outOfBounds();
        }

        public void getShootPoints(){
            if(getRotationVelocity() > 0 && Math.abs(getAngle() - 3 * Math.PI) < 0.05) {
                setVelocityRotation(0.0);
                setAngle(3 * Math.PI);
                shootNow = true;
            }

            else if(getRotationVelocity() < 0 && Math.abs(getAngle()) < 0.05) {
                setVelocityRotation(0.0);
                setAngle(0);
                shootNow = true;
            }
        }

        public void shoot(List<EnemyProjectile> enemyProjectiles, List<CollideWithPlayer> colideComPlayer) {
            getShootPoints();
            
            if(shootNow){
                Random rand = new Random();
                double[] angles = { 
                    Math.PI/2 + Math.PI/8, 
                    Math.PI/2, 
                    Math.PI/2 - Math.PI/8 
                };
                for(double angle : angles){
                    double a = angle + rand.nextDouble() * Math.PI/6 - Math.PI/12;

                    EnemyProjectile proj = new EnemyProjectile(
                        getX(),
                        getY(),
                        Math.cos(a) * 0.30,
                        Math.sin(a) * 0.30
                    );

                    enemyProjectiles.add(proj);
                    colideComPlayer.add(proj);
                }
                shootNow = false; 
            }
            
        }
    }