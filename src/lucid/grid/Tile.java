package lucid.grid;

import lucid.GUI.InspectorPanel;
import lucid.serialization.RoomTemplate;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class Tile {

    private static final String NEST_RADIUS = "3";
    private static final String NEST_CHANCE = "1.0";
    private static final String NEST_MIN = "0";
    private static final String NEST_MAX = "1";

    private final int mIndex;

    private TileType mTileType;


    private final Nest mNest;
    private Treasure mTreasrue;
    private POI mPOI;

    public Tile(int index, TileType tileType) {
        mIndex = index;
        mTileType = tileType;

        mNest = new Nest();
        mTreasrue = new Treasure();
        mPOI = new POI();
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
            nest.spawnRadius = Integer.parseInt(mNest.spawnRadius);
            nest.spawnChance = Float.parseFloat(mNest.spawnChance);
            nest.spawnAttemptsMin = Integer.parseInt(mNest.spawnAttemptsMin);
            nest.spawnAttemptsMax = Integer.parseInt(mNest.spawnAttemptsMax);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Illegally-formatted entry in nest tile!");
        }

        return nest;
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
        return new ArrayList<>(0);
    }

    private List<JLabel> getLabelsPOI() {
        return new ArrayList<>(0);
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

    private String getInspectorHeader() {
        return String.format("TYPE: %s     INDEX: %d", mTileType.toString(), mIndex);
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

    }
}
