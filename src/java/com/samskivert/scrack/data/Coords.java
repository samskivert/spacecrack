//
// $Id$

package com.samskivert.scrack.data;

import com.threerings.io.SimpleStreamableObject;
import com.threerings.io.Streamable;

/**
 * Represents 2D coordinates in space. Implements the necessary interfaces
 * to be {@link Streamable}.
 */
public class Coords extends SimpleStreamableObject
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
}
