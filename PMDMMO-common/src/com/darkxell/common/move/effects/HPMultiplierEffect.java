package com.darkxell.common.move.effects;

import java.util.ArrayList;

import com.darkxell.common.event.Event;
import com.darkxell.common.move.MoveContext;
import com.darkxell.common.move.calculator.MoveEffectCalculator;
import com.darkxell.common.move.effect.MoveEffect;

public class HPMultiplierEffect extends MoveEffect {

    @Override
    public double damageMultiplier(boolean isUser, MoveContext context, ArrayList<Event> events) {
        double hp = context.user.getHpPercentage();
        if (hp < .25)
            return 8;
        if (hp < .5)
            return 4;
        if (hp < .75)
            return 2;
        return super.damageMultiplier(isUser, context, events);
    }

    @Override
    public void effects(MoveContext context, MoveEffectCalculator calculator, boolean missed,
            ArrayList<Event> effects, boolean createAdditionals) {}

}
