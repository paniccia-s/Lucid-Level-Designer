package lucid.GUI;

import lucid.grid.TileGrid;
import lucid.grid.TileType;
import lucid.serialization.SerializationFormat;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.*;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LevelDesigner {
    private JPanel mMainPanel;
    private JToolBar mToolBar;
    private TileGridPanel mCanvas;
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
    private JRadioButton mRadioButtonEnemyNest;
    private JRadioButton mRadioButtonTreasure;
    private JRadioButton mRadioButtonPOI;
    private JButton mToolbarButtonClear;

    private final JFrame mFrame;

    private TileGrid mTileGrid;

    public static final String PATH = getPath();

    private static String getPath() {
        return Paths.get("").toAbsolutePath().toString();
    }

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

        mCanvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);

                mTileGrid.handleMouseClick(e.getPoint(), calculateScale(), mCanvas.getTopLeftOfTileGrid());
                drawGrid();
            }
        });
    }

    private void addButtonActionListeners() {
        mToolbarButtonNew.addActionListener(e -> createNewGrid());
        mToolbarButtonLoad.addActionListener(e -> loadGrid());
        mToolbarButtonSave.addActionListener(e -> saveGrid());
        mToolbarButtonClear.addActionListener(e -> clearGrid());
    }

    private void addRadioButtonsToButtonGroup() {
        ButtonGroup bg = new ButtonGroup();
        bg.add(mRadioButtonNone);
        bg.add(mRadioButtonFloor);
        bg.add(mRadioButtonWall);
        bg.add(mRadioButtonEnemyNest);
        bg.add(mRadioButtonTreasure);
        bg.add(mRadioButtonPOI);

        mRadioButtonFloor.setSelected(true);

        mRadioButtonNone.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED)
                setSelectedTileType(TileType.None);
        });
        mRadioButtonFloor.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED)
                setSelectedTileType(TileType.Floor);
        });
        mRadioButtonWall.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED)
                setSelectedTileType(TileType.Wall);
        });
        mRadioButtonEnemyNest.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED)
                setSelectedTileType(TileType.Nest);
        });
        mRadioButtonTreasure.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED)
                setSelectedTileType(TileType.Treasure);
        });
        mRadioButtonPOI.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED)
            setSelectedTileType(TileType.POI);
        });
    }

    private void setSelectedTileType(TileType type) {
        mTileGrid.setActiveTileType(type);
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

        // Create a new tile grid of those dimensions
        mTileGrid = new TileGrid(width, height);
        drawGrid();
    }

    private void loadGrid() {
        JFileChooser file = new JFileChooser();

        FileNameExtensionFilter filter = new FileNameExtensionFilter("JSON Files", "json");
        file.setFileFilter(filter);
        file.setCurrentDirectory(new File(PATH));

        if (file.showOpenDialog(mFrame) == JFileChooser.APPROVE_OPTION) {
            mTileGrid = new TileGrid(file.getSelectedFile(), SerializationFormat.JSON);
            drawGrid();
        }
    }

    private void saveGrid() {
        JFileChooser file = new JFileChooser();

        FileNameExtensionFilter filter = new FileNameExtensionFilter("JSON Files", "json");
        file.setFileFilter(filter);
        file.setCurrentDirectory(new File(PATH));


        if (!(file.showSaveDialog(mFrame) == JFileChooser.APPROVE_OPTION)) {
            return;
        }

        try {
            File selectedFile = file.getSelectedFile();
            if (!selectedFile.getName().contains(".")) {
                selectedFile = new File(selectedFile + ".json");
            }
            mTileGrid.serialize(selectedFile, SerializationFormat.JSON);
            mLabelError.setText("");
        } catch (RuntimeException e) {
            mLabelError.setText(e.getMessage());
        }
    }

    private void clearGrid() {
        if (mTileGrid != null) {
            mTileGrid.clear();
            drawGrid();
        }
    }

    private void drawGrid() {
        int scale = calculateScale();
        drawGrid(scale);
    }

    private void drawGrid(int scale) {
        mCanvas.acceptTileColors(mTileGrid.getTileColors(), mTileGrid.getWidth(), mTileGrid.getHeight(), scale);
    }


    private int calculateScale() {
        // Leave one tile of padding in each dimension
        int scaleX = mCanvas.getWidth() / (mTileGrid.getWidth() + 1);
        int scaleY = mCanvas.getHeight() / (mTileGrid.getHeight() + 1);

        return Math.min(scaleX, scaleY);
    }

}
