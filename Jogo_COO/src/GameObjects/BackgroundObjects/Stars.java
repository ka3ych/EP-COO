package GameObjects.BackgroundObjects;

import java.awt.Color;
import GameLib.GameLib;


public class Stars extends BackgroundObject {
    // construtor
    public Stars(double x, double y, Color color){
        this.x = x;
        this.y = y;
        this.color = color; // cor das estrelas
    }

    public void drawShape(double background_count){
        double yPos = (getY() + background_count) % GameLib.HEIGHT;
        GameLib.fillRect(getX(), yPos, 3, 3);
    }
}
