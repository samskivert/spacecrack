//
// $Id$

package com.samskivert.scrack.client;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import java.util.HashMap;
import java.util.Iterator;

import com.threerings.media.VirtualMediaPanel;

import com.threerings.presents.dobj.EntryAddedEvent;
import com.threerings.presents.dobj.EntryRemovedEvent;
import com.threerings.presents.dobj.EntryUpdatedEvent;
import com.threerings.presents.dobj.SetListener;

import com.threerings.crowd.client.PlaceView;
import com.threerings.crowd.data.PlaceObject;

import com.threerings.toybox.util.ToyBoxContext;

import com.samskivert.scrack.data.Coords;
import com.samskivert.scrack.data.Planet;
import com.samskivert.scrack.data.ScrackObject;
import com.samskivert.scrack.data.Ship;

import static com.samskivert.scrack.client.ScrackMetrics.*;

/**
 * Displays the main space interface.
 */
public class ScrackBoardView extends VirtualMediaPanel
    implements PlaceView, SetListener
{
    /** Defines the players' colors. */
    public static final Color[] COLORS = {
        Color.red.darker(), Color.blue.darker(), Color.green.darker(),
        Color.yellow.darker()
    };

    public ScrackBoardView (ToyBoxContext ctx, int boardSize)
    {
        super(ctx.getFrameManager());
        _ctx = ctx;
        _boardSize = boardSize;
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

        // and for each of the ships
        for (Iterator iter = _scrobj.ships.iterator(); iter.hasNext(); ) {
            ShipSprite sprite = new ShipSprite((Ship)iter.next());
            addSprite(sprite);
            _ships.put(sprite.getShip().coords, sprite);
        }
    }

    // documentation inherited from interface PlaceView
    public void willEnterPlace (PlaceObject plobj)
    {
        _scrobj = (ScrackObject)plobj;
        _scrobj.addListener(this);
    }

    // documentation inherited from interface PlaceView
    public void didLeavePlace (PlaceObject plobj)
    {
        if (_scrobj != null) {
            _scrobj.removeListener(this);
            _scrobj = null;
        }
    }

    // documentation inherited from interface SetListener
    public void entryAdded (EntryAddedEvent event)
    {
        if (event.getName().equals(ScrackObject.SHIPS)) {
            ShipSprite sprite = new ShipSprite((Ship)event.getEntry());
            addSprite(sprite);
            _ships.put(sprite.getShip().coords, sprite);
        }
    }

    // documentation inherited from interface SetListener
    public void entryUpdated (EntryUpdatedEvent event)
    {
        if (event.getName().equals(ScrackObject.PLANETS)) {
            Planet nplanet = (Planet)event.getEntry();
            _planets.get(((Planet)event.getOldEntry()).coords).updated(nplanet);
        } else if (event.getName().equals(ScrackObject.SHIPS)) {
            Ship nship = (Ship)event.getEntry();
            _ships.get(((Ship)event.getOldEntry()).coords).updated(nship);
        }
    }

    // documentation inherited from interface SetListener
    public void entryRemoved (EntryRemovedEvent event)
    {
        if (event.getName().equals(ScrackObject.SHIPS)) {
            removeSprite(_ships.remove((Coords)event.getKey()));
        }
    }

    @Override // documentation inherited
    protected void paintBehind (Graphics2D gfx, Rectangle dirtyRect)
    {
        super.paintBehind(gfx, dirtyRect);

        gfx.setColor(Color.black);
        gfx.fill(dirtyRect);

        gfx.setColor(Color.gray);
        int max = TILE_SIZE * _boardSize;
        for (int xx = 0; xx < _boardSize; xx++) {
            gfx.drawLine(xx * TILE_SIZE, 0, xx * TILE_SIZE, max);
        }
        for (int yy = 0; yy < _boardSize; yy++) {
            gfx.drawLine(0, yy * TILE_SIZE, max, yy * TILE_SIZE);
        }
    }

    protected ToyBoxContext _ctx;
    protected ScrackObject _scrobj;
    protected int _boardSize;

    protected HashMap<Coords,PlanetSprite> _planets =
        new HashMap<Coords,PlanetSprite>();
    protected HashMap<Coords,ShipSprite> _ships =
        new HashMap<Coords,ShipSprite>();
}
