package org.ah.gcode.preview.view;


public class HorizontalGroup extends Group {

    @Override
    public void doLayout() {
        int height = 0;
        int width = 0;
        for (Component component : getChildren()) {
            if (component.isVisible() && component.getHeight() > height) {
                height = component.getHeight();
            }
        }
        for (Component component : getChildren()) {
            if (component.isVisible()) {
                component.setPosition(getX() + width, getY());
                width = width + component.getWidth() + getMargin();
            }
        }
        setSize(width, height);
    }

}
