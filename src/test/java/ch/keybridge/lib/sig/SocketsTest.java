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
package ch.keybridge.lib.sig;

import ch.keybridge.lib.sig.sw.run.SocketInfo;
import org.junit.Test;

/**
 *
 * @author Key Bridge LLC
 */
public class SocketsTest {

  @Test
  public void testSockets() throws Exception {
    for (SocketInfo socket : SocketInfo.getAllSockets()) {
      System.out.println(socket);
    }
  }

}
