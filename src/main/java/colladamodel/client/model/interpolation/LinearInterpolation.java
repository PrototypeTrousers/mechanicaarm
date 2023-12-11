/**
 * Copyright (c) 2014 Hea3veN
 * <p>
 * This file is part of lib-colladamodel.
 * <p>
 * lib-colladamodel is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * lib-colladamodel is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with lib-colladamodel.  If not, see <http://www.gnu.org/licenses/>.
 */

package colladamodel.client.model.interpolation;

import colladamodel.client.model.animation.KeyFrame;

public class LinearInterpolation implements Interpolation {

    @Override
    public double interpolate(double time, KeyFrame frame, KeyFrame nextFrame) {
        double s = (time - frame.getFrame())
                / (nextFrame.getFrame() - frame.getFrame());
        return frame.getValue() + (nextFrame.getValue() - frame.getValue()) * s;
    }

}
