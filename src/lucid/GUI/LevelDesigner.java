package lucid.GUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LevelDesigner {
    private JPanel mMainPanel;
    private JToolBar mToolBar;
    private JPanel mGridPanel;
    private JButton mToolbarButtonNew;
    private JButton mToolbarButtonLoad;
    private JButton mToolbarButtonSave;
    private JRadioButton mRadioButtonNone;
    private JRadioButton mRadioButtonFloor;
    private JRadioButton mRadioButtonWall;
    private JLabel mLabelWidth;
    private JTextField mTextFieldWidth;
    private JLabel mLabelHeight;
    private JTextField mTextFieldHeight;
    private JLabel mLabelError;

    private JFrame mFrame;

    public LevelDesigner() {
        mFrame = new JFrame("Lucid Level Editor");

        mFrame.setContentPane(mMainPanel);
        mFrame.pack();
        mFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mFrame.setResizable(false);
        mFrame.setVisible(true);

        addButtonActionListeners();
        addRadioButtonsToButtonGroup();

        mToolBar.add(Box.createHorizontalGlue());
    }

    private void addButtonActionListeners() {
        mToolbarButtonNew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createNewGrid();
            }
        });

        mToolbarButtonLoad.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadGrid();
            }
        });

        mToolbarButtonSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveGrid();
            }
        });
    }

    private void addRadioButtonsToButtonGroup()
    {
        ButtonGroup bg = new ButtonGroup();
        bg.add(mRadioButtonNone);
        bg.add(mRadioButtonFloor);
        bg.add(mRadioButtonWall);

        mRadioButtonFloor.setSelected(true);
    }

    private void createNewGrid() {
        // Try to parse the width and height
        String widthString = mTextFieldWidth.getText();
        String heightString = mTextFieldHeight.getText();

        int width, height;

        try {
            width = Integer.parseInt(widthString);
            height = Integer.parseInt(heightString);
        } catch (NumberFormatException e) {
            mLabelError.setText("Illegal width or height!");
            return;
        }

        // Create a new
    }

    private void loadGrid() {

    }

    private void saveGrid() {

    }
}
