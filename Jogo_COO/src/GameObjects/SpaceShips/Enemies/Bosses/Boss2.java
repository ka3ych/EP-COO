package GameObjects.SpaceShips.Enemies.Bosses;

import java.awt.Color;
import java.util.List;
import java.util.Random;

import GameObjects.GameManager;
import GameObjects.Colliders.CollideWithPlayer;
import GameLib.GameLib;
import GameObjects.Projectiles.EnemyProjectile;
import GameObjects.SpaceShips.Player;


public class Boss2 extends Boss{
    public static final double HEALTH_BAR_SIZE = 20.0;
    public static final int INITIAL_HEALTH = 10;
    public static final double BOSS2_RADIUS = 30.0;

    // atributos
    long timePassed = 0;
    long changeDirectionTime = 1500; // tempo para mudar direção
    double newX, newY;
    int xOrientation = 1; // 1 para direita, -1 para esquerda
    int yOrientation = 1; // 1 para baixo, -1 para cima
    private double movementOffset = 0; 

    // construtor
    public Boss2(double x, double y, double escalarVelocity, double angle, double velocityRotation){
        super(x, y, escalarVelocity, angle, velocityRotation, BOSS2_RADIUS, 1.0, INITIAL_HEALTH, HEALTH_BAR_SIZE);
        color = Color.MAGENTA;
    }

    // métodos
    public void drawShape(){
        // Define a cor antes de desenhar
        GameLib.setColor(color);
        double size = getRadius() * 2;
        // Desenha o quadrado (boss)
        GameLib.fillRect(getX(), getY(), size, size);
        // Desenha a barra de vida
        healthBar.drawShape();
    }

    public void moveAndDirection(long time){
        // Limite vertical máximo (80% da altura da tela)
        double maxY = GameLib.HEIGHT * 0.80;
        
        double positionsX[] = {
            GameLib.WIDTH / 6,
            GameLib.WIDTH / 6 * 2,
            GameLib.WIDTH / 6 * 3,
            GameLib.WIDTH / 6 * 4,
            GameLib.WIDTH / 6 * 5
        };
        double positionsY[] = {
            GameLib.HEIGHT / 10,
            GameLib.HEIGHT / 10 * 2,
            GameLib.HEIGHT / 10 * 3,
            // Ajustado para não passar de 80% da tela
            Math.min(maxY, GameLib.HEIGHT / 10 * 4),
            Math.min(maxY, GameLib.HEIGHT / 10 * 5)
        };

        Random rand = new Random();
        movementOffset += 0.001 * time; // Movimento senoidal

        // Fator de redução de velocidade (60% da velocidade original)
        double speedFactor = 0.6;
        
        if(timePassed > changeDirectionTime){
            double waveEffect = Math.sin(movementOffset) * 50; // Efeito de onda
            
            if(((xOrientation == 1 && x < newX) || (xOrientation == -1 && x > newX)) && 
               ((yOrientation == 1 && y < newY) || (yOrientation == -1 && y > newY))){
                x += speedFactor * getEscalarVelocity() * time * xOrientation;
                y += speedFactor * getEscalarVelocity() * time * yOrientation + waveEffect;
                angle += getRotationVelocity() * time;
            }
            else if((xOrientation == 1 && x < newX) || (xOrientation == -1 && x > newX)){
                x += speedFactor * getEscalarVelocity()  * time * xOrientation;
                y += waveEffect;
                angle += getRotationVelocity() * time;
            }
            else if((yOrientation == 1 && y < newY) || (yOrientation == -1 && y > newY)){
                y += speedFactor * getEscalarVelocity() * time * yOrientation + waveEffect;
                angle += getRotationVelocity() * time;
            }
            else{
                timePassed = 0;
            }
        }
        else{ 
            newX = positionsX[rand.nextInt(positionsX.length)];
            newY = positionsY[rand.nextInt(positionsY.length)];

            // Limitar posições para não sair da tela
            newX = Math.max(BOSS2_RADIUS, Math.min(GameLib.WIDTH - BOSS2_RADIUS, newX));
            newY = Math.max(BOSS2_RADIUS, Math.min(maxY - BOSS2_RADIUS, newY)); // Limite vertical de 80%

            xOrientation = newX < x ? -1 : 1;
            yOrientation = newY < y ? -1 : 1;
        }

        // Garantir que o boss não saia da tela e não desça além de 80%
        x = Math.max(BOSS2_RADIUS, Math.min(GameLib.WIDTH - BOSS2_RADIUS, x));
        y = Math.max(BOSS2_RADIUS, Math.min(maxY - BOSS2_RADIUS, y)); // Limite vertical de 80%

        timePassed += time;
    }

    public void shoot(List<EnemyProjectile> enemyProjectiles, List<CollideWithPlayer> colideComPlayer, Player player) {
        if(System.currentTimeMillis() > getNextShoot()){
            // Disparo direcionado para baixo com pequeno espalhamento
            for(int i = -1; i <= 1; i++) {
                // Componente horizontal (pequeno espalhamento)
                double vx = i * 0.05; // 5% de velocidade lateral
                // Componente vertical principal (para baixo)
                double vy = 0.30; // Velocidade fixa para baixo
                
                EnemyProjectile proj = new EnemyProjectile(
                    getX(),
                    getY(),
                    vx,
                    vy
                );
                proj.setColor(Color.CYAN);
                enemyProjectiles.add(proj);
                colideComPlayer.add(proj);
            }
            
            setNextShoot((long)(System.currentTimeMillis() + 1500));
        }
    }

    @Override
    public void explode(double timeExplosionStart, double timeExplosionEnd){
        state = EXPLODING;
        this.explosionStart = timeExplosionStart;
        this.explosionEnd = timeExplosionEnd;
        GameManager.loadLevel("Jogo_COO/src/fases/fase3.txt");
    }
}