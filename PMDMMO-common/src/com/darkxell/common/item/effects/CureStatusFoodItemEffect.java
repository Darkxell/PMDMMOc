package com.darkxell.common.item.effects;

import java.util.ArrayList;

import com.darkxell.common.event.Event;
import com.darkxell.common.event.item.ItemUseEvent;
import com.darkxell.common.event.pokemon.StatusConditionEndedEvent.StatusConditionEndReason;
import com.darkxell.common.status.StatusCondition;

public class CureStatusFoodItemEffect extends FoodItemEffect {

    public final StatusCondition[] conditions;

    public CureStatusFoodItemEffect(int id, int food, int belly, int bellyIfFull, StatusCondition... conditions) {
        super(id, food, belly, bellyIfFull);
        this.conditions = conditions;
    }

    @Override
    public void use(ItemUseEvent itemEvent, ArrayList<Event> events) {
        super.use(itemEvent, events);
        for (StatusCondition c : this.conditions)
            if (itemEvent.target.hasStatusCondition(c))
                events.add(itemEvent.target.getStatusCondition(c).finish(itemEvent.floor, StatusConditionEndReason.HEALED, itemEvent));
    }

}
