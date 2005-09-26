//
// $Id$

package com.samskivert.scrack.server;

import com.threerings.presents.data.ClientObject;
import com.threerings.presents.server.InvocationProvider;

import com.samskivert.scrack.client.ScrackService;
import com.samskivert.scrack.data.Coords;

/**
 * Defines the server side of the {@link ScrackService}.
 */
public interface ScrackProvider extends InvocationProvider
{
    /**
     * Handles a {@link ScrackService#moveShip} request.
     */
    public void moveShip (ClientObject caller, Coords current, Coords dest);

    /**
     * Handles a {@link ScrackService#buildShip} request.
     */
    public void buildShip (ClientObject caller, Coords planet);
}
