package com.darkxell.client.launchable;

import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;

import com.darkxell.client.renderers.TextRenderer;
import com.darkxell.client.resources.images.SpriteFactory;
import com.darkxell.common.util.Util;

/**
 * (EXPERIMENTAL) A combined updater and renderer to avoid synchronization issues between game ticks and render events.
 *
 * @see com.darkxell.client.launchable.Updater
 * @see com.darkxell.client.launchable.Renderer
 */
public class UpdaterAndRenderer implements Runnable {
    public static final int targetUPS = 60;

    private long startTime, currentTime, timer;
    private int updatesCurrentSecond;
    private double updateTime, timePerUpdate;
    private int ups = 0;

    public UpdaterAndRenderer() {
    }

    public int currentUPS() {
        return this.ups;
    }

    protected boolean keepRunning() {
        return Launcher.isRunning && Launcher.getProcessingProfile() == Launcher.PROFILE_SYNCHRONIZED;
    }

    @Override
    public void run() {
        // Preparing FPS handling
        this.startTime = System.nanoTime();
        this.currentTime = this.startTime;
        this.updateTime = 0;
        this.timer = 0;
        this.timePerUpdate = 1000000000 / targetUPS;
        this.updatesCurrentSecond = 0;
        this.ups = 0;

        try {
            while (SpriteFactory.instance().hasLoadingSprites()) {
                Thread.sleep(5);
            }
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        while (this.keepRunning()) {
            this.update();

            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    protected void tickUpdate() {
        Persistance.stateManager.update();

        if (Persistance.frame == null || !Persistance.frame.isVisible()) {
            return;
        }

        BufferStrategy bf = Persistance.frame.canvas.getBufferStrategy();
        Graphics2D g = (Graphics2D) bf.getDrawGraphics();
        int width = Persistance.frame.canvas.getWidth(), height = Persistance.frame.canvas.getHeight();
        g.clearRect(0, 0, width, height);

        Persistance.stateManager.render(g, width, height);
        TextRenderer.render(g, "V " + Util.GAME_VERSION, 5, 5);

        g.dispose();
        bf.show();
    }

    private void update() {
        // Calculate elapsed time
        long elapsedTime = System.nanoTime() - this.currentTime;
        this.timer += elapsedTime;
        this.currentTime += elapsedTime;
        this.updateTime += elapsedTime / this.timePerUpdate;

        // If a tick has passed, update until there is no delayed update
        while (this.updateTime >= 1) {
            this.tickUpdate();

            ++this.updatesCurrentSecond;
            --this.updateTime;
        }

        if (this.timer >= 1000000000) {
            this.ups = this.updatesCurrentSecond;
            this.timer = 0;
            this.updatesCurrentSecond = 0;
        }
    }
}
