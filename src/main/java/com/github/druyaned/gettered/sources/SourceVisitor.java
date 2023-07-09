package com.github.druyaned.gettered.sources;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * "Source Visitor" provides a source files ({@code ending=".java"}) of all directory entries.
 * <p><i>NOTE</i>: The instances stop be valid after calling {@link #getSources() getSources}.
 * <p><i>USAGE</i>:
 * <pre>
 * SourceVisitor sourceVisitor = new SourceVisitor();
 * Files.walkFileTree(dirPath, sourceVisitor);
 * List&lt;Path&gt; pathsOfSourceFiles = sourceVisitor.getSources();
 </pre>
 * 
 * @author druyaned
 */
public class SourceVisitor extends SimpleFileVisitor<Path> {
    
//-Fields-------------------------------------------------------------------------------------------
    
    private final ArrayList<Path> filePaths = new ArrayList<>();
    private final String pattern = ".+\\.java$";
    private volatile boolean gotten;
    
//-Constructors-------------------------------------------------------------------------------------
    
    /**
     * Constructs a new "Source Visitor" which is intended for getting
     * a source files ({@code ending=".java"}) of all project source-directory entries.
     */
    public SourceVisitor() {
        gotten = false;
    }
    
//-Methods------------------------------------------------------------------------------------------
    
    /**
     * Returns sorted lexicographically by a file name
     * list of source-files ({@code ending=".java"}),
     * which were added while invoking the visitFile method.
     * 
     * @return list of source-files ({@code ending=".java"}), which were added
     *         while invoking the {@link #visitFile visitFile method}.
     * @throws IllegalStateException if the files have already been gotten.
     */
    public List<Path> getSources() throws IllegalStateException {
        if (gotten) {
            throw new IllegalStateException("the method must be called once");
        }
        gotten = true;
        return filePaths;
    }
    
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if (gotten) {
            throw new IllegalStateException("source files have been already gotten");
        }
        String fileName = file.toString();
        if (fileName.matches(pattern)) {
            filePaths.add(file);
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        if (exc != null) {
            throw exc;
        }
        return FileVisitResult.SKIP_SIBLINGS;
    }
    
}
