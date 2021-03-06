package com.darkxell.common.move.effects;

import java.util.ArrayList;

import com.darkxell.common.event.Event;
import com.darkxell.common.move.MoveContext;
import com.darkxell.common.move.calculator.MoveEffectCalculator;
import com.darkxell.common.move.effect.MoveEffect;

public class MultiplyWeightEffect extends MoveEffect {

    private final int[][] multiplierTable = new int[][] { { 0, 20, 30, 40, 50, 60, 70, 80, 200 },
            { 60, 70, 80, 90, 100, 110, 120, 130, 140 } };

    @Override
    public double damageMultiplier(boolean isUser, MoveContext context, ArrayList<Event> events) {
        float weight = context.target.species().getWeight();
        for (int i = this.multiplierTable.length - 1; i >= 0; --i)
            if (weight >= this.multiplierTable[i][0])
                return this.multiplierTable[i][1] * 1. / 100;
        return super.damageMultiplier(isUser, context, events);
    }

    @Override
    public void effects(MoveContext context, MoveEffectCalculator calculator, boolean missed,
            ArrayList<Event> effects, boolean createAdditionals) {
    }

}
