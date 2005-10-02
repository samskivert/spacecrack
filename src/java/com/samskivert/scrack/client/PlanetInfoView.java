//
// $Id$

package com.samskivert.scrack.client;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.samskivert.swing.VGroupLayout;
import com.threerings.util.MessageBundle;

import com.threerings.presents.dobj.EntryAddedEvent;
import com.threerings.presents.dobj.EntryRemovedEvent;
import com.threerings.presents.dobj.EntryUpdatedEvent;
import com.threerings.presents.dobj.SetListener;

import com.threerings.crowd.client.PlaceView;
import com.threerings.crowd.data.PlaceObject;

import com.threerings.toybox.util.ToyBoxContext;

import com.samskivert.scrack.data.Planet;
import com.samskivert.scrack.data.ScrackCodes;
import com.samskivert.scrack.data.ScrackObject;
import com.samskivert.scrack.data.Ship;

/**
 * Displays information on a planet.
 */
public class PlanetInfoView extends JPanel
    implements PlaceView, SetListener
{
    public PlanetInfoView (ToyBoxContext ctx)
    {
        super(new VGroupLayout(VGroupLayout.NONE, VGroupLayout.STRETCH,
                               5, VGroupLayout.TOP));
        _ctx = ctx;
        _msgs = _ctx.getMessageManager().getBundle(ScrackCodes.SCRACK_MSGS);

        add(new JLabel(_msgs.get("m.planet_info")));
//         add(_name = new JLabel(""));
        add(_owner = new JLabel(""));
        add(_size = new JLabel(""));
        add(_build = new JButton(_msgs.get("m.build_ship")));
        _build.setActionCommand(ScrackController.BUILD_SHIP);
        _build.addActionListener(ScrackController.DISPATCHER);
        _build.setEnabled(false);
    }

    public void setPlanet (Planet planet)
    {
        _planet = planet;
        if (_planet == null) {
//             _name.setText("");
            _owner.setText("");
            _size.setText("");
            _build.setEnabled(false);
        } else {
//             _name.setText(_msgs.get("m.planet_name", planet.name));
            String owner = _msgs.get("m.planet_unowned");
            if (_planet.owner != -1) {
                owner = _scrobj.players[_planet.owner].toString();
            }
            _owner.setText(_msgs.get("m.planet_owner", owner));
            _size.setText(_msgs.get("m.planet_size", "" + planet.size));
            _build.setEnabled(_planet.owner == _selfIdx &&
                              _scrobj.locateShip(_planet.coords) == null &&
                              _scrobj.crack[_selfIdx] >= _planet.size);
        }
    }

    // documentation inherited from interface PlaceView
    public void willEnterPlace (PlaceObject plobj)
    {
        _scrobj = (ScrackObject)plobj;
        _scrobj.addListener(this);
        _selfIdx = _scrobj.getPlayerIndex(_ctx.getUsername());
    }

    // documentation inherited from interface PlaceView
    public void didLeavePlace (PlaceObject plobj)
    {
        if (_scrobj != null) {
            _scrobj.removeListener(this);
            _scrobj = null;
        }
    }

    // documentation inherited from interface SetListener
    public void entryAdded (EntryAddedEvent event)
    {
        if (_planet != null && event.getName().equals(ScrackObject.SHIPS) &&
            ((Ship)event.getEntry()).coords.equals(_planet.coords)) {
            setPlanet(_planet);
        }
    }

    // documentation inherited from interface SetListener
    public void entryUpdated (EntryUpdatedEvent event)
    {
        if (_planet == null) {
            return;
        }

        if (event.getName().equals(ScrackObject.PLANETS) &&
            _planet.planetId == ((Planet)event.getEntry()).planetId) {
            setPlanet((Planet)event.getEntry());
        } else if (event.getName().equals(ScrackObject.SHIPS) &&
                   ((Ship)event.getOldEntry()).coords.equals(_planet.coords) ||
                   ((Ship)event.getEntry()).coords.equals(_planet.coords)) {
            setPlanet(_planet);
        }
    }

    // documentation inherited from interface SetListener
    public void entryRemoved (EntryRemovedEvent event)
    {
        if (_planet != null && event.getName().equals(ScrackObject.SHIPS) &&
            ((Ship)event.getOldEntry()).coords.equals(_planet.coords)) {
            setPlanet(_planet);
        }
    }

    protected ToyBoxContext _ctx;
    protected MessageBundle _msgs;
    protected ScrackObject _scrobj;
    protected int _selfIdx;
    protected Planet _planet;

    protected JLabel _name, _owner, _size;
    protected JButton _build;
}
