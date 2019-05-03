package inf112.skeleton.app;

/**
 * Lambda function interface for actions that are performed on robots/games
 * Use like any other Lambda function interface:
 *   ICommand cmd = (int amount, Robot robot, RoboRallyGame game) -> robot.doThing(amount);
 */
public interface ICommand {
    /**
     * Execute the commmand.
     *
     * @param amount Scales the command, i.e move 1 or move 2, rotate 90 or rotate 180
     * @param robot The robot to act upon.
     * @param game The roboRallyGame to act upon.
     * @return Whether or not the command succeeded fully.
     */
    boolean exec(int amount, Robot robot, RoboRallyGame game);

}
