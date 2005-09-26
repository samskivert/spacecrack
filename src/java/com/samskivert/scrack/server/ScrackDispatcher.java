//
// $Id$

package com.samskivert.scrack.server;

import com.samskivert.scrack.client.ScrackService;
import com.samskivert.scrack.data.Coords;
import com.samskivert.scrack.data.ScrackMarshaller;
import com.threerings.presents.client.Client;
import com.threerings.presents.data.ClientObject;
import com.threerings.presents.data.InvocationMarshaller;
import com.threerings.presents.server.InvocationDispatcher;
import com.threerings.presents.server.InvocationException;

/**
 * Dispatches requests to the {@link ScrackProvider}.
 */
public class ScrackDispatcher extends InvocationDispatcher
{
    /**
     * Creates a dispatcher that may be registered to dispatch invocation
     * service requests for the specified provider.
     */
    public ScrackDispatcher (ScrackProvider provider)
    {
        this.provider = provider;
    }

    // documentation inherited
    public InvocationMarshaller createMarshaller ()
    {
        return new ScrackMarshaller();
    }

    // documentation inherited
    public void dispatchRequest (
        ClientObject source, int methodId, Object[] args)
        throws InvocationException
    {
        switch (methodId) {
        case ScrackMarshaller.BUILD_SHIP:
            ((ScrackProvider)provider).buildShip(
                source,
                (Coords)args[0]
            );
            return;

        case ScrackMarshaller.MOVE_SHIP:
            ((ScrackProvider)provider).moveShip(
                source,
                (Coords)args[0], (Coords)args[1]
            );
            return;

        default:
            super.dispatchRequest(source, methodId, args);
        }
    }
}
