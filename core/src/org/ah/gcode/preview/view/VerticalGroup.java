package org.ah.gcode.preview.view;

import java.util.List;


public class VerticalGroup extends Group {

    @Override
    public void doLayout() {
        int heightLast = 0;
        int height = 0;
        int width = 0;
        List<Component> children = getChildren();
        for (Component component : children) {
            if (component.isVisible() && component.getHeight() > width) {
                width = component.getWidth();
            }
        }
        for (Component component : children) {
            if (component.isVisible()) {
                component.setPosition(getX(), getY() + height);
                heightLast = height + getMargin();
                height = height + component.getHeight() + getMargin();
            }
        }
        if (isFillWidth()) {
            for (Component component : children) {
                if (component.isVisible()) {
                    component.setSize(width, component.getHeight());
                }
            }
        }
        if (isStretchLast()) {
            Component c = children.get(children.size() - 1);
            c.setSize(c.getWidth(), getHeight() - heightLast);
        } else {
            setSize(width, height);
        }
    }

}
