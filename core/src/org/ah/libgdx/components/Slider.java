/*******************************************************************************
 * Copyright (c) 2014 Creative Sphere Limited.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Creative Sphere - initial API and implementation
 *
 *
 *
 *******************************************************************************/
package org.ah.libgdx.components;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.utils.TextureProvider;

/**
 *
 * @author Daniel Sendula
 */
public class Slider extends Component {

    protected Texture knobTexture;

    protected int max = 100;
    protected List<Knob> knobs = new ArrayList<Knob>(2);

    public Slider(TextureProvider textureProvider) {
        knobTexture = textureProvider.load("gui/knob.png");
        knobs.add(new Knob()); // Default
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getMax() {
        return max;
    }

    public List<Knob> getKnobs() {
        return knobs;
    }

    @Override
    public boolean receiveTouchDown(int screenX, int screenY, int pointer, int button) {
        return true;
    }

    @Override
    public boolean receiveTouchUp(int screenX, int screenY, int pointer, int button) {
        return true;
    }

    public class Knob {
        protected int position;
        protected PositionChangedListener positionChangedListener;

        public Knob() {
        }

        public void setPosition(int position) {
            if (position != this.position) {
                int oldPosition = this.position;
                this.position = position;
                if (positionChangedListener != null) {
                    positionChangedListener.positionChanged(this, oldPosition, position);
                }
            }
        }

        public int getPosition() {
            return position;
        }

        public void setPositionChangedListener(PositionChangedListener listener) {
            this.positionChangedListener = listener;
        }
    }

    public static interface PositionChangedListener {

        void positionChanged(Knob knob, int oldPosition, int newPosition);

    }
}
