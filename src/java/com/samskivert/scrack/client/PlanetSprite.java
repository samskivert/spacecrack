//
// $Id$

package com.samskivert.scrack.client;

import java.awt.Color;
import java.awt.Graphics2D;

import com.threerings.media.sprite.Sprite;

import com.samskivert.scrack.data.Planet;

/**
 * Displays a planet.
 */
public class PlanetSprite extends Sprite
{
    public PlanetSprite (Planet planet)
    {
        super(planet.size*5, planet.size*5);

        // our origin is in the center of the graphic
        _oxoff = _bounds.width/2;
        _oyoff = _bounds.height/2;

        _planet = planet;

        // position ourselves based on our planet's coordinates
        setLocation(_planet.coords.x * 50, _planet.coords.y * 50);
    }

    /** Returns the planet we're rendering. */
    public Planet getPlanet ()
    {
        return _planet;
    }

    /** Called when our planet is updated, updates our display of it. */
    public void updated (Planet planet)
    {
        // just update our reference and dirty ourselves
        _planet = planet;
        invalidate();
    }

    @Override // documentation inherited
    public void paint (Graphics2D gfx)
    {
        if (_planet.owner == -1) {
            gfx.setColor(Color.gray);
        } else {
            gfx.setColor(ScrackBoardView.COLORS[_planet.owner]);
        }
        gfx.fillOval(_bounds.x, _bounds.y, _bounds.width-1, _bounds.height-1);
        gfx.setColor(Color.white);
        gfx.drawOval(_bounds.x, _bounds.y, _bounds.width-1, _bounds.height-1);
    }

    protected Planet _planet;
}
