//
// $Id$

package com.samskivert.scrack.client;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import java.util.HashMap;
import java.util.Iterator;

import com.threerings.media.VirtualMediaPanel;

import com.threerings.crowd.client.PlaceView;
import com.threerings.crowd.data.PlaceObject;

import com.threerings.toybox.util.ToyBoxContext;

import com.samskivert.scrack.data.Coords;
import com.samskivert.scrack.data.Planet;
import com.samskivert.scrack.data.ScrackObject;

/**
 * Displays the main space interface.
 */
public class ScrackBoardView extends VirtualMediaPanel
    implements PlaceView
{
    /** Defines the players' colors. */
    public static final Color[] COLORS = {
        Color.red.darker(), Color.blue.darker(), Color.green.darker(),
        Color.yellow.darker()
    };

    public ScrackBoardView (ToyBoxContext ctx)
    {
        super(ctx.getFrameManager());
        _ctx = ctx;
    }

    /**
     * Called when the game starts.
     */
    public void gameDidStart ()
    {
        // add sprites for each of the planets
        for (Iterator iter = _scrobj.planets.iterator(); iter.hasNext(); ) {
            PlanetSprite sprite = new PlanetSprite((Planet)iter.next());
            addSprite(sprite);
            _planets.put(sprite.getPlanet().coords, sprite);
        }

//         // and for each of the ships
//         for (Iterator iter = _scrobj.ships.iterator(); iter.hasNext(); ) {
//             ShipSprite sprite = new ShipSprite((Ship)iter.next());
//         }
    }

    // documentation inherited from interface PlaceView
    public void willEnterPlace (PlaceObject plobj)
    {
        _scrobj = (ScrackObject)plobj;
    }

    // documentation inherited from interface PlaceView
    public void didLeavePlace (PlaceObject plobj)
    {
    }

    @Override // documentation inherited
    protected void paintBehind (Graphics2D gfx, Rectangle dirtyRect)
    {
        super.paintBehind(gfx, dirtyRect);

        gfx.setColor(Color.black);
        gfx.fill(dirtyRect);
    }

    protected ToyBoxContext _ctx;
    protected ScrackObject _scrobj;

    protected HashMap<Coords,PlanetSprite> _planets =
        new HashMap<Coords,PlanetSprite>();
    protected HashMap<Coords,ShipSprite> _ships =
        new HashMap<Coords,ShipSprite>();
}
