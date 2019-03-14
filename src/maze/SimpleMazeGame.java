/*
 * SimpleMazeGame.java
 * Copyright (c) 2008, Drexel University.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Drexel University nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY DREXEL UNIVERSITY ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL DREXEL UNIVERSITY BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package maze;

import java.io.*;
import java.util.*;

import maze.ui.MazeViewer;

/**
 * 
 * @author Sunny
 * @version 1.0
 * @since 1.0
 */
public class SimpleMazeGame
{
	/**
	 * Creates a small maze.
	 */
	public static Maze createMaze()
	{
		
		Maze maze = new Maze();
		Room r1 = new Room(0);
		Room r2 = new Room(1);
		Door theDoor = new Door(r1, r2, 0);
		maze.addRoom(r1);
		maze.addRoom(r2);
		maze.setCurrentRoom(0);
		r1.setSide(Direction.North, theDoor);
		r1.setSide(Direction.East, new Wall());
		r1.setSide(Direction.South, new Wall());
		r1.setSide(Direction.West, new Wall());
		r2.setSide(Direction.North, new Wall());
		r2.setSide(Direction.East, new Wall());
		r2.setSide(Direction.South,  theDoor);
		r2.setSide(Direction.West,  new Wall());
		return maze;
	}

	public static Maze loadMaze(final String path)
	{
		ArrayList<Room> rooms = new ArrayList<Room>();
		ArrayList<Door> doors = new ArrayList<Door>();
		ArrayList<String> lines = new ArrayList<String>();
		
		Maze maze = new Maze();
		BufferedReader buffer;
		try {
			buffer = new BufferedReader(new FileReader(path));
			String line;
			
			while ((line = buffer.readLine() ) != null) {
				lines.add(line);
				String[] splitLine = line.split(" ");
				if(splitLine[0].equals("room")) {
					//make new room
					rooms.add(new Room(rooms.size()));
					
				} else if(splitLine[0].equals("door")) {
					int roomNum1 = Integer.parseInt(splitLine[2]);
					int roomNum2 = Integer.parseInt(splitLine[3]);
					
					Door newDoor = new Door(rooms.get(roomNum1), rooms.get(roomNum2), doors.size());
					newDoor.setOpen(splitLine[4].equals("open"));
					doors.add(newDoor);
				}
				System.out.println(line);
			}
			buffer.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		//add rooms to maze
		for(int roomNum = 0; roomNum<rooms.size(); roomNum++) {
			maze.addRoom(rooms.get(roomNum));
		}
		maze.setCurrentRoom(0);
		makeWalls(rooms, doors, lines);
		return maze;
	}
	
	/**
	 * Makes the walls for each direction
	 * TODO - one bug that exists is that the doors are not visible if a wall is placed between the two rooms afterwards.
	 * This can be fixed by placing all of the doors to be added in the direction in the certain rooms after the walls are placed.
	 * @param rooms
	 * @param doors
	 * @param lines
	 */
	private static void makeWalls(ArrayList<Room> rooms, ArrayList<Door> doors, ArrayList<String> lines) {
		System.out.println("in make walls");
		SideSetup sideSetUp = new SideSetup();
		for(int lineIndex=0; lineIndex < lines.size(); lineIndex++) {
			String[] splitLine = lines.get(lineIndex).split(" ");
			if(splitLine[0].equals("room")) {
				int roomIndex = Integer.parseInt(splitLine[1]);
				Room currRoom = rooms.get(roomIndex);
				
				//North Wall
				MapSite northDir= null;
				String side = splitLine[2];
				if(side.equals("wall")) { //for wall side
					northDir = new Wall();
				} else if (side.startsWith("d")){ //for door side
					int doorIndex = getDoorIndex(side);
					northDir = doors.get(doorIndex);
				} else { //for room side
					northDir = rooms.get(Integer.parseInt(side));
				}
				
				sideSetUp.addRoomDir(northDir, currRoom, Direction.North);
				//currRoom.setSide(Direction.North, northDir);
				
				//East Wall
				MapSite eastDir= null;
				side = splitLine[3];
				if(side.equals("wall")) { //for wall side
					eastDir = new Wall();
				} else if (side.startsWith("d")){ //for door side
					System.out.println("eastern door: " + roomIndex);
					int doorIndex = getDoorIndex(side);
					eastDir = doors.get(doorIndex);
				} else { //for room side
					eastDir = rooms.get(Integer.parseInt(side));
				}
				
				sideSetUp.addRoomDir(eastDir, currRoom, Direction.East);
				//currRoom.setSide(Direction.East, eastDir);
				
				//South Wall
				MapSite southDir= null;
				side = splitLine[4];
				if(side.equals("wall")) { //for wall side
					southDir = new Wall();
				} else if (side.startsWith("d")){ //for door side
					int doorIndex = getDoorIndex(side);
					southDir = doors.get(doorIndex);
				} else { //for room side
					southDir = rooms.get(Integer.parseInt(side));
				}
				
				sideSetUp.addRoomDir(southDir, currRoom, Direction.South);
				//currRoom.setSide(Direction.South, southDir);
				
				//West Wall
				MapSite westDir= null;
				side = splitLine[5];
				if(side.equals("wall")) { //for wall side
					westDir = new Wall();
				} else if (side.startsWith("d")){ //for door side
					int doorIndex = getDoorIndex(side);
					westDir = doors.get(doorIndex);
				} else { //for room side
					westDir = rooms.get(Integer.parseInt(side));
				}
				
				sideSetUp.addRoomDir(westDir, currRoom, Direction.West);
				//currRoom.setSide(Direction.West, westDir);
				
				
			} 
		}
		readSides(sideSetUp, rooms, doors);
		
	}
	
	private static int getDoorIndex(String door) {
		return Integer.parseInt(door.substring(1));
	}
	
	/**
	 * adding side function calls first by room, then by wall, then by door
	 * @param sideSetup
	 */
	private static void readSides(SideSetup sideSetup, ArrayList<Room> rooms, ArrayList<Door> doors) {
		//first walls
		HashMap<Room, ArrayList<Direction>> walls = sideSetup.getWalls();
		iterateMap(walls, new Wall());
		
		//then add rooms and doors
		HashMap<String, HashMap<Room, ArrayList<Direction>>> doorRoomMap = sideSetup.getMapSiteRoom();
		Iterator it = doorRoomMap.entrySet().iterator();
		while (it.hasNext()) { //key is room or door in string, val is hashmap of room and direction
	        Map.Entry pair = (Map.Entry)it.next();
	        if(((String) pair.getKey()).startsWith("r")) {
	        	String roomNum = ((String) pair.getKey()).substring(1);
		        Room currSiteRoom = rooms.get(Integer.parseInt(roomNum));
		        HashMap<Room, ArrayList<Direction>> roomDirMap = (HashMap<Room, ArrayList<Direction>>) pair.getValue();
		        
		        iterateMap(roomDirMap, currSiteRoom);
	        } else if(((String) pair.getKey()).startsWith("d")) {
	        	String doorNum = ((String) pair.getKey()).substring(1);
		        Door currSiteDoor = doors.get(Integer.parseInt(doorNum));
		        HashMap<Room, ArrayList<Direction>> roomDirMap = (HashMap<Room, ArrayList<Direction>>) pair.getValue();
		        
		        iterateMap(roomDirMap, currSiteDoor);
	        }
	        
	    }
		
	}
	
	private static void iterateMap(HashMap<Room, ArrayList<Direction>> roomDir, MapSite mapSite) {
		Iterator it = roomDir.entrySet().iterator();
		while (it.hasNext()) { //key is room, val is list of directions
	        Map.Entry pair = (Map.Entry)it.next();
	        ArrayList<Direction> directions = (ArrayList<Direction>) pair.getValue();
	        Room currRoom = (Room) pair.getKey();
	        for(int dirIndex = 0; dirIndex < directions.size(); dirIndex++) {
	        	currRoom.setSide(directions.get(dirIndex), mapSite);
	        }
	        
	        it.remove(); // avoids a ConcurrentModificationException
	    }
	}
	
	private static void makeRoom() {
		
	}

	public static void main(String[] args)
	{
		Maze maze = null;
		if(args.length == 0) {
			maze = createMaze();
		} else {
			maze = loadMaze(args[0]);
		}
	    MazeViewer viewer = new MazeViewer(maze);
	    viewer.run();
	}
}
