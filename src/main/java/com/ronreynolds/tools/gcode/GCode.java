package com.ronreynolds.tools.gcode;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Builder
@Slf4j
@Data
public class GCode {
    private static final Command[] allCommands = Command.values();
    private static final Pattern linePattern = Pattern.compile("N(\\d+)(.*)");
    private static final Pattern moveArgsPattern = Pattern.compile("([XYZF])(-?\\d+\\.\\d+)");
    private static final Optional<GCode> NULL_CODE = Optional.empty();

    int line;
    Command command;
    String commandArgs; // N767720 G1 X-22.560Y149.129Z0.017
    Point3D point;
    Float feed;

    private GCode(int lineNum, Command command, String commandArgs, Point3D ignore, Float ignore2) {
        this.line = lineNum;
        this.command = command;
        this.commandArgs = commandArgs;

        var pointBuilder = Point3D.builder();

        if (commandArgs != null) {
            // what args are available depends on the command
            if (command == Command.G00 || command == Command.G01) {
                Matcher mat = moveArgsPattern.matcher(commandArgs);
                if (mat.find()) {
                    while (true) {
                        String variable = mat.group(1);
                        float value = Float.parseFloat(mat.group(2));
                        switch (variable.charAt(0)) {
                            case 'X':
                                pointBuilder.x(value);
                                break;
                            case 'Y':
                                pointBuilder.y(value);
                                break;
                            case 'Z':
                                pointBuilder.z(value);
                                break;
                            case 'F':
                                this.feed = value;
                                break;
                            default:
                                log.warn("unrecognized variable '{}'='{}'", variable, value);
                        }
                        // no more parts found
                        if (!mat.find()) {
                            break;
                        }
                    }
                } else {
                    log.warn("move args don't match expected pattern - line:{} cmd:{} args:{}", lineNum, command, commandArgs);
                }
                this.point = pointBuilder.build();
            }
        }
    }

    public static Optional<GCode> parse(String line) {
        if (line == null || line.isBlank()) {
            log.info("encountered null or blank line");
            return NULL_CODE;
        }

        line = line.trim();

        // EOF marked with %
        if ("%".equals(line)) {
            log.info("hit EOF");
            return NULL_CODE;
        }

        // comments start with (...)
        if (line.startsWith("(")) {
            log.info("encountered comment; '{}'", line);
            return NULL_CODE;
        }

        Matcher mat = linePattern.matcher(line);
        if (mat.matches() == false) {
            log.warn("weird line; lacks line-num - '{}'", line);
            return NULL_CODE;
        }

        int lineNum = Integer.parseInt(mat.group(1));
        String commandData = mat.group(2);

        for (Command c : allCommands) {
            mat = c.pattern.matcher(commandData);
            if (mat.matches()) {
                return Optional.of(
                        GCode.builder()
                                .line(lineNum)
                                .command(c)
                                .commandArgs(mat.group(1))
                                .build()
                );
            }
        }
        log.warn("unrecognized line format - '{}'", line);
        return NULL_CODE;
    }

    // some of the G-Codes we recognize
    enum Command {
        G00("rapid travel", "G0{1,2}(.*)"),
        G01("linear interpolation", "G0?1(.*)"),
        G02("circular interpolation CW", "G02(.*)"),
        G03("circular interpolation CCW", "G03(.*)"),
        G04("dwell", "G04(.*)"),
        G10("set working datum position", "G10(.*)"),
        M03("", "M03(.*)"),
        M05("", "M05(.*)"),
        M30("", "M30(.*)");
        final String name;
        final Pattern pattern;

        Command(String name, String pattern) {
            this.name = name;
            this.pattern = Pattern.compile(pattern);
        }
    }
}
