package com.darkxell.common.ai.states;

import java.util.ArrayList;

import com.darkxell.common.ai.AI;
import com.darkxell.common.ai.AI.AIState;
import com.darkxell.common.ai.AIUtils;
import com.darkxell.common.dungeon.floor.Tile;
import com.darkxell.common.event.DungeonEvent;
import com.darkxell.common.event.pokemon.PokemonRotateEvent;
import com.darkxell.common.event.pokemon.PokemonTravelEvent;
import com.darkxell.common.util.Direction;
import com.darkxell.common.util.Logger;
import com.darkxell.common.util.RandomUtil;

public class AIStateExplore extends AIState
{

	private Tile currentDestination;

	public AIStateExplore(AI ai)
	{
		this(ai, null);
	}

	public AIStateExplore(AI ai, Tile startingDestination)
	{
		super(ai);
		this.currentDestination = startingDestination;
	}

	private void findNewDestination()
	{
		Direction facing = this.ai.pokemon.facing();
		ArrayList<Tile> candidates;

		if (this.ai.pokemon.tile().isInRoom()) candidates = this.ai.floor.room(this.ai.pokemon.tile()).exits();
		else
		{
			candidates = AIUtils.adjacentReachableTiles(this.ai.floor, this.ai.pokemon);
			if (candidates.size() == 0)
			{
				this.currentDestination = this.ai.pokemon.tile();
				return;
			}
			if (candidates.size() == 1)
			{
				this.currentDestination = candidates.get(0);
				return;
			}
			if (candidates.size() > 2) candidates = AIUtils.furthestWalkableTiles(this.ai.floor, this.ai.pokemon);
		}

		if (candidates.size() == 0) Logger.e(this.ai.pokemon + " didn't find a way to go :(");

		boolean continu = candidates.size() > 1;
		while (continu) // If more than one solution, we should remove the one it's coming from.
		{
			Tile delete = null;
			for (Tile t : candidates)
				if (AIUtils.generalDirection(this.ai.pokemon, t) == facing.opposite())
				{
					delete = t;
					break;
				}
			if (delete == null) continu = false;
			else
			{
				candidates.remove(delete);
				continu = candidates.size() > 1;
			}
		}

		this.currentDestination = RandomUtil.random(candidates, this.ai.floor.random);
	}

	@Override
	public DungeonEvent takeAction()
	{
		if (this.ai.pokemon.tile() == this.currentDestination || this.currentDestination == null) this.findNewDestination();
		Direction dir = AIUtils.direction(this.ai.pokemon, this.currentDestination);
		if (dir == null)
			return new PokemonRotateEvent(this.ai.floor, this.ai.pokemon, AIUtils.generalDirection(this.ai.pokemon, this.currentDestination), true);
		return new PokemonTravelEvent(this.ai.floor, this.ai.pokemon, dir);
	}

	@Override
	public String toString()
	{
		return "Explores to " + this.currentDestination;
	}

}
