package org.ah.gcode.preview.view;


public class HorizontalGroup extends Component {

    private int margin;

    public int getMargin() {
        return margin;
    }

    public void setMargin(int margin) {
        this.margin = margin;
    }

    @Override
    public void addChild(Component component) {
        getChildren().add(component);
        component.setParent(this);
    }

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
                width = width + component.getWidth() + margin;
            }
        }
        setWidth(width);
        setHeight(height);
    }

}
