package GameObjects.Colliders;

public class CheckerCollider {
    public static final double COLLISION_FACTOR = 0.8;

    public boolean checkCollision(double x1, double y1, double r1, double x2, double y2, double r2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        double dist = Math.sqrt(dx * dx + dy * dy);
        return dist < (r1 + r2) * COLLISION_FACTOR;
    }
}
