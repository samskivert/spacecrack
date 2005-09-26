//
// $Id$

package com.samskivert.scrack.data;

import com.threerings.io.SimpleStreamableObject;
import com.threerings.presents.dobj.DSet;

/**
 * Represents a single planet in the game world. <em>Note:</em> all
 * planets must have unique coordinates.
 */
public class Planet extends SimpleStreamableObject
    implements DSet.Entry
{
    /** The coordinates of this planet in space. */
    public Coords coords;

    /** Represents the size of this planet. This dictates how much crack
     * is produced by the planet and the size of ships built thereon. */
    public int size;

    /** The index of the player that owns this planet, or -1 if it is
     * independent. */
    public int owner;

    // documentation inherited from interface DSet.Entry
    public Comparable getKey ()
    {
        return coords;
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
        coords = new Coords(x, y);
        this.size = size;
        this.owner = owner;
    }
}
