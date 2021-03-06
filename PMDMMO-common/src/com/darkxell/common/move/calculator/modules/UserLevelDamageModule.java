package com.darkxell.common.move.calculator.modules;

import java.util.ArrayList;

import com.darkxell.common.event.Event;
import com.darkxell.common.move.MoveContext;
import com.darkxell.common.move.calculator.CalculatorDamageModule;
import com.darkxell.common.move.calculator.MoveEffectCalculator;

public class UserLevelDamageModule implements CalculatorDamageModule {

    @Override
    public int compute(MoveContext context, MoveEffectCalculator calculator, ArrayList<Event> events) {
        return context.user.level();
    }

}
