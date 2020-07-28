package lucid.grid;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lucid.serialization.RoomTemplate;
import lucid.serialization.SerializationFormat;

import java.awt.*;
import java.io.*;
import java.util.*;

/**
 * Tile grid.
 */
public class TileGrid {

    private int mWidth, mHeight;

    private Tile[] mTiles;

    private TileType mActiveTileType;

    public TileGrid(int width, int height, TileType currentTileType) {
        mWidth = width;
        mHeight = height;

        mTiles = InitTiles();

        mActiveTileType = currentTileType;
    }

    public TileGrid(File file, SerializationFormat format, TileType currentTileType) {
        deserialize(file, format);

        mActiveTileType = currentTileType;
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

    /**
     * Returns the border indices in no particular order.
     */
    private int[] getBorderIndices() {
        int[] indices = new int[(mWidth * 2) + ((mHeight - 2) * 2)];
        int index = 0;

        for (int x = 0; x < mWidth; x++) {
            indices[index++] = x;
            indices[index++] = (mWidth * mHeight) - x - 1;
        }

        for (int y = 1; y < mHeight - 1; y++) {
            indices[index++] = y * mWidth;
            indices[index++] = (y + 1) * mWidth - 1;
        }

        return indices;
    }

    private boolean isIndexOnBorder(int index) {
        int x = index % mWidth;
        int y = index / mWidth;

        return (x == 0 || x == mWidth - 1 || y == 0 || y == mHeight - 1);
    }


    public void clear() {
        // Revert tile types to floor
        for (Tile tile : mTiles) {
            tile.setTileType(TileType.Floor);
            setDefaultBorderWalls();
        }
    }

    // vvv getters and setters vvv

    public void setActiveTileType(TileType type) {
        mActiveTileType = type;
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

    // vvv neighbors vvv

    private int[] getNeighbors(int index) {
        int[] neighbors = new int[4];
        int neighborsAdded = 0;

        // North
        if (index >= mWidth) {
            neighbors[neighborsAdded++] = index - mWidth;
        }
        // East
        if (index % mWidth < mWidth - 1) {
            neighbors[neighborsAdded++] = index + 1;
        }
        // South
        if (index <= mTiles.length - mWidth - 1) {
            neighbors[neighborsAdded++] = index + mWidth;
        }
        // West
        if (index % mWidth > 0) {
            neighbors[neighborsAdded++] = index - 1;
        }

        if (neighborsAdded == 4) {
            return neighbors;
        }

        int[] actualNeighbors = new int[neighborsAdded];
        System.arraycopy(neighbors, 0, actualNeighbors, 0, neighborsAdded);

        return actualNeighbors;
    }

    private int[] getNeighborsWithDiagonals(int index) {
        // Get normal neighbors
        int[] cardinalNeighbors = getNeighbors(index);

        // Calculate diagonal neighbors
        int[] neighbors = new int[4];
        int neighborsAdded = 0;

        // Northeast
        if (index > mWidth && (index % mWidth) < mWidth - 1) {
            neighbors[neighborsAdded++] = index - mWidth + 1;
        }
        // Southeast
        if (index < mTiles.length - mWidth - 1 && (index % mWidth) < mWidth - 1) {
            neighbors[neighborsAdded++] = index + mWidth + 1;
        }
        // Southwest
        if (index < mTiles.length - mWidth - 1 && (index % mWidth) > 0) {
            neighbors[neighborsAdded++] = index + mWidth - 1;
        }
        // Northwest
        if (index > mWidth && (index % mWidth) > 0) {
            neighbors[neighborsAdded++] = index - mWidth - 1;
        }

        int[] allNeighbors = new int[cardinalNeighbors.length + neighborsAdded];
        int i = 0;

        for (int c : cardinalNeighbors) {
            allNeighbors[i++] = c;
        }
        for (int j = 0; j < neighborsAdded; j++) {
            allNeighbors[i++] = neighbors[j];
        }

        return allNeighbors;
    }

    // vvv user interaction vvv

    public void handleMouseClick(Point click, int scale, Point topLeftOfGrid) {
        // Make sure that invalid clicks don't continue
        if (isClickNotOnGrid(click, scale, topLeftOfGrid)) return;

        // scale down x and y to calculate index
        int index = getTileIndexFromMouseClick(click, scale, topLeftOfGrid);

        mTiles[index].setTileType(mActiveTileType);
    }

    private int getTileIndexFromMouseClick(Point click, int scale, Point topLeftOfGrid) {
        if (isClickNotOnGrid(click, scale, topLeftOfGrid)) return -1;

        return getTileIndexFromMouseClickUnchecked(click, scale, topLeftOfGrid);
    }

    private int getTileIndexFromMouseClickUnchecked(Point click, int scale, Point topLeftOfGrid) {
        int startX = topLeftOfGrid.x;
        int startY = topLeftOfGrid.y;

        int x = (click.x - startX) / scale;
        int y = (click.y - startY) / scale;

        return x + y * mWidth;
    }

    private boolean isClickNotOnGrid(Point click, int scale, Point topLeftOfGrid) {
        int clickX = click.x - topLeftOfGrid.x;
        int clickY = click.y - topLeftOfGrid.y;
        return clickX < 0 || clickX > mWidth * scale || clickY < 0 || clickY > mHeight * scale;
    }

    public Tile getTileAt(Point click, int scale, Point topLeftOfGrid) {
        int index = getTileIndexFromMouseClick(click, scale, topLeftOfGrid);
        return (index != -1) ? mTiles[index] : null;
    }

    // vvv (de)serialization vvv

    private void throwIfBoardIsInvalid() throws RuntimeException {
        // Strategy: iterate border tiles, continue if Wall; if None, dfs for walls; any other tile found is invalid
        int[] borderIndices = getBorderIndices();

        for (int index : borderIndices) {
            Tile tile = mTiles[index];
            switch (tile.getTileType()) {
                case Wall:
                case Door:
                    // Fine - continue to the next
                    break;
                case None:
                    // Search for invalid
                    if (!isBorderTileEncasedByWallsOrDoors(index)) {
                        throw new RuntimeException(String.format("Tile at index %d is not encased by walls!", index));
                    }
                    break;
                default:
                    // Bad!
                    throw new RuntimeException(String.format("Tile at index %d contains invalid tile type %s", index, tile.getTileType()));
            }
        }
    }

    private boolean isBorderTileEncasedByWallsOrDoors(int index) {
        Set<Integer> seen = new HashSet<>();
        Stack<Integer> dfs = new Stack<>();
        dfs.push(index);

        // DFS nones starting at the index, stopping at walls and doors
        while (!dfs.empty()) {
            int tile = dfs.pop();

            if (seen.contains(tile)) continue;

            seen.add(tile);

            int[] neighbors = getNeighborsWithDiagonals(tile);
            for (int neighbor : neighbors) {
                // Pass if wall; push if none, else return
                switch (mTiles[neighbor].getTileType()) {
                    case Wall:
                    case Door:
                        break;
                    case None:
                        if (!seen.contains(neighbor)) {
                            dfs.push(neighbor);
                        }
                        break;
                    default:
                        return false;
                }
            }
        }

        return true;
    }

    public void serialize(File saveFile, SerializationFormat format) throws RuntimeException {
        // Ensure that the board is valid
        throwIfBoardIsInvalid();

        switch (format) {
            case JSON:
                serializeJson(saveFile);
                break;
            default:
                throw new IllegalArgumentException("Invalid SerializationFormat in serialize()!");
        }
    }

    private void serializeJson(File saveFile) throws RuntimeException {
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


    private RoomTemplate CreateRoomTemplate() throws RuntimeException {
        RoomTemplate template = new RoomTemplate();

        // Create the dimensions first
        template.dimensions = getRoomTemplateDimensions();

        // Iterate the tiles to find the rest
        ArrayList<RoomTemplate.Wall> walls = new ArrayList<>();
        ArrayList<RoomTemplate.None> nones = new ArrayList<>();
        ArrayList<RoomTemplate.EnemyNest> nests = new ArrayList<>();
        ArrayList<RoomTemplate.Treasure> treasures = new ArrayList<>();
        ArrayList<RoomTemplate.POI> pois = new ArrayList<>();
        ArrayList<RoomTemplate.Door> doors = new ArrayList<>(4);

        for (Tile tile : mTiles) {
            switch (tile.getTileType()) {
                case None:
                    // Add a new none
                    nones.add(createRoomTemplateNone(tile));
                case Floor:
                    // Floors are inherent in the template
                    break;
                case Wall:
                    // Add a new wall
                    walls.add(createRoomTemplateWall(tile));
                    break;
                case Nest:
                    // Add a new nest
                    nests.add(createRoomTemplateNest(tile));
                    break;
                case Treasure:
                    // Add a new treasure
                    treasures.add(createRoomTemplateTreasure(tile));
                    break;
                case POI:
                    // Add a new POI
                    pois.add(createRoomTemplatePOI(tile));
                    break;
                case Door:
                    // Add a new door
                    doors.add(createRoomTemplateDoor(tile));
            }
        }

        template.walls = walls.toArray(new RoomTemplate.Wall[0]);
        template.nones = nones.toArray(new RoomTemplate.None[0]);
        template.enemyNests = nests.toArray(new RoomTemplate.EnemyNest[0]);
        template.treasures = treasures.toArray(new RoomTemplate.Treasure[0]);
        template.pois = pois.toArray(new RoomTemplate.POI[0]);
        template.doors = doors.toArray(new RoomTemplate.Door[0]);

        return template;
    }

    private RoomTemplate.Dimensions getRoomTemplateDimensions() {
        RoomTemplate.Dimensions dim = new RoomTemplate.Dimensions();

        dim.width = mWidth;
        dim.height = mHeight;
        dim.tileSize = 4;  // !TODO

        return dim;
    }

    private RoomTemplate.EnemyNest createRoomTemplateNest(Tile tile) {
        return tile.getEnemyNest();
    }

    private RoomTemplate.Treasure createRoomTemplateTreasure(Tile tile) {
        RoomTemplate.Treasure treasure = new RoomTemplate.Treasure();

        treasure.index = tile.getIndex();

        return treasure;
    }

    private RoomTemplate.POI createRoomTemplatePOI(Tile tile) {
        return tile.getPOI();
    }

    private RoomTemplate.Wall createRoomTemplateWall(Tile tile) {
        RoomTemplate.Wall wall = new RoomTemplate.Wall();

        wall.index = tile.getIndex();

        return wall;
    }

    private RoomTemplate.None createRoomTemplateNone(Tile tile) {
        RoomTemplate.None none = new RoomTemplate.None();

        none.index = tile.getIndex();

        return none;
    }

    private RoomTemplate.Door createRoomTemplateDoor(Tile tile) {
        RoomTemplate.Door door = new RoomTemplate.Door();

        door.index = tile.getIndex();

        return door;
    }

    private void deserialize(File loadFile, SerializationFormat format) {
        switch(format) {
            case JSON:
                deserializeJson(loadFile);
                break;
            default:
                throw new IllegalArgumentException("Unimplemented SerializationFormat in deserialize()!");
        }
    }

    private void deserializeJson(File loadFile) {
        // Deserialize the contents of the file
        RoomTemplate template;

        try {
            Scanner scanner = new Scanner(loadFile);
            StringBuilder data = new StringBuilder(128);

            while (scanner.hasNext()) {
                data.append(scanner.nextLine());
            }

            Gson gson = new Gson();
            template = gson.fromJson(data.toString(), RoomTemplate.class);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        
        // Fill grid
        fillGridFromTemplate(template);
    }

    private void fillGridFromTemplate(RoomTemplate template) {
        // Set grid dimensions
        setRoomTemplateDimensions(template.dimensions);

        // Iterate over the rest of the elements
        for (RoomTemplate.Wall wall : template.walls) {
            createWall(wall);
        }
        for (RoomTemplate.None none : template.nones) {
            createNone(none);
        }
        for (RoomTemplate.EnemyNest nest : template.enemyNests) {
            checkCreatingNonBorderTile(nest.index);
            createEnemyNest(nest);
        }
        for (RoomTemplate.Treasure treasure : template.treasures) {
            checkCreatingNonBorderTile(treasure.index);
            createTreasure(treasure);
        }
        for (RoomTemplate.POI poi : template.pois) {
            checkCreatingNonBorderTile(poi.index);
            createPOI(poi);
        }
        for (RoomTemplate.Door door : template.doors) {
            createDoor(door);
        }
    }

    private void setRoomTemplateDimensions(RoomTemplate.Dimensions dimensions) {
        mWidth = dimensions.width;
        mHeight = dimensions.height;
        // !TODO deal with size

        mTiles = new Tile[mWidth * mHeight];
        for (int i = 0; i < mWidth * mHeight; i++) {
            mTiles[i] = new Tile(i, TileType.Floor);
        }
    }

    private void createWall(RoomTemplate.Wall wall) {
        mTiles[wall.index].setTileType(TileType.Wall);
    }

    private void createEnemyNest(RoomTemplate.EnemyNest nest) {
        mTiles[nest.index].setTileType(TileType.Nest);
    }

    private void createTreasure(RoomTemplate.Treasure treasure) {
        mTiles[treasure.index].setTileType(TileType.Treasure);
    }

    private void createPOI(RoomTemplate.POI poi) {
        mTiles[poi.index].setTileType(TileType.POI);
    }

    private void createNone(RoomTemplate.None none) {
        mTiles[none.index].setTileType(TileType.None);
    }

    private void createDoor(RoomTemplate.Door door) { mTiles[door.index].setTileType(TileType.Door); }

    private void checkCreatingNonBorderTile(int index) {
        if (isIndexOnBorder(index)) {
            // !TODO
            throw new IllegalArgumentException("Index on border: " + index);
        }
    }
}
