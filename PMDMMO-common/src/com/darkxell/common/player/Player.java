package com.darkxell.common.player;

import java.util.ArrayList;

import com.darkxell.common.pokemon.DungeonPokemon;
import com.darkxell.common.pokemon.Pokemon;

public class Player
{

	/** The Pok�mon in the rescue team. */
	private ArrayList<Pokemon> allies;
	private ArrayList<DungeonPokemon> dungeonAllies;
	/** If in a Dungeon, reference to the Dungeon Pok�mon. null else. */
	private DungeonPokemon dungeonPokemon;
	/** This Player's ID. */
	public final int id;
	/** This Player's Inventory. */
	public final Inventory inventory;
	/** The Pok�mon this Player embodies. */
	private Pokemon mainPokemon;
	/** The current amount of Money of this Player. */
	public int money;
	public String name;

	public Player(int id, String name, Pokemon pokemon)
	{
		this.id = id;
		this.name = name;
		this.setMainPokemon(pokemon);
		this.inventory = new Inventory(Inventory.MAX_SIZE);
		this.money = 0;
		this.allies = new ArrayList<Pokemon>();
		this.dungeonAllies = new ArrayList<DungeonPokemon>();
	}

	public void addAlly(Pokemon pokemon)
	{
		this.allies.add(pokemon);
		this.dungeonAllies.add(new DungeonPokemon(pokemon));
		pokemon.setPlayer(this);
	}

	public void clearAllies()
	{
		for (Pokemon pokemon : this.allies)
			pokemon.setPlayer(this);
		this.allies.clear();
		this.resetDungeonTeam();
	}

	public DungeonPokemon getDungeonLeader()
	{
		if (this.dungeonPokemon == null) this.dungeonPokemon = new DungeonPokemon(this.mainPokemon);
		return this.dungeonPokemon;
	}

	public DungeonPokemon[] getDungeonTeam()
	{
		DungeonPokemon[] team = new DungeonPokemon[this.allies.size() + 1];
		for (int i = 0; i < team.length; ++i)
			team[i] = this.getMember(i);
		return team;
	}

	public DungeonPokemon getMember(int index)
	{
		if (index == 0) return this.getDungeonLeader();
		else if (index < this.dungeonAllies.size() + 1) return this.dungeonAllies.get(index - 1);
		return null;
	}

	public Pokemon[] getTeam()
	{
		Pokemon[] team = new Pokemon[this.allies.size() + 1];
		team[0] = this.getTeamLeader();
		for (int i = 1; i < team.length; ++i)
			team[i] = this.allies.get(i - 1);
		return team;
	}

	public Pokemon getTeamLeader()
	{
		return this.mainPokemon;
	}

	public boolean isAlly(DungeonPokemon pokemon)
	{
		return pokemon.player() == this;
	}

	public boolean isAlly(Pokemon pokemon)
	{
		return pokemon != null && pokemon.player() == this;
	}

	public void removeAlly(Pokemon pokemon)
	{
		this.dungeonAllies.remove(this.allies.indexOf(pokemon));
		this.allies.remove(pokemon);
		pokemon.setPlayer(null);
	}

	public void resetDungeonTeam()
	{
		for (int i = 0; i < this.dungeonAllies.size(); ++i)
			this.dungeonAllies.set(i, new DungeonPokemon(this.allies.get(i)));
	}

	public void setDungeonPokemon(DungeonPokemon dungeonPokemon)
	{
		this.dungeonPokemon = dungeonPokemon;
	}

	public void setDungeonPokemon(Pokemon pokemon, DungeonPokemon dungeonPokemon)
	{
		if (pokemon == this.getTeamLeader()) this.setDungeonPokemon(dungeonPokemon);
		else if (this.allies.contains(pokemon)) this.dungeonAllies.set(this.allies.indexOf(pokemon), dungeonPokemon);
	}

	public void setMainPokemon(Pokemon pokemon)
	{
		if (this.mainPokemon != null) this.mainPokemon.setPlayer(null);
		this.mainPokemon = pokemon;
		this.mainPokemon.setPlayer(this);
	}

}
