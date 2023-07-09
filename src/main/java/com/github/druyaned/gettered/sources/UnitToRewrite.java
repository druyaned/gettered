package com.github.druyaned.gettered.sources;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.VariableTree;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A unit to rewrite which provides {@link CompilationUnitTree compilation unit}, its
 * {@link VariableTree variable declarations} of missed getters and names of these getters.
 * 
 * @author druyaned
 */
public class UnitToRewrite {
    
//-Static-------------------------------------------------------------------------------------------
    
    private static final Pattern JAVA_SOURCE_PATTERN;
    
    static {
        String regex = ".*/(.+)\\.java$";
        JAVA_SOURCE_PATTERN = Pattern.compile(regex);
    }
    
//-Fields-------------------------------------------------------------------------------------------
    
    private final CompilationUnitTree unit;
    private final ClassTree classTree;
    private final Map<String, VariableTree> missedGetterToVar;
    
//-Constructors-------------------------------------------------------------------------------------

    public UnitToRewrite(CompilationUnitTree unit, ClassTree classTree,
            Map<String, VariableTree> missedGetterToVar) {
        this.unit = unit;
        String fileUri = unit.getSourceFile().toUri().toString();
        Matcher matcher = JAVA_SOURCE_PATTERN.matcher(fileUri);
        if (!matcher.matches()) {
            throw new IllegalStateException("fileUri \"" + fileUri +
                    "\" doesn't matches the patter \"" + JAVA_SOURCE_PATTERN.pattern() + "\"");
        }
        this.classTree = classTree;
        this.missedGetterToVar = missedGetterToVar;
    }
    
//-Getters------------------------------------------------------------------------------------------

    public CompilationUnitTree getUnit() {
        return unit;
    }
    
    public ClassTree getClassTree() {
        return classTree;
    }
    
    public Map<String, VariableTree> getMissedGetterToVar() {
        return Collections.unmodifiableMap(missedGetterToVar);
    }

}
