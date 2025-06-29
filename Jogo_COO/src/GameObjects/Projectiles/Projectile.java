package GameObjects.Projectiles;

import GameObjects.GameObject;


public abstract class Projectile extends GameObject{
    private double vx, vy; // velocidade

    public Projectile(double x, double y, double vx, double vy){
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.state = ACTIVE;
        radius = PROJECTILE_RADIUS; // raio do projetil
        this.damage = 1.0; // dano do projetil 
    }

    public void move(long delta){
        x += vx * delta;
        y += vy * delta;
    }

    // getters
    public double getVelocityX() {return this.vx;}
    public double getVelocityY() {return this.vy;}
}