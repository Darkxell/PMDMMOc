package com.darkxell.client.renderers.floor;

import static com.darkxell.client.resources.image.tileset.dungeon.AbstractDungeonTileset.TILE_SIZE;

import java.awt.Graphics2D;
import java.util.HashSet;

import com.darkxell.client.launchable.Persistence;
import com.darkxell.client.renderers.MasterDungeonRenderer;
import com.darkxell.client.resources.image.Sprites.DungeonSprites;
import com.darkxell.client.ui.Keys.Key;
import com.darkxell.common.dungeon.floor.Floor;
import com.darkxell.common.dungeon.floor.Tile;
import com.darkxell.common.dungeon.floor.TileType;
import com.darkxell.common.pokemon.DungeonPokemon;

public class GridRenderer extends LocatedRenderer {

    public final Floor floor;

    public GridRenderer() {
        super(0, 0, MasterDungeonRenderer.LAYER_GRID);
        this.floor = Persistence.floor;
    }

    @Override
    public void render(Graphics2D g, int width, int height) {
        int xStart = (int) (this.xLocation() / TILE_SIZE), yStart = (int) (this.yLocation() / TILE_SIZE);
        int drawX = (int) this.drawX(), drawY = (int) this.drawY();

        DungeonPokemon player = Persistence.player.getDungeonLeader();

        HashSet<Tile> faced = new HashSet<>();
        Tile tile = player.tile();
        do {
            faced.add(tile);
            tile = tile.adjacentTile(player.facing());
        } while (tile != null);

        for (int x = xStart; x < this.floor.getWidth() && x <= xStart + width / TILE_SIZE + 1; ++x)
            for (int y = yStart; y < this.floor.getHeight() && y <= yStart + height / TILE_SIZE + 1; ++y) {
                tile = this.floor.tileAt(x, y);
                if (tile != null && tile.type() != TileType.WALL && tile.type() != TileType.WALL_END)
                    g.drawImage(DungeonSprites.dungeonCommon.grid(faced.contains(tile)), tile.x * TILE_SIZE + drawX,
                            tile.y * TILE_SIZE + drawY, null);
            }
    }

    @Override
    public boolean shouldRender(int width, int height) {
        return Persistence.dungeonState.isMain() && Persistence.dungeonState.actionSelectionState.isMain()
                && Key.ROTATE.isPressed();
    }

}
