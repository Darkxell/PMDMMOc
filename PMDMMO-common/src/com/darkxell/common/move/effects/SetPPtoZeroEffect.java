package com.darkxell.common.move.effects;

import java.util.ArrayList;

import com.darkxell.common.event.DungeonUtils;
import com.darkxell.common.event.Event;
import com.darkxell.common.event.stats.PPChangedEvent;
import com.darkxell.common.move.MoveContext;
import com.darkxell.common.move.effect.MoveEffect;
import com.darkxell.common.move.effect.MoveEffectCalculator;
import com.darkxell.common.pokemon.LearnedMove;

public class SetPPtoZeroEffect extends MoveEffect {

    @Override
    public void effects(MoveContext context, MoveEffectCalculator calculator, boolean missed,
            ArrayList<Event> effects, boolean createAdditionals) {

        if (!missed && !createAdditionals) {
            LearnedMove move = DungeonUtils.findLastMove(context.floor, context.target);
            if (move != null && move.pp() > 0)
                effects.add(new PPChangedEvent(context.floor, context.event, context.target, -move.pp(),
                        context.target.moveIndex(move)));
        }
    }

}
