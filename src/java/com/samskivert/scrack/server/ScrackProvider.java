//
// $Id$

package com.samskivert.scrack.server;

import javax.annotation.Generated;

import com.samskivert.scrack.client.ScrackService;

import com.threerings.presents.data.ClientObject;
import com.threerings.presents.server.InvocationProvider;

/**
 * Defines the server-side of the {@link ScrackService}.
 */
@Generated(value={"com.threerings.presents.tools.GenServiceTask"},
           comments="Derived from ScrackService.java.")
public interface ScrackProvider extends InvocationProvider
{
    /**
     * Handles a {@link ScrackService#buildShip} request.
     */
    void buildShip (ClientObject caller, int arg1);

    /**
     * Handles a {@link ScrackService#moveShip} request.
     */
    void moveShip (ClientObject caller, int arg1, int arg2);
}
