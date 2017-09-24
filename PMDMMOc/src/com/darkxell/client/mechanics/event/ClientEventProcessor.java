package com.darkxell.client.mechanics.event;

import java.util.ArrayList;
import java.util.Stack;

import com.darkxell.client.launchable.Launcher;
import com.darkxell.client.persistance.DungeonPersistance;
import com.darkxell.client.renderers.ItemRenderer;
import com.darkxell.client.state.DialogState;
import com.darkxell.client.state.DialogState.DialogEndListener;
import com.darkxell.client.state.FreezoneExploreState;
import com.darkxell.client.state.dungeon.AnimationState;
import com.darkxell.client.state.dungeon.NextFloorState;
import com.darkxell.client.state.dungeon.PokemonTravelState;
import com.darkxell.client.state.menu.dungeon.StairMenuState;
import com.darkxell.common.ai.AI;
import com.darkxell.common.dungeon.DungeonInstance;
import com.darkxell.common.event.DungeonEvent;
import com.darkxell.common.event.TurnSkippedEvent;
import com.darkxell.common.event.dungeon.DungeonExitEvent;
import com.darkxell.common.event.dungeon.NextFloorEvent;
import com.darkxell.common.event.item.ItemUseSelectionEvent;
import com.darkxell.common.event.move.MoveSelectionEvent;
import com.darkxell.common.event.move.MoveUseEvent;
import com.darkxell.common.event.pokemon.DamageDealtEvent;
import com.darkxell.common.event.pokemon.FaintedPokemonEvent;
import com.darkxell.common.event.pokemon.PokemonTravelEvent;
import com.darkxell.common.event.pokemon.PokemonTravelEvent.PokemonTravel;
import com.darkxell.common.event.stats.ExperienceGainedEvent;
import com.darkxell.common.event.stats.StatChangedEvent;
import com.darkxell.common.pokemon.DungeonPokemon;

/** Translates game logic events into displayable content to the client.<br />
 * Takes in Events to display messages, manage resources or change game states. */
public final class ClientEventProcessor
{
	/** Pending events to process. */
	private static final Stack<DungeonEvent> pending = new Stack<DungeonEvent>();
	public static final DialogEndListener processEventsOnDialogEnd = new DialogEndListener()
	{
		@Override
		public void onDialogEnd(DialogState dialog)
		{
			if (!hasPendingEvents()) Launcher.stateManager.setState(DungeonPersistance.dungeonState);
			else processPending();
		}
	};
	/** While processing an event, setting this to false will stop processing the pending events. */
	static boolean processPending = true;

	public static void actorTravels(short direction)
	{
		ArrayList<PokemonTravel> travellers = new ArrayList<PokemonTravel>();
		travellers.add(new PokemonTravel(DungeonPersistance.dungeon.getActor(), direction));
		boolean flag = true;
		DungeonEvent e = null;
		while (flag)
		{
			e = AI.makeAction(DungeonPersistance.floor, DungeonPersistance.dungeon.nextActor());

			if (e instanceof PokemonTravelEvent)
			{
				PokemonTravel event = ((PokemonTravelEvent) e).getTravel();
				travellers.add(event);
				// Simulating travel
				event.origin.removePokemon(event.pokemon);
				event.destination.setPokemon(event.pokemon);
			} else if (!(e instanceof TurnSkippedEvent)) flag = false;
		}

		// Resetting simulations
		for (PokemonTravel travel : travellers)
		{
			travel.destination.removePokemon(travel.pokemon);
			travel.origin.setPokemon(travel.pokemon);
		}

		if (e != null) addToPending(e);
		PokemonTravelEvent event = new PokemonTravelEvent(DungeonPersistance.floor, travellers.toArray(new PokemonTravel[travellers.size()]));
		processEvent(event);
	}

	/** Adds the input events to the pending stack, without processing them. */
	public static void addToPending(ArrayList<DungeonEvent> arrayList)
	{
		for (int i = arrayList.size() - 1; i >= 0; --i)
			addToPending(arrayList.get(i));
	}

	/** Adds the input event to the pending stack, without processing it. */
	public static void addToPending(DungeonEvent event)
	{
		if (event != null) pending.add(event);
	}

	public static boolean hasPendingEvents()
	{
		return pending.size() != 0;
	}

	private static void processDungeonExitEvent(DungeonExitEvent event)
	{
		if (event.pokemon == DungeonPersistance.player.getDungeonPokemon()) Launcher.stateManager.setState(new FreezoneExploreState());
	}

	/** Processes the input event and adds the resulting events to the pending stack. */
	public static void processEvent(DungeonEvent event)
	{
		processPending = true;
		DungeonPersistance.dungeon.eventOccured(event);

		if (event.isValid())
		{
			addToPending(event.processServer());
			DungeonPersistance.dungeonState.logger.showMessages(event.getMessages());

			if (event instanceof MoveSelectionEvent) MoveEventProcessor.processMoveEvent((MoveSelectionEvent) event);
			if (event instanceof MoveUseEvent) MoveEventProcessor.processMoveUseEvent((MoveUseEvent) event);
			if (event instanceof DamageDealtEvent) MoveEventProcessor.processDamageEvent((DamageDealtEvent) event);

			if (event instanceof PokemonTravelEvent) processTravelEvent((PokemonTravelEvent) event);
			if (event instanceof FaintedPokemonEvent) MoveEventProcessor.processFaintedEvent((FaintedPokemonEvent) event);

			if (event instanceof StatChangedEvent) MoveEventProcessor.processStatEvent((StatChangedEvent) event);
			if (event instanceof ExperienceGainedEvent) MoveEventProcessor.processExperienceEvent((ExperienceGainedEvent) event);

			if (event instanceof ItemUseSelectionEvent) processItemEvent((ItemUseSelectionEvent) event);

			if (event instanceof StairLandingEvent) processStairEvent((StairLandingEvent) event);
			if (event instanceof NextFloorEvent) processFloorEvent((NextFloorEvent) event);
			if (event instanceof DungeonExitEvent) processDungeonExitEvent((DungeonExitEvent) event);
		}

		if (processPending) processPending();
	}

	private static void processFloorEvent(NextFloorEvent event)
	{
		Launcher.stateManager.setState(new NextFloorState(event.floor.id + 1));
	}

	private static void processItemEvent(ItemUseSelectionEvent event)
	{
		AnimationState a = new AnimationState(DungeonPersistance.dungeonState);
		a.animation = ItemRenderer.createItemAnimation(event, a);
		DungeonPersistance.dungeonState.setSubstate(a);
		processPending = false;
	}

	/** Processes the next pending event. */
	public static void processPending()
	{
		if (!pending.empty()) processEvent(pending.pop());
		else
		{
			DungeonInstance dungeon = DungeonPersistance.dungeon;
			DungeonPokemon actor = dungeon.nextActor();
			if (actor == null)
			{
				addToPending(dungeon.endTurn());
				processPending();
				return;
			} else if (actor.pokemon.player != null && actor.pokemon.player.getDungeonPokemon() == actor) return;
			else
			{
				addToPending(AI.makeAction(DungeonPersistance.floor, actor));
				processPending();
			}
		}
	}

	private static void processStairEvent(StairLandingEvent event)
	{
		processPending = false;
		Launcher.stateManager.setState(new StairMenuState());
	}

	private static void processTravelEvent(PokemonTravelEvent event)
	{
		processPending = false;
		DungeonPersistance.dungeonState.setSubstate(new PokemonTravelState(DungeonPersistance.dungeonState, event.travels()));
	}

	private ClientEventProcessor()
	{}

}
