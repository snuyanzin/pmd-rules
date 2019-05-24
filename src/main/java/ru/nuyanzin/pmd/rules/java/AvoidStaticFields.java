package ru.nuyanzin.pmd.rules.java;

import java.util.Set;

import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class AvoidStaticFields extends AbstractJavaRule {
  private static final Set<String> FORBIDDEN_STATIC_FIELDS =
      ResourceFileReader.readFromFile("/AvoidStaticFieldsRule/exactMatching");
  @Override
  public Object visit(ASTFieldDeclaration node, Object data) {
    if (node.isStatic() && node.getType() != null
        && FORBIDDEN_STATIC_FIELDS.contains(
            node.getType().getCanonicalName())) {
      addViolation(data, node);
    }
    return data;
  }
}
