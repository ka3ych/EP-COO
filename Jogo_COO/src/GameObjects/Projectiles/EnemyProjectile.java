package GameObjects.Projectiles;

import java.awt.Color;
import GameLib.GameLib;
import GameObjects.SpaceShips.Player;
import GameObjects.Colliders.CollideWithPlayer;


public class EnemyProjectile extends Projectile implements CollideWithPlayer{

        // construtor
        public EnemyProjectile(double x, double y, double vx, double vy){
            super(x, y, vx, vy);
            color = Color.RED; // cor do projetil do inimigo
        }

        // métodos
        public void drawShape(){
            if(isStateTrue(ACTIVE)) {
                GameLib.drawCircle(getX(), getY(), PROJECTILE_RADIUS);
            }
        }

        @Override
        public void colideWithPlayer(Player player) {
            if(player.isStateTrue(ACTIVE) && isStateTrue(ACTIVE) && checkerCollider.checkCollision(getX(), getY(), radius, player.getX(), player.getY(), player.getRadius())) {
                player.hit(PLAYER_EXPLOSION_DURATION);
                state = INACTIVE; // Desativa o projetil após colidir
            }
        }
    }