package com.darkxell.common.item;

import java.util.ArrayList;

import org.jdom2.Element;

import com.darkxell.common.pokemon.DungeonPokemon;
import com.darkxell.common.util.Message;

/** An Item that teaches a move to a Pok�mon when used, then turns into a Used TM. */
public class ItemTM extends ItemHM
{

	public ItemTM(Element xml)
	{
		super(xml);
	}

	public ItemTM(int id, int price, int sell, int sprite, boolean isStackable, int moveID)
	{
		super(id, price, sell, sprite, isStackable, moveID);
	}

	@Override
	public ArrayList<ItemAction> getLegalActions(boolean inDungeon)
	{
		ArrayList<ItemAction> actions = super.getLegalActions(inDungeon);
		if (!actions.contains(ItemAction.USE)) actions.add(ItemAction.USE);
		return actions;
	}

	@Override
	public ArrayList<Message> use(DungeonPokemon pokemon)
	{
		if (pokemon.player != null)
		{
			if (pokemon.player.inventory.isFull()) pokemon.tile.setItem(new ItemStack(205));
			else pokemon.player.inventory.add(new ItemStack(205));
		}
		return super.use(pokemon);
	}
}
