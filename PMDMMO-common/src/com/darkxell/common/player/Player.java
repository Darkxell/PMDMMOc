package com.darkxell.common.player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import com.darkxell.common.dbobject.DBPlayer;
import com.darkxell.common.dbobject.DatabaseIdentifier;
import com.darkxell.common.dungeon.DungeonAccessibility;
import com.darkxell.common.dungeon.data.Dungeon;
import com.darkxell.common.pokemon.DungeonPokemon;
import com.darkxell.common.pokemon.Pokemon;
import com.darkxell.common.pokemon.PokemonSpecies;
import com.darkxell.common.registry.Registries;
import com.darkxell.common.zones.FriendArea;

public class Player {
    public ArrayList<Pokemon> allies;
    private DBPlayer data;

    /** Lists IDs of unlocked Friend Areas. */
    public ArrayList<FriendArea> friendAreas = new ArrayList<>();

    /** This Player's Inventory. */
    private Inventory inventory;

    public Pokemon leaderPokemon;

    public HashMap<Long, Pokemon> pokemonInZones;

    /** This Player's storage. */
    private Inventory storage;

    public Player(DBPlayer data) {
        this.setData(data);
    }

    public Player(String name, Pokemon pokemon) {
        this(new DBPlayer(0, name, null, 0, 0, 0, new ArrayList<>(), new ArrayList<>(),
                pokemon == null ? null : new DatabaseIdentifier(pokemon.id()), null, null, null, 0, false, false));
        this.setLeaderPokemon(pokemon);
    }

    public void addAlly(Pokemon pokemon) {
        this.data.pokemonsinparty.add(new DatabaseIdentifier(pokemon.id()));
        this.allies.add(pokemon);
        pokemon.setPlayer(this);
    }

    public void addPokemonInZone(Pokemon pokemon) {
        this.pokemonInZones.put(pokemon.id(), pokemon);
        boolean already = false;
        for (DatabaseIdentifier id : this.data.pokemonsinzones)
            if (id.id == pokemon.id()) {
                already = true;
                break;
            }
        if (!already)
            this.data.pokemonsinzones.add(new DatabaseIdentifier(pokemon.id()));
        pokemon.setPlayer(this);
    }

    public void clearAllies() {
        for (Pokemon pokemon : this.allies)
            pokemon.setPlayer(this);
        this.data.pokemonsinparty.clear();
        this.allies.clear();
        this.resetDungeonTeam();
    }

    public ArrayList<Dungeon> getAccessibleDungeons() {
        ArrayList<Dungeon> dungeons = new ArrayList<>(Registries.dungeons().toList());
        dungeons.sort(Comparator.naturalOrder());
        dungeons.removeIf(
                d -> DungeonAccessibility.isAvailable(this.getData(), d.getID()) != DungeonAccessibility.ACCESSIBLE);
        return dungeons;
    }

    public DBPlayer getData() {
        return this.data;
    }

    public DungeonPokemon getDungeonLeader() {
        return this.leaderPokemon.getDungeonPokemon();
    }

    public DungeonPokemon getDungeonMember(int index) {
        Pokemon p = this.getMember(index);
        if (p == null)
            return null;
        return p.getDungeonPokemon();
    }

    public DungeonPokemon[] getDungeonTeam() {
        DungeonPokemon[] team = new DungeonPokemon[this.allies.size() + 1];
        for (int i = 0; i < team.length; ++i)
            team[i] = this.getDungeonMember(i);
        return team;
    }

    public ArrayList<PokemonSpecies> getKnownPokemon() {
        ArrayList<PokemonSpecies> known = new ArrayList<>();
        ArrayList<Dungeon> dungeons = this.getAccessibleDungeons();
        for (Dungeon dungeon : dungeons) {
            HashSet<PokemonSpecies> recruitable = dungeon.getRecruitablePokemon();
            for (PokemonSpecies pokemon : recruitable)
                if (!known.contains(pokemon))
                    known.add(pokemon);
        }
        
        known.sort(Comparator.naturalOrder());

        return known;
    }

