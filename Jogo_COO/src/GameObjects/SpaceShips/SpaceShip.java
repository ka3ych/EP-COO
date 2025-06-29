package GameObjects.SpaceShips;

import GameObjects.GameObject;
import GameLib.GameLib;

public abstract class SpaceShip extends GameObject{
        // atributos
        protected long nextShoot; // proximo momento permitido para disparo
        protected double explosionStart, explosionEnd; // tempo de explosão

        // métodos
        public void hit(long explosionDuration) {
            healthPoints--;
            if(healthPoints <= 0) {
                explode(System.currentTimeMillis(), System.currentTimeMillis() + explosionDuration);
            }
        }

        public void explode(double timeExplosionStart, double timeExplosionEnd){
            state = EXPLODING;
            this.explosionStart = timeExplosionStart;
            this.explosionEnd = timeExplosionEnd;
        }

        @Override
        public void draw(){
            if(isStateTrue(EXPLODING)){
                double alpha = (System.currentTimeMillis()- getExplosionStart()) / (getExplosionEnd() - getExplosionStart());
                GameLib.drawExplosion(getX(), getY(), alpha);
            }
            else if(isStateTrue(ACTIVE)){
                super.draw();
            }
        }

        // getters
        public long getNextShoot() {return nextShoot;}
        public double getExplosionStart() {return explosionStart;}
        public double getExplosionEnd() {return explosionEnd;}
    }
