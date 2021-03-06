package com.darkxell.common.event.pokemon;

import java.util.ArrayList;

import com.darkxell.common.dungeon.floor.Floor;
import com.darkxell.common.dungeon.floor.Tile;
import com.darkxell.common.event.Event;
import com.darkxell.common.event.EventSource;
import com.darkxell.common.pokemon.DungeonPokemon;

public class SwitchedPokemonEvent extends Event {

    public final DungeonPokemon switcher, target;

    public SwitchedPokemonEvent(Floor floor, EventSource eventSource, DungeonPokemon switcher,
            DungeonPokemon target) {
        super(floor, eventSource);
        this.switcher = switcher;
        this.target = target;
    }

    @Override
    public String loggerMessage() {
        return this.switcher + " switched with " + this.target;
    }

    @Override
    public ArrayList<Event> processServer() {
        Tile s = this.switcher.tile(), t = this.target.tile();
        s.setPokemon(this.target);
        t.setPokemon(this.switcher);
        return super.processServer();
    }

}
