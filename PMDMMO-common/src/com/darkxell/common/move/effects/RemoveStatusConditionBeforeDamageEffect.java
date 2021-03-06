package com.darkxell.common.move.effects;

import java.util.ArrayList;

import com.darkxell.common.event.Event;
import com.darkxell.common.event.pokemon.StatusConditionEndedEvent.StatusConditionEndReason;
import com.darkxell.common.move.MoveContext;
import com.darkxell.common.move.calculator.MoveEffectCalculator;
import com.darkxell.common.move.effect.MoveEffect;
import com.darkxell.common.status.StatusCondition;

public class RemoveStatusConditionBeforeDamageEffect extends MoveEffect {

    public final StatusCondition condition;

    public RemoveStatusConditionBeforeDamageEffect(StatusCondition condition) {
        super(-1);
        this.condition = condition;
    }

    @Override
    public void effects(MoveContext context, MoveEffectCalculator calculator, boolean missed, ArrayList<Event> effects,
            boolean createAdditionals) {

        if (!missed && context.target != null && context.target.hasStatusCondition(this.condition) && createAdditionals) {
            effects.add(context.target.getStatusCondition(this.condition).finish(context.floor,
                    StatusConditionEndReason.BROKEN, context.event));
        }
    }

}
