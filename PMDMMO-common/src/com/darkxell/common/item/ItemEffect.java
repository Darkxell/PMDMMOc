package com.darkxell.common.item;

import java.util.ArrayList;

import com.darkxell.common.dungeon.floor.Floor;
import com.darkxell.common.dungeon.floor.Tile;
import com.darkxell.common.event.Event;
import com.darkxell.common.event.item.ItemSelectionEvent;
import com.darkxell.common.event.item.ItemUseEvent;
import com.darkxell.common.pokemon.AffectsPokemon;
import com.darkxell.common.pokemon.DungeonPokemon;
import com.darkxell.common.util.Direction;
import com.darkxell.common.util.language.Message;

public class ItemEffect implements AffectsPokemon {
    public final int id;

    public ItemEffect(int id) {
        this.id = id;
        ItemEffects.effects.put(this.id, this);
    }

    public Message description(Item item) {
        return new Message("item.info." + this.id).addReplacement("<item>", item.name());
    }

    /** @return The Tile an Item will land at if it's thrown by the input Pokemon. */
    public Tile findDestinationStraight(Floor floor, DungeonPokemon pokemon, Item item, boolean targetsAllies) {
        Direction direction = pokemon.facing();
        Tile current = pokemon.tile().adjacentTile(direction);

        while (current.getPokemon() == null || (!targetsAllies && pokemon.isAlliedWith(current.getPokemon()))) {
            current = current.adjacentTile(direction);
            if (current.isWall()) return current;
        }

        return current;
    }

    /** The ID of the translation to use for the message to display when using this Item. */
    protected String getUseEffectID() {
        return "item.used";
    }

    /** @param event - The Pokemon using the Item.
     * @return The message to display when using this Item. */
    public Message getUseEffectMessage(ItemSelectionEvent event) {
        return new Message(this.getUseEffectID()).addReplacement("<user>", event.user().getNickname())
                .addReplacement("<target>", event.target() == null ? new Message("?", false) : event.target().getNickname())
                .addReplacement("<item>", event.item().name());
    }

    /** @return The name of the "Use" option for this Item. */
    public Message getUseName() {
        return new Message("item.use");
    }

    /** @return True if the Item disappears after use. */
    public boolean isConsummable() {
        return true;
    }

    public boolean isThrowable() {
        return true;
    }

    /** @return True if the Item can be used. */
    public boolean isUsable() {
        return false;
    }

    public boolean isUsableOnCatch() {
        return this.isUsable();
    }

    /** @return True if the user has to select a Team member as target for this Item. */
    public boolean isUsedOnTeamMember() {
        return false;
    }

    /** @return The name of an Item with this Effect. */
    public Message name(Item item) {
        return new Message("item." + item.getID());
    }

    /** Method called just after an Event is processed.
     *
     * @param floor - The Floor context.
     * @param event - The processed Event.
     * @param concerned - A reference to a Pokemon for various uses. It is the owner this is an Item, a Status Condition or an Ability.
     * @param resultingEvents - List to store any created Events. They will be added to the list of pending Events.
     * @param item            - The Item with this effect.
     * @param container       - The object containing the Item with this effect.
     * @param containerIndex  - The index of the Item in the container.
     */
    public void onPostEvent(Floor floor, Event event, DungeonPokemon concerned,
            ArrayList<Event> resultingEvents, ItemStack item, ItemContainer container, int containerIndex) {
    }

    /** Method called just before an Event is processed.
     *
     * @param floor - The Floor context.
     * @param event - The processed Event.
     * @param concerned - A reference to a Pokemon for various uses. It is the owner this is an Item, a Status Condition or an Ability.
     * @param resultingEvents - List to store any created Events. They will be added to the list of pending Events.
     * @param item            - The Item with this effect.
     * @param container       - The object containing the Item with this effect.
     * @param containerIndex  - The index of the Item in the container.
     */
    public void onPreEvent(Floor floor, Event event, DungeonPokemon concerned,
            ArrayList<Event> resultingEvents, ItemStack item, ItemContainer container, int containerIndex) {
    }

    /** Called when an Item with this Effect is used.
     *
     * @param itemEvent - The current Floor.
     */
    public void use(ItemUseEvent itemEvent, ArrayList<Event> events) {
    }

    /** Called when an Item with this Effect is used when caught.
     * 
     * @param itemEvent - The concerned Move use event
     */
    public void useThrown(ItemUseEvent itemEvent, ArrayList<Event> events) {
        this.use(itemEvent, events);
    }

}
