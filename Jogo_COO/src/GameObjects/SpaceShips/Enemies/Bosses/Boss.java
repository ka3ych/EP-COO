package GameObjects.SpaceShips.Enemies.Bosses;

import GameObjects.SpaceShips.Enemies.Enemy;
import GameObjects.BackgroundObjects.HealthBar;

public abstract class Boss extends Enemy{
    // atributos
    protected HealthBar healthBar = new HealthBar(this);
    

    // construtor
    public Boss(double x, double y, double escalarVelocity, double angle, double velocityRotation, double radius, double damage, int healthPoints, double healthBarSize) {
        super(x, y, escalarVelocity, angle, velocityRotation, radius, damage, healthPoints, healthBarSize);
        this.initialHealth = healthPoints;
    }
        public void setVida(int vida) {
        this.healthPoints = vida;
        this.initialHealth = vida; // importante manter proporção correta na barra
    }
}