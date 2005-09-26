//
// $Id$

package com.samskivert.scrack.data;

import com.threerings.presents.dobj.DSet;

import com.threerings.parlor.game.data.GameObject;

/**
 * Maintains the distributed state for the game.
 */
public class ScrackObject extends GameObject
{
    // AUTO-GENERATED: FIELDS START
    /** The field name of the <code>service</code> field. */
    public static final String SERVICE = "service";

    /** The field name of the <code>planets</code> field. */
    public static final String PLANETS = "planets";

    /** The field name of the <code>ships</code> field. */
    public static final String SHIPS = "ships";

    /** The field name of the <code>crack</code> field. */
    public static final String CRACK = "crack";

    /** The field name of the <code>points</code> field. */
    public static final String POINTS = "points";
    // AUTO-GENERATED: FIELDS END

    /** Provides the mechanism for the client to make requests of the
     * server. */
    public ScrackMarshaller service;

    /** Contains all of the planets in the game. */
    public DSet planets;

    /** Contains all of the ships in play. */
    public DSet ships;

    /** Contains the quantity of crack possessed by each player. */
    public int[] crack;

    /** Contains the number of command points possessed by each player. */
    public int[] points;

    // AUTO-GENERATED: METHODS START
    /**
     * Requests that the <code>service</code> field be set to the
     * specified value. The local value will be updated immediately and an
     * event will be propagated through the system to notify all listeners
     * that the attribute did change. Proxied copies of this object (on
     * clients) will apply the value change when they received the
     * attribute changed notification.
     */
    public void setService (ScrackMarshaller value)
    {
        ScrackMarshaller ovalue = this.service;
        requestAttributeChange(
            SERVICE, value, ovalue);
        this.service = value;
    }

    /**
     * Requests that the specified entry be added to the
     * <code>planets</code> set. The set will not change until the event is
     * actually propagated through the system.
     */
    public void addToPlanets (DSet.Entry elem)
    {
        requestEntryAdd(PLANETS, planets, elem);
    }

    /**
     * Requests that the entry matching the supplied key be removed from
     * the <code>planets</code> set. The set will not change until the
     * event is actually propagated through the system.
     */
    public void removeFromPlanets (Comparable key)
    {
        requestEntryRemove(PLANETS, planets, key);
    }

    /**
     * Requests that the specified entry be updated in the
     * <code>planets</code> set. The set will not change until the event is
     * actually propagated through the system.
     */
    public void updatePlanets (DSet.Entry elem)
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
    public void setPlanets (DSet value)
    {
        requestAttributeChange(PLANETS, value, this.planets);
        this.planets = (value == null) ? null : (DSet)value.clone();
    }

    /**
     * Requests that the specified entry be added to the
     * <code>ships</code> set. The set will not change until the event is
     * actually propagated through the system.
     */
    public void addToShips (DSet.Entry elem)
    {
        requestEntryAdd(SHIPS, ships, elem);
    }

    /**
     * Requests that the entry matching the supplied key be removed from
     * the <code>ships</code> set. The set will not change until the
     * event is actually propagated through the system.
     */
    public void removeFromShips (Comparable key)
    {
        requestEntryRemove(SHIPS, ships, key);
    }

    /**
     * Requests that the specified entry be updated in the
     * <code>ships</code> set. The set will not change until the event is
     * actually propagated through the system.
     */
    public void updateShips (DSet.Entry elem)
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
    public void setShips (DSet value)
    {
        requestAttributeChange(SHIPS, value, this.ships);
        this.ships = (value == null) ? null : (DSet)value.clone();
    }

    /**
     * Requests that the <code>crack</code> field be set to the
     * specified value. The local value will be updated immediately and an
     * event will be propagated through the system to notify all listeners
     * that the attribute did change. Proxied copies of this object (on
     * clients) will apply the value change when they received the
     * attribute changed notification.
     */
    public void setCrack (int[] value)
    {
        int[] ovalue = this.crack;
        requestAttributeChange(
            CRACK, value, ovalue);
        this.crack = (value == null) ? null : (int[])value.clone();
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
    public void setCrackAt (int value, int index)
    {
        int ovalue = this.crack[index];
        requestElementUpdate(
            CRACK, index, new Integer(value), new Integer(ovalue));
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
    public void setPoints (int[] value)
    {
        int[] ovalue = this.points;
        requestAttributeChange(
            POINTS, value, ovalue);
        this.points = (value == null) ? null : (int[])value.clone();
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
    public void setPointsAt (int value, int index)
    {
        int ovalue = this.points[index];
        requestElementUpdate(
            POINTS, index, new Integer(value), new Integer(ovalue));
        this.points[index] = value;
    }
    // AUTO-GENERATED: METHODS END
}
