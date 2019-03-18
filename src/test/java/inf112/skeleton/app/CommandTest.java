package inf112.skeleton.app;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CommandTest {
    /**
     * Rotate a robot 90 degrees using an ICommand and verify the angle.
     */
    @Test
    public void rotateCommand() {
        ICommand cmd = Commands.getComand("rotate");
        Robot robot = new Robot();
        Vector2D dir = robot.getDir();
        int rot_amount = 90;
        double orig_angle = dir.angle();
        cmd.exec(rot_amount, robot, null);
        assertEquals(orig_angle + rot_amount, dir.angle(), 0.1);
    }
}
