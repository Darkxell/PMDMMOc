package com.darkxell.common.pokemon;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;

import org.jdom2.Element;

import com.darkxell.common.util.Logger;
import com.darkxell.common.util.XMLUtils;

/** Holds all Pok�mon species. */
public final class PokemonRegistry
{

	private static HashMap<Integer, PokemonSpecies> pokemon = new HashMap<Integer, PokemonSpecies>();

	/** @return The Pok�mon species with the input ID. */
	public static PokemonSpecies find(int id)
	{
		if (!pokemon.containsKey(id))
		{
			Logger.e("There is no Pok�mon with ID " + id + ".");
			return null;
		} else if (id == 0) Logger.w("Using default Pok�mon!");
		return pokemon.get(id);
	}

	/** @return All Pok�mon species. */
	public static Collection<PokemonSpecies> list()
	{
		return pokemon.values();
	}

	/** Loads this Registry for the Client. */
	public static void load()
	{
		Logger.instance().debug("Loading Pok�mon...");

		Element root = XMLUtils.readFile(new File(PokemonRegistry.class.getResource("/data/pokemon.xml").getFile()));
		for (Element e : root.getChildren("pokemon", root.getNamespace()))
			try
			{
				PokemonSpecies species = new PokemonSpecies(e);
				pokemon.put(species.compoundID(), species);
				for (PokemonSpecies form : species.forms())
					pokemon.put(form.compoundID(), form);
			} catch (Exception e1)
			{
				e1.printStackTrace();
			}
	}

	/** Saves this Registry for the Client. */
	public static void saveClient()
	{
		Element species = new Element("species");
		for (PokemonSpecies pk : pokemon.values())
			if (pk.formID == 0) species.addContent(pk.toXML());
		XMLUtils.saveFile(new File("resources/data/pokemon.xml"), species);
	}

}
