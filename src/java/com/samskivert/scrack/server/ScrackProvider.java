//
// $Id$

package com.samskivert.scrack.server;

import com.samskivert.scrack.client.ScrackService;
import com.threerings.presents.client.Client;
import com.threerings.presents.data.ClientObject;
import com.threerings.presents.server.InvocationException;
import com.threerings.presents.server.InvocationProvider;

/**
 * Defines the server-side of the {@link ScrackService}.
 */
public interface ScrackProvider extends InvocationProvider
{
    /**
     * Handles a {@link ScrackService#buildShip} request.
     */
    public void buildShip (ClientObject caller, int arg1);

    /**
     * Handles a {@link ScrackService#moveShip} request.
     */
    public void moveShip (ClientObject caller, int arg1, int arg2);
}
