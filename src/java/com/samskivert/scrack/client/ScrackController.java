//
// $Id$

package com.samskivert.scrack.client;

import com.threerings.crowd.client.PlaceView;
import com.threerings.crowd.util.CrowdContext;

import com.threerings.parlor.game.client.GameController;
import com.threerings.toybox.data.ToyBoxGameConfig;
import com.threerings.toybox.util.ToyBoxContext;

/**
 * Coordinates the client side of the game interface.
 */
public class ScrackController extends GameController
{
    /** The name of the command posted by the "Back to lobby" button in
     * the side bar. */
    public static final String BACK_TO_LOBBY = "BackToLobby";

    /**
     * This method is called automatically by the controller system when
     * the player clicks the button that was configured with the {@link
     * #BACK_TO_LOBBY} action and the special {@link #DISPATCHER} which
     * does the necessary reflection magic.
     */
    public void handleBackToLobby (Object source)
    {
        _ctx.getLocationDirector().moveBack();
    }

    @Override // documentation inherited
    protected void didInit ()
    {
        super.didInit();

        // cast our context
        _ctx = (ToyBoxContext)super._ctx;
    }

    @Override // documentation inherited
    protected PlaceView createPlaceView (CrowdContext ctx)
    {
        _panel = new ScrackPanel(
            (ToyBoxContext)ctx, (ToyBoxGameConfig)_config, this);
        return _panel;
    }

    @Override // documentation inherited
    protected void gameDidStart ()
    {
        super.gameDidStart();
        _panel.view.gameDidStart();
    }

//     @Override // documentation inherited
//     protected void gameDidEnd ()
//     {
//         super.gameDidEnd();
//         _panel.displayStatus("m.game_over");
//     }

    /** Provides access to various client services. */
    protected ToyBoxContext _ctx;

    /** Our game panel. */
    protected ScrackPanel _panel;
}
