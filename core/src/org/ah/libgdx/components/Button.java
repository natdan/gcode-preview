package org.ah.libgdx.components;

import com.badlogic.gdx.graphics.Texture;

public class Button extends Image {

    private Texture textureSelected;
    private ButtonClicked buttonClickedListener;

    public Button(Texture textureUnselected, Texture textureSelected) {
        super(textureUnselected);
        this.textureSelected = textureSelected;
        setPreferredSize(Math.max(textureUnselected.getWidth(), textureSelected.getWidth()), Math.max(textureUnselected.getHeight(), textureSelected.getHeight()));
        setSize(getPreferredWidth(), getPreferredHeight());
    }

    @Override
    protected Texture getImageTexture() {
        if (mouseOver) {
            return textureSelected;
        } else {
            return super.getImageTexture();
        }

    }

    public void registerButtonClickedListener(ButtonClicked buttonClickedListener) {
        this.buttonClickedListener = buttonClickedListener;
    }

    public static interface ButtonClicked {
        void buttonClicked(Button button);
    }

    public boolean receiveTouchDown(int screenX, int screenY, int pointer, int button) {
        return true;
    }

    public boolean receiveTouchUp(int screenX, int screenY, int pointer, int button) {
        if (getX() <= screenX && getX() + getWidth() >= screenX
                && getY() <= screenY && getY() + getHeight() >= screenY) {
            if (buttonClickedListener != null) {
                buttonClickedListener.buttonClicked(this);
            }
        }
        return true;
    }
}
