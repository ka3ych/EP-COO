package GameObjects.BackgroundObjects;

import java.awt.Color;

public abstract class BackgroundObject{
    // atributos
    protected double x, y; // Posição
    protected Color color; // cor do objeto


    // setters
    public void setColor(Color color){
        this.color = color;
    }

    // getters
    public double getX() {return x;}
    public double getY() {return y;}
    public Color getColor() {return color;}
}

