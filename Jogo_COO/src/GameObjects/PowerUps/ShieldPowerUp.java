package GameObjects.PowerUps;

import GameLib.GameLib;
import GameObjects.SpaceShips.Player;
import GameObjects.PowerUps.base.PowerUp;
import GameObjects.GameObject;

import java.awt.Color;

public class ShieldPowerUp extends PowerUp {

    public ShieldPowerUp(double x, double y, double vx, double vy, double lifespan) {
        super(x, y, GameObject.POWERUP_RADIUS, vx, vy, lifespan);
    }

    @Override
    public void applyEffect(Player player) {
        player.activateShield(); // Método a ser criado no Player
        System.out.println("Power-up de Escudo Coletado!"); // Para depuração
    }

    @Override
    public void drawShape() {
        if (isStateTrue(GameObject.ACTIVE)) {
            GameLib.setColor(Color.ORANGE); // Cor do power-up
            GameLib.drawCircle(x, y, radius); // Pode ser um círculo
            GameLib.drawText("ESCUDO", x, y); // Texto para identificar
        }
    }
}