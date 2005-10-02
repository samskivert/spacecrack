//
// $Id$

package com.samskivert.scrack.client;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Line2D;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import com.samskivert.util.Tuple;

import com.threerings.media.VirtualMediaPanel;
import com.threerings.media.sprite.Sprite;

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
            _planets.put(sprite.getPlanet().planetId, sprite);
        }

        // and for each of the ships
        for (Iterator iter = _scrobj.ships.iterator(); iter.hasNext(); ) {
            ShipSprite sprite = new ShipSprite((Ship)iter.next());
            addSprite(sprite);
            _ships.put(sprite.getShip().shipId, sprite);
        }

        // compute our neighbor information and convert the edges to a rapidly
        // renderable set of lines
        HashSet<Tuple> edges = new HashSet<Tuple>();
        for (Iterator iter = _scrobj.planets.iterator(); iter.hasNext(); ) {
            Planet p = (Planet)iter.next();
            for (int ii = 0; ii < p.neighbors.length; ii++) {
                edges.add(new Tuple(p.planetId, p.neighbors[ii]));
            }
        }
        _edges = new Line2D[edges.size()];
        System.err.println("Created " + _edges.length + " edges.");
        int eidx = 0;
        for (Tuple edge : edges) {
            Planet p1 = (Planet)_scrobj.planets.get((Integer)edge.left);
            Planet p2 = (Planet)_scrobj.planets.get((Integer)edge.right);
            _edges[eidx++] = new Line2D.Float(
                p1.coords.x * TILE_SIZE, p1.coords.y * TILE_SIZE, 
                p2.coords.x * TILE_SIZE, p2.coords.y * TILE_SIZE);
        }

        // redraw everything now that we have our edges
        repaint();
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
            _ships.put(sprite.getShip().shipId, sprite);
        }
    }

    // documentation inherited from interface SetListener
    public void entryUpdated (EntryUpdatedEvent event)
    {
        if (event.getName().equals(ScrackObject.PLANETS)) {
            Planet planet = (Planet)event.getEntry();
            _planets.get(planet.planetId).updated(planet);
        } else if (event.getName().equals(ScrackObject.SHIPS)) {
            Ship ship = (Ship)event.getEntry();
            _ships.get(ship.shipId).updated(ship);
        }
    }

    // documentation inherited from interface SetListener
    public void entryRemoved (EntryRemovedEvent event)
    {
        if (event.getName().equals(ScrackObject.SHIPS)) {
            removeSprite(_ships.remove((Integer)event.getKey()));
        }
    }

    @Override // documentation inherited
    protected void paintBehind (Graphics2D gfx, Rectangle dirtyRect)
    {
        super.paintBehind(gfx, dirtyRect);

        gfx.setColor(Color.black);
        gfx.fill(dirtyRect);
        gfx.setColor(Color.gray);

        // draw the routes between planets
        if (_edges != null) {
            for (int ii = 0; ii < _edges.length; ii++) {
                gfx.draw(_edges[ii]);
            }
        }
    }

    protected ToyBoxContext _ctx;
    protected ScrackObject _scrobj;

    protected HashMap<Integer,PlanetSprite> _planets =
        new HashMap<Integer,PlanetSprite>();
    protected HashMap<Integer,ShipSprite> _ships =
        new HashMap<Integer,ShipSprite>();
    protected Line2D[] _edges;
}
