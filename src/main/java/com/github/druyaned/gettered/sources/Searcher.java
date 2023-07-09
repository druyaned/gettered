package com.github.druyaned.gettered.sources;

import com.github.druyaned.gettered.Gettered;
import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Firstly searches for annotated by {@link Gettered} annotation files;
 * secondary generates {@link UnitToRewrite units to rewrite}.
 * 
 * @author druyaned
 */
public class Searcher {
    
    private static final String GETTERED_ANNO_NAME = "Gettered";
    
    /**
     * Constructs an instance of the Searcher.
     * 
     * @return an instance of the Searcher.
     */
    public static Searcher instance() {
        return new Searcher();
    }

//-Fields-------------------------------------------------------------------------------------------

    private boolean processed = false;
    private final List<UnitToRewrite> unitsToRewrite = new ArrayList<>();

//-Methods------------------------------------------------------------------------------------------
    
    /**
     * Generates {@link UnitToRewrite units to rewrite} by searching
     * annotated by {@link Gettered} annotation files and missing getters.
     * 
     * @param compUnits compilation units to search for
     * @return generated during the search {@link UnitToRewrite units to rewrite}
     */
    public List<UnitToRewrite> unitsToRewriteIn(
            Iterable<? extends CompilationUnitTree> compUnits) {
        if (processed) {
            throw new IllegalStateException("the method find can be used only once");
        }
        processed = true;
        compUnits.forEach(u -> {
            CompilationUnitTree compUnit = (CompilationUnitTree)u;
            compUnit.getTypeDecls().forEach(typeDecl -> handleClassTree(compUnit, typeDecl));
        }); // unitsToRewrite are added in handleClassTree(compUnit, typeDecl)
        return Collections.unmodifiableList(unitsToRewrite);
    }
    
//-Private-methods----------------------------------------------------------------------------------
    
    /**
     * Handles {@code typeDecl} if it's an instance of {@link ClassTree};
     * if the class is annotated with {@link Gettered Gettered} annotation
     * and has missed getters then adds it into {@code unitsToRewrite}.
     * 
     * @param compUnit compilation unit to be handled
     * @param typeDecl type declaration to be handled
     */
    private void handleClassTree(CompilationUnitTree compUnit, Tree typeDecl) {
        if (!(typeDecl instanceof ClassTree)) {
            return; // only class trees are needed
        }
        ClassTree classTree = (ClassTree)typeDecl;
        List<? extends AnnotationTree> annos = classTree.getModifiers().getAnnotations();
        boolean notGettered = true;
        for (Object annoObj : annos) {
            AnnotationTree anno = (AnnotationTree)annoObj;
            String annoName = anno.getAnnotationType().toString();
            if (annoName.equals(GETTERED_ANNO_NAME)) {
                notGettered = false;
                break;
            }
        }
        if (notGettered) {
            return; // not annotated classes aren't interesting here
        }
        Set<VariableTree> varTrees = new HashSet<>();
        Map<String, MethodTree> methodNameToTree = new HashMap<>();
        classTree.getMembers().forEach(classTreeMember -> { // search for vars and methods
            if (classTreeMember instanceof VariableTree varTree) {
                varTrees.add(varTree);
            } else if (classTreeMember instanceof MethodTree methodTree) {
                methodNameToTree.put(methodTree.getName().toString(), methodTree);
            }
        });
        Map<String, VariableTree> missedGetterToVar = new HashMap<>();
        varTrees.forEach(v -> { // search for missed getters
            String getterName = getterNameFor(v);
            if (!methodNameToTree.containsKey(getterName)) {
                missedGetterToVar.put(getterName, v);
            }
        });
        if (!missedGetterToVar.isEmpty()) { // main action
            unitsToRewrite.add(new UnitToRewrite(compUnit, classTree, missedGetterToVar));
        }
    }

    private String getterNameFor(VariableTree varTree) {
        String varTreeName = varTree.getName().toString();
        return "get" + Character.toUpperCase(varTreeName.charAt(0)) + varTreeName.substring(1);
    }

}
