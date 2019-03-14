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
	 * Creates a small maze if no file with maze args is given
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

	/**
	 * Used to read in and interpret the maze files given
	 * @param path to file
	 * @return Maze to be shown as a gui
	 */
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
					//make new door
					int roomNum1 = Integer.parseInt(splitLine[2]);
					int roomNum2 = Integer.parseInt(splitLine[3]);
					
					Door newDoor = new Door(rooms.get(roomNum1), rooms.get(roomNum2), doors.size());
					newDoor.setOpen(Boolean.parseBoolean(splitLine[4]));
					doors.add(newDoor);
				}
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
	 * I tried placing doors after walls and vice versa and this was not fixed, so I am not sure what the solution is
	 * @param rooms list of rooms to add sides to
	 * @param doors list of doors to be added to room sides
	 * @param lines the list of strings which explain what each room has for sides
	 */
	private static void makeWalls(ArrayList<Room> rooms, ArrayList<Door> doors, ArrayList<String> lines) {
		for(int linesIndex=0; linesIndex < lines.size(); linesIndex++) {
			String[] splitLine = lines.get(linesIndex).split(" ");
			if(splitLine[0].equals("room")) {
				int roomIndex = Integer.parseInt(splitLine[1]);
				Room currRoom = rooms.get(roomIndex);
				Direction[] directions = {Direction.North, Direction.East, Direction.South, Direction.West};
				
				for(int splitIndex = 2; splitIndex <6; splitIndex++) {
					Direction currDir = directions[splitIndex-2];
					MapSite mapSite= null;
					String side = splitLine[splitIndex];
					if(side.equals("wall")) { //for wall side
						mapSite = new Wall();
					} else if (side.startsWith("d")){ //for door side
						int doorIndex = getDoorIndex(side);
						mapSite = doors.get(doorIndex);
					} else { //for room side
						mapSite = rooms.get(Integer.parseInt(side));
					}
					currRoom.setSide(currDir, mapSite);
				}
				
			} 
		}
		
	}
	
	/**
	 * returns index of door in door list
	 * @param door number in door name to be parsed into ind
	 * @return index of door
	 */
	private static int getDoorIndex(String door) {
		return Integer.parseInt(door.substring(1));
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