package org.jetbrains.jps.build;

import com.intellij.util.ParameterizedRunnable;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.jetbrains.jps.model.JpsModel;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author nik
 */
public class GroovyModelInitializer implements ParameterizedRunnable<JpsModel> {
  private File myScriptFile;

  public GroovyModelInitializer(File scriptFile) {
    myScriptFile = scriptFile;
  }

  @Override
  public void run(JpsModel model) {
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("project", model.getProject());
    variables.put("global", model.getGlobal());
    try {
      new GroovyShell(new Binding(variables)).evaluate(myScriptFile);
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
