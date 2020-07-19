package lucid.GUI;

import javax.swing.*;
import java.awt.*;

public class ToolbarWidthHeightTextField extends JTextField {

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(20, 49);
    }

    public void createUIComponents() {

    }
}
