package edu.umd.cmsc131.grader;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import javassist.compiler.ast.Variable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StaticFieldCheck {
    private ArrayList<String> staticFields;

    private static Function<? super VariableDeclarator, String> fieldNameFunction = field -> field.getName().asString();

    private class MethodNamePrinter extends VoidVisitorAdapter<Void> {
        @Override
        public void visit(FieldDeclaration n, Void arg) {
            super.visit(n, arg);
            if (n.isStatic())
                staticFields.addAll(n.getVariables().stream().map(fieldNameFunction).collect(Collectors.toList()));
        }
    }

    public StaticFieldCheck(File file, boolean allowConstants) throws IOException {
        staticFields = new ArrayList<>();

        StaticJavaParser.parse(file).findAll(FieldDeclaration.class).stream()
                .filter(FieldDeclaration::isStatic)
                .filter(field -> allowConstants ? !field.isFinal() : true)
                .forEach(declaration ->
                    staticFields.addAll(
                            declaration.getVariables().stream()
                                    .map(fieldNameFunction).collect(Collectors.toList())
                    )
        );
    }

    public boolean hasStaticFields() {
        return staticFields.size() > 0;
    }

    public int numStaticFields() {
        return staticFields.size();
    }

    public String[] getStaticFields() {
        return staticFields.toArray(new String[staticFields.size()]);
    }

}
