//
// $Id$

package com.samskivert.scrack.client;

import com.threerings.media.VirtualMediaPanel;

import com.threerings.toybox.util.ToyBoxContext;

/**
 * Displays the main space interface.
 */
public class ScrackBoardView extends VirtualMediaPanel
{
    public ScrackBoardView (ToyBoxContext ctx)
    {
        super(ctx.getFrameManager());
        _ctx = ctx;
    }

    protected ToyBoxContext _ctx;
}
