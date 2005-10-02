//
// $Id$

package com.samskivert.scrack.data;

import com.samskivert.scrack.client.ScrackService;
import com.threerings.presents.client.Client;
import com.threerings.presents.data.InvocationMarshaller;
import com.threerings.presents.dobj.InvocationResponseEvent;

/**
 * Provides the implementation of the {@link ScrackService} interface
 * that marshalls the arguments and delivers the request to the provider
 * on the server. Also provides an implementation of the response listener
 * interfaces that marshall the response arguments and deliver them back
 * to the requesting client.
 */
public class ScrackMarshaller extends InvocationMarshaller
    implements ScrackService
{
    /** The method id used to dispatch {@link #buildShip} requests. */
    public static final int BUILD_SHIP = 1;

    // documentation inherited from interface
    public void buildShip (Client arg1, int arg2)
    {
        sendRequest(arg1, BUILD_SHIP, new Object[] {
            new Integer(arg2)
        });
    }

    /** The method id used to dispatch {@link #moveShip} requests. */
    public static final int MOVE_SHIP = 2;

    // documentation inherited from interface
    public void moveShip (Client arg1, int arg2, int arg3)
    {
        sendRequest(arg1, MOVE_SHIP, new Object[] {
            new Integer(arg2), new Integer(arg3)
        });
    }

}
