package com.ronreynolds.tools.gcode;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Point3D {
    Float x, y, z;
}
