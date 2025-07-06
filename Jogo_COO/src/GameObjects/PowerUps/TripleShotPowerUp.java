package GameObjects.PowerUps;

import GameLib.GameLib;
import GameObjects.SpaceShips.Player; 
import GameObjects.PowerUps.base.PowerUp; 
import GameObjects.GameObject; 

import java.awt.Color; 

public class TripleShotPowerUp extends PowerUp {

    private static final long DEFAULT_EFFECT_DURATION = 10000; // 10 segundos para o disparo triplo

    public TripleShotPowerUp(double x, double y, double vx, double vy, double lifespan) {
        super(x, y, GameObject.POWERUP_RADIUS, vx, vy, lifespan);
        this.effectDuration = DEFAULT_EFFECT_DURATION; // a duração do efeito
    }

    @Override
    public void applyEffect(Player player) {
        player.activateDoubleShot(this.effectDuration);
        System.out.println("Power-up de Disparo Triplo Coletado!"); // utilizado para depuração
    }

    @Override
    public Color getColor() {
        return Color.PINK; 
    }

    @Override
    public void drawShape() {
        if (isStateTrue(GameObject.ACTIVE)) {
            GameLib.drawDiamond(x, y, radius + 15.0); // Desenha um diamante
            GameLib.drawText("3X", x, y, 12f); // Texto para identificar (3x disparos)
        }
    }
}