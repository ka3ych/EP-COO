package GameObjects.SpaceShips;
import GameLib.GameLib;
import java.awt.Color;

public class Player extends SpaceShip{
    double invincibleTime = 2000;
    double timePassed = 0;
    // construtor do Player
    public Player(double x, double y, long nextShoot){
        this.x = x;
        this.y = y;
        this.nextShoot = nextShoot;
        state = ACTIVE;
        this.radius = PLAYER_RADIUS;
        this.damage = 1.0;
        this.healthPoints = 1;
        initialHealth = healthPoints;
        color = Color.BLUE; // cor do player
        this.healthBarSize = 20; // tamanho da barra de vida do player
    }

    public void moveX(double varX){this.x += varX;}

    public void moveY(double varY){this.y += varY;}

    public void shoot(long time){nextShoot = time + 100;}

    public void activate(long time){
        if(isStateTrue(EXPLODING)) state = INVINCIBLE;

        if(timePassed >= invincibleTime){
            timePassed = 0;
            revive();
        }
        else timePassed += time;
    }

    public void revive(){
        this.healthPoints = 1;
        this.state = ACTIVE;
    }

    public void outOfBounds(){
        if(this.getX() < 0.0) this.x = 0.0;
        if(this.getX() >= GameLib.WIDTH) this.x = GameLib.WIDTH - 1;
        if(this.getY() < 25.0) this.y = 25.0;
        if(this.getY() >= GameLib.HEIGHT) this.y = GameLib.HEIGHT - 1;
    }

    public void drawShape(){
        GameLib.drawPlayer(getX(), getY(), getRadius()); 

        if(isStateTrue(INVINCIBLE)){
            GameLib.setColor(Color.YELLOW);
            GameLib.drawCircle(getX(), getY(), getRadius() + 5);
        }
    }
    
}