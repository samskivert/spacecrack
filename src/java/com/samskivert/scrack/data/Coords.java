//
// $Id$

package com.samskivert.scrack.data;

import com.samskivert.util.StringUtil;

import com.threerings.io.Streamable;

/**
 * Represents 2D coordinates in space. Implements the necessary interfaces
 * to be {@link Streamable}.
 */
public class Coords
    implements Streamable, Comparable<Coords>
{
    /** Our x coordinate. */
    public int x;

    /** Our y coordinate. */
    public int y;

    /** Constructs blank coordinates (0, 0). */
    public Coords ()
    {
    }

    /** Constructs coordinates with the specified values. */
    public Coords (int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    // documentation inherited from interface Comparable
    public int compareTo (Coords oc)
    {
        return (oc.x == x) ? (oc.y - y) : (oc.x - x);
    }

    @Override // documentation inherited
    public boolean equals (Object other)
    {
        Coords oc = (Coords)other;
        return (x == oc.x) && (y == oc.y);
    }

    @Override // documentation inherited
    public int hashCode ()
    {
        return x ^ y;
    }

    @Override // documentation inherited
    public String toString ()
    {
        return StringUtil.coordsToString(x, y);
    }
}
