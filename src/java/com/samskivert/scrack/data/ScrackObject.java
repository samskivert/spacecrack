//
// $Id$

package com.samskivert.scrack.data;

import java.util.HashMap;
import java.util.HashSet;
import javax.annotation.Generated;

import com.samskivert.util.ArrayIntSet;
import com.samskivert.util.Tuple;
import com.threerings.media.util.MathUtil;

import com.threerings.presents.dobj.DSet;

import com.threerings.parlor.game.data.GameObject;

/**
 * Maintains the distributed state for the game.
 */
public class ScrackObject extends GameObject
{
    // AUTO-GENERATED: FIELDS START
    /** The field name of the <code>service</code> field. */
    @Generated(value={"com.threerings.presents.tools.GenDObjectTask"})
    public static final String SERVICE = "service";

    /** The field name of the <code>turnsLeft</code> field. */
    @Generated(value={"com.threerings.presents.tools.GenDObjectTask"})
    public static final String TURNS_LEFT = "turnsLeft";

    /** The field name of the <code>planets</code> field. */
    @Generated(value={"com.threerings.presents.tools.GenDObjectTask"})
    public static final String PLANETS = "planets";

    /** The field name of the <code>ships</code> field. */
    @Generated(value={"com.threerings.presents.tools.GenDObjectTask"})
    public static final String SHIPS = "ships";

    /** The field name of the <code>crack</code> field. */
    @Generated(value={"com.threerings.presents.tools.GenDObjectTask"})
    public static final String CRACK = "crack";

    /** The field name of the <code>points</code> field. */
    @Generated(value={"com.threerings.presents.tools.GenDObjectTask"})
    public static final String POINTS = "points";

    /** The field name of the <code>finished</code> field. */
    @Generated(value={"com.threerings.presents.tools.GenDObjectTask"})
    public static final String FINISHED = "finished";
    // AUTO-GENERATED: FIELDS END

    /** Provides the mechanism for the client to make requests of the
     * server. */
    public ScrackMarshaller service;

    /** The number of turns remaining. */
    public int turnsLeft;

    /** Contains all of the planets in the game. */
    public DSet<Planet> planets = new DSet<Planet>();

    /** Contains all of the ships in play. */
    public DSet<Ship> ships = new DSet<Ship>();

    /** Contains the quantity of crack possessed by each player. */
    public int[] crack;

    /** Contains the number of command points possessed by each player. */
    public int[] points;

    /** Indicates whether each player is finished with their turn. */
    public boolean[] finished;

    /**
     * Returns the ship at the specified coordinates or null if no ship is
     * docked at those coordinates.
     */
    public Ship locateShip (Coords coords)
    {
        for (Ship ship : ships) {
            if (ship.coords.equals(coords)) {
                return ship;
            }
        }
        return null;
    }

    /**
     * Returns the planet at the specified coordinates or null if no planet is
     * docked at those coordinates.
     */
    public Planet locatePlanet (Coords coords)
    {
        for (Planet planet : planets) {
            if (planet.coords.equals(coords)) {
                return planet;
            }
        }
        return null;
    }

    /**
     * Returns the number of planets owned by the player with the specified
     * index.
     */
    public int countPlanets (int owner)
    {
        int pcount = 0;
        for (Planet planet : planets) {
            if (planet.owner == owner) {
                pcount++;
            }
        }
        return pcount;
    }

    /**
     * Returns the number of ships owned by the player with the specified
     * index.
     */
    public int countShips (int owner)
    {
        int scount = 0;
        for (Ship ship : ships) {
            if (ship.owner == owner) {
                scount++;
            }
        }
        return scount;
    }

    /**
     * Returns the size of the smallest planet owned by the specified player or
     * {@link ScrackCodes#MAX_PLANET_SIZE} if they own no planets.
     */
    public int smallestPlanet (int owner)
    {
        int smallest = ScrackCodes.MAX_PLANET_SIZE;
        for (Planet planet : planets) {
            if (planet.owner == owner && planet.size < smallest) {
                smallest = planet.size;
            }
        }
        return smallest;
    }

