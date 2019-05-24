package ru.nuyanzin.pmd.rules.java;

import java.util.Set;

import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class AvoidMethodCalls extends AbstractJavaRule {
  private static final Set<String> FORBIDDEN_METHODS =
      ResourceFileReader.readFromFile("/AvoidMethodCallsRule/exactMatching");
  @Override
  public Object visit(ASTName node, Object data) {
    if (FORBIDDEN_METHODS.contains(node.getImage())) {
      addViolation(data, node);
    }
    return data;
  }
}
