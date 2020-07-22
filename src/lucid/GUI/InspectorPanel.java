package lucid.GUI;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class InspectorPanel extends JPanel {

    private String mHeader;

    public InspectorPanel() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
    }

    public void acceptInspectorData(String header, List<JLabel> labels, List<JComponent> components) {
        mHeader = header;

        if (labels.size() != components.size()) {
            throw new IllegalArgumentException("Labels and Components sizes don't match!");
        }

        this.removeAll();

        JLabel headerLabel = new JLabel(header);
        JPanel headerPanel = new JPanel();
        headerPanel.add(headerLabel);
        add(headerPanel);

        for (int i = 0; i < components.size(); i++) {
            JComponent componentLeft = labels.get(i);
            JComponent componentRight = components.get(i);

            JPanel panel = new JPanel();
            panel.add(componentLeft);
            panel.add(componentRight);

            add(panel);
        }

        add(Box.createVerticalGlue());

        invalidate();
        validate();
        repaint();
    }
}
