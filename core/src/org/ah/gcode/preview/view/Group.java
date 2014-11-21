package org.ah.gcode.preview.view;


public class Group extends Component {

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
}
