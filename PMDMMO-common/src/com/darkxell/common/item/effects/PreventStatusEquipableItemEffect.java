package com.darkxell.common.item.effects;

import java.util.ArrayList;

import com.darkxell.common.dungeon.floor.Floor;
import com.darkxell.common.event.Event;
import com.darkxell.common.event.Event.MessageEvent;
import com.darkxell.common.event.pokemon.StatusConditionCreatedEvent;
import com.darkxell.common.item.ItemContainer;
import com.darkxell.common.item.ItemEffect;
import com.darkxell.common.item.ItemStack;
import com.darkxell.common.pokemon.DungeonPokemon;
import com.darkxell.common.status.StatusCondition;
import com.darkxell.common.util.language.Message;

public class PreventStatusEquipableItemEffect extends ItemEffect {

    private final StatusCondition[] conditions;

    public PreventStatusEquipableItemEffect(int id, StatusCondition... conditions) {
        super(id);
        this.conditions = conditions;
    }

    @Override
    public void onPreEvent(Floor floor, Event event, DungeonPokemon concerned,
            ArrayList<Event> resultingEvents, ItemStack item, ItemContainer container, int containerIndex) {
        super.onPreEvent(floor, event, concerned, resultingEvents, item, container, containerIndex);

        if (event instanceof StatusConditionCreatedEvent) {
            StatusConditionCreatedEvent e = (StatusConditionCreatedEvent) event;
            boolean shouldPrevent = e.condition.pokemon == concerned && concerned.hasItem() && concerned.getItem().item().effect() == this;
            for (StatusCondition c : this.conditions)
                if (e.condition.condition == c) break;

            if (shouldPrevent) {
                e.consume();
                resultingEvents
                        .add(new MessageEvent(floor, event, new Message("status.prevented.item").addReplacement("<pokemon>", concerned.getNickname())
                                .addReplacement("<item>", concerned.getItem().name()).addReplacement("<condition>", e.condition.condition.name())));
            }
        }
    }

}
