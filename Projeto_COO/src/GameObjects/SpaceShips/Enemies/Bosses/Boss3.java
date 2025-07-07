package GameObjects.SpaceShips.Enemies.Bosses;

import java.awt.Color;
import java.util.List;
import java.util.Random;

import GameObjects.GameManager;
import GameObjects.Colliders.CollideWithPlayer;
import GameLib.GameLib;
import GameObjects.Projectiles.EnemyProjectile;
import GameObjects.SpaceShips.Player;

public class Boss3 extends Boss {
    public static final double HEALTH_BAR_SIZE = 20.0;
    public static final int INITIAL_HEALTH = 10;
    public static final double BOSS3_RADIUS = 30.0;
    
    // Atributos para movimento circular
    private double centerX, centerY;
    private double radius = 150.0;
    private double angle = 0;
    private double angularSpeed = 0.001;
    private int direction = 1;
    private long lastDirectionChange = 0;
    private long directionChangeInterval = 3000;
    private double movementOffset = 0;
    private int shotCount = 0; // Contador para padrões alternados de tiro

    public Boss3(double x, double y, double escalarVelocity, double angle, double velocityRotation) {
        super(x, y, escalarVelocity, angle, velocityRotation, BOSS3_RADIUS, 1.0, INITIAL_HEALTH, HEALTH_BAR_SIZE);
        color = Color.YELLOW;
        
        centerX = GameLib.WIDTH / 2;
        centerY = GameLib.HEIGHT * 0.4;
        
        this.x = centerX + radius * Math.cos(this.angle);
        this.y = centerY + radius * Math.sin(this.angle);
    }

    public void drawShape() {
        if(isStateTrue(ACTIVE)){
            GameLib.setColor(color);
            double size = getRadius() * 2;
            
            double baseLeftX = x - size/2;
            double baseLeftY = y - size/2;
            double baseRightX = x + size/2;
            double baseRightY = y - size/2;
            double bottomX = x;
            double bottomY = y + size/2;
            
            GameLib.drawLine(baseLeftX, baseLeftY, baseRightX, baseRightY);
            GameLib.drawLine(baseRightX, baseRightY, bottomX, bottomY);
            GameLib.drawLine(bottomX, bottomY, baseLeftX, baseLeftY);
            
            healthBar.drawShape();
        }
    }

    public void moveAndDirection(long delta) {
        double maxY = GameLib.HEIGHT * 0.80;
        
        if (System.currentTimeMillis() - lastDirectionChange > directionChangeInterval) {
            Random rand = new Random();
            direction = rand.nextBoolean() ? 1 : -1;
            lastDirectionChange = System.currentTimeMillis();
            directionChangeInterval = 2000 + rand.nextInt(3000);
        }
        
        angle += angularSpeed * direction * delta;
        
        if (angle > 2 * Math.PI) angle -= 2 * Math.PI;
        if (angle < 0) angle += 2 * Math.PI;
        
        movementOffset += 0.0005 * delta;
        double waveEffect = Math.sin(movementOffset) * 20;
        
        x = centerX + radius * Math.cos(angle);
        y = centerY + radius * Math.sin(angle) + waveEffect;
        
        y = Math.max(BOSS3_RADIUS, Math.min(maxY - BOSS3_RADIUS, y));
        
        this.angle += getRotationVelocity() * delta;
    }

    public void shoot(List<EnemyProjectile> enemyProjectiles, List<CollideWithPlayer> colideComPlayer, Player player) {
        if(System.currentTimeMillis() > getNextShoot()){
            shotCount++; // Alterna entre padrões de tiro
            
            if(shotCount % 2 == 0) {
                // Padrão 1: Disparo radial completo
                fireRadialShots(enemyProjectiles, colideComPlayer, 12, 0.25);
            } else {
                // Padrão 2: Disparo em espiral
                fireSpiralShots(enemyProjectiles, colideComPlayer, 8, 0.3);
            }
            
            setNextShoot((long)(System.currentTimeMillis() + 1500));
        }
    }

    // Dispara projéteis em todas as direções (radial)
    private void fireRadialShots(List<EnemyProjectile> enemyProjectiles, 
                                List<CollideWithPlayer> colideComPlayer, 
                                int numProjectiles, double speed) {
        for(int i = 0; i < numProjectiles; i++) {
            double angle = 2 * Math.PI * i / numProjectiles;
            double vx = Math.cos(angle) * speed;
            double vy = Math.sin(angle) * speed;
            
            EnemyProjectile proj = new EnemyProjectile(x, y, vx, vy);
            proj.setColor(Color.CYAN);
            enemyProjectiles.add(proj);
            colideComPlayer.add(proj);
        }
    }

    // Dispara projéteis em padrão espiral
    private void fireSpiralShots(List<EnemyProjectile> enemyProjectiles, 
                                List<CollideWithPlayer> colideComPlayer, 
                                int numArms, double speed) {
        double spiralAngle = shotCount * 0.5; // Ângulo base que aumenta com o tempo
        
        for(int i = 0; i < numArms; i++) {
            double angle = spiralAngle + 2 * Math.PI * i / numArms;
            double vx = Math.cos(angle) * speed;
            double vy = Math.sin(angle) * speed;
            
            EnemyProjectile proj = new EnemyProjectile(x, y, vx, vy);
            proj.setColor(new Color(0, 200, 255)); // Azul mais claro
            enemyProjectiles.add(proj);
            colideComPlayer.add(proj);
        }
    }

    @Override
    public void explode(double timeExplosionStart, double timeExplosionEnd){
        state = EXPLODING;
        this.explosionStart = timeExplosionStart;
        this.explosionEnd = timeExplosionEnd;
        GameManager.loadLevel("./src/fases/freeplay.txt");
    }
}