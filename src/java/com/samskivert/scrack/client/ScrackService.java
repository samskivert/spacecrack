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
     * Requests to move the specified ship to the specified destination planet.
     */
    public void moveShip (Client client, int shipId, int planetId);

    /**
     * Requests to build a ship on the specified planet.
     */
    public void buildShip (Client client, int planetId);
}
