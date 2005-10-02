//
// $Id$

package com.samskivert.scrack.client;

import java.awt.Dimension;
import java.awt.Rectangle;
import javax.swing.JTable;
import javax.swing.Scrollable;
import javax.swing.table.AbstractTableModel;

import com.threerings.presents.dobj.AttributeChangeListener;
import com.threerings.presents.dobj.AttributeChangedEvent;
import com.threerings.presents.dobj.ElementUpdatedEvent;
import com.threerings.presents.dobj.ElementUpdateListener;

import com.threerings.crowd.client.PlaceView;
import com.threerings.crowd.data.PlaceObject;

import com.threerings.toybox.util.ToyBoxContext;

import com.samskivert.scrack.data.ScrackCodes;
import com.samskivert.scrack.data.ScrackObject;

/**
 * Displays the status of all players in the game.
 */
public class PlayerInfoView extends JTable
    implements PlaceView, Scrollable
{
    public PlayerInfoView (ToyBoxContext ctx)
    {
        _ctx = ctx;
        setModel(_model = new ScrackTableModel());
    }

    // documentation inherited from interface PlaceView
    public void willEnterPlace (PlaceObject plobj)
    {
        _scrobj = (ScrackObject)plobj;
        _scrobj.addListener(_model);
        _model.init();
    }

    // documentation inherited from interface PlaceView
    public void didLeavePlace (PlaceObject plobj)
    {
        if (_scrobj != null) {
            _scrobj.removeListener(_model);
            _scrobj = null;
        }
    }

    // documentation inherited from interface Scrollable
    public Dimension getPreferredScrollableViewportSize()
    {
        return getPreferredSize();
    }

    // documentation inherited from interface Scrollable
    public int getScrollableUnitIncrement (
        Rectangle visibleRect, int orientation, int direction)
    {
        return 1;
    }

    // documentation inherited from interface Scrollable
    public int getScrollableBlockIncrement (
        Rectangle visibleRect, int orientation, int direction)
    {
        return 1;
    }  

    // documentation inherited from interface Scrollable
    public boolean getScrollableTracksViewportWidth ()
    {
        return true;
    }

    // documentation inherited from interface Scrollable
    public boolean getScrollableTracksViewportHeight ()
    {
        return true;
    }

    protected class ScrackTableModel extends AbstractTableModel
        implements ElementUpdateListener, AttributeChangeListener
    {
        public void init () {
            fireTableDataChanged();
        }

        public String getColumnName (int col) {
            return _ctx.xlate(ScrackCodes.SCRACK_MSGS, "m.col_" + COLUMNS[col]);
        }

        public Class getColumnClass (int col) {
            return (col == 0 || col == 4) ? String.class : Integer.class;
        }

        public int getRowCount () {
            return (_scrobj == null) ? 0 : _scrobj.players.length;
        }

        public int getColumnCount() {
            return COLUMNS.length;
        }

        public Object getValueAt (int row, int col) {
            if (_scrobj == null) {
                return (col == 0 || col == 4) ? (Object)"" : (Object)0;
            }
            switch (col) {
            case 0: return _scrobj.players[row].toString();
            case 1: return _scrobj.countPlanets(row);
            case 2: return _scrobj.crack[row];
            case 3: return _scrobj.points[row];
            case 4: return _scrobj.finished[row] ?
                        _ctx.xlate(ScrackCodes.SCRACK_MSGS, "m.done") : "";
            default: return null;
            }
        }

        public boolean isCellEditable (int row, int col) {
            return false;
        }

        public void setValueAt (Object value, int row, int col) {
            // nada
        }

        // documentation inherited from interface ElementUpdateListener
        public void elementUpdated (ElementUpdatedEvent event)
        {
            String name = event.getName();
            if (name.equals(ScrackObject.POINTS) ||
                name.equals(ScrackObject.CRACK) ||
                name.equals(ScrackObject.FINISHED)) {
                fireTableDataChanged();
            }
        }

        // documentation inherited from interface AttributeChangeListener
        public void attributeChanged (AttributeChangedEvent event)
        {
            String name = event.getName();
            if (name.equals(ScrackObject.POINTS) ||
                name.equals(ScrackObject.CRACK) ||
                name.equals(ScrackObject.FINISHED) ||
                name.equals(ScrackObject.STATE)) {
                fireTableDataChanged();
            }
        }
    }

    protected ToyBoxContext _ctx;
    protected ScrackObject _scrobj;
    protected ScrackTableModel _model;

    protected static final String[] COLUMNS = {
        "player", "planets", "crack", "points", "done" };
}
