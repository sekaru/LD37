package net.sleepystudios.bankvault;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;

import net.sleepystudios.bankvault.entities.Drone;
import net.sleepystudios.bankvault.entities.Player;
import net.sleepystudios.bankvault.proc.Camera;
import net.sleepystudios.bankvault.proc.DecalProcObject;
import net.sleepystudios.bankvault.proc.Heart;
import net.sleepystudios.bankvault.proc.HiddenProcObject;
import net.sleepystudios.bankvault.proc.ProcObject;

public class MapHandler {
	public TiledMap map; 
	private TiledMapRenderer mapRenderer;
	private int[] layers = {0}, fringeLayers = {1,2};
	public ArrayList<Rectangle> rects = new ArrayList<Rectangle>();
	public ArrayList<ProcObject> procObjs = new ArrayList<ProcObject>();
	public ArrayList<Drone> drones = new ArrayList<Drone>();
	
	public int spawnX, spawnY;
	public Player p;
	
	public MapHandler(TiledMap map) {
		this.map = map;
		mapRenderer = new FixedTiledMapRenderer(map);
		
		loadRects();
	}
	
	private void loadRects() {
		int s = getTileSize();
		
		TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(fringeLayers[0]);
        for(int x = 0; x < layer.getWidth();x++){
            for(int y = 0; y < layer.getHeight();y++){
                TiledMapTileLayer.Cell cell = layer.getCell(x,y);
                
                if(cell!=null) {
                	Object property = cell.getTile().getProperties().get("wall");
                    if(property != null){
                        rects.add(new Rectangle(x*s, y*s, s, s));
                    } else {
                    	property = cell.getTile().getProperties().get("spawn");
                        if(property != null){
                        	spawnX = x*s + getTileSize()/2;
                        	spawnY = (y+2)*s;
                        	rects.add(new Rectangle(x*s, y*s, s, s));
                        }
                    }
                }
            }
        }
	}
	
	public void gen(OrthographicCamera camera) {
		int size[] = {30, 15, 5, 1};
		
		procObjs.clear();
		
		procObjs.add(new Heart(this));
		
		String decals[] = {"notes1", "notes2", "notes3", "coins1", "coins2", "coins3", "vault"};
		procObjs.add(new HiddenProcObject(this));
		for(int i=0; i<size[0]; i++) {
			procObjs.add(new DecalProcObject(decals[BankVault.rand(0, decals.length-1)], this));
		}
		
		String objs[] = {"barstack", "shelf"};
		for(int i=0; i<size[1]; i++) {
			procObjs.add(new ProcObject(objs[BankVault.rand(0, objs.length-1)], this));
		}
		
		for(int i=0; i<size[2]; i++) {
			procObjs.add(new Camera(this));
		}
		
		drones.clear();
		for(int i=0; i<size[3]; i++) {
			drones.add(new Drone(this));
		}
		
		p = new Player(camera, this);
	}
	
	public void render(OrthographicCamera camera) {
		mapRenderer.setView(camera);
        mapRenderer.render(layers);
	}
	
	public void renderFringe(OrthographicCamera camera) {
		mapRenderer.setView(camera);
		mapRenderer.render(fringeLayers);
	}
	
	public int getTileSize() {
		return map.getProperties().get("tilewidth", Integer.class);
	}
	
	public int getWidth() {
		return map.getProperties().get("width", Integer.class) * getTileSize();
	}
	
	public int getHeight() {
		return map.getProperties().get("height", Integer.class) * getTileSize();
	}
}
