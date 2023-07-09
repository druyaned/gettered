package com.github.druyaned.gettered.sources;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Provides utilities of source files in the project.
 * 
 * @author druyaned
 */
public class Sources {

    /**
     * Returns all sources in the project directory.
     * 
     * @return all sources in the project directory.
     */
    public static List<Path> get() {
        Path sourcesPath = Paths.get(System.getProperty("user.dir"), "src", "main", "java");
        SourceVisitor visitor = new SourceVisitor();
        try {
            Files.walkFileTree(sourcesPath, visitor);
        } catch (IOException exc) {
            throw new UncheckedIOException(exc);
        }
        return visitor.getSources();
    }

}
