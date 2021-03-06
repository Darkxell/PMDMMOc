package com.darkxell.common.event.dungeon;

import java.util.ArrayList;

import com.darkxell.common.dungeon.floor.Floor;
import com.darkxell.common.event.Event;
import com.darkxell.common.event.EventSource;
import com.darkxell.common.mission.DungeonMission;

public class MissionClearedEvent extends Event {

    public final DungeonMission mission;

    public MissionClearedEvent(Floor floor, EventSource eventSource, DungeonMission mission) {
        super(floor, eventSource);
        this.mission = mission;
    }

    @Override
    public String loggerMessage() {
        // this.messages.add(new Message("mission.cleared").addReplacement("<team>", this.mission.owner.name()));
        return "Cleared mission: " + this.mission.missionData.toString();
    }

    @Override
    public ArrayList<Event> processServer() {
        this.mission.clear();
        return super.processServer();
    }

}
