package com.darkxell.client.state.menu.dungeon;

import java.awt.Rectangle;

import com.darkxell.client.launchable.Persistance;
import com.darkxell.client.renderers.TextRenderer;
import com.darkxell.client.state.ConfirmDialogState;
import com.darkxell.client.state.DialogState;
import com.darkxell.client.state.DialogState.DialogEndListener;
import com.darkxell.client.state.DialogState.DialogScreen;
import com.darkxell.client.state.dungeon.DungeonState;
import com.darkxell.client.state.mainstates.PrincipalMainState;
import com.darkxell.client.state.menu.components.MoveSelectionWindow;
import com.darkxell.common.event.move.MoveLearnedEvent;
import com.darkxell.common.move.Move;
import com.darkxell.common.pokemon.LearnedMove;
import com.darkxell.common.pokemon.Pokemon;
import com.darkxell.common.util.language.Message;

public class MoveLearnMenuState extends MovesMenuState implements DialogEndListener
{

	public final LearnedMove move;
	public final Pokemon pokemon;

	public MoveLearnMenuState(DungeonState parent, Pokemon pokemon, Move move)
	{
		super(parent, pokemon);
		this.pokemon = pokemon;
		this.move = new LearnedMove(move.id);
		this.canOrder = false;

		this.tabs.get(0).addOption(new MoveMenuOption(this.move, this.pokemon == Persistance.player.getTeamLeader()));
	}

	protected Message infoText()
	{
		return new Message("moves.info.learn");
	}

	@Override
	protected Rectangle mainWindowDimensions()
	{
		Rectangle r = super.mainWindowDimensions();
		return new Rectangle(r.x, r.y, r.width + MoveSelectionWindow.ppLength, r.height + (TextRenderer.height() + TextRenderer.lineSpacing()));
	}

	@Override
	public void onDialogEnd(DialogState dialog)
	{
		if (((ConfirmDialogState) dialog).hasConfirmed())
		{
			if (Persistance.stateManager instanceof PrincipalMainState) ((PrincipalMainState) Persistance.stateManager).setState(Persistance.dungeonState);
			if (this.optionIndex() < 4)
				Persistance.eventProcessor.processEvent(new MoveLearnedEvent(Persistance.floor, this.pokemon, this.move.move(), this.optionIndex()));
			else Persistance.eventProcessor.processPending();
		} else((PrincipalMainState) Persistance.stateManager).setState(this);
	}

	@Override
	protected void onExit()
	{}

	@Override
	protected void onOptionSelected(MenuOption option)
	{
		MoveMenuOption o = (MoveMenuOption) option;

		ConfirmDialogState s = new ConfirmDialogState(this.backgroundState, this, false, new DialogScreen(
				new Message("moves.forget").addReplacement("<pokemon>", this.pokemon.getNickname()).addReplacement("<move>", o.move.move().name())));
		((PrincipalMainState) Persistance.stateManager).setState(s);
	}

}