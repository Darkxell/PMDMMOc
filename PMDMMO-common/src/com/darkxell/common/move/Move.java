package com.darkxell.common.move;

import java.util.ArrayList;

import com.darkxell.common.event.Event;
import com.darkxell.common.event.move.MoveSelectionEvent;
import com.darkxell.common.event.move.MoveUseEvent;
import com.darkxell.common.model.move.MoveModel;
import com.darkxell.common.move.behavior.MoveBehavior;
import com.darkxell.common.move.behavior.MoveBehaviors;
import com.darkxell.common.pokemon.DungeonPokemon;
import com.darkxell.common.pokemon.Pokemon;
import com.darkxell.common.pokemon.PokemonType;
import com.darkxell.common.registry.Registrable;
import com.darkxell.common.util.language.Localization;
import com.darkxell.common.util.language.Message;

public class Move implements Registrable<Move> {

    private final MoveModel model;

    public Move(MoveModel model) {
        this.model = model;
    }

    public MoveBehavior behavior() {
        return MoveBehaviors.find(this.getEffectID());
    }

    @Override
    public int compareTo(Move o) {
        return Integer.compare(this.getID(), o.getID());
    }

    /** @return This Move's description. */
    public Message description() {
        return this.behavior().description(this);
    }

    public int displayedPower() {
        return this.getPower() * 5;
    }

    public int getAccuracy() {
        return this.model.getAccuracy();
    }

    public MoveCategory getCategory() {
        return this.model.getCategory();
    }

    public int getCritical() {
        return this.model.getCritical();
    }

    public int getEffectID() {
        return this.model.getEffectID();
    }

    public int getID() {
        return this.model.getID();
    }

    public int getPower() {
        return this.model.getPower();
    }

    public int getPP() {
        return this.model.getPP();
    }

    public MoveRange getRange() {
        return this.model.getRange();
    }

    public MoveTarget getTargets() {
        return this.model.getTargets();
    }

    public PokemonType getType() {
        return this.model.getType();
    }

    public PokemonType getType(Pokemon pokemon) {
        return this.behavior().getMoveType(this, pokemon);
    }

    public boolean hasUseMessage() {
        return Localization.containsKey("move." + this.getID());
    }

    public boolean isDealsDamage() {
        return this.model.isDealsDamage();
    }

    public boolean isGinsengable() {
        return this.model.isGinsengable();
    }

    public boolean isPiercesFreeze() {
        return this.model.isPiercesFreeze();
    }

    public boolean isReflectable() {
        return this.model.isReflectable();
    }

    public boolean isSnatchable() {
        return this.model.isSnatchable();
    }

    public boolean isSound() {
        return this.model.isSound();
    }

    /** @return This Move's name. */
    public Message name() {
        return new Message("move." + this.getID()).addPrefix("<type-" + this.getType().id + "> ").addPrefix("<green>")
                .addSuffix("</color>");
    }

    /**
     * @param  moveEvent - The used move.
     * @return           The Events created by this selection. Creates MoveUseEvents, distributing this Move on targets.
     */
    public final void prepareUse(MoveSelectionEvent moveEvent, ArrayList<Event> events) {
        this.behavior().onMoveSelected(moveEvent, events);
    }

    @Override
    public String toString() {
        return this.name().toString().replaceAll("<.*?>", "");
    }

    public Message unaffectedMessage(DungeonPokemon target) {
        return new Message("move.effectiveness.none").addReplacement("<pokemon>", target.getNickname());
    }

    /**
     * Applies this Move's effects to a Pokemon.
     *
     * @param  moveEvent - The Move instance that was selected.
     * @param  events    - The events resulting from this Move. They typically include damage, healing, stat changes...
     * @return           <code>true</code> if the Move missed.
     */
    public boolean useOn(MoveUseEvent moveEvent, ArrayList<Event> events) {
        return this.behavior().onMoveUsed(new MoveContext(moveEvent.floor, this, this.behavior(),
                moveEvent.usedMove.user, moveEvent.target, moveEvent, moveEvent.usedMove.move), events);
    }

    public MoveModel getModel() {
        return this.model;
    }
}
