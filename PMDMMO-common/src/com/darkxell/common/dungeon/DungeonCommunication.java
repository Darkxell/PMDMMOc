package com.darkxell.common.dungeon;

import com.darkxell.common.dungeon.TempIDRegistry.ItemsTempIDRegistry;
import com.darkxell.common.dungeon.TempIDRegistry.MovesTempIDRegistry;
import com.darkxell.common.dungeon.TempIDRegistry.PokemonTempIDRegistry;
import com.darkxell.common.dungeon.floor.Floor;
import com.darkxell.common.event.Event;
import com.darkxell.common.event.EventCommunication;
import com.darkxell.common.event.turns.GameTurn;
import com.darkxell.common.item.ItemContainer;
import com.darkxell.common.item.ItemContainer.ItemContainerType;
import com.darkxell.common.player.Player;
import com.darkxell.common.pokemon.Pokemon;
import com.darkxell.common.util.Communicable.JsonReadingException;
import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

public class DungeonCommunication {
    private static long readLong(JsonObject json, String key) throws JsonReadingException {
        JsonValue valObj = json.get(key);
        if (valObj == null)
            throw new JsonReadingException("No value for " + key + ".");
        return valObj.asLong();
    }

    public static AutoDungeonExploration readExploration(JsonObject json) throws JsonReadingException {
        int dungeonID = (int) readLong(json, "dungeon");
        long seed = readLong(json, "seed");

        if (dungeonID <= 0)
            throw new JsonReadingException("Dungeon ID must be positive.");
        AutoDungeonExploration dungeon = new AutoDungeonExploration(dungeonID, seed);

        if (json.get("events") != null && json.get("events").isArray())
            for (JsonValue event : json.get("events").asArray())
                if (event.isObject())
                    dungeon.pendingEvents.add(event.asObject());

        return dungeon;
    }

    public final DungeonExploration dungeon;
    public final ItemsTempIDRegistry itemIDs = new ItemsTempIDRegistry();
    public final MovesTempIDRegistry moveIDs = new MovesTempIDRegistry();
    public final PokemonTempIDRegistry pokemonIDs = new PokemonTempIDRegistry();

    public DungeonCommunication(DungeonExploration dungeon) {
        this.dungeon = dungeon;
    }

    /**
     * @param  onlyPAE - True if only Player Action Events should be returned.
     * @return         The list of Events in this exploration, with Dungeon ID and Seed.
     */
    public JsonObject explorationSummary(boolean onlyPAE) {
        JsonObject root = Json.object();
        root.add("dungeon", this.dungeon.id);
        root.add("seed", this.dungeon.seed);

        JsonArray array = (JsonArray) Json.array();

        for (GameTurn turn : this.dungeon.listTurns())
            for (Event e : turn.events())
                if (!onlyPAE || e.isPAE())
                    array.add(EventCommunication.prepareToSend(e));

        root.add("events", array);

        return root;
    }

    public ItemContainer identifyContainer(Floor floor, ItemContainerType type, long id) throws JsonReadingException {
        try {
            switch (type) {
            case TILE:
                int y = (int) (id / floor.getWidth());
                int x = (int) (id - y * floor.getWidth());
                return floor.tileAt(x, y);

            case INVENTORY:
                for (Player p : floor.dungeon.exploringPlayers())
                    if (p.inventory().containerID() == id)
                        return p.inventory();
                throw new JsonReadingException(
                        "Inventory with id " + id + " doesn't match any known players inventory.");

            case DUNGEON_POKEMON:
            case POKEMON:
                Pokemon p = this.pokemonIDs.get(id);
                if (p == null)
                    throw new JsonReadingException("No Pokemon with id " + id);
                return p;

            default:
                throw new JsonReadingException("ItemContainerType " + type + " CANNOT EXIST!!! WTF??");
            }
        } catch (JsonReadingException e) {
            throw e;
        } catch (Exception e) {
            throw new JsonReadingException("Couldn't find inventory with type " + type + " and id " + id);
        }
    }

}
