Space Crack
===========

Compete to win the most planets with your armada of ships.

This is a game written for [Game Gardens], you can [play it there].

Instructions
------------

For detailed design information, see Danc's blog entries:

http://lostgarden.com/2005/06/space-crack-introduction.html

The object of the game is to control the most planets after a set number of
turns have elapsed. On each turn, players are awarded a fixed number of command
points and they can use those points to take two actions:

- Move a ship from one planet to a neighboring planet
- Build a new ship at a planet they control
- Each action costs one command point.

When moving a ship, a number of things can happen:

- If the planet is unoccupied by a ship, the player takes control of the planet without a battle
- If the planet is occupied by a ship owned by the player, the two ships are
  combined into a new ship that is the sum of their size
- If the planet is occupied by an opposing ship larger than the player's
  (attacking) ship, the player's ship is destroyed and the defending ship's
  size is reduced by one half of the size of the attacking ship
- If the planet is occupied by an opposing ship smaller than the player's ship,
  the defending ship is destroyed, the player takes control of the planet and
  the player's (attacking) ship is reduced in size by one half the size of the
  defending ship
- If the planet is occupied by an opposing ship the same size as the player's
  ship, both ships are destroyed

A player may make their moves at any time as long as they have the command
points to do so. When all players have exhausted their command points, the turn
is ended. Players are awarded crack based on the planets they control.

- All planets produce crack equal to one half of their size (using the default
  settings)
- Planets that are surrounded on six sides by friendly planets are "core"
  planets and produce a bonus quantity of crack (an additional half using the
  default settings)

Crack is used to build ships. A planet consumes units of crack in equal numbers
to its size to build a ship and the constructed ship is the size of the
creating planet.

In this way the game is played out turn by turn until the final turn elapses
and the player that controls the most planets (regardless of planet size) is
the winner. Ties are broken by the players' remaining crack supplies and if
that ties as well, then there is simply a tie.

Many of the parameters are tweakable presently because this is a prototype and
we want to find out what works and what doesn't.

TODO
----

- allow a player to end their turn without using all of their command points
  and save their command points for the next turn
- incorporate the "perimiter world" bonus described by Danc in his design
- tweak, refine and improve!

Credits
-------

Design: Copyright (c) 2005 Daniel Cook 

Code: Copyright (c) 2005 Michael Bayne

[Game Gardens]: http://www.gamegardens.com/
[play it there]: http://www.gamegardens.com/gardens/view_game.wm?gameid=26
