package com.darkxell.common.event.item;

import java.util.ArrayList;

import com.darkxell.common.dungeon.floor.Floor;
import com.darkxell.common.dungeon.floor.Tile;
import com.darkxell.common.event.Event;
import com.darkxell.common.event.EventSource;
import com.darkxell.common.item.Item;
import com.darkxell.common.item.ItemStack;
import com.darkxell.common.pokemon.DungeonPokemon;
import com.darkxell.common.util.language.Message;

public class MoneyCollectedEvent extends Event {

    public final ItemStack moneyItem;
    public final DungeonPokemon pokemon;
    public final Tile tile;

    public MoneyCollectedEvent(Floor floor, EventSource eventSource, DungeonPokemon pokemon) {
        super(floor, eventSource);
        this.pokemon = pokemon;
        this.tile = this.pokemon.tile();
        this.moneyItem = this.tile.getItem();
    }

    @Override
    public boolean isValid() {
        return this.pokemon.player() != null && this.moneyItem != null && this.moneyItem.itemid() == Item.POKEDOLLARS;
    }

    @Override
    public String loggerMessage() {
        return this.messages.get(0).toString();
    }

    @Override
    public ArrayList<Event> processServer() {
        this.messages.add(new Message("ground.pickup").addReplacement("<pokemon>", this.pokemon.getNickname())
                .addReplacement("<item>", this.moneyItem.name()));
        this.tile.setItem(null);
        this.pokemon.player().setMoneyInBag(this.pokemon.player().moneyInBag() + this.moneyItem.quantity());
        return super.processServer();
    }

}
