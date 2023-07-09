package com.github.druyaned.gettered.sources;

import java.io.IOException;
import java.io.UncheckedIOException;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.JavacTask;
import java.nio.file.Path;
import java.util.List;
import javax.tools.ToolProvider;

/**
 * Parse {@link Sources#get() project sources} to get
 * {@link CompilationUnitTree compilation units}.
 * 
 * @author druyaned
 */
public class Parser {
    
    /**
     * Returns parsed {@link CompilationUnitTree compilation units}
     * from {@link Sources#get() project sources}.
     * 
     * @param sources project sources ({@code .java}) to be parsed.
     * @return parsed {@link CompilationUnitTree compilation units}
     *         from {@link Sources#get() project sources}.
     */
    public static Iterable<? extends CompilationUnitTree> getCompilationUnits(List<Path> sources) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        Iterable<? extends JavaFileObject> fileObjects = fileManager
                .getJavaFileObjectsFromPaths(sources);
        JavacTask task = (JavacTask)compiler
                .getTask(null, fileManager, null, null, null, fileObjects);
        try {
            return task.parse();
        } catch (IOException exc) {
            throw new UncheckedIOException(exc);
        }
    }
    
}
