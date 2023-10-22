package roadtofarming60;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import org.jnativehook.GlobalScreen;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

public class Mushroom implements NativeKeyListener {
    private static boolean isRunning = false;
    private static boolean isExitRequested = false;
    private static Robot robot;
    private static int wButtonCounter = 0;

    public static void main(String[] args) {
        try {
            // Initialize the GlobalScreen to listen for key events
            GlobalScreen.registerNativeHook();

            // Add the Farming class as the NativeKeyListener
            Mushroom mushroom = new Mushroom();
            GlobalScreen.addNativeKeyListener(mushroom);

            // Disable logging for jnativehook
            java.util.logging.Logger logger = java.util.logging.Logger.getLogger(GlobalScreen.class.getPackage().getName());
            logger.setLevel(java.util.logging.Level.WARNING);

            // Create a Robot instance
            robot = new Robot();

            // Add shutdown hook to release buttons on program exit
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (robot != null) {
                    releaseAllButtons(robot);
                }
            }));

            while (!isExitRequested) {
                if (isRunning) {
                    executeFarming(robot);
                }
                // Perform other tasks or idle until arrow keys are pressed
                Thread.sleep(100);
            }
        } catch (AWTException | InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Unregister the GlobalScreen on program exit
            try {
                GlobalScreen.unregisterNativeHook();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        // Not used
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
        // Not used
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        if (e.getKeyCode() == NativeKeyEvent.VC_UP) {
            if (!isRunning) {
                isRunning = true; // Start the farming process
                System.out.println("Farming started");
            }
        } else if (e.getKeyCode() == NativeKeyEvent.VC_DOWN) {
            if (isRunning) {
                stopFarming(); // Stop the farming process
                System.out.println("Farming stopped");
            } else {
                isExitRequested = true; // Set the exit flag
                System.out.println("Program exit requested");
                System.exit(0); // Terminate the program
            }
        }
    }

    private static void executeFarming(Robot robot) {
    	robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
    	//yaw: 49
        try {            
            while(isRunning) {
            	robot.keyPress(KeyEvent.VK_W);
                System.out.println("Left mouse button and W button pressed");
                Thread.sleep(40500);
                
                robot.keyRelease(KeyEvent.VK_W);
                wButtonCounter++;
                
                // Check if the desired count of W key presses is reached
                if (wButtonCounter == 10) {
                    wButtonCounter = 0; // Reset the counter
                    releaseAllButtons(robot);
                    simulateEnterWarpGarden(robot);
                    return; // Exit the method after executing /warp garden
                }
                
                robot.keyPress(KeyEvent.VK_A);
                System.out.println("W button released, A button pressed");
                Thread.sleep(46000);
                
                robot.keyRelease(KeyEvent.VK_A);
                
                
                
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    private static void stopFarming() {
        isRunning = false; // Stop the farming process
        releaseAllButtons(robot);
        System.out.println("Left mouse button, D button, and W button released");
    }

    private static void releaseAllButtons(Robot robot) {
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        robot.keyRelease(KeyEvent.VK_A);
        robot.keyRelease(KeyEvent.VK_W);
    }

    private static void simulateEnterWarpGarden(Robot robot) {
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
        robot.delay(500); // Delay before typing "/warp garden"
        typeStringWithDelay(robot, "/warp garden", KeyEvent.VK_SPACE, 40);
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);

        // Stop the farming process
        stopFarming();

        // Reset A button counter
        wButtonCounter = 0;

        // Restart the program
        isRunning = true;
        isExitRequested = false;
        System.out.println("Program restarted");
    }

    private static void typeStringWithDelay(Robot robot, String text, int spaceKey, int delay) {
        for (char c : text.toCharArray()) {
            int keyCode = Character.toUpperCase(c);
            if (keyCode == ' ') {
                robot.keyPress(spaceKey);
                robot.keyRelease(spaceKey);
            } else {
                robot.keyPress(keyCode);
                robot.keyRelease(keyCode);
            }
            robot.delay(delay);
        }
    }
}