    /**
     * Returns true if the specified player can build on at least one of their
     * planets.
     */
    public boolean canBuild (int owner)
    {
        for (Planet planet : planets) {
            if (planet.owner == owner && crack[owner] >= planet.size) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if the specified planet is surrounded by friendly planets.
     */
    public boolean isInterior (Planet planet)
    {
        for (int ii = 0; ii < planet.neighbors.length; ii++) {
            Planet n = planets.get(planet.neighbors[ii]);
            if (n.owner != planet.owner) {
                return false;
            }
        }
        // must not be on the edge of the board
        return (planet.neighbors.length == 6);
    }

    protected transient HashMap<Integer,ArrayIntSet> _neighbors =
        new HashMap<Integer,ArrayIntSet>();

    // AUTO-GENERATED: METHODS START
    /**
     * Requests that the <code>service</code> field be set to the
     * specified value. The local value will be updated immediately and an
     * event will be propagated through the system to notify all listeners
     * that the attribute did change. Proxied copies of this object (on
     * clients) will apply the value change when they received the
     * attribute changed notification.
     */
    @Generated(value={"com.threerings.presents.tools.GenDObjectTask"})
    public void setService (ScrackMarshaller value)
    {
        ScrackMarshaller ovalue = this.service;
        requestAttributeChange(
            SERVICE, value, ovalue);
        this.service = value;
    }

    /**
     * Requests that the <code>turnsLeft</code> field be set to the
     * specified value. The local value will be updated immediately and an
     * event will be propagated through the system to notify all listeners
     * that the attribute did change. Proxied copies of this object (on
     * clients) will apply the value change when they received the
     * attribute changed notification.
     */
    @Generated(value={"com.threerings.presents.tools.GenDObjectTask"})
    public void setTurnsLeft (int value)
    {
        int ovalue = this.turnsLeft;
        requestAttributeChange(
            TURNS_LEFT, Integer.valueOf(value), Integer.valueOf(ovalue));
        this.turnsLeft = value;
    }

    /**
     * Requests that the specified entry be added to the
     * <code>planets</code> set. The set will not change until the event is
     * actually propagated through the system.
     */
    @Generated(value={"com.threerings.presents.tools.GenDObjectTask"})
    public void addToPlanets (Planet elem)
    {
        requestEntryAdd(PLANETS, planets, elem);
    }

    /**
     * Requests that the entry matching the supplied key be removed from
     * the <code>planets</code> set. The set will not change until the
     * event is actually propagated through the system.
     */
    @Generated(value={"com.threerings.presents.tools.GenDObjectTask"})
    public void removeFromPlanets (Comparable<?> key)
    {
        requestEntryRemove(PLANETS, planets, key);
    }

    /**
     * Requests that the specified entry be updated in the
     * <code>planets</code> set. The set will not change until the event is
     * actually propagated through the system.
     */
    @Generated(value={"com.threerings.presents.tools.GenDObjectTask"})
    public void updatePlanets (Planet elem)
    {
        requestEntryUpdate(PLANETS, planets, elem);
    }

    /**
     * Requests that the <code>planets</code> field be set to the
     * specified value. Generally one only adds, updates and removes
     * entries of a distributed set, but certain situations call for a
     * complete replacement of the set value. The local value will be
     * updated immediately and an event will be propagated through the
     * system to notify all listeners that the attribute did
     * change. Proxied copies of this object (on clients) will apply the
     * value change when they received the attribute changed notification.
     */
    @Generated(value={"com.threerings.presents.tools.GenDObjectTask"})
    public void setPlanets (DSet<Planet> value)
    {
        requestAttributeChange(PLANETS, value, this.planets);
        DSet<Planet> clone = (value == null) ? null : value.clone();
        this.planets = clone;
    }

    /**
     * Requests that the specified entry be added to the
     * <code>ships</code> set. The set will not change until the event is
     * actually propagated through the system.
     */
    @Generated(value={"com.threerings.presents.tools.GenDObjectTask"})
    public void addToShips (Ship elem)
    {
        requestEntryAdd(SHIPS, ships, elem);
    }

    /**
     * Requests that the entry matching the supplied key be removed from
     * the <code>ships</code> set. The set will not change until the
     * event is actually propagated through the system.
     */
    @Generated(value={"com.threerings.presents.tools.GenDObjectTask"})
    public void removeFromShips (Comparable<?> key)
    {
        requestEntryRemove(SHIPS, ships, key);
    }

    /**
     * Requests that the specified entry be updated in the
     * <code>ships</code> set. The set will not change until the event is
     * actually propagated through the system.
     */
    @Generated(value={"com.threerings.presents.tools.GenDObjectTask"})
    public void updateShips (Ship elem)
    {
        requestEntryUpdate(SHIPS, ships, elem);
    }

    /**
     * Requests that the <code>ships</code> field be set to the
     * specified value. Generally one only adds, updates and removes
     * entries of a distributed set, but certain situations call for a
     * complete replacement of the set value. The local value will be
     * updated immediately and an event will be propagated through the
     * system to notify all listeners that the attribute did
     * change. Proxied copies of this object (on clients) will apply the
     * value change when they received the attribute changed notification.
     */
    @Generated(value={"com.threerings.presents.tools.GenDObjectTask"})
    public void setShips (DSet<Ship> value)
    {
        requestAttributeChange(SHIPS, value, this.ships);
        DSet<Ship> clone = (value == null) ? null : value.clone();
        this.ships = clone;
    }

    /**
     * Requests that the <code>crack</code> field be set to the
     * specified value. The local value will be updated immediately and an
     * event will be propagated through the system to notify all listeners
     * that the attribute did change. Proxied copies of this object (on
     * clients) will apply the value change when they received the
     * attribute changed notification.
     */
    @Generated(value={"com.threerings.presents.tools.GenDObjectTask"})
    public void setCrack (int[] value)
    {
        int[] ovalue = this.crack;
        requestAttributeChange(
            CRACK, value, ovalue);
        this.crack = (value == null) ? null : value.clone();
    }

    /**
     * Requests that the <code>index</code>th element of
     * <code>crack</code> field be set to the specified value.
     * The local value will be updated immediately and an event will be
     * propagated through the system to notify all listeners that the
     * attribute did change. Proxied copies of this object (on clients)
     * will apply the value change when they received the attribute
     * changed notification.
     */
    @Generated(value={"com.threerings.presents.tools.GenDObjectTask"})
    public void setCrackAt (int value, int index)
    {
        int ovalue = this.crack[index];
        requestElementUpdate(
            CRACK, index, Integer.valueOf(value), Integer.valueOf(ovalue));
        this.crack[index] = value;
    }

    /**
     * Requests that the <code>points</code> field be set to the
     * specified value. The local value will be updated immediately and an
     * event will be propagated through the system to notify all listeners
     * that the attribute did change. Proxied copies of this object (on
     * clients) will apply the value change when they received the
     * attribute changed notification.
     */
    @Generated(value={"com.threerings.presents.tools.GenDObjectTask"})
    public void setPoints (int[] value)
    {
        int[] ovalue = this.points;
        requestAttributeChange(
            POINTS, value, ovalue);
        this.points = (value == null) ? null : value.clone();
    }

    /**
     * Requests that the <code>index</code>th element of
     * <code>points</code> field be set to the specified value.
     * The local value will be updated immediately and an event will be
     * propagated through the system to notify all listeners that the
     * attribute did change. Proxied copies of this object (on clients)
     * will apply the value change when they received the attribute
     * changed notification.
     */
    @Generated(value={"com.threerings.presents.tools.GenDObjectTask"})
    public void setPointsAt (int value, int index)
    {
        int ovalue = this.points[index];
        requestElementUpdate(
            POINTS, index, Integer.valueOf(value), Integer.valueOf(ovalue));
        this.points[index] = value;
    }

    /**
     * Requests that the <code>finished</code> field be set to the
     * specified value. The local value will be updated immediately and an
     * event will be propagated through the system to notify all listeners
     * that the attribute did change. Proxied copies of this object (on
     * clients) will apply the value change when they received the
     * attribute changed notification.
     */
    @Generated(value={"com.threerings.presents.tools.GenDObjectTask"})
    public void setFinished (boolean[] value)
    {
        boolean[] ovalue = this.finished;
        requestAttributeChange(
            FINISHED, value, ovalue);
        this.finished = (value == null) ? null : value.clone();
    }

    /**
     * Requests that the <code>index</code>th element of
     * <code>finished</code> field be set to the specified value.
     * The local value will be updated immediately and an event will be
     * propagated through the system to notify all listeners that the
     * attribute did change. Proxied copies of this object (on clients)
     * will apply the value change when they received the attribute
     * changed notification.
     */
    @Generated(value={"com.threerings.presents.tools.GenDObjectTask"})
    public void setFinishedAt (boolean value, int index)
    {
        boolean ovalue = this.finished[index];
        requestElementUpdate(
            FINISHED, index, Boolean.valueOf(value), Boolean.valueOf(ovalue));
        this.finished[index] = value;
    }
    // AUTO-GENERATED: METHODS END
}
