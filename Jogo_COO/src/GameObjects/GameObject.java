package GameObjects;

import java.awt.Color;
import GameLib.*;
import GameObjects.Colliders.*;


public abstract class GameObject{
    /* Constantes relacionadas aos estados que os elementos do jogo (player, projeteis ou inimigos) podem assumir. */

    public static final int INACTIVE = 0;
    public static final int ACTIVE = 1;
    public static final int EXPLODING = 2;
    
    /* constantes para temporização e duração de explosões    */
    public static final long PLAYER_EXPLOSION_DURATION = 2000;
    public static final long ENEMY_EXPLOSION_DURATION = 500;
    
    /* constantes para propriedades do player, inimigos e projetil    */
    public static final double PLAYER_INITIAL_VELOCITY = 0.25;
    public static final double PLAYER_RADIUS = 12.0;
    public static final double ENEMY1_RADIUS = 9.0;
    public static final double ENEMY2_RADIUS = 12.0;
    public static final double PROJECTILE_RADIUS = 2.0;
    public static final double COLLISION_FACTOR = 0.8;

    // atributos
    protected double x, y; // Posição
    protected double radius; // raio/tamanho de um GameObject
    protected double damage; // quanto de dano que um GameObject aplicará em outro GameObject 
    protected int healthPoints; // número de vidas
    protected int state; // estado do objeto
    protected Color color; // cor do objeto
    protected CheckerCollider checkerCollider = new CheckerCollider(); // objeto que verifica colisões

    // métodos 
    public boolean isStateTrue(int state){ // verifica se o estado do objeto é igual ao estado passado como parâmetro
        if(state == this.state) return true;
        else return false;
    }

    public abstract void drawShape(); // desenha a forma do objeto

    public void draw(){ // desenha o objeto
        GameLib.setColor(getColor());
        drawShape();
    }

    // setters
    public void setColor(Color color) {this.color = color;} // define a cor do objeto

    // getters
    public double getX() {return x;}
    public double getY() {return y;}
    public double getRadius() {return radius;}
    public Color getColor() {return color;}
    public double getDamage() {return damage;}
    public int getHealthPoints() {return healthPoints;}
    public int getState() {return state;}
}