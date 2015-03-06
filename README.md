# Wumpus
Ai wumpus agent

A simple wumpus game implementation for university AI project.
The agent has to move to an uknown map to find the gold.
The map contains safe areas,hole areas, one area with wumpus(doesnt move) and one area with gold.
The agent can perceive breeze if standing next to the area of a hole, glitter if at the area of gold
smell if next to an area of the wumpus.The agent can do the following moves:
Change his direction,move forward,catch the gold,throw only one arrow at one direction,bump into wall.
There is a limit of actions the agent can do.If he cross it he dies.If he goes to an area of a hole or where wumpus is he dies.
There is gui and  cmd implementation.
