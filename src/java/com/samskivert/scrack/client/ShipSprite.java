//
// $Id$

package com.samskivert.scrack.client;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;

import com.threerings.media.sprite.Sprite;

import com.samskivert.scrack.data.Ship;

import static com.samskivert.scrack.client.ScrackMetrics.*;

/**
 * Displays a ship.
 */
public class ShipSprite extends Sprite
{
    public ShipSprite (Ship ship)
    {
        super(ship.size*SIZE, ship.size*SIZE);

        // our origin is in the center of the graphic
        _oxoff = _bounds.width/2;
        _oyoff = _bounds.height/2;

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
        _ship = ship;

        // position ourselves based on our ship's coordinates; TODO: animate
        setLocation(_ship.coords.x * TILE_SIZE, _ship.coords.y * TILE_SIZE);

        // update our glyph
        _glyph.reset();
        _glyph.addPoint(_bounds.x + _bounds.width/2, _bounds.y);
        _glyph.addPoint(_bounds.x, _bounds.y+_bounds.height-1);
        _glyph.addPoint(_bounds.x+_bounds.width-1, _bounds.y+_bounds.height-1);

        invalidate();
    }

    @Override // documentation inherited
    public void paint (Graphics2D gfx)
    {
        gfx.setColor(ScrackBoardView.COLORS[_ship.owner]);
        gfx.fill(_glyph);
        gfx.setColor(Color.white);
        gfx.draw(_glyph);
    }

    protected Ship _ship;
    protected Polygon _glyph = new Polygon();
}
