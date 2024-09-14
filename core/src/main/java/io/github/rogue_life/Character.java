package io.github.rogue_life;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import java.util.Comparator;

import static java.lang.Math.*;

public class Character {
    private String defaultAnimation;
    private Animation<TextureRegion> animation;
    private TextureRegion texture;
    private int animStep;
    private boolean isPlaying = false;
    private float stateTime = 0.0f;
    final String name;
    private Thread playThread;
    float speed = 50;
    float rotationSpeed = 1;
    boolean flipX = false;

    float x, y;
    private Character player;

    public Character(String name, String texture) {
        this.name = name;
        if (RogueLife.animations.containsKey(texture)) {
            this.defaultAnimation = texture;
            setAnim(defaultAnimation);
        } else if (RogueLife.textures.containsKey(texture)) {
            this.texture = new TextureRegion(RogueLife.textures.get(texture));
        }
    }
    public Character(String name, String defaultAnimation, int animStep) {
        this.name = name;
        this.defaultAnimation = defaultAnimation;
        setAnim(defaultAnimation, animStep);
    }
    public void play() {
        if (!isPlaying) {
            isPlaying = true;
            playThread = new Thread(() -> {
                int i = 0;
                int frame = animation.getKeyFrameIndex(stateTime % (animation.getFrameDuration() * animation.getKeyFrames().length));
                while (i < animStep) {
                    stateTime += Gdx.graphics.getDeltaTime();
                    int nextFrame = animation.getKeyFrameIndex(stateTime % (animation.getFrameDuration() * animation.getKeyFrames().length));
                    if (nextFrame != frame) {
                        frame = nextFrame;
                        i++;
                    }
                    try {
                        Thread.sleep(round(Gdx.graphics.getDeltaTime() * 1000));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                isPlaying = false;
            });
            playThread.start();
        }
    }
    public void input() {
        float dist = Gdx.graphics.getDeltaTime() * speed;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            if (flipX) {
                flipX = false;
            }
            play();
            RogueLife.rotateCamera(rotationSpeed);
            double direction = RogueLife.rotateBy(RogueLife.cameraRotation, -90);
            //x += (float) sin(toRadians(direction)) * dist;
            //y += (float) cos(toRadians(direction)) * dist;
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            if (!flipX) {
                flipX = true;
            }
            play();
            RogueLife.rotateCamera(-rotationSpeed);
            double direction = RogueLife.rotateBy(RogueLife.cameraRotation, 90);
            //x += (float) sin(toRadians(direction)) * dist;
            //y += (float) cos(toRadians(direction)) * dist;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            play();
            double direction = RogueLife.cameraRotation;
            x += (float) sin(toRadians(direction)) * dist;
            y += (float) cos(toRadians(direction)) * dist;
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            play();
            double direction = RogueLife.rotateBy(RogueLife.cameraRotation, 180);
            x += (float) sin(toRadians(direction)) * dist;
            y += (float) cos(toRadians(direction)) * dist;
        }
    }
    public void setAnim(String name) {
        setAnim(name, -1);
    }
    public void setAnim(String name, int animStep) {
        if (RogueLife.animations.containsKey(name)) {
            animation = RogueLife.animations.get(name);
            if (animStep != -1) {
                this.animStep = animStep;
            } else {
                this.animStep = animation.getKeyFrames().length;
            }

            stateTime = 0.0f;
        }
    }
    public TextureRegion getKey() throws IllegalArgumentException {
        if (texture != null) {
            TextureRegion temp = texture;
            if (flipX && !temp.isFlipX() || !flipX && temp.isFlipX()) {
                temp.flip(true, false);
            }
            return temp;
        }
        try {
            TextureRegion temp = animation.getKeyFrame(stateTime, true);
            if (flipX && !temp.isFlipX() || !flipX && temp.isFlipX()) {
                temp.flip(true, false);
            }
            return temp;
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("Animations map does not contain an animation for \"%s\".%n", name));
        }
    }

    public float getSizeModifier() {
        if (player == null) {
            return 1;
        }
        double angleA = RogueLife.FOV / 2;
        double legA = RogueLife.playerY;
        double hypotenuse = legA / cos(toRadians(angleA));
        double angleB = 90 - angleA;
        double legB = (hypotenuse * cos(toRadians(angleB)));
        float playerDistance = (float) legB;
        return playerDistance / (playerDistance + getOffset().y);
    }
    public Vector2 getOffset() {
        return new Vector2(x - player.x, y - player.y).rotateDeg(RogueLife.cameraRotation);
    }
    public float getOnScreenX() {
        if (player == null) {
            return RogueLife.playerX;
        }
        return RogueLife.playerX + getOffset().x * getSizeModifier() - getWidth() / 2;
    }
    public float getOnScreenY() {
        if (player == null) {
            return RogueLife.playerY;
        }
        double angleA = RogueLife.FOV / 2;
        double angleB = 90 - angleA;
        double legB = getOffset().y;
        double hypotenuse = legB / cos(toRadians(angleB));
        double legA = hypotenuse * cos(toRadians(angleA));
        float offsetY = (float) legA;
        return min(RogueLife.playerY + offsetY, RogueLife.horizonY - getHeight() / 2);
    }
    public float getWidth() {
        if (player != null) {
            return getKey().getRegionWidth() * getSizeModifier();
        }
        return getKey().getRegionWidth();
    }
    public float getHeight() {
        if (player != null) {
            return getKey().getRegionHeight() * getSizeModifier();
        }
        return getKey().getRegionHeight();
    }

    public void setPlayer(Character player) {
        this.player = player;
    }

    public void dispose() {
        playThread.interrupt();
    }
}
