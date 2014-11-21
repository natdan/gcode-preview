package org.ah.gcode.preview.view;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Button extends Component {

    private Texture texture;
    private Texture textureSelected;
    private ButtonClicked buttonClickedListener;
    private int preferredWidth;
    private int preferredHeight;

    public Button(Texture texture, Texture textureSelected) {
        this.texture = texture;
        this.textureSelected = textureSelected;
        setPreferredWidth(Math.max(texture.getWidth(), textureSelected.getWidth()));
        setPreferredHeight(Math.max(texture.getHeight(), textureSelected.getHeight()));
        setWidth(getPreferredWidth());
        setHeight(getPreferredHeight());
    }

    public void render(SpriteBatch spriteBatch) {
        if (mouseOver) {
            spriteBatch.draw(textureSelected, getX(), getY(), getWidth(), getHeight(), 0, 0, textureSelected.getWidth(), textureSelected.getHeight(), false, true);
        } else {
            spriteBatch.draw(texture, getX(), getY(), getWidth(), getHeight(), 0, 0, texture.getWidth(), texture.getHeight(), false, true);
        }
    }

    public void setPreferredSize(int preferredWidth, int preferredHeight) {
        setPreferredWidth(preferredWidth);
        setPreferredHeight(preferredHeight);
    }

    protected void setPreferredWidth(int preferredWidth) {
        this.preferredWidth = preferredWidth;
    }

    protected void setPreferredHeight(int preferredHeight) {
        this.preferredHeight = preferredHeight;
    }

    public int getPreferredWidth() {
        return preferredWidth;
    }

    public int getPreferredHeight() {
        return preferredHeight;
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

//    public boolean receiveMouseMoved(int screenX, int screenY) {
//        if (getX() <= screenX && getX() + getWidth() >= screenX
//                && getY() <= screenY && getY() + getHeight() >= screenY) {
//            if (!mouseOver) {
//                mouseOver = true;
//                if (mouseOverListener != null) {
//                    mouseOverListener.mouseOverButton(this, screenX, screenY, true);
//                }
//            }
//            return true;
//        } else {
//            if (mouseOver) {
//                mouseOver = false;
//                if (mouseOverListener != null) {
//                    mouseOverListener.mouseOverButton(this, screenX, screenY, false);
//                }
//            }
//            return false;
//        }
//    }
}
