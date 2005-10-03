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
    public static final String PLANET_CLICKED = "PlanetClicked";

    /** A command posted by a ship when it is clicked. */
    public static final String SHIP_CLICKED = "ShipClicked";

    /** A command posted when the player wants to build a ship at the selected
     * planet. */
    public static final String BUILD_SHIP = "BuildShip";

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
    public void handlePlanetClicked (Object source, PlanetSprite psprite)
    {
        // if we already have a ship selected, then the player wants to move
        // that ship to this planet; if we still have command points, do so
        if (_selection instanceof ShipSprite && _scrobj.points[_selfIdx] > 0) {
            Ship ship = ((ShipSprite)_selection).getShip();
            Planet planet = psprite.getPlanet();

            // make sure this planet is a neighbor to the planet the ship
            // currently occupies
            Planet cplanet = _scrobj.locatePlanet(ship.coords);
            if (cplanet != null && cplanet.isNeighbor(planet)) {
                _scrobj.service.moveShip(
                    _ctx.getClient(), ship.shipId, planet.planetId);
                clearSelection();
                return;
            }
        }

        // otherwise if there's a planet selected, clear that selection
        clearSelection();

        // display this planet as selected and display status on it
        _selection = psprite;
        _selection.setSelected(true);
        _panel.pinfo.setPlanet(psprite.getPlanet());
    }

    /**
     * This method is called when the player clicks on a ship.
     */
    public void handleShipClicked (Object source, ShipSprite ssprite)
    {
        // don't allow selecting other player's ships
        if (ssprite.getShip().owner != _selfIdx) {
            return;
        }

        // if there's another ship or planet selected, clear that selection; if
        // they clicked again on the already selected ship, just deselect it
        CelestialSprite oselection = _selection;
        clearSelection();
        if (oselection == ssprite) {
            return;
        }

        // display this ship as selected
        _selection = ssprite;
        _selection.setSelected(true);
        _panel.pinfo.setShip(ssprite.getShip());

        // TODO: highlight the planets to which this ship can move?
    }

    /**
     * Requests that we build a ship at the selected planet.
     */
    public void handleBuildShip (Object source)
    {
        if (_selection instanceof PlanetSprite) {
            Planet planet = ((PlanetSprite)_selection).getPlanet();
            _scrobj.service.buildShip(_ctx.getClient(), planet.planetId);
        }
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
        if (_selection instanceof PlanetSprite) {
            _panel.pinfo.setPlanet(null);
        }
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
