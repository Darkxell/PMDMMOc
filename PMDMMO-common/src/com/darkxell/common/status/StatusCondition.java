package com.darkxell.common.status;

import java.util.ArrayList;
import java.util.Random;

import com.darkxell.common.dungeon.floor.Floor;
import com.darkxell.common.event.DungeonEvent;
import com.darkxell.common.event.DungeonEventListener;
import com.darkxell.common.event.pokemon.DamageDealtEvent.DamageSource;
import com.darkxell.common.event.stats.ExperienceGeneratedEvent;
import com.darkxell.common.pokemon.AffectsPokemon;
import com.darkxell.common.pokemon.DungeonPokemon;
import com.darkxell.common.status.conditions.PreventsOtherStatusCondition;
import com.darkxell.common.util.Pair;
import com.darkxell.common.util.RandomUtil;
import com.darkxell.common.util.language.Message;

public class StatusCondition extends Status implements AffectsPokemon, DamageSource, DungeonEventListener
{

	public StatusCondition(int id, int durationMin, int durationMax)
	{
		super(id, durationMin, durationMax);
		StatusConditions._registry.put(this.id, this);
	}

	/** @return - True if this Status Condition affects the input Pokemon.<br>
	 *         - A Message to display if this Condition doesn't affect the Pokemon. May be <code>null</code> if there is no necessary message. */
	public Pair<Boolean, Message> affects(DungeonPokemon pokemon)
	{
		if (pokemon.hasStatusCondition(this)) return new Pair<>(false,
				new Message("status.already").addReplacement("<pokemon>", pokemon.getNickname()).addReplacement("<condition>", this.name()));
		for (AppliedStatusCondition c : pokemon.activeStatusConditions())
			if (c.condition instanceof PreventsOtherStatusCondition && ((PreventsOtherStatusCondition) c.condition).prevents(this))
				new Message("status.prevented.condition").addReplacement("<pokemon>", pokemon.getNickname()).addReplacement("<prevented>", this.name())
						.addReplacement("<preventer>", c.condition.name());
		return new Pair<>(true, null);
	}

	public AppliedStatusCondition create(DungeonPokemon target, Object source, Random random)
	{
		return new AppliedStatusCondition(this, target, source, RandomUtil.nextIntInBounds(this.durationMin, this.durationMax, random));
	}

	@Override
	public ExperienceGeneratedEvent getExperienceEvent()
	{
		return null;
	}

	protected Message immune(DungeonPokemon pokemon)
	{
		return new Message("status.immune").addReplacement("<pokemon>", pokemon.getNickname()).addReplacement("<condition>", this.name());
	}

	public Message name()
	{
		return new Message("status." + this.id);
	}

	public void onEnd(Floor floor, AppliedStatusCondition instance, ArrayList<DungeonEvent> events)
	{}

	public void onStart(Floor floor, AppliedStatusCondition instance, ArrayList<DungeonEvent> events)
	{}

	public void tick(Floor floor, AppliedStatusCondition instance, ArrayList<DungeonEvent> events)
	{}

}
