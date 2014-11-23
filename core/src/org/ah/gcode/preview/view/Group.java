package org.ah.gcode.preview.view;


public class Group extends Component {

    private boolean fillWidth = false;
    private boolean fillHeight = false;
    private boolean stretchLast = false;
    private int margin;

    public boolean isFillWidth() {
        return fillWidth;
    }

    public void setFillWidth(boolean fillWidth) {
        this.fillWidth = fillWidth;
    }

    public boolean isFillHeight() {
        return fillHeight;
    }

    public void setFillHeight(boolean fillHeight) {
        this.fillHeight = fillHeight;
    }

    public boolean isStretchLast() {
        return stretchLast;
    }

    public void setStretchLast(boolean stretchLast) {
        this.stretchLast = stretchLast;
    }

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
}
