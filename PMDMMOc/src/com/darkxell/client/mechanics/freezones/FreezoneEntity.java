package com.darkxell.client.mechanics.freezones;

import java.awt.Graphics2D;

import com.darkxell.client.mechanics.freezones.entities.renderers.DefaultFreezoneEntityRenderer;
import com.darkxell.client.renderers.AbstractRenderer;
import com.darkxell.common.util.DoubleRectangle;

/** Describes an entity in a freezone. Note that the player does not extends this class. */
public abstract class FreezoneEntity
{

	/** Is true if this entity is solid, meaning you can't go through it. */
	public boolean isSolid;
	/** Is true if you can interact with this entity */
	public boolean canInteract;
	/** The X position of the entity. */
	public double posX;
	/** the Y position of the entity. */
	public double posY;

	public FreezoneEntity(boolean isSolid, boolean canInteract, double x, double y)
	{
		this.isSolid = isSolid;
		this.canInteract = canInteract;
		this.posX = x;
		this.posY = y;
	}

	/** Called when the pplayers interact with this entity. Note that this might be called even if this entity has the <code>canInteract</code> tag set to false. Note that this method will be called from the KeyEvent thread. */
	public abstract void onInteract();

	/** Prints this entity on the drawable graphics provided. graphics transition should have already been applied before calling this method. This will use a 8px/unit position ratio. */
	public abstract void print(Graphics2D g);

	/** Updates this entity */
	public abstract void update();

	/** Returns the hitbox of this entity at the wanted position. This should be overrided in a lot of cases, as it will return the default sized hitbox. */
	public DoubleRectangle getHitbox(double x, double y)
	{
		return new DoubleRectangle(x - 0.9, y - 0.9, 1.8, 1.7);
	}

	public AbstractRenderer createRenderer()
	{
		return new DefaultFreezoneEntityRenderer(this);
	}

}
