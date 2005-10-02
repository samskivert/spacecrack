//
// $Id$

package com.samskivert.scrack.data;

import com.samskivert.util.IntListUtil;

import com.threerings.io.SimpleStreamableObject;

import com.threerings.presents.dobj.DSet;

/**
 * Represents a single planet in the game world. <em>Note:</em> all
 * planets must have unique coordinates.
 */
public class Planet extends SimpleStreamableObject
    implements DSet.Entry
{
    /** A unique identifier for this planet. */
    public Integer planetId;

    /** The coordinates of this planet in space. */
    public Coords coords;

    /** Represents the size of this planet. This dictates how much crack
     * is produced by the planet and the size of ships built thereon. */
    public int size;

    /** The index of the player that owns this planet, or -1 if it is
     * independent. */
    public int owner;

    /** The planet ids of all this planet's neighbors. */
    public int[] neighbors;

    // documentation inherited from interface DSet.Entry
    public Comparable getKey ()
    {
        return planetId;
    }

    /** A blank constructor used for serialization. */
    public Planet ()
    {
    }

    /**
     * Creates a new planet at the specified coordinates, with the
     * specified size, owned by the specified player.
     */
    public Planet (int x, int y, int size, int owner)
    {
        planetId = ++_nextPlanetId;
        coords = new Coords(x, y);
        this.size = size;
        this.owner = owner;
    }

    /**
     * Returns true if the specified planet is a neighbor of this planet.
     */
    public boolean isNeighbor (Planet other)
    {
        return IntListUtil.contains(neighbors, other.planetId);
    }

    /** Used to assign unique ids to all planets. */
    protected static int _nextPlanetId;
}
