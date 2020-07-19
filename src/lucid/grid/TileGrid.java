package lucid.grid;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lucid.serialization.RoomTemplate;
import lucid.serialization.SerializationFormat;

import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.util.Arrays;

/**
 * Tile grid.
 */
public class TileGrid {

    private int mWidth, mHeight;

    private Tile[] mTiles;

    private TileType mActiveTileType;

    public TileGrid(int width, int height) {
        mWidth = width;
        mHeight = height;

        mTiles = InitTiles();

        mActiveTileType = TileType.Floor;
    }

    private Tile[] InitTiles() {
        Tile[] tiles = new Tile[mWidth * mHeight];

        for (int y = 0; y < mHeight; y++) {
            for (int x = 0; x < mWidth; x++) {
                int index = x + y * mWidth;
                tiles[index] = new Tile(index, TileType.Floor);
            }
        }

        setDefaultBorderWalls(tiles);

        return tiles;
    }

    private void setDefaultBorderWalls() {
        setDefaultBorderWalls(mTiles);
    }

    private void setDefaultBorderWalls(Tile[] tiles) {
        for (int x = 0; x < mWidth; ++x) {
            tiles[x].setTileType(TileType.Wall);
            tiles[mHeight * mWidth - x - 1].setTileType(TileType.Wall);
        }

        for (int y = 0; y < mHeight; ++y) {
            tiles[y * mWidth].setTileType(TileType.Wall);
            tiles[(y + 1) * mWidth - 1].setTileType(TileType.Wall);
        }
    }


    public void setActiveTileType(TileType type) {
        mActiveTileType = type;
    }

    public Tile getTileAt(int index) {
        return mTiles[index];
    }

    public void clear() {
        // Revert tile types to floor
        for (Tile tile : mTiles) {
            tile.setTileType(TileType.Floor);
            setDefaultBorderWalls();
        }
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public Color[] getTileColors() {
        return Arrays.stream(mTiles).map((Tile::getColor)).toArray(Color[]::new);
    }

    public void handleMouseClick(Point locationOnScreen, int scale, Point topLeftOfGrid) {
        // scale down x and y to calculate index
        int startX = topLeftOfGrid.x;
        int startY = topLeftOfGrid.y;

        int x = (locationOnScreen.x - startX) / scale;
        int y = (locationOnScreen.y - startY) / scale;
        int index = x + y * mWidth;

        mTiles[index].setTileType(mActiveTileType);
    }

    public void serialize(File saveFile, SerializationFormat format) {
        switch (format) {
            case JSON:
                serializeJson(saveFile);
                break;
            default:
                throw new IllegalArgumentException("Invalid SerializationFormat in serialize()!");
        }
    }

    private void serializeJson(File saveFile) {
        // Must create a room template from the tile data
        RoomTemplate template = CreateRoomTemplate();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String str = gson.toJson(template);

        try {
            FileWriter writer = new FileWriter(saveFile);
            writer.write(str);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private RoomTemplate CreateRoomTemplate() {
        RoomTemplate template = new RoomTemplate();

        // Create the dimensions first
        template.dimensions = getRoomTemplateDimensions();

        // Iterate the tiles to find the rest
        ArrayList<RoomTemplate.Wall> walls = new ArrayList<RoomTemplate.Wall>();
        ArrayList<RoomTemplate.EnemyNest> nests = new ArrayList<RoomTemplate.EnemyNest>();
        ArrayList<RoomTemplate.Treasure> treasures = new ArrayList<RoomTemplate.Treasure>();
        ArrayList<RoomTemplate.POI> pois = new ArrayList<RoomTemplate.POI>();

        for (Tile tile : mTiles) {
            // !TODO
            if (isIndexOnBorder(tile.getIndex())) {
                if (tile.getTileType() != TileType.Wall) {
                    // !TODO
                    throw new RuntimeException("Border tile is not wall at index " + tile.getIndex() + "!");
                }
                continue;
            }

            switch (tile.getTileType()) {
                case None:
                case Floor:
                    // Do nothing for these
                    break;
                case Wall:
                    // Add a new wall
                    walls.add(CreateRoomTemplateWall(tile));
                    break;
                case Nest:
                    // Add a new nest
                    nests.add(CreateRoomTemplateNest(tile));
                    break;
                case Treasure:
                    // Add a new treasure
                    treasures.add(CreateRoomTemplateTreasure(tile));
                    break;
                case POI:
                    // Add a new POI
                    pois.add(CreateRoomTemplatePOI(tile));
                    break;
            }
        }

        template.walls = walls.toArray(new RoomTemplate.Wall[0]);
        template.enemyNests = nests.toArray(new RoomTemplate.EnemyNest[0]);
        template.treasures = treasures.toArray(new RoomTemplate.Treasure[0]);
        template.pois = pois.toArray(new RoomTemplate.POI[0]);

        return template;
    }

    private RoomTemplate.EnemyNest CreateRoomTemplateNest(Tile tile) {
        RoomTemplate.EnemyNest nest = new RoomTemplate.EnemyNest();

        nest.index = tile.getIndex();
        nest.spawnChance = 1f;
        nest.spawnAttemptsMin = 1;
        nest.spawnAttemptsMax = 1;
        nest.spawnRadius = 3;

        return nest;
    }

    private RoomTemplate.Treasure CreateRoomTemplateTreasure(Tile tile) {
        RoomTemplate.Treasure treasure = new RoomTemplate.Treasure();

        treasure.index = tile.getIndex();

        return treasure;
    }

    private RoomTemplate.POI CreateRoomTemplatePOI(Tile tile) {
        RoomTemplate.POI poi = new RoomTemplate.POI();

        poi.index = tile.getIndex();

        return poi;
    }

    private RoomTemplate.Wall CreateRoomTemplateWall(Tile tile) {
        RoomTemplate.Wall wall = new RoomTemplate.Wall();

        wall.index = tile.getIndex();

        return wall;
    }

    private boolean isIndexOnBorder(int index) {
        int x = index % mWidth;
        int y = index / mWidth;

        return (x == 0 || x == mWidth - 1 || y == 0 || y == mHeight - 1);
    }

    private RoomTemplate.Dimensions getRoomTemplateDimensions() {
        RoomTemplate.Dimensions dim = new RoomTemplate.Dimensions();

        dim.width = mWidth;
        dim.height = mHeight;
        dim.tileSize = 4;  // !TODO

        return dim;
    }
}
