package com.mygdx.game;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class TileMapActor extends Actor{
    public static int windowWidth = 800;
    public static int windowHeight = 600;

    private TiledMap tileMap;
    private OrthographicCamera tileCamera;
    private OrthoCachedTiledMapRenderer tiledMapRenderer;


    public TileMapActor(String filename, Stage stage){

        // sets the dimensions of the tilemap
        tileMap = new TmxMapLoader().load(filename, null); // loads the tilemap file into a TiledMap object
        int tileWidth = (int) tileMap.getProperties().get("tilewidth", null, null);
        int tileHeight = (int) tileMap.getProperties().get("tileheight", null, null);
        int numTilesWidth = (int) tileMap.getProperties().get("width", null, null);
        int numTilesHeight = (int) tileMap.getProperties().get("height", null, null);
        int mapWidth = tileWidth * numTilesWidth;
        int mapHeight = tileHeight * numTilesHeight;

        BaseActor.setWorldBounds(mapWidth, mapHeight); // we haven't set up boundaries yet

        // renders the map
        tiledMapRenderer = new OrthoCachedTiledMapRenderer(tileMap, mapWidth, mapHeight);
        tiledMapRenderer.setBlending(true);

        // sets up the tile camera
        tileCamera = new OrthographicCamera();
        tileCamera.setToOrtho(false, windowWidth, windowHeight);
        tileCamera.update();

        stage.addActor(this);
        
    }

    public void act(float dt){
        super.act(dt);
    }

    public void draw(Batch batch, float parentAlpha){
        
        // tilemap camera in sync with the main camera
        Camera mainCamera = getStage().getCamera();
        tileCamera.position.x = mainCamera.position.y;
        tileCamera.position.y = mainCamera.position.y;
        tileCamera.update();
        tiledMapRenderer.setView(tileCamera);
        
        // map gets rendered immediately before other actors on screen
        batch.end();
        tiledMapRenderer.render();
        batch.begin();
    }


}
