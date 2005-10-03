//
// $Id$

package com.samskivert.scrack.client;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;

import com.threerings.media.sprite.Sprite;
import com.threerings.media.sprite.action.CommandSprite;
import com.threerings.media.sprite.action.HoverSprite;

/**
 * A base class for planet and ship sprites.
 */
public abstract class CelestialSprite extends Sprite
    implements HoverSprite, CommandSprite
{
    /**
     * Indicates that this sprite has been selected (clicked on) by the player.
     */
    public void setSelected (boolean selected)
    {
        if (_selected != selected) {
            _selected = selected;
            invalidate();
        }
    }

    // documentation inherited from interface HoverSprite
    public void setHovered (boolean hovered)
    {
        if (_hovered != hovered) {
            _hovered = hovered;
            invalidate();
        }
    }

    // documentation inherited from interface CommandSprite
    public String getActionCommand ()
    {
        return _clickAction;
    }

    // documentation inherited from interface CommandSprite
    public Object getCommandArgument()
    {
        return this;
    }

    @Override // documentation inherited
    public boolean hitTest (int x, int y)
    {
        return _shape.contains(x, y);
    }

    @Override // documentation inherited
    public void paint (Graphics2D gfx)
    {
        gfx.setColor(getFillColor());
        gfx.fill(_shape);

        // selected overrides hovered
        Color outline = _selected ? Color.white :
            (_hovered ? Color.lightGray : Color.black);
        gfx.setColor(outline);
        gfx.draw(_shape);
    }

    protected CelestialSprite (String clickAction, int width, int height)
    {
        super(width, height);

        // our origin is in the center of the shape
        _oxoff = _bounds.width/2;
        _oyoff = _bounds.height/2;

        _clickAction = clickAction;
        _shape = createShape();
    }

    protected abstract Shape createShape ();

    protected abstract Color getFillColor ();

    protected Shape _shape;
    protected String _clickAction;
    protected boolean _hovered, _selected;
}
