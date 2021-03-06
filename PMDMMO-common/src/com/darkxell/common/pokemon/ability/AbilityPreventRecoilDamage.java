package com.darkxell.common.pokemon.ability;

import java.util.ArrayList;

import com.darkxell.common.dungeon.floor.Floor;
import com.darkxell.common.event.Event;
import com.darkxell.common.event.pokemon.DamageDealtEvent;
import com.darkxell.common.event.pokemon.DamageDealtEvent.DamageType;
import com.darkxell.common.pokemon.DungeonPokemon;

public class AbilityPreventRecoilDamage extends Ability {

    public AbilityPreventRecoilDamage(int id) {
        super(id);
    }

    @Override
    public void onPreEvent(Floor floor, Event event, DungeonPokemon concerned,
            ArrayList<Event> resultingEvents) {
        super.onPreEvent(floor, event, concerned, resultingEvents);
        if (event instanceof DamageDealtEvent) {
            DamageDealtEvent e = (DamageDealtEvent) event;
            if (e.damageType == DamageType.RECOIL && e.target.ability() == this)
                e.consume();
        }
    }

}
