package GameObjects.PowerUps.base;

import GameLib.GameLib;
import GameObjects.SpaceShips.Player; // Para o método colideWithPlayer
import GameObjects.GameObject; // Para as constantes de estado

// import java.awt.Color; // Para desenhar

public abstract class PowerUp extends GameObject {
    protected double vx, vy; // Velocidade de movimento do power-up (pode ser 0 para alguns)
    protected double lifespan; // Tempo de vida do power-up na tela se não for coletado
    protected long spawnTime; // Quando o power-up apareceu
    protected long effectDuration; // Duração do efeito após coletado (se aplicável)

    public PowerUp(double x, double y, double radius, double vx, double vy, double lifespan) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.vx = vx;
        this.vy = vy;
        this.lifespan = lifespan;
        this.state = GameObject.ACTIVE;
        this.spawnTime = System.currentTimeMillis();
        // healthPoints e damage podem ser 0 ou não relevantes para power-ups
        this.healthPoints = 1; // Pode ser útil para indicar se foi coletado (setar para INACTIVE)
        this.damage = 0; // Power-ups não causam dano
    }

    public void move(long delta) {
        x += vx * delta;
        y += vy * delta;
    }

    // Método abstrato para aplicar o efeito ao jogador
    public abstract void applyEffect(Player player);

    // Método para desenhar (pode ser abstrato ou ter uma implementação default)
    @Override
    public abstract void drawShape();

    // Método para colisão com o jogador
    public void colideWithPlayer(Player player) {
        if (isStateTrue(GameObject.ACTIVE) && GameLib.checkCollision(getX(), getY(), radius, player.getX(), player.getY(), player.getRadius())) {
            applyEffect(player);
            this.state = GameObject.INACTIVE; // Desativa o power-up após coleta
        }
    }

    // Getters para novos atributos
    public double getLifespan() { return lifespan; }
    public long getSpawnTime() { return spawnTime; }
    public long getEffectDuration() { return effectDuration; }
}