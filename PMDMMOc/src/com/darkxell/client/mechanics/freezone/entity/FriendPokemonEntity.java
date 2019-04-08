package com.darkxell.client.mechanics.freezone.entity;

import java.util.Random;

import com.darkxell.client.graphics.AbstractRenderer;
import com.darkxell.client.graphics.renderer.AbstractPokemonRenderer;
import com.darkxell.client.resources.images.pokemon.PokemonSprite;
import com.darkxell.client.resources.images.pokemon.PokemonSprite.PokemonSpriteState;
import com.darkxell.client.resources.images.pokemon.PokemonSpritesets;
import com.darkxell.common.ai.AIUtils;
import com.darkxell.common.pokemon.Pokemon;
import com.darkxell.common.util.Direction;
import com.darkxell.common.util.MathUtil;

public class FriendPokemonEntity extends PokemonFreezoneEntity {

    public static final int IDLE_TIME_MIN = 50, IDLE_TIME_MAX = 100;
    public static final double MAX_DISTANCE = 3;
    public static final double SPEED = .2;

    /** AI variables. */
    private double destinationX = -1, destinationY = -1;
    private int idleTime = IDLE_TIME_MAX;
    public final Pokemon pokemon;

    public FriendPokemonEntity(Pokemon pokemon) {
        this.pokemon = pokemon;
        this.sprite = new PokemonSprite(PokemonSpritesets.getSpriteset(this.pokemon));
        this.solid = false;
    }

    @Override
    public AbstractRenderer createRenderer() {
        AbstractPokemonRenderer r = (AbstractPokemonRenderer) super.createRenderer();
        r.sprite().setFacingDirection(Direction.randomDirection(new Random()));
        return r;
    }

    public void spawnAt(double x, double y) {
        this.startingX = x;
        this.startingY = y;

        this.posX = this.startingX + Math.random() * (MAX_DISTANCE * 2) - MAX_DISTANCE;
        this.posY = this.startingY + Math.random() * (MAX_DISTANCE * 2) - MAX_DISTANCE;
    }

    @Override
    public void update() {
        super.update();

        if (this.idleTime > 0) --this.idleTime; // Pause
        else if (this.destinationX == -1 || this.destinationY == -1) { // Find new destination
            this.destinationX = Math.random() * MAX_DISTANCE * 2 - MAX_DISTANCE + this.startingX;
            this.destinationY = Math.random() * MAX_DISTANCE * 2 - MAX_DISTANCE + this.startingY;
            this.sprite.setFacingDirection(AIUtils.closestDirection(MathUtil.angle(this.posX, this.posY, this.destinationX, this.destinationY)));
            this.sprite.setState(PokemonSpriteState.MOVE);
        } else { // Move
            this.posX += Math.signum(this.destinationX - this.posX) * Math.min(SPEED, Math.abs(this.destinationX - this.posX));
            this.posY += Math.signum(this.destinationY - this.posY) * Math.min(SPEED, Math.abs(this.destinationY - this.posY));

            if (this.destinationX == this.posX && this.destinationY == this.posY) { // Destination reached
                this.idleTime = (int) (IDLE_TIME_MIN + Math.random() * (IDLE_TIME_MAX - IDLE_TIME_MIN));
                this.destinationX = this.destinationY = -1;
                this.sprite.setState(PokemonSpriteState.IDLE);
            }
        }
    }

}