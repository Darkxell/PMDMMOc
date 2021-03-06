package com.darkxell.common.event.turns;

import java.util.ArrayList;

import com.darkxell.common.dungeon.floor.Floor;
import com.darkxell.common.model.pokemon.Stat;
import com.darkxell.common.move.MoveContext;
import com.darkxell.common.pokemon.DungeonPokemon;
import com.darkxell.common.pokemon.DungeonStats;
import com.darkxell.common.pokemon.PropertyModificator;
import com.darkxell.common.status.AppliedStatusCondition;

public class SpeedCalculator {

    public final Floor floor;
    private PropertyModificator modificator = new PropertyModificator();
    public final DungeonPokemon pokemon;

    public SpeedCalculator(Floor floor, DungeonPokemon pokemon) {
        this.floor = floor;
        this.pokemon = pokemon;
        this.modificator.add(this.pokemon.ability());
        if (this.pokemon.hasItem())
            this.modificator.add(this.pokemon.getItem().item());
        for (AppliedStatusCondition condition : this.pokemon.activeStatusConditions())
            this.modificator.add(condition.condition);
        this.modificator.add(floor.currentWeather().weather);
    }

    public double compute() {
        int stage = this.pokemon.stats.getStage(Stat.Speed);
        stage = this.modificator.applyStatStageModifications(Stat.Speed, stage,
                new MoveContext(floor, null, null, null, this.pokemon, null, null), new ArrayList<>());
        if (stage < 0)
            stage = 0;
        if (stage >= DungeonStats.speedTable.length)
            stage = DungeonStats.speedTable.length - 1;
        double speed = DungeonStats.speedTable[stage];
        speed = this.modificator.applyStatModifications(Stat.Speed, stage,
                new MoveContext(floor, null, null, null, this.pokemon, null, null), new ArrayList<>());
        if (speed < 0)
            speed = .5;
        if (speed > Stat.MAX_SPEED)
            speed = Stat.MAX_SPEED;

        return speed;
    }

}
