package ru.nuyanzin.pmd.rules.java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ResourceFileReader {
  private ResourceFileReader() {}

  public static Set<String> readFromFile(String resource) {
    Set<String> setOfRows = new HashSet<>();
    try (BufferedReader bufferedReader =
        new BufferedReader(
            new InputStreamReader(
                ResourceFileReader.class.getResourceAsStream(resource),
                    StandardCharsets.UTF_8))) {

      String line;

      while ((line = bufferedReader.readLine()) != null) {
        setOfRows.add(line.trim());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return Collections.unmodifiableSet(setOfRows);
  }
}

