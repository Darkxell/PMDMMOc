package com.darkxell.common.util;

import java.util.Random;

import com.darkxell.common.item.ItemID;
import com.darkxell.common.item.ItemStack;
import com.darkxell.common.player.Player;
import com.darkxell.common.pokemon.PokemonRegistry;

public class Util
{

	public static Player createDefaultPlayer()
	{
		Player player = new Player("Offline debug account name", PokemonRegistry.find(4).generate(new Random(), 1));
		player.setStoryPosition(1);
		player.addAlly(PokemonRegistry.find(1).generate(new Random(), 1, 1));
		player.addAlly(PokemonRegistry.find(255).generate(new Random(), 1));
		player.getTeamLeader().setItem(new ItemStack(ItemID.XRaySpecs));
		return player;
	}

}