package com.github.druyaned.gettered.sources;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import java.util.List;
import java.util.Map;
import javax.lang.model.element.Modifier;

/**
 * TODO: write a description
 * 
 * @author druyaned
 */
public class Rewriter {
    
//-Fields-------------------------------------------------------------------------------------------
    
    private final UnitToRewrite unitToRewrite;
    private final String getteredContent;
    
//-Constructors-------------------------------------------------------------------------------------
    
    public Rewriter(UnitToRewrite unitToRewrite) {
        this.unitToRewrite = unitToRewrite;
        CompilationUnitTree compUnit = unitToRewrite.getUnit();
        Map<String, VariableTree> missedGetterToVar = unitToRewrite.getMissedGetterToVar();
        StringBuilder builder = new StringBuilder();
        // package
        builder.append("package ").append(compUnit.getPackageName()).append(";\n\n");
        // imports
        for (ImportTree importTree : compUnit.getImports()) {
            builder.append(importTree);
        }
        builder.append('\n');
        // modifiers and class name
        ClassTree classTree = unitToRewrite.getClassTree();
        String simpleClassName = classTree.getSimpleName().toString();
        ModifiersTree classMods = classTree.getModifiers();
        for (AnnotationTree classAnno : classMods.getAnnotations()) {
            builder.append(classAnno).append('\n');
        }
        for (Modifier classFlag : classMods.getFlags()) {
            builder.append(classFlag).append(' ');
        }
        builder.append("class ").append(simpleClassName);
        // extends
        Tree extendsClause = classTree.getExtendsClause();
        if (extendsClause != null) {
            builder.append('\n').append("extends ").append(classTree.getExtendsClause());
        }
        // implements
        List<? extends Tree> impls = classTree.getImplementsClause();
        if (!impls.isEmpty()) {
            builder.append('\n').append("implements ").append(impls.get(0));
            for (int i = 1; i < impls.size(); ++i) {
                builder.append(", ").append(impls.get(i));
            }
        }
        builder.append(" {\n");
        // class body
        classTree.getMembers().forEach(member -> {
            if (member instanceof VariableTree varTree) { // variables
                // modifiers
                ModifiersTree varMods = varTree.getModifiers();
                for (AnnotationTree varAnno : varMods.getAnnotations()) {
                    builder.append("\n    ").append(varAnno);
                }
                builder.append("\n    ");
                for (Modifier varFlag : varMods.getFlags()) {
                    builder.append(varFlag).append(' ');
                }
                // type and name
                builder.append(varTree.getType()).append(' ').append(varTree.getName());
                // initializer
                ExpressionTree initializer = varTree.getInitializer();
                if (initializer != null) {
                    builder.append(" = ").append(initializer);
                }
                builder.append(';');
            } else if (member instanceof MethodTree methodTree) { // methods
                builder.append('\n');
                // modifiers
                ModifiersTree methodMods = methodTree.getModifiers();
                for (AnnotationTree methodAnno : methodMods.getAnnotations()) {
                    builder.append("\n    ").append(methodAnno);
                }
                builder.append("\n    ");
                for (Modifier methodFlag : methodMods.getFlags()) {
                    builder.append(methodFlag).append(' ');
                }
                // return type and name
                Tree returnType = methodTree.getReturnType();
                if (returnType == null) { // constructor
                    builder.append(simpleClassName);
                } else {
                    builder.append(returnType).append(' ').append(methodTree.getName());
                }
                // parameters
                builder.append('(').append(methodTree.getParameters()).append(')');
                // throws
                List<? extends ExpressionTree> throwsList = methodTree.getThrows();
                if (!throwsList.isEmpty()) {
                    builder.append("\n    throws ").append(throwsList.get(0));
                    for (int i = 1; i < throwsList.size(); ++i) {
                        builder.append(", ").append(throwsList.get(i));
                    }
                }
                // body
                builder.append(" {");
                BlockTree body = methodTree.getBody();
                for (StatementTree stat : body.getStatements()) {
                    builder.append("\n        ").append(stat);
                }
                builder.append("\n    }");
            }
        });
        // missed getters
        for (Map.Entry<String, VariableTree> entry : missedGetterToVar.entrySet()) {
            String getteredName = entry.getKey();
            VariableTree varTree = entry.getValue();
            builder.append("\n\n    public ").append(varTree.getType())
                    .append(" ").append(getteredName)
                    .append("() {\n        return ").append(varTree.getName())
                    .append(";\n    }");
        }
        // EOF
        builder.append("\n}");
        getteredContent = builder.toString();
    }

//-Methods------------------------------------------------------------------------------------------
    
    public UnitToRewrite getUnitToRewrite() {
        return unitToRewrite;
    }
    
    public String getGetteredContent() {
        return getteredContent;
    }

}
