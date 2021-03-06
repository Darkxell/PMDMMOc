package com.darkxell.common.event.pokemon;

import java.util.ArrayList;

import com.darkxell.common.dungeon.floor.Floor;
import com.darkxell.common.dungeon.floor.Tile;
import com.darkxell.common.event.Event;
import com.darkxell.common.event.EventSource;
import com.darkxell.common.event.pokemon.DamageDealtEvent.DamageType;
import com.darkxell.common.event.pokemon.DamageDealtEvent.DefaultDamageSource;
import com.darkxell.common.pokemon.DungeonPokemon;
import com.darkxell.common.util.Direction;
import com.darkxell.common.util.language.Message;

public class BlowbackPokemonEvent extends Event {
    
    public static final int MAX_BLOWBACK_DISTANCE = 10;

    private Tile destination;
    public final Direction direction;
    public final Tile origin;
    public final DungeonPokemon pokemon;
    private boolean wasHurt;

    public BlowbackPokemonEvent(Floor floor, EventSource eventSource, DungeonPokemon pokemon,
            Direction direction) {
        super(floor, eventSource);
        this.pokemon = pokemon;
        this.direction = direction;
        this.origin = this.pokemon.tile();
    }

    public Tile destination() {
        return this.destination;
    }

    @Override
    public String loggerMessage() {
        return this.pokemon + "was blown back " + this.direction;
    }

    @Override
    public ArrayList<Event> processServer() {
        this.messages.add(new Message("pokemon.blowback").addReplacement("<pokemon>", this.pokemon.getNickname()));

        Tile current = this.pokemon.tile();
        Tile temp = current.adjacentTile(this.direction);
        int count = 0;

        while (!temp.isWall() && temp.getPokemon() == null
                && count < MAX_BLOWBACK_DISTANCE) {
            ++count;
            current = temp;
            temp = current.adjacentTile(direction);
        }

        this.wasHurt = count < MAX_BLOWBACK_DISTANCE;
        this.destination = count < MAX_BLOWBACK_DISTANCE ? temp : current;
        current.setPokemon(this.pokemon);
        if (count < MAX_BLOWBACK_DISTANCE && (temp.isWall() || temp.getPokemon() != null)) {
            this.resultingEvents.add(new DamageDealtEvent(this.floor, this, this.pokemon,
                    new DefaultDamageSource(this.floor, null, this), DamageType.COLLISION, 5));
            if (temp.getPokemon() != null)
                this.resultingEvents.add(new DamageDealtEvent(this.floor, this, temp.getPokemon(),
                        new DefaultDamageSource(this.floor, null, eventSource), DamageType.COLLISION, 5));
        }

        return super.processServer();
    }

    public boolean wasHurt() {
        return this.wasHurt;
    }

}
