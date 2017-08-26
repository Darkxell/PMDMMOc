package com.darkxell.common.dungeon.floor.layout;

import static com.darkxell.common.dungeon.floor.Floor.*;

import java.util.Random;

import com.darkxell.common.dungeon.floor.Floor;
import com.darkxell.common.dungeon.floor.Room;
import com.darkxell.common.dungeon.floor.Tile;
import com.darkxell.common.dungeon.floor.TileType;

/** Represents a Floor's layout. */
public abstract class Layout
{

	public static final Layout SINGLE_ROOM = new SingleRoomLayout();

	/** Temporary storage for the generating Floor. */
	protected Floor floor;
	/** This Layout's id. */
	public final int id;
	/** RNG */
	protected Random random;
	/** The number of Rooms in this Layout. */
	public final int roomCount;
	/** Temporary storage for the floor's rooms. */
	protected Room[] rooms;
	/** Temporary storage for the floor's tiles. */
	protected Tile[][] tiles;

	public Layout(int id, int roomCount)
	{
		this.id = id;
		this.roomCount = roomCount;
	}

	/** Generates the default layout: Unbreakable walls surrounding breakable walls. */
	public void defaultTiles()
	{
		for (int x = 0; x < ALL_WIDTH; ++x)
			for (int y = 0; y < ALL_HEIGHT; ++y)
				if (WALKABLE.contains(x, y)) this.tiles[x][y] = new Tile(this.floor, x, y, TileType.WALL);
				else this.tiles[x][y] = new Tile(this.floor, x, y, TileType.WALL_END);
	}

	/** Generates a Floor. The tiles and rooms arrays contain null value that should be replaced.
	 * 
	 * @param floor - The Floor to build.
	 * @param tiles - The Tiles of the Floor.
	 * @param rooms - The Rooms of the Floor. */
	public void generate(Floor floor, Tile[][] tiles, Room[] rooms)
	{
		this.floor = floor;
		this.random = this.floor.random;
		this.tiles = tiles;
		this.rooms = rooms;
		this.generateRooms();
		this.generateTiles();
		this.generatePaths();
		this.generateLiquids();
		this.placeStairs();
		this.placeWonderTiles();
		if (this.floor.dungeon.hasTraps) this.placeTraps();
		this.placeItems();
		this.summonPokemon();

		// Clear temp variables
		this.tiles = null;
		this.rooms = null;
		this.floor = null;
		this.random = null;
	}

	/** Creates Water, Lava or Air. */
	protected abstract void generateLiquids();

	/** Creates paths between the rooms. */
	protected abstract void generatePaths();

	/** Creates the rooms. */
	protected abstract void generateRooms();

	/** Creates default tiles from the Rooms. */
	private void generateTiles()
	{
		this.defaultTiles();
		for (Room room : this.rooms)
			for (Tile tile : room.listTiles())
				tile.setType(TileType.GROUND);
	}

	/** Places Items. */
	private void placeItems()
	{
		// TODO Layout.placeItems()
	}

	/** Randomly places the Stairs tile in a random room. */
	private void placeStairs()
	{
		Room exitRoom = this.randomRoom();
		exitRoom.randomTile(this.random).setType(TileType.STAIR);
	}

	/** Places traps. */
	private void placeTraps()
	{
		// TODO Layout.placeTraps()
	}

	/** Places Wonder Tiles. */
	private void placeWonderTiles()
	{
		int wonder = 2; // Number of wonder tiles to place
		wonder += this.random.nextInt(3) - 2; // wonder = random[wonder-1;wonder+1]
		for (int i = 0; i <= wonder; ++i)
			this.randomRoom().randomTile(this.random, TileType.GROUND).setType(TileType.WONDER_TILE);
	}

	private Room randomRoom()
	{
		return this.rooms[random.nextInt(this.rooms.length)];
	}

	/** Summons Pok�mon. */
	private void summonPokemon()
	{
		// TODO Layout.summonPokemon()
	}

}
