package GameObjects.BackgroundObjects;

import java.awt.Color;


import GameLib.GameLib;
import GameObjects.SpaceShips.*;

public class HealthBar extends BackgroundObject {
    protected SpaceShip spaceShip;

    // construtor
    public HealthBar(SpaceShip spaceShip) {
        this.spaceShip = spaceShip;
    }

    // métodos
    public void drawShape() {
        GameLib.setColor(Color.GREEN);
        GameLib.drawLine(spaceShip.getX()-spaceShip.getHealthBarSize()/2, spaceShip.getY()-spaceShip.getRadius()-5, (spaceShip.getX()-spaceShip.getHealthBarSize()/2)+spaceShip.getHealthBarSize()*((double)spaceShip.getHealthPoints()/spaceShip.getInitialHealth()), spaceShip.getY()-spaceShip.getRadius()-5);
        GameLib.setColor(Color.RED);
        GameLib.drawLine((spaceShip.getX()-spaceShip.getHealthBarSize()/2)+spaceShip.getHealthBarSize()*((double)spaceShip.getHealthPoints()/spaceShip.getInitialHealth()), spaceShip.getY()-spaceShip.getRadius()-5, (spaceShip.getX()-spaceShip.getHealthBarSize()/2)+spaceShip.getHealthBarSize(), spaceShip.getY()-spaceShip.getRadius()-5);
    }

    // setters
    public void setSpaceShip(SpaceShip spaceShip) {
        this.spaceShip = spaceShip;
    }

    // getters
    public SpaceShip getSpaceShip() {
        return spaceShip;
    }
}
