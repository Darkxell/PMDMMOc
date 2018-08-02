package com.darkxell.common.mission;

/** Structure that contains all the informations for a mission. */
public class Mission {

	public static final int TYPE_RESCUEME = 1;
	public static final int TYPE_RESCUEHIM = 2;
	public static final int TYPE_ESCORT = 3;
	public static final int TYPE_BRINGITEM = 4;
	public static final int TYPE_DEFEAT = 5;
	public static final int TYPE_FINDITEM = 6;

	/**
	 * The difficulty of the mission. Note that the values of this string are
	 * not restricted, so this should not be used to determine other
	 * informations about a mission. Such info should be attributes of this
	 * object.<br/>
	 * TL:DR : for display purposes mainly.
	 */
	private String difficulty = "";
	/** The id of the dungeon where this mission takes place. */
	private int dungeonid = 1;
	/** The floor at wich you cazn complete this mission. */
	private int floor = 1;
	/**
	 * The id of the first pokemon. Usually, this is the one asking for rescue,
	 * to escort...
	 */
	private int pokemonid1 = 1;
	/** The id of the secondary pokemon for the missions that need one. */
	private int pokemonid2 = 4;
	/**
	 * An itemID used for the missions that resolve about finding or bringing an
	 * item.
	 */
	private int itemid = 2;
	/** The list of rewards for this mission. This is it's own structure. */
	private MissionReward rewards = new MissionReward();
	/** The type of the mission. This defines what you have to do. */
	private int missiontype = TYPE_RESCUEME;

	/** Creates a new Mission Object with the wanted attributes. */
	public Mission(String difficulty, int dungeonid, int floor, int pokemonid1, int pokemonid2, int itemid,
			MissionReward rewards, int missiontype) {
		if (difficulty != null)
			this.difficulty = difficulty;
		this.dungeonid = dungeonid;
		this.floor = floor;
		this.pokemonid1 = pokemonid1;
		this.pokemonid2 = pokemonid2;
		this.itemid = itemid;
		if (rewards != null)
			this.rewards = rewards;
		this.missiontype = missiontype;
	}

	/** Creates a new Mission Object with the wanted attributes. */
	public Mission(String code) throws InvalidParammetersException {
		try {
			String[] splitted = code.split(":");
			this.difficulty = splitted[0];
			this.dungeonid = Integer.parseInt(splitted[1]);
			this.floor = Integer.parseInt(splitted[2]);
			this.pokemonid1 = Integer.parseInt(splitted[3]);
			this.pokemonid2 = Integer.parseInt(splitted[4]);
			this.itemid = Integer.parseInt(splitted[5]);
			this.rewards = new MissionReward(splitted[6]);
			this.missiontype = Integer.parseInt(splitted[7]);
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidParammetersException();
		}
	}

	@Override
	public String toString() {
		// diff::did:floor:pkmn1:pkmn2:itemid:rewards:type
		return difficulty + ":" + dungeonid + ":" + floor + ":" + pokemonid1 + ":" + pokemonid2 + ":" + itemid + ":"
				+ rewards + ":" + missiontype;
	}

	public String getDifficulty() {
		return difficulty;
	}

	public int getDungeonid() {
		return dungeonid;
	}

	public int getFloor() {
		return floor;
	}

	public int getPokemonid1() {
		return pokemonid1;
	}

	public int getPokemonid2() {
		return pokemonid2;
	}

	public int getItemid() {
		return itemid;
	}

	public MissionReward getRewards() {
		return rewards;
	}

	public int getMissiontype() {
		return missiontype;
	}

	@Override
	public boolean equals(Object obj) {
		Mission x;
		if (obj instanceof Mission)
			x = (Mission) obj;
		else
			return false;
		return x.difficulty.equals(difficulty) && dungeonid == x.dungeonid && floor == x.floor
				&& pokemonid1 == x.pokemonid1 && pokemonid2 == x.pokemonid2 && itemid == x.itemid
				&& rewards.equals(x.rewards) && missiontype == x.missiontype;
	}

}