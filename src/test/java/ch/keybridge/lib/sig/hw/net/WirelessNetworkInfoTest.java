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
package ch.keybridge.lib.sig.hw.net;

import ch.keybridge.lib.sig.utility.UTFCharacterUtility;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.Test;

/**
 *
 * @author Key Bridge LLC
 */
public class WirelessNetworkInfoTest {

  public WirelessNetworkInfoTest() {
  }

  @Test
  public void testScanAllPorts() throws IOException, URISyntaxException, Exception {

//    Collection<String> response = SIGUtility.execute("sudo", "iwlist", "wlan0", "scanning");
//    for (String string : response) {      System.out.println(string);    }
//    Collection<String> response = SIGUtility.execute("ifconfig", "-a");
    Collection<WirelessNetworkInfo> networks = WirelessNetworkInfo.scanIW();
    for (WirelessNetworkInfo network : networks) {
      System.out.println("NETWORL " + network);
    }

  }

//  @Test
  public void testIWScan() throws IOException, URISyntaxException {
    URL resource = WirelessNetworkInfoTest.class.getClassLoader().getResource("iw.scan.2.txt");
    if (resource == null) {
      System.err.println("Failed to get resource ");
      return;
    }
    List<String> scanData = Files.readAllLines(Paths.get(resource.toURI()));
//    for (String string : scanData) {      System.out.println(string);    }
    Collection<WirelessNetworkInfo> networks = new HashSet<>();

    WirelessNetworkInfo wireless = null;
    /**
     * Scan the data and populate the 'networks' HashSet.
     */
    for (String string : scanData) {
      /**
       * Trim the string entry of white space.
       */
      String line = string.trim();
      /**
       * Capture the device port.
       */
      if (line.startsWith("BSS")) {
        /**
         * If there is already a wireless entry then save it to the set and
         * initialize a new instance.
         */
        if (wireless != null) {
          networks.add(wireless);                                        // SAVE
        }
        /**
         * Initialize a new Wireless Network configuration instance.
         */
        wireless = new WirelessNetworkInfo();                            // NEW
        // BSS f8:e4:fb:a0:fe:91 (on wlan0)
        wireless.setName("Cell " + networks.size());
        wireless.setMacAddress(line.split("\\s+")[1]);
        wireless.setBssid(line.split("\\s+")[1]);
        wireless.setPort(line.split("\\s+")[3].replaceAll("\\W", ""));
      }
      /**
       * Null check: If the wireless entry was not initialized then fast forward
       * until a new "Cell" line is discovered and a new Wireless Network
       * configuration instance is created.
       * <p>
       * This is an error condition and should never occur unless the output of
       * the 'iwlist' command is scrambled. This null check is a fail safe.
       */
      if (wireless == null) {
        continue;
      }

      /**
       * Parse the string and populate the current Wireless Network
       * configuration.
       */
      if (line.startsWith("DS Parameter set")) {
        // 	DS Parameter set: channel 6
        wireless.setChannel(Integer.valueOf(line.split(":")[1].replaceAll("\\D", "")));
      } else if (line.startsWith("freq")) {
        // freq: 2437
        wireless.setFrequency(Double.valueOf(line.split(":")[1].trim()));
      } else if (line.startsWith("signal")) {
        // signal: -64.00 dBm
        wireless.setSignalLevel(Double.valueOf(line.split(":")[1].replaceAll("[a-zA-Z]", "").trim()));
      } else if (line.contains("ESS Privacy")) {
        // capability: ESS Privacy ShortPreamble ShortSlotTime (0x0431)
        /**
         * If there is Privacy, for example capability: ESS Privacy
         * ShortSlotTime (0x0411), then the network is protected somehow.
         * <p>
         * If you see an RSN information block, then the network is protected by
         * Robust Security Network protocol, also known as WPA2.
         * <p>
         * If you see an WPA information block, then the network is protected by
         * Wi-Fi Protected Access protocol.
         * <p>
         * If you do not see neither RSN nor WPA blocks but there is Privacy,
         * then WEP is used.
         */
        wireless.setEncryption(Boolean.TRUE);
        wireless.setWep(Boolean.TRUE);
      } else if (line.startsWith("SSID")) {
        // SSID: WIFINET
        /**
         * Use UTFCharacterUtility to handle UTF characters in the name.
         */
        wireless.setEssid(UTFCharacterUtility.substitute(line.split(":")[1].trim()));
        wireless.setSsid(UTFCharacterUtility.substitute(line.split(":")[1].trim()));
      } else if (line.startsWith("RSN")) {
        // RSN:	 * Version: 1
        wireless.setWpa(Boolean.TRUE);
        wireless.setWep(Boolean.FALSE);
      } else if (line.startsWith("Supported rates") || line.startsWith("Extended supported rates")) {
        // Supported rates: 1.0* 2.0* 5.5* 11.0* 6.0 9.0 12.0 18.0
        for (String rate : line.split(":")[1].trim().split("\\s")) {
          wireless.addBitRate(Double.valueOf(rate.replace("*", "")));
        }
      } else if (line.startsWith("TSF")) {
        wireless.setTSF(Long.valueOf(line.split("\\s+")[1]));
      } else if (line.startsWith("last seen")) {
        wireless.setLastSeen(Integer.valueOf(line.split(":")[1].replaceAll("\\D", "")));
      }
    }
    /**
     * Add the last wireless network in the list. This also captures the case
     * where there is only one entry.
     */
    networks.add(wireless);

    for (WirelessNetworkInfo wirelessNetworkInfo : networks) {
      System.out.println(wirelessNetworkInfo);
    }
  }

