package com.darkxell.common.util;

import java.util.Random;

import com.darkxell.common.item.ItemStack;
import com.darkxell.common.mission.InvalidParammetersException;
import com.darkxell.common.mission.Mission;
import com.darkxell.common.mission.MissionReward;
import com.darkxell.common.player.Player;
import com.darkxell.common.pokemon.LearnedMove;
import com.darkxell.common.pokemon.Pokemon;
import com.darkxell.common.pokemon.PokemonRegistry;
import com.darkxell.common.pokemon.PokemonSpecies;
import com.darkxell.common.registry.Registries;
import com.darkxell.common.zones.FriendArea;

public class Util {

	public static final int POST_WIGGLYTUFF_STORYPOS = 14;
	public static final int POST_GAME_STORYPOS = 100;

	public static Player createDefaultPlayer() {
		PokemonRegistry species = Registries.species();

		Player player = new Player("Offline debug account name", species.find(4).generate(new Random(), 10, 1));
		player.setStoryPosition(17);
		player.setMoneyInBag(6900);
		player.setMoneyInBank(456789);
		player.addAlly(species.find(258).generate(new Random(), 80));
		// player.addAlly(PokemonRegistry.find(255).generate(new Random(), 80));
		player.getTeamLeader().setItem(new ItemStack(208));
		player.allies.get(0).setItem(new ItemStack(1));
		player.getTeamLeader().setMove(0, new LearnedMove(352));
		player.getTeamLeader().setMove(1, new LearnedMove(1));
		player.getTeamLeader().setMove(2, new LearnedMove(404));
		player.getTeamLeader().setMove(3, new LearnedMove(806));
		// player.getTeamLeader().getData().abilityid = 42;

		for (int i = 1; i < 13; ++i)
			player.inventory().addItem(new ItemStack(i));
		player.inventory().addItem(new ItemStack(21, 6));
		player.inventory().addItem(new ItemStack(25, 6));
		player.inventory().addItem(new ItemStack(31, 3));
		player.inventory().addItem(new ItemStack(41));
		player.inventory().addItem(new ItemStack(42, 3));
		player.inventory().addItem(new ItemStack(81));
		player.inventory().addItem(new ItemStack(86));
		player.inventory().addItem(new ItemStack(90, 3));
		player.inventory().addItem(new ItemStack(102, 3));
		player.inventory().addItem(new ItemStack(125, 3));
		player.inventory().addItem(new ItemStack(133, 3));
		player.inventory().addItem(new ItemStack(152));
		player.inventory().addItem(new ItemStack(223));
		player.inventory().addItem(new ItemStack(387));

        player.friendAreas.add(FriendArea.BEAU_PLAINS);
        player.friendAreas.add(FriendArea.WILD_PLAINS);

		for (PokemonSpecies s : Registries.species().toList()) {
			if (s.getID() == 0)
				continue;

			Pokemon p = s.generate(new Random(), 1, .5);
			p.getData().id = s.getID();
			player.addPokemonInZone(p);
		}

		try {
			player.getData().missionsids.add(
					new Mission("E", 1, 2, 3, 200, 1, new MissionReward(55, new int[] { 1 }, new int[] { 1 }, 5, null),
							Mission.TYPE_RESCUEHIM).toString());
			player.getData().missionsids.add(new Mission("A", 12, 14, 15, 71, 2,
					new MissionReward(70, new int[] { 1 }, new int[] { 1 }, 5, null), Mission.TYPE_DEFEAT).toString());
			player.getData().missionsids.add(new Mission("A", 12, 15, 15, 71, 2,
					new MissionReward(70, new int[] { 1 }, new int[] { 1 }, 5, null), Mission.TYPE_DEFEAT).toString());
			player.getData().missionsids.add(new Mission("C", 12, 14, 15, 71, 2,
					new MissionReward(70, new int[] { 1 }, new int[] { 1 }, 5, null), Mission.TYPE_ESCORT).toString());
		} catch (InvalidParammetersException e) {
			e.printStackTrace();
		}

		return player;
	}

}
