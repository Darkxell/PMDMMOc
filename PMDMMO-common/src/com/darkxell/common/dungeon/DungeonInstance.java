package com.darkxell.common.dungeon;

import java.util.ArrayList;
import java.util.Random;

import com.darkxell.common.dungeon.floor.Floor;
import com.darkxell.common.event.DungeonEvent;
import com.darkxell.common.event.GameTurn;
import com.darkxell.common.pokemon.DungeonPokemon;

public class DungeonInstance
{

	/** The Pok�mon to take turn in order. */
	private ArrayList<DungeonPokemon> actors = new ArrayList<DungeonPokemon>();
	/** The current Pok�mon taking its turn. */
	private int currentActor;
	/** The current Floor. */
	private Floor currentFloor;
	/** The current turn. */
	private GameTurn currentTurn;
	/** ID of the Dungeon. */
	public final int id;
	/** Lists the previous turns. */
	private ArrayList<GameTurn> pastTurns = new ArrayList<GameTurn>();
	/** RNG for floor generation. */
	public final Random random;

	public DungeonInstance(int id, Random random)
	{
		this.id = id;
		this.random = random;
		this.currentFloor = this.createFloor(4);
		this.endTurn();
		this.currentActor = 1;
	}

	private Floor createFloor(int floorID)
	{
		return new Floor(floorID, this.dungeon().getLayout(floorID), this, new Random(this.random.nextLong()));
	}

	public Floor currentFloor()
	{
		return this.currentFloor;
	}

	public GameTurn currentTurn()
	{
		return this.currentTurn;
	}

	public Dungeon dungeon()
	{
		return DungeonRegistry.find(this.id);
	}

	/** Ends the current Floor, creates the next and returns it. */
	public Floor endFloor()
	{
		return this.currentFloor = this.createFloor(this.currentFloor.id + 1);
	}

	/** Ends the current Turn, creates the next and returns it. */
	public GameTurn endTurn()
	{
		if (this.currentTurn != null) this.pastTurns.add(this.currentTurn);
		this.currentActor = 0;
		return this.currentTurn = new GameTurn(this.currentFloor);
	}

	/** Called when the input event is processed. */
	public void eventOccured(DungeonEvent event)
	{
		this.currentTurn.addEvent(event);
	}

	/** @return The last past turn. null if this is the first turn. */
	public GameTurn getLastTurn()
	{
		if (this.pastTurns.isEmpty()) return null;
		return this.pastTurns.get(this.pastTurns.size() - 1);
	}

	/** @return The next Pok�mon to take its turn. null if there is no actor left, thus the turn is over. */
	public DungeonPokemon getNextActor()
	{
		if (this.currentActor >= this.actors.size()) return null;
		++this.currentActor;
		return this.actors.get(this.currentActor - 1);
	}

	public void registerActor(DungeonPokemon pokemon)
	{
		this.actors.add(pokemon);
	}

	public void unregisterActor(DungeonPokemon pokemon)
	{
		this.actors.remove(pokemon);
	}

}
