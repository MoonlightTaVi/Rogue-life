package io.github.rogue_life;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Math.abs;
import static java.lang.Math.round;

public class WorldScreen implements Screen {
    final RogueLife game;
    OrthographicCamera camera;
    ShapeDrawer drawer;
    Character player;
    //Character tree;
    List<Character> objects = new ArrayList<>();

    public WorldScreen(RogueLife game) {
        this.game = game;

        game.loadAnimation("male_dress0", "textures/characters/male_dress0.png", 4, 1);
        game.loadTexture("tree0", "textures/trees/tree0_1.png");

        drawer = new ShapeDrawer(game.polyBatch, game.whitePixel);

        player = new Character("Player", "male_dress0", 2);
        player.x = 0;
        player.y = 0;
        objects.add(player);
        //tree = new Character("Tree", "tree0");
        //tree.x = 100;
        //tree.y = 0;
        //tree.setPlayer(player);
        for (int i = 0; i < 100; i++) {
            Character tree = new Character("Tree", "tree0");
            tree.x = MathUtils.random(-3000,3000);
            tree.y = MathUtils.random(-3000,3000);
            tree.setPlayer(player);
            objects.add(tree);
        }

        camera = new OrthographicCamera();
        camera.setToOrtho(false, RogueLife.SCREEN_WIDTH, RogueLife.SCREEN_HEIGHT);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float v) {
        ScreenUtils.clear(0.0f, 0.0f, 0.2f, 1.0f);
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.polyBatch.begin();
        drawer.filledRectangle(0, 0, RogueLife.SCREEN_WIDTH, RogueLife.horizonY, Color.FOREST);
        game.polyBatch.end();

        game.batch.begin();
        player.input();
        //game.batch.draw(player.getKey(), RogueLife.playerX - 24, RogueLife.playerY);
        //if (tree.getSizeModifier() > 0.2f) {
            //game.batch.draw(tree.getKey(), tree.getOnScreenX(), tree.getOnScreenY(), tree.getWidth(), tree.getHeight());
        //}
        List<Character> objectsToDraw = objects.stream()
            .filter(o -> o.getSizeModifier() > 0.2f)
            .filter(o -> o.getSizeModifier() < 2.0f)
            .filter(o -> o.getOnScreenX() > -o.getWidth())
            .filter(o -> o.getOnScreenX() < RogueLife.SCREEN_WIDTH)
            .filter(o -> o.getOnScreenY() > -o.getHeight())
            .filter(o -> o.getOnScreenY() < RogueLife.SCREEN_HEIGHT)
            .sorted((a, b) -> -round((a.getOnScreenY() - b.getOnScreenY()) / abs(a.getOnScreenY() - b.getOnScreenY())))
            .collect(Collectors.toList());
        for(Character object : objectsToDraw) {
            game.batch.draw(object.getKey(), object.getOnScreenX(), object.getOnScreenY(),
                object.getWidth(), object.getHeight());
        }
        game.batch.end();
    }

    @Override
    public void resize(int i, int i1) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        player.dispose();
    }
}
