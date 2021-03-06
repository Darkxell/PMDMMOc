package com.darkxell.common.event.dungeon.weather;

import com.darkxell.common.dungeon.floor.Floor;
import com.darkxell.common.event.Event;
import com.darkxell.common.event.EventSource;
import com.darkxell.common.util.language.Message;
import com.darkxell.common.weather.ActiveWeather;

public class WeatherChangedEvent extends Event {

    /** The new current Weather. */
    public final ActiveWeather next;
    /** The previous Weather. may be null if this describes the application of the Floor's prevailing Weather. */
    public final ActiveWeather previous;

    public WeatherChangedEvent(Floor floor, EventSource eventSource, ActiveWeather previous,
            ActiveWeather next) {
        super(floor, eventSource);
        this.previous = previous;
        this.next = next;
        this.messages.add(new Message("weather.changed").addReplacement("<weather>", this.next.weather.name()));
    }

    @Override
    public String loggerMessage() {
        return this.next.weather.name() + " is now the main Weather.";
    }

}
