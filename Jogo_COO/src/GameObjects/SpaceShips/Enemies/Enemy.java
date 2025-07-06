package GameObjects.SpaceShips.Enemies;

import GameLib.GameLib;
import GameObjects.SpaceShips.SpaceShip;
import GameObjects.Colliders.CollideWithPlayer;
import GameObjects.SpaceShips.Player;

// inimigo
public abstract class Enemy extends SpaceShip implements CollideWithPlayer{
    //protected int type; // tipo 1 ou 2
    protected double v; // velocidade escalar
    protected double angle; // angulo da direcao
    protected double vr; // velocidade rotacao

    // construtor
    public Enemy(double x, double y, double escalarVelocity, double angle, double velocityRotation, double radius, double damage, int healthPoints, double healthBarSize) {
        //this.type = type;
        this.x = x;
        this.y = y;
        this.v = escalarVelocity;
        this.angle = angle;
        this.vr = velocityRotation;
        this.state = ACTIVE;
        this.radius = radius;
        this.damage = damage;
        this.healthPoints = healthPoints;
        this.healthBarSize = healthBarSize;
        this.initialHealth = healthPoints;
    }

    // métodos
    abstract public void moveAndDirection(long time);
    

    @Override
    public void colideWithPlayer(Player player) {
        if(isStateTrue(ACTIVE) && player.isStateTrue(ACTIVE) && checkerCollider.checkCollision(getX(), getY(), radius, player.getX(), player.getY(), player.getRadius())) {
            player.hit(PLAYER_EXPLOSION_DURATION);
            hit(ENEMY_EXPLOSION_DURATION);
        }
    }
    

    public void changeDirection(){
        if(x < GameLib.WIDTH / 2) vr = 0.003;
        else vr = -0.003;
    }

    // setters
    public void setNextShoot(long cooldown) {nextShoot = cooldown;}
    public void setAngle(double angle) {this.angle = angle;}
    public void setVelocityRotation(double vr) {this.vr = vr;}

    
    // setter de vida
    public void setVida(int vida) {
        this.healthPoints = vida;
        this.initialHealth = vida;
    }

    // getter de vida
    public int getVida() {
        return this.healthPoints;
    }

    public void reseting(){
        state = INACTIVE;
    }

    // getters
    public double getEscalarVelocity(){return v;}
    public double getAngle(){return angle;}
    public double getRotationVelocity(){return vr;}
    //public int getType(){return type;}
}
