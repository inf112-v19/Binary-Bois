package inf112.skeleton.app;

import java.util.HashMap;

/**
 * WARNING: Command names are sent across the network, the server
 *          sending the command is able to set both the command name,
 *          the priority, and the amount. Keep this in mind when you
 *          write commands.
 */
public class Commands {
    /** Move a robot along its direction vector. */
    public static ICommand moveCommand = (int amount, Robot robot, RoboRallyGame roboRallyGame) -> {
        if (robot.hasDied())
            return false;
        int sgn = (int) Math.signum(amount);
        Vector2Di dir_v = robot.getDir().copy();
        dir_v.mul(sgn);
        for (int i = 0; i < Math.abs(amount); i++) {
            if (!roboRallyGame.canMoveTo(robot.getPos(), dir_v, robot))
                return false;
            robot.move(sgn);
            roboRallyGame.handlePlunge(robot);
        }
        roboRallyGame.handleLaserTile(robot);
        boolean get_conveyed = true;
        while (get_conveyed)
            get_conveyed = roboRallyGame.handleConveyorTile(robot);
        roboRallyGame.handleGyroTile(robot);
        roboRallyGame.handlePlunge(robot);
        return true;
    };

    /** Rotate the direction vector of a robot. */
    public static ICommand rotateCommand = (int amount, Robot robot, RoboRallyGame roboRallyGame) -> {
        if (robot.hasDied())
            return false;
        robot.rot(amount);
        return true;
    };

    public static ICommand none = (int amount, Robot robot, RoboRallyGame roboRallyGame) -> true;

    /** GameMap strings to command functions. */
    private static HashMap<String, ICommand> cmd_map;
    static {
        cmd_map = new HashMap<>();
        cmd_map.put("move", Commands.moveCommand);
        cmd_map.put("rotate", Commands.rotateCommand);
    }

    /**
     * Get a command function by name.
     *
     * @param name Name of the command, see #{cmd_map}
     * @return The command.
     */
    public static ICommand getComand(String name) {
        ICommand cmd = cmd_map.get(name);
        if (cmd == null) {
            throw new IllegalArgumentException("The command '" + name + "' is not defined");
        }
        return cmd;
    }
}
