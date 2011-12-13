//
// $Id$

package com.samskivert.scrack.data;

import javax.annotation.Generated;

import com.samskivert.scrack.client.ScrackService;

import com.threerings.presents.data.InvocationMarshaller;

/**
 * Provides the implementation of the {@link ScrackService} interface
 * that marshalls the arguments and delivers the request to the provider
 * on the server. Also provides an implementation of the response listener
 * interfaces that marshall the response arguments and deliver them back
 * to the requesting client.
 */
@Generated(value={"com.threerings.presents.tools.GenServiceTask"},
           comments="Derived from ScrackService.java.")
public class ScrackMarshaller extends InvocationMarshaller
    implements ScrackService
{
    /** The method id used to dispatch {@link #buildShip} requests. */
    public static final int BUILD_SHIP = 1;

    // from interface ScrackService
    public void buildShip (int arg1)
    {
        sendRequest(BUILD_SHIP, new Object[] {
            Integer.valueOf(arg1)
        });
    }

    /** The method id used to dispatch {@link #moveShip} requests. */
    public static final int MOVE_SHIP = 2;

    // from interface ScrackService
    public void moveShip (int arg1, int arg2)
    {
        sendRequest(MOVE_SHIP, new Object[] {
            Integer.valueOf(arg1), Integer.valueOf(arg2)
        });
    }
}
