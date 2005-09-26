//
// $Id$

package com.samskivert.scrack.client;

import com.threerings.presents.client.Client;
import com.threerings.presents.client.InvocationService;

import com.samskivert.scrack.data.Coords;

/**
 * Defines the set of requests that the client can make to the server.
 */
public interface ScrackService extends InvocationService
{
    /**
     * Requests to move the ship at the specified coordinates to the
     * specified destination coordinates.
     */
    public void moveShip (Client client, Coords current, Coords dest);

    /**
     * Requests to build a ship on the planet at the specified
     * coordinates.
     */
    public void buildShip (Client client, Coords planet);
}
