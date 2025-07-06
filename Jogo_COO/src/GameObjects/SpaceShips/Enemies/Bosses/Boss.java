package GameObjects.SpaceShips.Enemies.Bosses;

import GameObjects.SpaceShips.Player;
import GameObjects.SpaceShips.Enemies.Enemy;

import java.util.List;

import GameObjects.GameManager;
import GameObjects.BackgroundObjects.HealthBar;
import GameObjects.Colliders.CollideWithPlayer;
import GameObjects.Projectiles.EnemyProjectile;

public abstract class Boss extends Enemy{
    // atributos
    protected HealthBar healthBar = new HealthBar(this);
    protected GameManager gameManager;
    

    // construtor
    public Boss(double x, double y, double escalarVelocity, double angle, double velocityRotation, double radius, double damage, int healthPoints, double healthBarSize) {
        super(x, y, escalarVelocity, angle, velocityRotation, radius, damage, healthPoints, healthBarSize);
        this.initialHealth = healthPoints;
    }

    abstract public void shoot(List<EnemyProjectile> enemyProjectiles, List<CollideWithPlayer> colideComPlayer, Player player);

    public void setVida(int vida) {
        this.healthPoints = vida;
        this.initialHealth = vida; // importante manter proporção correta na barra
    }
}