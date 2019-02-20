package inf112.skeleton.app;

/**These are the program cards that the player uses to program their robot
 */
public abstract class ICard {

    private int priority;

    /**
     * Cards that do movement of the robot
     */
    public class MoveCard extends ICard{

        private int movement;

        public MoveCard(int movement, int priority) {
            super(priority);
            this.movement = movement;
        }

        public int getMovement() {
            return movement;
        }
    }

    /**
     * Cards that rotate the robot
     */
    public class RotateCard extends ICard {

        private int rotation;

        public RotateCard(int rotation, int priority) {
            super(priority);
            this.rotation = rotation;
        }

        public int getRotation() {
            return rotation;
        }
    }

    private ICard(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    //Todo: 'type' getTexture(); method
}

