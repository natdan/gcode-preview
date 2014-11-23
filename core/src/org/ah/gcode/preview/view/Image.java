package org.ah.gcode.preview.view;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Image extends Component {

    private Texture imageTexture;
    private int preferredWidth;
    private int preferredHeight;
    private boolean stretchImage = true;

    public Image(Texture imageTexture) {
        this.imageTexture = imageTexture;
        setPreferredSize(imageTexture.getWidth(), imageTexture.getHeight());
        setSize(getPreferredWidth(), getPreferredHeight());
    }

    protected Texture getImageTexture() {
        return imageTexture;
    }

    public void render(SpriteBatch spriteBatch) {
        Texture texture = getImageTexture();

        if (stretchImage) {
            spriteBatch.draw(texture, getX(), getY(), getWidth(), getHeight(), 0, 0, texture.getWidth(), texture.getHeight(), false, true);
        } else {
            spriteBatch.draw(texture, getX() + (getWidth() - texture.getWidth()) / 2, getY() + (getHeight() - texture.getHeight()) / 2, texture.getWidth(), texture.getHeight(), 0, 0, texture.getWidth(), texture.getHeight(), false, true);
        }
    }

    public void setPreferredSize(int preferredWidth, int preferredHeight) {
        this.preferredWidth = preferredWidth;
        this.preferredHeight = preferredHeight;
    }

    public int getPreferredWidth() {
        return preferredWidth;
    }

    public int getPreferredHeight() {
        return preferredHeight;
    }

    public void setStretchImage(boolean stretchImage) {
        this.stretchImage = stretchImage;
    }

    public boolean isStretchImage() {
        return stretchImage;
    }
}
