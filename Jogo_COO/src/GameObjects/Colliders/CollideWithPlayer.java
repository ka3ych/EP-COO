package GameObjects.Colliders;

import GameObjects.SpaceShips.Player;

public interface CollideWithPlayer {
    void colideWithPlayer(Player player);
    void reseting();
}