package com.darkxell.common.event;

import com.darkxell.common.pokemon.DungeonPokemon;

public class DungeonExitEvent extends DungeonEvent
{

	public final DungeonPokemon pokemon;

	public DungeonExitEvent(DungeonPokemon pokemon)
	{
		this.pokemon = pokemon;
	}

}
