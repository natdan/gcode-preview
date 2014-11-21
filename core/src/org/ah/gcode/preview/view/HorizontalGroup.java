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
                component.setX(getX() + width);
                component.setY(getY());
                width = width + component.getWidth() + getMargin();
            }
        }
        setWidth(width);
        setHeight(height);
    }

}
