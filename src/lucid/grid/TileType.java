package lucid.grid;

import java.awt.*;

public enum TileType {
    None, Floor, Wall, Nest, Treasure, POI, Door, Portal, Lava;

    public Color getColor() {
        switch (this) {
            case None:
                return Color.white;
            case Floor:
                return Color.gray;
            case Wall:
                return Color.black;
            case Nest:
                return Color.red;
            case Treasure:
                return Color.yellow;
            case POI:
                return Color.green;
            case Door:
                return Color.blue;
            case Portal:
                return Color.pink;
            case Lava:
                return Color.orange;
            default:
                throw new IllegalArgumentException("Bad TileType in getColor()!");
        }
    }
}
