//
// $Id$

package com.samskivert.scrack.server;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import com.threerings.util.RandomUtil;

import com.threerings.presents.data.ClientObject;
import com.threerings.presents.dobj.DSet;
import com.threerings.presents.dobj.ElementUpdateListener;
import com.threerings.presents.dobj.ElementUpdatedEvent;
import com.threerings.presents.server.PresentsServer;

import com.threerings.crowd.data.BodyObject;

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
    public void moveShip (ClientObject caller, int shipId, int planetId)
    {
        BodyObject user = (BodyObject)caller;
        int pidx = getPlayerIndex(user.username);

        Ship ship = (Ship)_scrobj.ships.get(shipId);
        if (ship == null || ship.owner != pidx) {
            log.warning("Requested to move bogus ship [game=" + where() +
                        ", who=" + user.who() + ", shipId=" + shipId +
                        ", ship=" + ship + "].");
            return;
        }

        Planet planet = (Planet)_scrobj.planets.get(planetId);
        if (planet == null) {
            log.warning("Requested to move to non-existent planet " +
                        "[game=" + where() + ", who=" + user.who() +
                        ", ship=" + ship + ", planetId=" + planetId + "].");
            return;
        }

        // no NOOPing
        if (planet.coords.equals(ship.coords)) {
            return;
        }

        // TODO: make sure the destination planet neighbors the planet at the
        // ships current coordinates

        // make sure the caller has sufficient command points
        if (_scrobj.points[pidx] <= 0) {
            return;
        }

        // there are three cases...
        Ship target = _scrobj.locateShip(planet.coords);

        // if there's no ship there, we occupy the planet
        if (target == null) {
            // assume ownership of the planet if it's not already ours
            if (planet.owner != pidx) {
                planet.owner = pidx;
                _scrobj.updatePlanets(planet);
            }

            // and update the ship's coordinates
            ship.coords = planet.coords;
            _scrobj.updateShips(ship);

        // if it's our ship, we merge with it
        } else if (target.owner == pidx) {
            _scrobj.removeFromShips(ship.getKey());
            target.size += ship.size;
            _scrobj.updateShips(target);

        // otherwise we attack it
        } else {
            if (ship.size == target.size) {
                // mutually assured destruction
                _scrobj.removeFromShips(ship.getKey());
                _scrobj.removeFromShips(target.getKey());
            } else if (ship.size > target.size) {
                _scrobj.removeFromShips(target.getKey());
                ship.size -= (ship.size/2);
                _scrobj.updateShips(ship);
            } else {
                _scrobj.removeFromShips(ship.getKey());
                target.size -= (target.size/2);
                _scrobj.updateShips(target);
            }
        }

        useCommandPoint(pidx);
    }

    // documentation inherited from interface ScrackProvider
    public void buildShip (ClientObject caller, int planetId)
    {
        BodyObject user = (BodyObject)caller;
        int pidx = getPlayerIndex(user.username);

        Planet planet = (Planet)_scrobj.planets.get(planetId);
        if (planet == null || planet.owner != pidx) {
            log.warning("Requested to build ship on invalid planet " +
                        "[game=" + where() + ", who=" + user.who() +
                        ", planetId=" + planetId + ", planet=" + planet + "].");
            return;
        }

        // make sure the caller has sufficient command points
        if (_scrobj.points[pidx] <= 0) {
            return;
        }

        // make sure the caller has sufficient crack
        if (_scrobj.crack[pidx] < planet.size) {
            return;
        }

        // make sure there's not a ship already on the planet or just join with
        // the ship there?

        // create the ship and publish it to the game object
        _scrobj.addToShips(
            new Ship(planet.coords.x, planet.coords.y, planet.size, pidx));

        // deduct the requisite crack
        _scrobj.setCrackAt(_scrobj.crack[pidx] - planet.size, pidx);

        useCommandPoint(pidx);
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
        _scrobj.addListener(_turnEnder);

        // fill in blank defaults for our game data
        int players = getPlayerCount();
        _scrobj.setCrack(new int[players]);
        _scrobj.setFinished(new boolean[players]);
        _scrobj.setPoints(new int[players]);
    }

    @Override // documentation inherited
    protected void gameWillStart ()
    {
        super.gameWillStart();

        ToyBoxGameConfig gconfig = (ToyBoxGameConfig)_config;
        int size = (Integer)gconfig.params.get("board_size");
        _cpoints = (Integer)gconfig.params.get("command_points");

        // configure the per-player arrays
        int players = getPlayerCount();
        _scrobj.setCrack(new int[players]);
        _scrobj.setFinished(new boolean[players]);
        int[] cpoints = new int[players];
        Arrays.fill(cpoints, _cpoints);
        _scrobj.setPoints(cpoints);

        HashMap<Coords,Planet> planets = new HashMap<Coords,Planet>();

        // place the players' starting planets
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

    protected void useCommandPoint (int pidx)
    {
        // use up a command point
        _scrobj.setPointsAt(_scrobj.points[pidx]-1, pidx);

        // if they are out of command points, auto-end their turn for them
        if (_scrobj.points[pidx] == 0) {
            _scrobj.setFinishedAt(true, pidx);
        }
    }

    protected void maybeEndTurn ()
    {
        // make sure everyone is finished with their turn
        for (int ii = 0; ii < _scrobj.finished.length; ii++) {
            if (!_scrobj.finished[ii]) {
                return;
            }
        }

        // award crack for all owned planets
        for (Iterator iter = _scrobj.planets.iterator(); iter.hasNext(); ) {
            Planet planet = (Planet)iter.next();
            if (planet.owner != -1) {
                _scrobj.crack[planet.owner] += planet.size;
            }
        }
        _scrobj.setCrack(_scrobj.crack);

        // award command points to each player
        for (int ii = 0; ii < _scrobj.points.length; ii++) {
            _scrobj.points[ii] += _cpoints;
        }
        _scrobj.setPoints(_scrobj.points);

        // finally reset the "finished" array
        _scrobj.setFinished(new boolean[_scrobj.finished.length]);
    }

    protected ElementUpdateListener _turnEnder = new ElementUpdateListener() {
        public void elementUpdated (ElementUpdatedEvent event) {
            if (event.getName().equals(ScrackObject.FINISHED)) {
                maybeEndTurn();
            }
        }
    };

    /** A casted reference to our game object. */
    protected ScrackObject _scrobj;

    /** The number of command points awarded every "turn". */
    protected int _cpoints;
}
