package com.darkxell.client.mechanics.cutscene.end;

import java.util.Random;

import org.jdom2.Element;

import com.darkxell.client.launchable.GameSocketEndpoint;
import com.darkxell.client.launchable.Persistance;
import com.darkxell.client.mechanics.cutscene.Cutscene;
import com.darkxell.client.mechanics.cutscene.Cutscene.CutsceneEnd;
import com.darkxell.client.state.StateManager;
import com.darkxell.common.util.XMLUtils;

public class EnterDungeonCutsceneEnd extends CutsceneEnd
{

	public final int dungeonID;

	public EnterDungeonCutsceneEnd(Cutscene cutscene, Element xml)
	{
		super(cutscene, xml);
		this.dungeonID = XMLUtils.getAttribute(xml, "id", 1);
	}

	public EnterDungeonCutsceneEnd(int dungeonID, String function)
	{
		super(null, function);
		this.dungeonID = dungeonID;
	}

	@Override
	public void onCutsceneEnd()
	{
		super.onCutsceneEnd();
		if (Persistance.socketendpoint.connectionStatus() == GameSocketEndpoint.CONNECTED)
			StateManager.setDungeonState(Persistance.cutsceneState, this.dungeonID);
		else StateManager.setDungeonState(Persistance.cutsceneState, this.dungeonID, new Random().nextLong());
	}

	@Override
	public Element toXML()
	{
		return super.toXML().setAttribute("id", String.valueOf(this.dungeonID));
	}

	@Override
	protected String xmlName()
	{
		return "enterdungeon";
	}

}
