//
// $Id$

package com.samskivert.scrack.server;

import java.util.HashMap;
import java.util.HashSet;

import com.threerings.util.RandomUtil;

import com.threerings.presents.data.ClientObject;
import com.threerings.presents.dobj.DSet;
import com.threerings.presents.server.PresentsServer;

import com.threerings.parlor.game.server.GameManager;
import com.threerings.toybox.data.ToyBoxGameConfig;

import com.samskivert.scrack.data.Coords;
import com.samskivert.scrack.data.Planet;
import com.samskivert.scrack.data.ScrackMarshaller;
import com.samskivert.scrack.data.ScrackObject;
import com.samskivert.scrack.data.Ship;

import static com.samskivert.scrack.Log.log;
import static com.samskivert.scrack.data.ScrackCodes.*;

/**
 * Manages the server side of the game.
 */
public class ScrackManager extends GameManager
    implements ScrackProvider
{
    // documentation inherited from interface ScrackProvider
    public void moveShip (ClientObject caller, Coords current, Coords dest)
    {
        // TODO
    }

    // documentation inherited from interface ScrackProvider
    public void buildShip (ClientObject caller, Coords planet)
    {
        // TODO
    }

    @Override // documentation inherited
    protected Class getPlaceObjectClass ()
    {
        return ScrackObject.class;
    }

    @Override // documentation inherited
    public void didStartup ()
    {
        super.didStartup();

        // grab and set up our game object
        _scrobj = (ScrackObject)_gameobj;
        _scrobj.setService(
            (ScrackMarshaller)PresentsServer.invmgr.registerDispatcher(
                new ScrackDispatcher(this), false));
    }

    @Override // documentation inherited
    protected void gameWillStart ()
    {
        super.gameWillStart();

        ToyBoxGameConfig gconfig = (ToyBoxGameConfig)_config;
        int size = (Integer)gconfig.params.get("board_size");
        _cpoints = (Integer)gconfig.params.get("command_points");

        HashMap<Coords,Planet> planets = new HashMap<Coords,Planet>();

        // place the players' starting planets
        int players = getPlayerCount();
        int[] sx = { 1, size-1, 1, size-1 }, sy = { 1, size-1, size-1, 1 };
        for (int ii = 0; ii < getPlayerCount(); ii++) {
            Planet p = new Planet(sx[ii], sy[ii], MIN_PLANET_SIZE, ii);
            planets.put(p.coords, p);
        }

        // randomly distribute independent planets around the board
        int iplanets = 2 * size;
        for (int ii = 0; ii < iplanets; ii++) {
            int psize = RandomUtil.getInt(MAX_PLANET_SIZE - MIN_PLANET_SIZE) +
                MIN_PLANET_SIZE;
            Planet planet = null;
            for (int tt = 0; tt < 25; tt++) {
                Planet p = new Planet(RandomUtil.getInt(size-1)+1,
                                      RandomUtil.getInt(size-1)+1, psize, -1);
                if (!planets.containsKey(p.coords)) {
                    planet = p;
                    break;
                }
            }
            if (planet == null) {
                log.warning("Failed to place planet after 25 attempts " +
                            "[size=" + size + ", pidx=" + ii + "].");
                break;
            }
            planets.put(planet.coords, planet);
        }

        // publish this list of planets in the game object
        _scrobj.setPlanets(new DSet(planets.values().iterator()));

        // create a ship at each player's starting planet
        HashSet<Ship> ships = new HashSet<Ship>();
        for (int ii = 0; ii < getPlayerCount(); ii++) {
            ships.add(new Ship(sx[ii], sy[ii], MIN_PLANET_SIZE, ii));
        }
        _scrobj.setShips(new DSet(ships.iterator()));
    }

    /** A casted reference to our game object. */
    protected ScrackObject _scrobj;

    /** The number of command points awarded every "turn". */
    protected int _cpoints;
}
