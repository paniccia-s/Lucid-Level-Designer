package lucid.grid;

import lucid.GUI.InspectorPanel;
import lucid.serialization.RoomTemplate;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Tile {

    private static final String NEST_RADIUS = "3";
    private static final String NEST_CHANCE = "1.0";
    private static final String NEST_MIN = "0";
    private static final String NEST_MAX = "1";

    private static final String POI_TYPE = "Vendor";

    private final int mIndex;

    private TileType mTileType;


    private final Nest mNest;
    private Treasure mTreasrue;
    private POI mPOI;
    private Door mDoor;

    public Tile(int index, TileType tileType) {
        mIndex = index;
        mTileType = tileType;

        mNest = new Nest();
        mTreasrue = new Treasure();
        mPOI = new POI();
        mDoor = new Door();
    }

    /**
     * Returns the previous type.
     */
    public TileType setTileType(TileType type) {
        TileType old = mTileType;
        mTileType = type;
        return old;
    }

    public Color getColor() {
        return mTileType.getColor();
    }

    public TileType getTileType() {
        return mTileType;
    }

    public int getIndex() {
        return mIndex;
    }


    public RoomTemplate.EnemyNest getEnemyNest() throws IllegalArgumentException {
        // Throw if invalid type
        if (mTileType != TileType.Nest) throw new IllegalArgumentException("Wrong type!");

        RoomTemplate.EnemyNest nest = new RoomTemplate.EnemyNest();

        try {
            nest.index = mIndex;
            nest.spawnRadius = Integer.parseInt(mNest.spawnRadius);
            nest.spawnChance = Float.parseFloat(mNest.spawnChance);
            nest.spawnAttemptsMin = Integer.parseInt(mNest.spawnAttemptsMin);
            nest.spawnAttemptsMax = Integer.parseInt(mNest.spawnAttemptsMax);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Illegally-formatted entry in nest tile!");
        }

        return nest;
    }

    public RoomTemplate.POI getPOI() {
        // Throw if invalid type
        if (mTileType != TileType.POI) throw new IllegalArgumentException("Wrong type!");

        RoomTemplate.POI poi = new RoomTemplate.POI();

        poi.index = mIndex;
        poi.type = mPOI.type;

        return poi;
    }

    public RoomTemplate.Door getDoor() {
        // Throw if invalid type
        if (mTileType != TileType.Door) throw new IllegalArgumentException("Wrong type!");

        RoomTemplate.Door door = new RoomTemplate.Door();

        door.index = mIndex;
        door.direction = mDoor.direction;

        return door;
    }

    public void renderOnInspector(InspectorPanel inspector) {
        // The header is constant across all tile types
        String header = getInspectorHeader();


        // The rest depends on the tile type
        List<JLabel> labels; List<JComponent> components;
        switch (mTileType) {
            case None:
            case Floor:
            case Wall:
                // Nothing extra for these types
                labels = new ArrayList<>(0);
                components = new ArrayList<>(0);
                break;
            case Door:
                // Create for door
                labels = getLabelsDoor();
                components = getComponentsDoor();
                break;
            case Nest:
                // Create for nest
                labels = getLabelsEnemyNest();
                components = getComponentsEnemyNest();
                break;
            case Treasure:
                labels = getLabelsTreasure();
                components = getComponentsTreasure();
                break;
            case POI:
                labels = getLabelsPOI();
                components = getComponentsPOI();
                break;
            default:
                throw new IllegalArgumentException();
        }

        inspector.acceptInspectorData(header, labels, components);
    }

    private List<JComponent> getComponentsPOI() {
        List<JComponent> components = new ArrayList<>(1);

        JRadioButton buttonTest1 = createRadioButtonComponent("Vendor", () -> mPOI.type = "Vendor");
        JRadioButton buttonTest2 = createRadioButtonComponent("TotemHealth", () -> mPOI.type = "TotemHealth");
        JRadioButton buttonTest3 = createRadioButtonComponent("TotemGold", () -> mPOI.type = "TotemGold");
        JRadioButton buttonTest4 = createRadioButtonComponent("TotemMystery", () -> mPOI.type = "TotemMystery");

        ButtonGroup bg = new ButtonGroup();
        bg.add(buttonTest1); bg.add(buttonTest2);
        bg.add(buttonTest3); bg.add(buttonTest4);

        JPanel panel = new JPanel();
        BoxLayout layout = new BoxLayout(panel, BoxLayout.PAGE_AXIS);
        panel.setLayout(layout);
        panel.add(buttonTest1);
        panel.add(buttonTest2);
        panel.add(buttonTest3);
        panel.add(buttonTest4);

        components.add(panel);

        return components;
    }

    private List<JLabel> getLabelsPOI() {
        return Stream.of(new JLabel("POI Type:")).collect(Collectors.toList());
    }

    private List<JComponent> getComponentsTreasure() {
        return new ArrayList<>(0);
    }

    private List<JLabel> getLabelsTreasure() {
        return new ArrayList<>(0);
    }

    private List<JComponent> getComponentsEnemyNest() {
        List<JComponent> components = new ArrayList<>(4);

        JTextField fieldRadius = new JTextField(mNest.spawnRadius); fieldRadius.setPreferredSize(new Dimension(25, 30));
        fieldRadius.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                mNest.spawnRadius = fieldRadius.getText();
            }
        });
        components.add(fieldRadius);

        JTextField fieldChance = new JTextField(mNest.spawnChance); fieldChance.setPreferredSize(new Dimension(25, 30));
        fieldChance.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                mNest.spawnChance = fieldChance.getText();
            }
        });
        components.add(fieldChance);

        JTextField fieldMin = new JTextField(mNest.spawnAttemptsMin); fieldMin.setPreferredSize(new Dimension(25, 30));
        fieldMin.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                mNest.spawnAttemptsMin = fieldMin.getText();
            }
        });
        components.add(fieldMin);

        JTextField fieldMax = new JTextField(mNest.spawnAttemptsMax); fieldMax.setPreferredSize(new Dimension(25, 30));
        fieldMax.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                mNest.spawnAttemptsMax = fieldMax.getText();
            }
        });

        components.add(fieldMax);

        return components;
    }

    private List<JLabel> getLabelsEnemyNest() {
        List<JLabel> labels = new ArrayList<>(4);
        labels.add(new JLabel("Spawn Radius"));
        labels.add(new JLabel("Spawn Chance"));
        labels.add(new JLabel("Spawn Attempts Minimum"));
        labels.add(new JLabel("Spawn Attempts Maximum"));

        return labels;
    }

    private List<JLabel> getLabelsDoor() {
        List<JLabel> labels = new ArrayList<>(1);
        labels.add(new JLabel("Direction"));
        return labels;
    }

    private List<JComponent> getComponentsDoor() {
        List<JComponent> components = new ArrayList<>(1);

        JRadioButton buttonTest1 = createRadioButtonComponent("N", () -> mDoor.direction = "N");
        JRadioButton buttonTest2 = createRadioButtonComponent("E", () -> mDoor.direction = "E");
        JRadioButton buttonTest3 = createRadioButtonComponent("S", () -> mDoor.direction = "S");
        JRadioButton buttonTest4 = createRadioButtonComponent("W", () -> mDoor.direction = "W");

        ButtonGroup bg = new ButtonGroup();
        bg.add(buttonTest1); bg.add(buttonTest2);
        bg.add(buttonTest3); bg.add(buttonTest4);

        JPanel panel = new JPanel();
        BoxLayout layout = new BoxLayout(panel, BoxLayout.PAGE_AXIS);
        panel.setLayout(layout);
        panel.add(buttonTest1);
        panel.add(buttonTest2);
        panel.add(buttonTest3);
        panel.add(buttonTest4);

        components.add(panel);

        return components;
    }

    private String getInspectorHeader() {
        return String.format("TYPE: %s     INDEX: %d", mTileType.toString(), mIndex);
    }

    private JTextField createTextFieldComponent(String initialString, Consumer<String> setter, int width) {
        JTextField field = new JTextField(initialString); field.setPreferredSize(new Dimension(width, 30));

        field.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                setter.accept(field.getText());
            }
        });

        return field;
    }

    private JRadioButton createRadioButtonComponent(String title, Runnable selector) {
        JRadioButton button = new JRadioButton(title);
        button.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                selector.run();
            }
        });
        return button;
    }

    public void createNest(RoomTemplate.EnemyNest nest) {
        mTileType = TileType.Nest;
        mNest.spawnAttemptsMax = String.valueOf(nest.spawnAttemptsMax);
        mNest.spawnAttemptsMin = String.valueOf(nest.spawnAttemptsMin);
        mNest.spawnChance = String.valueOf(nest.spawnChance);
        mNest.spawnRadius = String.valueOf(nest.spawnRadius);
    }

    public void createDoor(RoomTemplate.Door door) {
        mTileType = TileType.Door;
        mDoor.direction = door.direction;
    }

    private static class Nest {
        private String spawnRadius = NEST_RADIUS;
        private String spawnChance = NEST_CHANCE;
        private String spawnAttemptsMin = NEST_MIN;
        private String spawnAttemptsMax = NEST_MAX;
    }

    private static class Treasure {

    }

    private static class POI {
        private String type = POI_TYPE;
    }

    private static class Door {
        private String direction = "N";
    }
}
