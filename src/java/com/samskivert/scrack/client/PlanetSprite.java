//
// $Id$

package com.samskivert.scrack.client;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import com.samskivert.scrack.data.Planet;

import static com.samskivert.scrack.client.ScrackMetrics.*;

/**
 * Displays a planet.
 */
public class PlanetSprite extends CelestialSprite
{
    public PlanetSprite (Planet planet)
    {
        super(ScrackController.PLANET_SELECTED,
              planet.size*SIZE, planet.size*SIZE);

        // update ourselves for the first time
        updated(planet);
    }

    /** Returns the planet we're rendering. */
    public Planet getPlanet ()
    {
        return _planet;
    }

    /** Called when our planet is updated, updates our display of it. */
    public void updated (Planet planet)
    {
        _planet = planet;

        // position ourselves based on our planet's coordinates
        setLocation(_planet.coords.x * TILE_SIZE, _planet.coords.y * TILE_SIZE);

        // update our planet shape
        updateShape((Ellipse2D)_shape);

        invalidate();
    }

    @Override // documentation inherited
    protected Shape createShape ()
    {
        Ellipse2D.Float circle = new Ellipse2D.Float();
        updateShape(circle);
        return circle;
    }

    @Override // documentation inherited
    protected Color getFillColor ()
    {
        return (_planet.owner == -1) ?
            Color.gray : ScrackBoardView.COLORS[_planet.owner];
    }

    protected void updateShape (Ellipse2D circle)
    {
        circle.setFrame(
            _bounds.x, _bounds.y, _bounds.width-1, _bounds.height-1);
    }

    protected Planet _planet;
}
