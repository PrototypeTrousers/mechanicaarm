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

package colladamodel.client.model.animation;

import java.util.Iterator;
import java.util.List;

public class AnimationSampler implements IAnimable {
    private final String geometryName;
    private final String transformName;
    private final String channelName;
    private final List<KeyFrame> frames;

    public AnimationSampler(String geometryName, String transformName,
                            String channelName, List<KeyFrame> frames) {
        this.geometryName = geometryName;
        this.transformName = transformName;
        this.channelName = channelName;
        this.frames = frames;
    }

    public double getValue(double time) {
        KeyFrame prevFrame = null;
        KeyFrame nextFrame = null;
        for (Iterator<KeyFrame> i = frames.iterator(); i.hasNext(); ) {
            nextFrame = i.next();
            if (time <= nextFrame.getFrame())
                break;
            prevFrame = nextFrame;
        }
        if (prevFrame == null)
            return nextFrame.getValue();
        if (prevFrame == nextFrame)
            return nextFrame.getValue();

        return prevFrame.interpolate(time, nextFrame);
    }

    public double getAnimationLength() {
        return frames.get(frames.size() - 1).getFrame();
    }
}
