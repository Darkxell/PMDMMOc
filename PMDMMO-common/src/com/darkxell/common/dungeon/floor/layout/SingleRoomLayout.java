package com.darkxell.common.dungeon.floor.layout;

import com.darkxell.common.dungeon.floor.Floor;
import com.darkxell.common.dungeon.floor.Room;

/** A single room using the hole floor, always a Monster house. */
public class SingleRoomLayout extends Layout
{

	public SingleRoomLayout()
	{
		super(2, 1);
	}

	@Override
	protected void generateLiquids()
	{}

	@Override
	protected void generatePaths()
	{}

	@Override
	protected void generateRooms()
	{
		this.rooms[0] = new Room(this.floor, (int) Floor.WALKABLE.x + 1, (int) Floor.WALKABLE.y + 1, (int) Floor.WALKABLE.getWidth() - 2,
				(int) Floor.WALKABLE.getHeight() - 2, true);
	}

}
