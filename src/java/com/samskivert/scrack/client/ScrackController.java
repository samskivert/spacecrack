//
// $Id$

package com.samskivert.scrack.client;

import com.threerings.crowd.client.PlaceView;
import com.threerings.crowd.util.CrowdContext;

import com.threerings.parlor.game.client.GameController;
import com.threerings.toybox.data.ToyBoxGameConfig;
import com.threerings.toybox.util.ToyBoxContext;

import com.samskivert.scrack.data.Coords;
import com.samskivert.scrack.data.Planet;
import com.samskivert.scrack.data.ScrackObject;
import com.samskivert.scrack.data.Ship;

import static com.samskivert.scrack.Log.log;

/**
 * Coordinates the client side of the game interface.
 */
public class ScrackController extends GameController
{
    /** The name of the command posted by the "Back to lobby" button in
     * the side bar. */
    public static final String BACK_TO_LOBBY = "BackToLobby";

    /** A command posted by a planet when it is clicked. */
    public static final String PLANET_SELECTED = "PlanetSelected";

    /** A command posted by a ship when it is clicked. */
    public static final String SHIP_SELECTED = "ShipSelected";

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

    /**
     * This method is called when the player clicks on a planet.
     */
    public void handlePlanetSelected (Object source, PlanetSprite psprite)
    {
        // if we already have a ship selected, then the player wants to move
        // that ship to this planet; if we still have command points, do so
        if (_selection instanceof ShipSprite && _scrobj.points[_selfIdx] > 0) {
            Ship ship = ((ShipSprite)_selection).getShip();
            Planet planet = psprite.getPlanet();

            // TODO: make sure this planet is a neighbor to the planet the ship
            // currently occupies
//             if () {

                _scrobj.service.moveShip(
                    _ctx.getClient(), ship.shipId, planet.planetId);
                // we leave the ship selected so that the user can easily chain
                // moves with a single ship
//             }
            return;
        }

        // otherwise if there's a planet selected, clear that selection
        clearSelection();

        // TODO: look up this planet's neighbors and highlight them
        _selection = psprite;
        _selection.setSelected(true);

        // TODO: display status on the selected planet somewhere?
    }

    /**
     * This method is called when the player clicks on a ship.
     */
    public void handleShipSelected (Object source, ShipSprite ssprite)
    {
        // if there's another ship or planet selected, clear that selection
        clearSelection();

        // and note this selected ship
        _selection = ssprite;
        _selection.setSelected(true);

        // TODO: display status on the selected ship somewhere?
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
        _scrobj = (ScrackObject)_gobj;
        _selfIdx = _scrobj.getPlayerIndex(_ctx.getUsername());
        _panel.view.gameDidStart();
    }

//     @Override // documentation inherited
//     protected void gameDidEnd ()
//     {
//         super.gameDidEnd();
//         _panel.displayStatus("m.game_over");
//     }

    protected void clearSelection ()
    {
        if (_selection != null) {
            _selection.setSelected(false);
            _selection = null;
        }
    }

    /** Provides access to various client services. */
    protected ToyBoxContext _ctx;

    /** A reference to the game object. */
    protected ScrackObject _scrobj;

    /** Our index in the player array. */
    protected int _selfIdx;

    /** Our game panel. */
    protected ScrackPanel _panel;

    /** Contains our selection; either a {@link PlanetSprite} or a {@link
     * ShipSprite}. */
    protected CelestialSprite _selection;
}
