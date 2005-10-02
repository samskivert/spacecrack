//
// $Id$

package com.samskivert.scrack.data;

import com.threerings.io.SimpleStreamableObject;
import com.threerings.presents.dobj.DSet;

/**
 * Represents a single ship in the game world. <em>Note:</em> all ships
 * must have unique coordinates.
 */
public class Ship extends SimpleStreamableObject
    implements DSet.Entry
{
    /** A unique identifier for this ship. */
    public Integer shipId;

    /** The coordinates of this ship in space. */
    public Coords coords;

    /** Represents the size of this ship. */
    public int size;

    /** The index of the player that owns this ship. */
    public int owner;

    // documentation inherited from interface DSet.Entry
    public Comparable getKey ()
    {
        return shipId;
    }

    /** A blank constructor used for serialization. */
    public Ship ()
    {
    }

    /**
     * Creates a new ship with the specified coordinates, size, and owner.
     */
    public Ship (int x, int y, int size, int owner)
    {
        shipId = ++_nextShipId;
        coords = new Coords(x, y);
        this.size = size;
        this.owner = owner;
    }

    /** Used to assign unique ids to all ships. */
    protected static int _nextShipId;
}