    public Pokemon getMember(int index) {
        if (index == 0)
            return this.getTeamLeader();
        else if (index < this.allies.size() + 1)
            return this.allies.get(index - 1);
        return null;
    }

    public ArrayList<String> getMissions() {
        return this.data.missionsids;
    }

    /** Returns the full player's team. The team leader is at position 0 in the array. */
    public Pokemon[] getTeam() {
        Pokemon[] team = new Pokemon[this.allies.size() + 1];
        team[0] = this.getTeamLeader();
        for (int i = 1; i < team.length; ++i)
            team[i] = this.allies.get(i - 1);
        return team;
    }

    public Pokemon getTeamLeader() {
        return this.leaderPokemon;
    }

    public Inventory inventory() {
        return this.inventory;
    }

    public boolean isAlly(DungeonPokemon pokemon) {
        return pokemon != null && pokemon.player() == this;
    }

    public boolean isAlly(Pokemon pokemon) {
        return pokemon != null && pokemon.player() == this;
    }

    public long moneyInBag() {
        return this.data.moneyinbag;
    }

    public long moneyInBank() {
        return this.getData().moneyinbank;
    }

    public String name() {
        return this.data.name;
    }

    public int points() {
        return this.data.points;
    }

    public int positionInTeam(Pokemon pokemon) {
        if (pokemon == this.getTeamLeader())
            return 0;
        for (int i = 0; i < this.allies.size(); ++i)
            if (this.allies.get(i) == pokemon)
                return i + 1;
        return -1;
    }

    public void removeAlly(Pokemon pokemon) {
        if (!this.allies.contains(pokemon))
            return;
        this.data.pokemonsinparty.remove(this.allies.indexOf(pokemon));
        this.allies.remove(pokemon);
        pokemon.setPlayer(null);
    }

    public void removePokemonInZone(Pokemon pokemon) {
        this.pokemonInZones.remove(pokemon.id());
        this.data.pokemonsinzones.removeIf(id -> id.id == pokemon.id());
        pokemon.setPlayer(null);
    }

    public void resetDungeonTeam() {
        if (this.leaderPokemon != null && this.leaderPokemon.getDungeonPokemon() != null)
            this.leaderPokemon.getDungeonPokemon().dispose();
        for (Pokemon ally : this.allies)
            if (ally.getDungeonPokemon() != null)
                ally.getDungeonPokemon().dispose();
    }

    public void setData(DBPlayer data) {
        this.data = data;
        this.allies = new ArrayList<>();
        this.inventory = new Inventory(this);
        this.pokemonInZones = new HashMap<>();
        for (DatabaseIdentifier zone : this.data.pokemonsinzones)
            this.pokemonInZones.put(zone.id, null);
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public void setLeaderPokemon(Pokemon pokemon) {
        if (this.leaderPokemon != null)
            this.leaderPokemon.setPlayer(null);
        this.leaderPokemon = pokemon;
        if (this.leaderPokemon != null) {
            this.leaderPokemon.createDungeonPokemon();
            this.leaderPokemon.setPlayer(this);
            this.data.mainpokemon = new DatabaseIdentifier(this.leaderPokemon.id());
        } else
            this.data.mainpokemon = null;
    }

    public void setMoneyInBag(long moneyInBag) {
        this.data.moneyinbag = moneyInBag;
    }

    public void setMoneyInBank(long moneyInBank) {
        this.data.moneyinbank = moneyInBank;
    }

    public void setPoints(int points) {
        this.data.points = points;
    }

    public void setStorage(Inventory storage) {
        this.storage = storage;
    }

    public void setStoryPosition(int storyPosition) {
        this.data.storyposition = storyPosition;
    }

    public Inventory storage() {
        return this.storage;
    }

    public int storyPosition() {
        return this.data.storyposition;
    }

    @Override
    public String toString() {
        return this.name();
    }
}
