package com.darkxell.common.event.pokemon;

import java.util.ArrayList;

import com.darkxell.common.dungeon.floor.Floor;
import com.darkxell.common.event.DungeonEvent;
import com.darkxell.common.pokemon.DungeonPokemon;
import com.darkxell.common.pokemon.ability.Ability;

public class TriggeredAbilityEvent extends DungeonEvent {

	public final Ability ability;
	/** An ID referencing a message for the triggered ability different than the default one. */
	public int messageID;
	public final DungeonPokemon pokemon;

	public TriggeredAbilityEvent(Floor floor, DungeonPokemon pokemon) {
		this(floor, pokemon, 0);
	}

	public TriggeredAbilityEvent(Floor floor, DungeonPokemon pokemon, int messageID) {
		super(floor);
		this.pokemon = pokemon;
		this.ability = this.pokemon.ability();
		this.messageID = messageID;
	}

	@Override
	public String loggerMessage() {
		return this.pokemon + "'s " + this.ability.name() + " ability triggered.";
	}

	@Override
	public ArrayList<DungeonEvent> processServer() {
		if (this.ability.hasTriggeredMessage())
			this.messages.add(this.ability.triggeredMessage(this.pokemon, this.messageID));
		return super.processServer();
	}

}
