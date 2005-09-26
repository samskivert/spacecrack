//
// $Id$

package com.samskivert.scrack.client;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.samskivert.swing.HGroupLayout;
import com.samskivert.swing.MultiLineLabel;
import com.samskivert.swing.ScrollBox;
import com.samskivert.swing.VGroupLayout;
import com.samskivert.swing.util.SwingUtil;

import com.threerings.media.VirtualRangeModel;
import com.threerings.util.MessageBundle;

import com.threerings.crowd.client.PlacePanel;

import com.threerings.toybox.client.ChatPanel;
import com.threerings.toybox.client.ToyBoxUI;
import com.threerings.toybox.data.ToyBoxGameConfig;
import com.threerings.toybox.util.ToyBoxContext;

import static com.samskivert.scrack.client.ScrackMetrics.*;
import static com.samskivert.scrack.data.ScrackCodes.*;

/**
 * Contains the main game interface.
 */
public class ScrackPanel extends PlacePanel
{
    /** Displays the main game visualization. */
    public ScrackBoardView view;

    /**
     * Creates the panel and its associated interface components.
     */
    public ScrackPanel (ToyBoxContext ctx, ToyBoxGameConfig config,
                        ScrackController ctrl)
    {
        super(ctrl);
        MessageBundle msgs = ctx.getMessageManager().getBundle(SCRACK_MSGS);

        // give ourselves a wee bit of a border
	setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

	HGroupLayout gl = new HGroupLayout(HGroupLayout.STRETCH);
	gl.setOffAxisPolicy(HGroupLayout.STRETCH);
	setLayout(gl);

        // create and add the board view
        _boardSize = (Integer)config.params.get("board_size");
        add(view = new ScrackBoardView(ctx, _boardSize)); 

        // create our side panel
        VGroupLayout sgl = new VGroupLayout(VGroupLayout.STRETCH);
        sgl.setOffAxisPolicy(VGroupLayout.STRETCH);
        sgl.setJustification(VGroupLayout.TOP);
        JPanel sidePanel = new JPanel(sgl);
        sidePanel.setPreferredSize(new Dimension(200, 10));

        // add a big fat label because we love it!
        MultiLineLabel vlabel = new MultiLineLabel(msgs.get("m.title"));
        vlabel.setAntiAliased(true);
        vlabel.setFont(ToyBoxUI.fancyFont);
        sidePanel.add(vlabel, VGroupLayout.FIXED);

//         // add our score indicator
//         sidePanel.add(new ScorePanel(ctx));

        // add a box for scrolling around the board view
        _vrange = new VirtualRangeModel(view);
        ScrollBox scroller = new ScrollBox(
            _vrange.getHorizModel(), _vrange.getVertModel());
        scroller.setPreferredSize(new Dimension(100, 100));
        sidePanel.add(scroller, VGroupLayout.FIXED);

        // de-opaquify everything before we add the chat box
        SwingUtil.setOpaque(sidePanel, false);
        setOpaque(true);
        setBackground(new Color(0xDAEB9C));

        // add a chat box
        ChatPanel chat = new ChatPanel(ctx);
        chat.removeSendButton();
        sidePanel.add(chat);

        // add a "back" button
        JButton back = new JButton(msgs.get("m.back_to_lobby"));
        back.setActionCommand(ScrackController.BACK_TO_LOBBY);
        back.addActionListener(ScrackController.DISPATCHER);
        sidePanel.add(back, VGroupLayout.FIXED);

        // add our side panel to the main display
        add(sidePanel, HGroupLayout.FIXED);
    }

    @Override // documentation inherited
    public void doLayout ()
    {
        super.doLayout();

        // after we've size the board view, we need to update the
        // scrollbox to let it know how big its scrollable area is and
        // trigger a resize of the scroll thumb based on the current size
        // of the board view
        int tile = SIZE * MAX_PLANET_SIZE;
        _vrange.setScrollableArea(0, 0, _boardSize*tile, _boardSize*tile);
    }

    protected int _boardSize;
    protected VirtualRangeModel _vrange;
}
