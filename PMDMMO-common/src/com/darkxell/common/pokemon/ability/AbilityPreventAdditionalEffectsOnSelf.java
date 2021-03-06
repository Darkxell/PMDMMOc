package com.darkxell.common.pokemon.ability;

import java.util.ArrayList;

import com.darkxell.common.dungeon.floor.Floor;
import com.darkxell.common.event.Event;
import com.darkxell.common.event.move.MoveUseEvent;
import com.darkxell.common.event.pokemon.TriggeredAbilityEvent;
import com.darkxell.common.move.effect.MoveEvents;
import com.darkxell.common.pokemon.DungeonPokemon;

public class AbilityPreventAdditionalEffectsOnSelf extends Ability {

    public AbilityPreventAdditionalEffectsOnSelf(int id) {
        super(id);
    }

    @Override
    public void onPreEvent(Floor floor, Event event, DungeonPokemon concerned,
            ArrayList<Event> resultingEvents) {
        super.onPreEvent(floor, event, concerned, resultingEvents);

        if (this.shouldPrevent(floor, event, concerned)) {
            resultingEvents.add(new TriggeredAbilityEvent(floor, event, concerned));
            event.consume();
        }
    }

    protected boolean shouldPrevent(Floor floor, Event event, DungeonPokemon concerned) {
        // Prevent if the event comes from a move targeting me, and the event is an additional effect
        return event.eventSource instanceof MoveUseEvent && event.hasFlag(MoveEvents.ADDITIONAL)
                && ((MoveUseEvent) event.eventSource).target == concerned;
    }

}
