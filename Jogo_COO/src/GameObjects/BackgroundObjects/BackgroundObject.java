package GameObjects.BackgroundObjects;

import java.awt.Color;
import GameLib.GameLib;

public abstract class BackgroundObject{
    // atributos
    protected double x, y; // Posição
    protected Color color; // cor do objeto

    // métodos
    public void draw(double background_count){
        GameLib.setColor(getColor());
        drawShape(background_count);
    }

    public abstract void drawShape(double background_count);

    // setters
    public void setColor(Color color){
        this.color = color;
    }

    // getters
    public double getX() {return x;}
    public double getY() {return y;}
    public Color getColor() {return color;}
}

