package com.darkxell.common.status.conditions;

import com.darkxell.common.dungeon.floor.Floor;
import com.darkxell.common.event.DungeonUtils;
import com.darkxell.common.pokemon.DungeonPokemon;
import com.darkxell.common.pokemon.LearnedMove;
import com.darkxell.common.status.AppliedStatusCondition;
import com.darkxell.common.status.StatusCondition;
import com.darkxell.common.util.Logger;
import com.darkxell.common.util.Pair;
import com.darkxell.common.util.language.Message;

public class ForceLastMoveStatusCondition extends StatusCondition {

    public ForceLastMoveStatusCondition(int id, boolean isAilment, int durationMin, int durationMax) {
        super(id, isAilment, durationMin, durationMax);
    }

    @Override
    public Pair<Boolean, Message> affects(Floor floor, AppliedStatusCondition condition, DungeonPokemon pokemon) {

        LearnedMove move = DungeonUtils.findLastMove(floor, pokemon);

        if (move == null)
            return new Pair<>(false,
                    new Message("status.not_affected").addReplacement("<pokemon>", pokemon.getNickname())); // Couldn't
                                                                                                            // find a
                                                                                                            // move to
                                                                                                            // target

        return super.affects(floor, condition, pokemon);
    }

    @Override
    public AppliedStatusCondition create(Floor floor, DungeonPokemon target, Object source) {
        AppliedStatusCondition c = super.create(floor, target, source);
        LearnedMove m = DungeonUtils.findLastMove(floor, target);
        if (m != null)
            c.addFlag("move:" + m.moveId());
        return c;
    }

    @Override
    public boolean preventsUsingMove(LearnedMove move, DungeonPokemon pokemon, Floor floor) {
        if (move != null && pokemon.hasStatusCondition(this)) {
            AppliedStatusCondition s = pokemon.getStatusCondition(this);
            String moveid = null;
            for (String flag : s.listFlags())
                if (flag.startsWith("move:"))
                    moveid = flag.substring("move:".length());

            if (moveid == null)
                Logger.e("Pokemon has Encore status condition without a moveid to encore!");
            if (moveid != null && moveid.matches("\\d+"))
                return move.moveId() != Integer.parseInt(moveid);
        }
        return super.preventsUsingMove(move, pokemon, floor);
    }

}
