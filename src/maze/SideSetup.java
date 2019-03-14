package maze;

import java.util.*;
import maze.ui.MazeViewer;

class SideSetup {

	private MapSite side;
	private HashMap<Room, ArrayList<Direction>> roomDir;
	private ArrayList<Direction> dirList;
	private HashMap<String, HashMap<Room, ArrayList<Direction>>> mapSiteRoom;
	
	public SideSetup() {
		mapSiteRoom = new HashMap<String, HashMap<Room, ArrayList<Direction>>>();
		roomDir = new HashMap<Room, ArrayList<Direction>>(); 
		dirList = new ArrayList<Direction>();
		side = new Wall();
				
		mapSiteRoom.put("wall", roomDir );
		
	}
	
	public SideSetup(MapSite mapsite, Room room, Direction direction) {
		side = mapsite;
		roomDir = new HashMap<Room, ArrayList<Direction>>();
		mapSiteRoom = new HashMap<>();
		
		dirList= new ArrayList<Direction>();
		dirList.add(direction);
		
		roomDir.put(room, dirList);
		
		side.getClass().toString();
		mapSiteRoom.put(side.getClass().toString(), roomDir);
		
	}
	
	/**
	 * adding rooms and direction specification to room types to be able to add sides in order
	 * @param theSide
	 * @param room
	 * @param dir
	 */
	public void addRoomDir(MapSite theSide, Room room, Direction dir) {
		HashMap<Room, ArrayList<Direction>> currSide= new HashMap<Room, ArrayList<Direction>>();
		
		ArrayList<Direction> currDir = new ArrayList<Direction>();
		currDir.add(dir); 
		if(this.mapSiteRoom.containsKey(theSide.toString())) {///side specification is in HashMap
			currSide = this.mapSiteRoom.get(theSide.toString());
			if(currSide.containsKey(room)) { // side and room combo exist, just need to add another direction
				currSide.get(room).add(dir);
			} else { //new room for this side
				currSide.put(room, currDir);
			}
		} else {
			System.out.println("new side not in mapsiteroom: "+ side.toString());
			HashMap<Room, ArrayList<Direction>> newRoom = new HashMap<Room, ArrayList<Direction>>();
			newRoom.put(room, currDir);
			this.mapSiteRoom.put(theSide.toString(), newRoom);
		}
	}
	
	/**
	 * return wall sides
	 * @return
	 */
	public HashMap<Room, ArrayList<Direction>> getWalls() {
		HashMap<Room, ArrayList<Direction>> tempWall = mapSiteRoom.get("wall");
		mapSiteRoom.remove("wall");
		return tempWall;
		
	}
	
	/**
	 * return door sides
	 * @return
	 */
	public HashMap<Room, ArrayList<Direction>> getRooms() {
		Iterator it = mapSiteRoom.entrySet().iterator();
		HashMap<String, HashMap<Room, ArrayList<Direction>>> rooms = new HashMap<String, HashMap<Room, ArrayList<Direction>>>();
		while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        if(((String) pair.getKey()).startsWith("r")) {//add room
	        	//rooms.add((String)pair.getKey(), (HashMap<Room, ArrayList<Direction>>)pair.getValue());
	        }
	       // rooms= (pair.getKey(), (ArrayList<Direction>)pair.getValue());
	        it.remove(); // avoids a ConcurrentModificationException
	    }
		return new HashMap<Room, ArrayList<Direction>>();
	}
	
	/**
	 * returns the map of rooms with associated rooms and doors on what side
	 * @return
	 */
	public HashMap<String, HashMap<Room, ArrayList<Direction>>> getMapSiteRoom() {
		return mapSiteRoom;
	}
	
	public HashMap<Room, ArrayList<Direction>> getRoomDir(MapSite theSide) {
		return mapSiteRoom.get(theSide);
	}
	
	public ArrayList<String> getSides() {
		Iterator it = mapSiteRoom.entrySet().iterator();
		ArrayList<String> sides = new ArrayList<String>();
		while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        System.out.println(pair.getKey() + " = " + pair.getValue());
	        sides.add((String) pair.getKey());
	        it.remove(); // avoids a ConcurrentModificationException
	    }
		return sides;
	}
	
}
