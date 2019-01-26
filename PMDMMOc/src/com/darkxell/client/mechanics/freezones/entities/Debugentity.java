package com.darkxell.client.mechanics.freezones.entities;

import com.darkxell.client.mechanics.cutscene.CutsceneManager;
import com.darkxell.client.mechanics.freezones.FreezoneEntity;
import com.darkxell.common.util.Logger;

import java.awt.*;

public class Debugentity extends FreezoneEntity {
    public Debugentity(double x, double y) {
        super();
        this.canInteract = true;
    }

    @Override
    public void onInteract() {
        Logger.d("Triggered debug entity script.");
        // Do your debug shit here. Please remember to not commit any changes.
        // TRIGGERED politeness police!!! --Cubi
        CutsceneManager.playCutscene("skarmory/team", true);
    }

    @Override
    public void print(Graphics2D g) {
        g.setColor(Color.BLUE);
        g.fillRect((int) (super.posX * 8) - 7, (int) (super.posY * 8) - 7, 15, 15);
        g.setColor(Color.RED);
        g.drawString("D", (int) (super.posX * 8) - 6, (int) (super.posY * 8) + 5);
    }
}
