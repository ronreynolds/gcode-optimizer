package com.ronreynolds.tools.gcode;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class DupePointFinder {
    public static void main(String[] args) {
        try {
            List<GCode> codes = new Parser(args).parse();
            log.info("loaded {} codes", codes.size());
            Map<Point3D, List<GCode>> codesByPoint = groupCodesByPoint(codes);
            for (Map.Entry<Point3D,List<GCode>> entry : codesByPoint.entrySet()) {
                if (entry.getValue().size() > 10) {
                    log.info("found {} codes for point {}", entry.getValue().size(), entry.getKey());
                }
            }
        } catch (Exception fail) {
            log.error("failed to process args {}", Arrays.toString(args), fail);
        }
    }

    static Map<Point3D, List<GCode>> groupCodesByPoint(List<GCode> codes) {
        Map<Point3D, List<GCode>> mapOfCodesByPoint = new HashMap<>();
        for (GCode code : codes) {
            Point3D point = code.getPoint();
            if (point == null) {
                log.info("code has null point - {}", code);
                continue;
            }

            mapOfCodesByPoint.computeIfAbsent(point, ignore -> new ArrayList<>())
                    .add(code);
        }
        return mapOfCodesByPoint;
    }

}
