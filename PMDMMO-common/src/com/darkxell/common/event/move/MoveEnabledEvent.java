package com.darkxell.common.event.move;

import java.util.ArrayList;

import com.darkxell.common.dungeon.floor.Floor;
import com.darkxell.common.event.Event;
import com.darkxell.common.event.EventSource;
import com.darkxell.common.pokemon.LearnedMove;
import com.darkxell.common.util.Communicable;
import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;

public class MoveEnabledEvent extends Event implements Communicable {

    /** The value of its "enabled" property when changed. */
    private boolean enabled;
    /** The modified move. */
    private LearnedMove move;

    public MoveEnabledEvent(Floor floor, EventSource eventSource) {
        super(floor, eventSource);
    }

    public MoveEnabledEvent(Floor floor, EventSource eventSource, LearnedMove move, boolean enabled) {
        super(floor, eventSource);
        this.move = move;
        this.enabled = enabled;
    }

    public boolean enabled() {
        return this.enabled;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MoveEnabledEvent))
            return false;
        MoveEnabledEvent o = (MoveEnabledEvent) obj;
        if (this.move != o.move)
            return false;
        if (this.enabled != o.enabled)
            return false;
        return true;
    }

    @Override
    public String loggerMessage() {
        return this.move.move() + " was " + (this.enabled ? "en" : "dis") + "abled.";
    }

    public LearnedMove move() {
        return this.move;
    }

    @Override
    public ArrayList<Event> processServer() {
        this.move.setEnabled(this.enabled);
        return super.processServer();
    }

    @Override
    public void read(JsonObject value) throws JsonReadingException {
        try {
            this.move = this.floor.dungeon.communication.moveIDs.get(value.getLong("move", 0));
            if (this.move == null)
                throw new JsonReadingException("No move with ID " + value.getLong("move", 0));
        } catch (JsonReadingException e) {
            throw e;
        } catch (Exception e) {
            throw new JsonReadingException("Wrong value for Pokemon ID: " + value.get("pokemon"));
        }
        try {
            this.enabled = value.getBoolean("enabled", false);
        } catch (Exception e) {
            throw new JsonReadingException("Wrong value for enabled: " + value.get("enabled"));
        }
    }

    @Override
    public JsonObject toJson() {
        return Json.object().add("move", this.move.id()).add("enabled", this.enabled);
    }

}
