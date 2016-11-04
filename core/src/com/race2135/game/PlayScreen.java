package com.race2135.game;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


public class PlayScreen implements Screen {
    private Main game;
    private OrthographicCamera gamecam;
    private Viewport gamePort;
    private World world;
    private Box2DDebugRenderer b2dr;

    PlayerCar playerCar;

    SpriteBatch spriteBatch;
    Texture texture;

    public PlayScreen(Main game) {
        this.game = game;
        this.gamecam = new OrthographicCamera(8.0F, 6.0F);
        this.gamePort = new FitViewport(8.0F, 6.0F, this.gamecam);
        this.gamecam.position.set(this.gamePort.getWorldWidth() / 2.0F, this.gamePort.getWorldHeight() / 2.0F, 0.0F);
        this.world = new World(new Vector2(0.0F, 0.0F), true);

        this.b2dr = new Box2DDebugRenderer();
        playerCar = new PlayerCar(world);

        spriteBatch = new SpriteBatch();
        texture = new Texture(Gdx.files.internal("badlogic.jpg"));
    }



    public void update(float dt) {
        playerCar.update();

        this.world.step(1f/60f, 6, 2);
        gamecam.position.set(playerCar.body.getPosition(), gamecam.position.z);
        this.gamecam.update();
    }

    public void render(float delta) {
        this.update(delta);
        Gdx.gl.glClearColor(0.3F, 0.3F, 0.3F, 1.0F);
        Gdx.gl.glClear(16384);

        spriteBatch.begin();
        spriteBatch.setProjectionMatrix(gamecam.combined);
        spriteBatch.draw(texture, 10 / Main.PPM, 10 / Main.PPM);
        spriteBatch.end();

        this.b2dr.render(this.world, this.gamecam.combined);
    }

    public void show() {
    }

    public void resize(int width, int height) {
    }

    public void pause() {
    }

    public void resume() {
    }

    public void hide() {
    }

    public void dispose() {
    }
}
