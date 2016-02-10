/*
 * Copyright 2016 Caulfield IP Holdings (Caulfield) and affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * Software Code is protected by copyright. Caulfield hereby
 * reserves all rights and copyrights and no license is
 * granted under said copyrights in this Software License Agreement.
 * Caulfield generally licenses software for commercialization
 * pursuant to the terms of either a Standard Software Source Code
 * License Agreement or a Standard Product License Agreement.
 * A copy of these agreements may be obtained by sending a request
 * via email to info@caufield.org.
 */
package ch.keybridge.lib.sig.utility;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.junit.Test;

/**
 *
 * @author Key Bridge LLC
 */
public class UTFCharacterTest {

  public UTFCharacterTest() {
  }

  @Test
  public void testSubstritute() throws IOException, URISyntaxException {
    URL resource = UTFCharacterTest.class.getClassLoader().getResource("iw.scan.2.txt");
    if (resource == null) {
      System.err.println("Failed to get resource ");
      return;
    }
    List<String> scanData = Files.readAllLines(Paths.get(resource.toURI()));
    for (String string : scanData) {
      String line = string.trim();

      if (line.startsWith("SSID")) {
        System.out.println("ssid " + line);

        String utf = UTFCharacterUtility.substitute(line);
        System.out.println("  UTFCharacter.substitute " + utf);
      }
    }
  }

}
