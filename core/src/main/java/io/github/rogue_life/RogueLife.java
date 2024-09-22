package io.github.rogue_life;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;

import static java.lang.Math.*;


public class RogueLife extends Game {
    public static ObjectMap<String, Texture> animationSheets = new ObjectMap<>();
    public static ObjectMap<String, Texture> textures = new ObjectMap<>();
    public static ObjectMap<String, Animation<TextureRegion>> animations = new ObjectMap<>();
    public static final float animationSpeed = 0.2f;
    public static float stateTime = 0.0f;

    public static final float SCREEN_WIDTH = 800.0f;
    public static final float SCREEN_HEIGHT = 500.0f;

    public static final float playerX = 400.0f;
    public static final float playerY = 150.0f;
    public static final float horizonY = 400.0f;
    public static final float viewDistance = 800.0f;
    public static final double FOV = 120 + 35; // It's not actually a vertical FOV, as the camera is a bit lowered in X-axis
                                            // (If we set the FOV to 120, which is usual for most of the games, we'll get such  a view
                                            //  as if camera wasn't rotated - the objects won't go up on the screen on increasing distance)
    public static float cameraRotation = 0;

    public SpriteBatch batch;
    public PolygonSpriteBatch polyBatch;
    public Texture whitePixelTexture;
    public TextureRegion whitePixel;
    public BitmapFont font;
    public void create() {
        batch = new SpriteBatch();
        polyBatch = new PolygonSpriteBatch();
        whitePixelTexture = new Texture(Gdx.files.internal("textures/white_pixel.png"));
        whitePixel = new TextureRegion(whitePixelTexture);
        font = new BitmapFont();
        this.screen = new WorldScreen(this);
    }
    public void render() {
        super.render();
        stateTime += Gdx.graphics.getDeltaTime();
        stateTime %= 1000.0f;
    }
    public void dispose() {
        batch.dispose();
        font.dispose();
        for (ObjectMap.Entry<String, Texture> entry : animationSheets) {
            entry.value.dispose();
        }
        polyBatch.dispose();
        whitePixelTexture.dispose();
        this.screen.dispose();
    }

    public void loadTexture(String name, String path) {
        Texture texture = new Texture(Gdx.files.internal(path));
        textures.put(name, texture);
    }

    public void loadAnimation(String name, String path, int columns, int rows) {
        Texture animationTexture = new Texture(Gdx.files.internal(path));
        animationSheets.put(name, animationTexture);
        TextureRegion[][] tmp = TextureRegion.split(animationTexture, animationTexture.getWidth() / columns,
            animationTexture.getHeight() / rows);
        TextureRegion[] frames = new TextureRegion[columns * rows];
        int index = 0;
        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                frames[index++] = tmp[j][i];
            }
        }
        animations.put(name, new Animation<>(RogueLife.animationSpeed, frames));
    }

    public static float rotateBy(float original, float by) {
        return ((((original + by) % 360) + 540) % 360) - 180;
    }
    public static void rotateCamera(float by) {
        cameraRotation = rotateBy(cameraRotation, by);
    }
}
