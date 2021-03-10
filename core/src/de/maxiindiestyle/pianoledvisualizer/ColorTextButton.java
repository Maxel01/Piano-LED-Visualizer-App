package de.maxiindiestyle.pianoledvisualizer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Null;

public class ColorTextButton extends TextButton {
    public ColorTextButton (@Null String text, Skin skin, Color color) {
        this(text, skin.get(TextButtonStyle.class), color);
        setSkin(skin);
    }

    public ColorTextButton (@Null String text, Skin skin, String styleName, Color color) {
        this(text, skin.get(styleName, TextButtonStyle.class), color);
        setSkin(skin);
    }

    public ColorTextButton (@Null String text, TextButtonStyle style, Color color) {
        super(text, style);
        Pixmap pixmap = new Pixmap(1,1, Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        add(new Image(new Sprite(new Texture(pixmap)))).width(25).height(25).padRight(25);
        getLabelCell().padLeft(50);
    }
}
