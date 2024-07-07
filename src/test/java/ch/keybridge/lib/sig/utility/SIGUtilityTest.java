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
package ch.keybridge.lib.sig.utility;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Key Bridge
 */
public class SIGUtilityTest {

  public SIGUtilityTest() {
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
  public void testCanExecute() {
    String[] commands = new String[]{"ss", "ps", "netstat", "ntpq", "df", "xrandr"};
    System.out.println("Test executable commands");
    for (String command : commands) {
      boolean canExecute = SIGUtility.canExecute(command);
      System.out.println(String.format("  %-8s %s", command, canExecute));
    }

  }

  @Test
  public void testExecute() throws Exception {
  }

  @Test
  public void testExecuteSudo() throws Exception {
  }

  @Test
  public void testExecuteSimple() throws Exception {
  }

  @Test
  public void testReadFileLines() throws Exception {
  }

  @Test
  public void testReadFileString() throws Exception {
  }

  @Test
  public void testReadFileDouble() throws Exception {
  }

  @Test
  public void testReadFileLong() throws Exception {
  }

  @Test
  public void testReadFileInteger() throws Exception {
  }

}
