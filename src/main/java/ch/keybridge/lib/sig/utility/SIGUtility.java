/*
 * Copyright 2016 Key Bridge LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.keybridge.lib.sig.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * System Inspector utilities. This class provides shortcuts to execute system
 * commands and read file contents.
 *
 * @author Key Bridge LLC
 * @since 1.0.0 (01/31/16) (01/20/16)
 */
public class SIGUtility {

  /**
   * Execute a system command and return the output in a String array.
   *
   * @param command the system command and arguments
   * @return the system command output
   * @throws Exception if the command fails to execute.
   */
  public static Collection<String> execute(String... command) throws Exception {
    Process p = Runtime.getRuntime().exec(command);
    Collection<String> output = new ArrayList<>();
    try (BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
      String line;
      while ((line = input.readLine()) != null) {
        output.add(line.trim());
      }
    }
    return output;
  }

  /**
   * Execute a simple command and return the response as a single, concatenated
   * String. This is intended for simple commands that return a one-line
   * response, such as {@code date} or {@code uname}, as examples.
   *
   * @param command the system command and arguments
   * @return the system command output
   * @throws Exception if the command fails to execute.
   */
  public static String executeSimple(String... command) throws Exception {
    Process p = Runtime.getRuntime().exec(command);
    StringBuilder sb = new StringBuilder();
    try (BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
      String line;
      while ((line = input.readLine()) != null) {
        sb.append(line.trim());
      }
    }
    return sb.toString();
  }

  /**
   * Internal method read a file.
   *
   * @param file the file
   * @return the file contents converted to the desired type
   * @throws IOException if the file fails to parse
   */
  public static List<String> readFileLines(Path file) throws IOException {
    return Files.readAllLines(file);
  }

  /**
   * Internal method read a file.
   *
   * @param file the file
   * @return the file contents converted to the desired type
   * @throws IOException if the file fails to parse
   */
  public static String readFileString(Path file) throws IOException {
    return new String(Files.readAllBytes(file)).trim();
  }

  /**
   * Read a file and interpret its contents at a Double.
   *
   * @param file the file
   * @return the file contents converted to the desired type
   * @throws IOException           if the file fails to parse
   * @throws NumberFormatException if the value fails to parse
   */
  public static Double readFileDouble(Path file) throws IOException, NumberFormatException {
    return new Double(readFileString(file));
  }

  /**
   * Read a file and interpret its contents at a Long.
   *
   * @param file the file
   * @return the file contents converted to the desired type
   * @throws IOException           if the file fails to parse
   * @throws NumberFormatException if the value fails to parse
   */
  public static Long readFileLong(Path file) throws IOException, NumberFormatException {
    return new Long(readFileString(file));
  }

  /**
   * Read a file and interpret its contents at an Integer.
   *
   * @param file the file
   * @return the file contents converted to the desired type
   * @throws IOException           if the file fails to parse
   * @throws NumberFormatException if the value fails to parse
   */
  public static Integer readFileInteger(Path file) throws IOException, NumberFormatException {
    return new Integer(readFileString(file));
  }

}
