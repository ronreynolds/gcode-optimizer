package com.ronreynolds.tools.gcode;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class Parser {
    private Path inputPath;

    public Parser(String[] args) {
        if (args == null || args.length < 1) {
            throw new IllegalArgumentException("missing required input path arg");
        }
        inputPath = Path.of(args[0]);
    }

    public static void main(String[] args) {
        try {
            List<GCode> codes = new Parser(args).parse();
            log.info("codes: {}", codes);
        } catch (Exception ex) {
            log.error("{}", ex);
        }
    }

    public List<GCode> parse() throws IOException {
        return Files.lines(inputPath)
                .map(GCode::parse)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
}
