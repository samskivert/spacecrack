//
// $Id$

package com.samskivert.scrack.client;

import java.awt.Color;
import java.awt.Polygon;
import java.awt.Shape;

import com.samskivert.scrack.data.Ship;

import static com.samskivert.scrack.client.ScrackMetrics.*;

/**
 * Displays a ship.
 */
public class ShipSprite extends CelestialSprite
{
    public ShipSprite (Ship ship)
    {
        super(ScrackController.SHIP_SELECTED, ship.size*SIZE, ship.size*SIZE);

        // render above the planets
        _renderOrder = 1;

        // update ourselves for the first time
        updated(ship);
    }

    /** Returns the ship we're rendering. */
    public Ship getShip ()
    {
        return _ship;
    }

    /** Called when our ship is updated, updates our display of it. */
    public void updated (Ship ship)
    {
        // if we're changing size, we have to do some jiggering
        if (_ship != null && ship.size != _ship.size) {
            invalidate();
            _bounds.width = ship.size * SIZE;
            _bounds.height = ship.size * SIZE;
            _oxoff = _bounds.width/2;
            _oyoff = _bounds.height/2;
        }
        _ship = ship;

        // position ourselves based on our ship's coordinates; TODO: animate
        setLocation(_ship.coords.x * TILE_SIZE, _ship.coords.y * TILE_SIZE);

        // update our ship shape
        ((Polygon)_shape).reset();
        updateShape((Polygon)_shape);

        invalidate();
    }

    @Override // documentation inherited
    protected Shape createShape ()
    {
        Polygon poly = new Polygon();
        updateShape(poly);
        return poly;
    }

    @Override // documentation inherited
    protected Color getFillColor ()
    {
        return ScrackBoardView.COLORS[_ship.owner];
    }

    protected void updateShape (Polygon poly)
    {
        poly.addPoint(_bounds.x + _bounds.width/2, _bounds.y);
        poly.addPoint(_bounds.x, _bounds.y+_bounds.height-1);
        poly.addPoint(_bounds.x+_bounds.width-1, _bounds.y+_bounds.height-1);
    }

    protected Ship _ship;
    protected Polygon _glyph = new Polygon();
}
