package com.darkxell.common.pokemon.ability;

import java.util.ArrayList;

import com.darkxell.common.dungeon.floor.Floor;
import com.darkxell.common.event.Event;
import com.darkxell.common.event.move.MoveSelectionEvent.MoveUse;
import com.darkxell.common.event.pokemon.DamageDealtEvent;
import com.darkxell.common.event.pokemon.TriggeredAbilityEvent;
import com.darkxell.common.move.MoveCategory;
import com.darkxell.common.pokemon.DungeonPokemon;

public abstract class AbilityOnHit extends Ability {

    public final int probability;

    public AbilityOnHit(int id, int probability) {
        super(id);
        this.probability = probability;
    }

    /**
     * Called when a Pokemon uses a damaging, physical move on a Pokemon with this ability, and this abtility triggers.
     *
     * @param floor           - The Floor context.
     * @param event           - The triggering Event.
     * @param source          - The Move that was used.
     * @param abilityEvent    - The Triggered Ability Event that was generated.
     * @param resultingEvents - List of Event to add resulting events to.
     */
    protected abstract void onHit(Floor floor, DamageDealtEvent event, MoveUse source,
            TriggeredAbilityEvent abilityEvent, ArrayList<Event> resultingEvents);

    @Override
    public void onPostEvent(Floor floor, Event event, DungeonPokemon concerned, ArrayList<Event> resultingEvents) {
        if (event instanceof DamageDealtEvent) {
            DamageDealtEvent d = (DamageDealtEvent) event;
            if (d.target.ability() == this && d.target == concerned && d.source instanceof MoveUse
                    && ((MoveUse) d.source).move.move().getCategory() == MoveCategory.Physical
                    && ((MoveUse) d.source).user != concerned && floor.random.nextDouble() * 100 < this.probability) {
                TriggeredAbilityEvent e = new TriggeredAbilityEvent(floor, event, d.target);
                resultingEvents.add(e);
                this.onHit(floor, d, (MoveUse) d.source, e, resultingEvents);
            }
        }
    }

}