  public void testIWListScan() throws IOException, URISyntaxException {

    URL resource = WirelessNetworkInfoTest.class.getClassLoader().getResource("iwlist.wlan0.2.txt");
    if (resource == null) {
      System.err.println("Failed to get resource ");
      return;
    }

    WirelessNetworkInfo wireless = new WirelessNetworkInfo();

    List<String> scan = Files.readAllLines(Paths.get(resource.toURI()));

    for (String string : scan) {

      String line = string.trim();
      System.out.println(line);

      if (line.contains("Scan completed")) {
        /**
         * Initialize a new Wireless Interface Info.
         */
        wireless = new WirelessNetworkInfo();
        // wlan0     Scan completed :
        wireless.setPort(line.split("\\s+")[0]);
      } else if (line.startsWith("Cell")) {
        System.out.println("New Wireless Info " + line);
        // Cell 01 - Address: F8:E4:FB:A0:FE:91
        wireless.setName(line.split("-")[0].trim());
        wireless.setMacAddress(line.split(":", 2)[1]);
      } else if (line.startsWith("Channel")) {
        // Channel:6
        wireless.setChannel(Integer.valueOf(line.split(":")[1]));
      } else if (line.startsWith("Frequency")) {
        // Frequency:2.437 GHz (Channel 6)
        wireless.setFrequency(Double.valueOf(line.split(":")[1].split(" ")[0]));
      } else if (line.startsWith("Quality")) {
        // Quality=57/70  Signal level=-53 dBm
        Matcher m = Pattern.compile("=(\\d+)/(\\d+) ").matcher(line);
        if (m.find()) {
          System.out.println("found " + m.group(1) + " / " + m.group(2));
          wireless.setQuality(Double.valueOf(m.group(1)) / Double.valueOf(m.group(2)));
        }
        wireless.setSignalLevel(Double.valueOf(line.split("=")[2].replaceAll("[a-zA-Z]", "").trim()));
      } else if (line.startsWith("Encryption key")) {
        // Encryption key:on
        wireless.setEncryption("on".equals(line.split(":")[1]));
      } else if (line.startsWith("ESSID")) {
        // ESSID:"WIFINET"
        wireless.setEssid(UTFCharacterUtility.substitute(line.split(":")[1].replaceAll("\"", "")));
      } else if (line.startsWith("Mode")) {
        // Mode:Master
        wireless.setMode(line.split(":")[1]);
      } else if (line.contains("IEEE 802.11i/WPA2")) {
        // IE: IEEE 802.11i/WPA2 Version 1
        wireless.setWpa(Boolean.TRUE);
      } else if (line.startsWith("WPA Version 1")) {
        // IE: WPA Version 1
//        wireless.setWep(Boolean.TRUE);
      } else if (line.contains("Mb/s;")) {
        // Bit Rates:1 Mb/s; 2 Mb/s; 5.5 Mb/s; 11 Mb/s; 9 Mb/s
        //           18 Mb/s; 36 Mb/s; 54 Mb/s
        // Bit Rates:6 Mb/s; 12 Mb/s; 24 Mb/s; 48 Mb/s
        for (String rate : line.split(";")) {
          System.out.println("parse rate " + rate);
          wireless.addBitRate(Double.valueOf(rate.replaceAll("[a-zA-Z:/ ]", "")));
        }
      } else if (line.startsWith("Extra")) {
        if (line.contains("tsf")) {
          // Extra:tsf=00000003856835fe
//          wireless.setTsf(line.split("=")[1]);
        } else if (line.contains("Last beacon")) {
          // Extra: Last beacon: 240ms ago
          wireless.setLastSeen(Integer.valueOf(line.split(":")[2].replaceAll("\\D", "")));
        }
      }
    }
    System.out.println(wireless);

  }

}
