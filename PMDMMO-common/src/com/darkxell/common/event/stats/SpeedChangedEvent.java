package com.darkxell.common.event.stats;

import java.util.ArrayList;

import com.darkxell.common.dungeon.floor.Floor;
import com.darkxell.common.event.Event;
import com.darkxell.common.event.EventSource;
import com.darkxell.common.model.pokemon.Stat;
import com.darkxell.common.pokemon.DungeonPokemon;
import com.darkxell.common.util.language.Message;

public class SpeedChangedEvent extends Event {

    public final DungeonPokemon pokemon;

    public SpeedChangedEvent(Floor floor, EventSource eventSource, DungeonPokemon pokemon) {
        super(floor, eventSource);
        this.pokemon = pokemon;
    }

    @Override
    public String loggerMessage() {
        return this.messages.get(0).toString();
    }

    @Override
    public ArrayList<Event> processServer() {
        this.messages.add(new Message("stat.speed." + pokemon.stats.getStage(Stat.Speed)).addReplacement("<pokemon>",
                this.pokemon.getNickname()));
        return super.processServer();
    }

}
