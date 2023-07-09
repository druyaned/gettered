package com.github.druyaned.gettered;

import com.github.druyaned.gettered.sources.GetteredFileObject;
import com.github.druyaned.gettered.sources.Parser;
import com.github.druyaned.gettered.sources.Searcher;
import com.github.druyaned.gettered.sources.Sources;
import com.github.druyaned.gettered.sources.UnitToRewrite;
import com.sun.source.tree.CompilationUnitTree;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

/**
 * This compiler compiles all java-sources (.java)
 * in the project sources-directory ("src/main/java")
 * adding missed getters into simple classes with
 * {@link Gettered} annotation.
 * <p><a href="https://openjdk.org/groups/compiler/doc/compilation-overview/index.html">
 * Compilation overview
 * </a>.<p>
 * To test the work of the class see {@code gettered-data} project.
 * <p>
 * The compiler should be packed into the jar-file and copied into
 * the work directory of {@code getter-data} project.
 * <p>
 * Usage (enter in the {@code Terminal} app):
 * <pre>
 * mvn -q clean package install;
 * cp target/gettered-1.0.jar ../gettered-data/;
 * </pre>
 * 
 * @author druyaned
 */
public class Compiler {

    public static void main(String[] args) {
        List<Path> sources = Sources.get();
        Iterable<? extends CompilationUnitTree> compUnits = Parser.getCompilationUnits(sources);
        List<UnitToRewrite> unitsToRewrite = Searcher.instance().unitsToRewriteIn(compUnits);
        List<JavaFileObject> files = new ArrayList<>();
        for (CompilationUnitTree compUnit : compUnits) {
            boolean notSwapped = true;
            for (UnitToRewrite unitToRewrite : unitsToRewrite) {
                if (compUnit.equals(unitToRewrite.getUnit())) {
                    files.add(new GetteredFileObject(unitToRewrite));
                    notSwapped = false;
                    break;
                }
            }
            if (notSwapped) {
                files.add(compUnit.getSourceFile());
            }
        }
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        List<String> options = Arrays.asList("-d", "target/classes");
        compiler.getTask(null, fileManager, null, options, null, files).call();
    }
    
}
