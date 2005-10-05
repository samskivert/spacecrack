//
// $Id$

package com.samskivert.scrack.server;

import java.util.ArrayList;
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
import com.samskivert.scrack.data.ScrackCodes;
import com.samskivert.scrack.data.ScrackMarshaller;
import com.samskivert.scrack.data.ScrackObject;
import com.samskivert.scrack.data.Ship;
import com.samskivert.util.ArrayIntSet;

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

        // make sure the destination planet neighbors the planet at the ships
        // current coordinates
        Planet splanet = _scrobj.locatePlanet(ship.coords);
        if (splanet == null || !splanet.isNeighbor(planet)) {
            log.warning("Requested to move from invalid planet " +
                        "[game=" + where() + ", who=" + user.who() +
                        ", ship=" + ship + ", src=" + splanet +
                        ", dest=" + planet + "].");
            return;
        }

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
                // destroy the defending ship
                _scrobj.removeFromShips(target.getKey());

                // move onto the planet
                ship.size -= (target.size/2);
                ship.coords = planet.coords;
                _scrobj.updateShips(ship);

                // and assume ownership of it
                planet.owner = pidx;
                _scrobj.updatePlanets(planet);

            } else {
                // destroy the attacking ship
                _scrobj.removeFromShips(ship.getKey());

                // shrink the defender
                target.size -= (ship.size/2);
                _scrobj.updateShips(target);
            }

            // TODO: check for players being knocked out
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
        _cpoints = (Integer)gconfig.params.get("command_points");
        _efficiency = (Integer)gconfig.params.get("crack_pct")/100f;
        _interiorBonus = (Integer)gconfig.params.get("interior_bonus")/100f;

        // start the turn counter with the total number of turns
        _scrobj.setTurnsLeft((Integer)gconfig.params.get("turns"));

        // configure the per-player arrays
        int players = getPlayerCount();
        _scrobj.setCrack(new int[players]);
        _scrobj.setFinished(new boolean[players]);
        int[] cpoints = new int[players];
        Arrays.fill(cpoints, _cpoints);
        _scrobj.setPoints(cpoints);

        // populate the board with planets
        HashMap<Coords,Planet> planets = new HashMap<Coords,Planet>();

        // determine the board size
        int size = (Integer)gconfig.params.get("board_size");
        // width must be 1 modulo 4; height must be 1 modulo 2
        int width = 4 * (size/4) + 1;
        int height = 2 * (size/2) + 1;

        // create a (quasi-)hexagonal arrangement of planets
        for (int yy = 0; yy < height; yy++) {
            for (int xx = (yy%2 == 0) ? 2 : 0; xx < width; xx += 4) {
                Planet p = new Planet(xx+1, yy+1, MIN_PLANET_SIZE+1, -1);
                planets.put(p.coords, p);
            }
        }

        // assign the players' starting planets
        Coords coords = new Coords();
        int[] sx = { 1, width, 1, width };
        int[] sy = { 2, height-1, height-1, 2 };
        for (int ii = 0; ii < getPlayerCount(); ii++) {
            coords.x = sx[ii];
            coords.y = sy[ii];
            planets.get(coords).owner = ii;
        }

        // fill in the neighbors array for the planets and while doing so,
        // randomly shift the planetary mass around a bit
        ArrayIntSet nset = new ArrayIntSet();
        ArrayList<Planet> nlist = new ArrayList<Planet>();
        for (Planet planet : planets.values()) {
            // determine which of our neighbors exist
            for (int ii = 0; ii < NDX.length; ii++) {
                coords.x = planet.coords.x + NDX[ii];
                coords.y = planet.coords.y + NDY[ii];
                Planet n = planets.get(coords);
                if (n != null) {
                    nset.add(n.planetId);
                    if (n.owner == -1) {
                        nlist.add(n);
                    }
                }
            }
            planet.neighbors = nset.toIntArray();

            if (planet.owner == -1) {
                // select one of our (unowned) neighbors at random and give
                // them one of our size points
                Planet n = (Planet)RandomUtil.pickRandom(nlist);
                n.size += 1;
                planet.size -= 1;
            }

            nlist.clear();
            nset.clear();
        }

        // publish this list of planets in the game object
        _scrobj.setPlanets(new DSet(planets.values().iterator()));

        // create a ship at each player's starting planet
        HashSet<Ship> ships = new HashSet<Ship>();
        for (int ii = 0; ii < getPlayerCount(); ii++) {
            ships.add(new Ship(sx[ii], sy[ii], MIN_PLANET_SIZE+1, ii));
        }
        _scrobj.setShips(new DSet(ships.iterator()));
    }

    @Override // documentation inherited
    protected void assignWinners (boolean[] winners)
    {
        // determine who has the highest planet count and amassed crack
        ArrayIntSet winidxs = new ArrayIntSet();
        int maxp = 0, maxc = 0;
        for (int ii = 0; ii < winners.length; ii++) {
            int pcount = _scrobj.countPlanets(ii);
            if (pcount > maxp) {
                maxp = pcount;
                maxc = _scrobj.crack[ii];
                winidxs.add(ii);
            } else if (pcount == maxp && _scrobj.crack[ii] >= maxc) {
                if (_scrobj.crack[ii] > maxc) {
                    winidxs.clear();
                }
                winidxs.add(ii);
            }
        }
        Arrays.fill(winners, false);
        for (int ii = 0; ii < winidxs.size(); ii++) {
            winners[winidxs.get(ii)] = true;
        }
    }

    @Override // documentation inherited
    protected void gameDidEnd ()
    {
        super.gameDidEnd();
        systemMessage(ScrackCodes.SCRACK_MSGS, "m.game_over");
    }

    protected void useCommandPoint (int pidx)
    {
        // use up a command point
        _scrobj.setPointsAt(_scrobj.points[pidx]-1, pidx);

        // if they are out of command points, auto-end their turn for them
        if (_scrobj.points[pidx] == 0) {
            _scrobj.setFinishedAt(true, pidx);
            // this will trigger a call to maybeEndTurn()
        } else {
            // maybe end the turn due to other wackiness
            maybeEndTurn("useCommandPoint");
        }
    }

    protected void maybeEndTurn (String where)
    {
        // make sure everyone is finished with their turn
        for (int ii = 0; ii < _scrobj.finished.length; ii++) {
            if (!_scrobj.finished[ii] &&
                (_scrobj.canBuild(ii) || _scrobj.countShips(ii) > 0)) {
                return;
            }
        }

        // award crack for all owned planets
        float[] crack = new float[_scrobj.crack.length];
        for (Iterator iter = _scrobj.planets.iterator(); iter.hasNext(); ) {
            Planet planet = (Planet)iter.next();
            if (planet.owner != -1) {
                float efficiency = _efficiency;
                if (_scrobj.isInterior(planet)) {
                    efficiency += _interiorBonus;
                }
                crack[planet.owner] +=  efficiency * planet.size;
            }
        }
        for (int ii = 0; ii < crack.length; ii++) {
            _scrobj.crack[ii] += (int)crack[ii];
        }
        _scrobj.setCrack(_scrobj.crack);

        // otherwise start the next turn
        _scrobj.setTurnsLeft(_scrobj.turnsLeft-1);

        // if that was the final turn, end the game
        if (_scrobj.turnsLeft == 0) {
            endGame();
            return;
        }

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
                maybeEndTurn("finishedUpdated");
            }
        }
    };

    /** A casted reference to our game object. */
    protected ScrackObject _scrobj;

    /** The number of command points awarded every turn. */
    protected int _cpoints;

    /** The "efficiency" of the planets; what percentage of their size they
     * generate in crack every turn. */
    protected float _efficiency;

    /** The efficiency bonus for interior planets. */
    protected float _interiorBonus;

    protected static final int[] NDX = { -2, 0, 2, 2, 0, -2 };
    protected static final int[] NDY = { -1, -2, -1, 1, 2, 1 };
}
