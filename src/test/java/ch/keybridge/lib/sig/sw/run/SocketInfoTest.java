/*
 * Copyright (C) 2024 Key Bridge. All rights reserved. Use is subject to license
 * terms.
 *
 * This software code is protected by Copyrights and remains the property of
 * Key Bridge and its suppliers, if any. Key Bridge reserves all rights in and to
 * Copyrights and no license is granted under Copyrights in this Software
 * License Agreement.
 *
 * Key Bridge may license Copyrights for commercialization pursuant to
 * the terms of either a Standard Software Source Code License Agreement or a
 * Standard Product License Agreement. A copy of either Agreement can be
 * obtained upon request by sending an email to info@keybridgewireless.com.
 *
 * All information contained herein is the property of Key Bridge and its
 * suppliers, if any. The intellectual and technical concepts contained herein
 * are proprietary.
 */
package ch.keybridge.lib.sig.sw.run;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Key Bridge
 */
public class SocketInfoTest {

  public SocketInfoTest() {
  }

  @BeforeClass
  public static void setUpClass() {
  }

  @Before
  public void setUp() {
  }

  @After
  public void tearDown() {
  }

  @Test
  public void testGetAllSockets() throws Exception {
    for (SocketInfo socket : SocketInfo.getAllSockets()) {
      System.out.println(socket);
    }

  }

  @Test
  public void testParseNetstatEntry() throws Exception {
  }

  @Test
  public void testGetProtocol() {
  }

  @Test
  public void testSetProtocol() {
  }

  @Test
  public void testGetLocalAddress() {
  }

  @Test
  public void testSetLocalAddress() {
  }

  @Test
  public void testGetLocalPort() {
  }

  @Test
  public void testSetLocalPort() {
  }

  @Test
  public void testGetRemoteAddress() {
  }

  @Test
  public void testSetRemoteAddress() {
  }

  @Test
  public void testGetRemotePort() {
  }

  @Test
  public void testSetRemotePort() {
  }

  @Test
  public void testGetConnectionState() {
  }

  @Test
  public void testSetConnectionState() {
  }

  @Test
  public void testGetTxQueue() {
  }

  @Test
  public void testSetTxQueue() {
  }

  @Test
  public void testGetRxQueue() {
  }

  @Test
  public void testSetRxQueue() {
  }

  @Test
  public void testIsIPv4() {
  }

  @Test
  public void testIsIPv6() {
  }

  @Test
  public void testHashCode() {
  }

  @Test
  public void testEquals() {
  }

  @Test
  public void testToString() {
  }

}
