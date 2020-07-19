package lucid;

import lucid.GUI.LevelDesigner;

import javax.swing.*;

/**
 * Starter class with main()
 */
public class Father {

    public static void main(String[] args)
    {
        // Create a new form
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LevelDesigner editor = new LevelDesigner();
            }
        });
    }
}
