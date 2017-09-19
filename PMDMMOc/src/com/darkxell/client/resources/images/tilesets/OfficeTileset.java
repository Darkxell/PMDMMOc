package com.darkxell.client.resources.images.tilesets;

import com.darkxell.client.resources.images.AbstractTileset;

public class OfficeTileset extends AbstractTileset {

	public static OfficeTileset instance = new OfficeTileset();

	public OfficeTileset() {
		super("/tilesets/office.png", 8, 8);
	}

}
