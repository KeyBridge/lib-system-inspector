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

import ch.keybridge.lib.sig.utility.SIGUtility;
import ch.keybridge.lib.sig.utility.UTFCharacterUtility;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Wireless network information.
 * <p>
 * Contains information about the local WiFI network environment and
 * configurations information collected from a wireless interface.
 * <p>
 * Wireless network information is typically collected using the
 * {@code iwlist [interface] scanning} or the equivalent
 * {@code iw [interface] scan} system commands.
 * <p>
 * Note that triggering scanning is typically a privileged operation (root only)
 * and normal users can only read left-over scan results. By default, the way
 * scanning is done (the scope of the scan) is dependant on the card and card
 * settings. Recommend allowing access by creating a sudo permission file as:
 * {@code echo "[username] ALL=(root) NOPASSWD: /sbin/iwlist" > /etc/sudoers.d/iwlist.d}
 *
 * @author Key Bridge LLC 02/09/2016
 * @since 1.0.3
 */
public class WirelessNetworkInfo implements Comparable<WirelessNetworkInfo> {

  /**
   * The textual name of the interface from which this wireless information was
   * detected on the CPE. i.e. "wlan0"
   */
  private String port;
  /**
   * The WLAN cell number. This is an automatically assigned cell count number
   * created by the WLAN driver. It has no real use.
   */
  private String name;
  /**
   * The MAC address.
   * <p>
   * A media access control address (MAC address), also called physical address,
   * is a unique identifier assigned to network interfaces for communications on
   * the physical network segment.
   * <p>
   * This is the same as the {@link #bssid}.
   */
  private String macAddress;
  /**
   * The WLAN Channel.
   * <p>
   * Depending on regulations, some frequencies / channels may not be available.
   * Each frequency range (2.4 GHz, 5 GHz, and 5.9 GHz) is divided into
   * channels.
   * <p>
   * 2.4 GHz (802.11 b/g/n)<br/>
   * There are 14 channels designated in the 2.4 GHz range spaced 5 MHz apart
   * (with the exception of a 12 MHz spacing before channel 14).
   * <p>
   * 5 GHz (802.11a/h/j/n/ac)<br/>
   * U.S. devices operating at 5.250–5.350 GHz and 5.470–5.725 GHz must employ
   * dynamic frequency selection (DFS) and transmit power control (TPC)
   * capabilities.
   *
   * @see <a href="https://en.wikipedia.org/wiki/List_of_WLAN_channels">WLAN
   * Channels</a>
   */
  private Integer channel;
  /**
   * The channel center frequency (MHz).
   * <p>
   * Depending on regulations, some frequencies / channels may not be available.
   */
  private Double frequency;
  /**
   * The reported signal quality (%). Quality is an aggregate value, and depends
   * totally on the driver and hardware.
   */
  private Double quality;
  /**
   * The received signal strength (RSSI) (dBm). In Ad-Hoc mode, this may be
   * undefined.
   */
  private Double signalLevel;
  /**
   * Indicator that this WLAN network uses some form of encryption. To determine
   * which modes are supported inspect the {@link #wep} and {@link #wpa}
   * indicators.
   */
  private Boolean encryption;
  /**
   * Indicator that this WLAN network supports Wired Equivalent Privacy (WEP)
   * security.
   * <p>
   * WEP supports Open System authentication and Shared Key authentication using
   * a key of 10 or 26 hexadecimal digits. WEP uses a 40-bit or 104-bit
   * encryption key that must be manually entered on wireless access points and
   * devices and does not change.
   * <p>
   * WEP is superseded by Wi-Fi Protected Access (WPA).
   */
  private Boolean wep;
  /**
   * Indicator that this WLAN network supports Wi-Fi Protected Access (WPA) and
   * Wi-Fi Protected Access II (WPA2, RSN) security.
   * <p>
   * WPA uses Temporal Key Integrity Protocol (TKIP), where a new 128-bit key is
   * generated for each packet.
   * <p>
   * WPA2 (IEEE 802.11i) includes mandatory support for CCMP (CTR mode with
   * CBC-MAC Protocol), an AES-based encryption mode.
   * <p>
   * There are two main flavors of WPA implementation:
   * <ul><li> WPA-Personal employs a pre-shared key of either as a string of 64
   * hexadecimal digits or a passphrase of 8 to 63 printable ASCII
   * characters.</li>
   * <li> WPA-Enterprise (WPA-802.1X mode) requires a RADIUS authentication
   * server. WPA-Enterprise mode is available with both WPA and WPA2.</li></ul>
   */
  private Boolean wpa;
  /**
   * The service set ID (SSID) of the network.
   * <p>
   * Because multiple WLANs can coexist in one airspace, each WLAN needs a
   * unique name—this name is the service set ID (SSID).
   * <p>
   * The terms BSSID, ESSID, and SSID are all used to describe sections of a
   * wireless network (WLAN). The three terms have slightly different meanings.
   * Wireless users are concerned only with the broadcast SSIDs to connect to a
   * wireless network. Administrators also need to keep track of BSSIDs and, to
   * a lesser degree, ESSIDs.
   *
   * @see
   * <a href="http://www.juniper.net/documentation/en_US/network-director1.5/topics/concept/wireless-ssid-bssid-essid.html">Juniper
   * Tech Library</a>
   */
  private String ssid;
  /**
   * The basic service set identifier (BSSID).
   * <p>
   * By convention, an access point’s MAC address is used as the ID of a BSS
   * (BSSID). BSSIDs identify access points and their plients. Each Access Point
   * has its own BSS.
   * <p>
   * There are usually multiple access points within each WLAN, and there has to
   * be a way to identify those access points and their associated clients. This
   * identifier is called a basic service set identifier (BSSID) and is included
   * in all wireless packets.
   * <p>
   * Ad-Hoc Networks do not have a MAC address.
   * <p>
   * Every BSS needs a BSSID, and using the access point’s MAC address works
   * fine most of the time. However, an ad-hoc network, a network that forwards
   * traffic from node to node, has no access point. When a BSS does not have a
   * physical access point, in an ad-hoc network for example, the network
   * generates a 48-bit string of numbers that looks and functions just like a
   * MAC address, and that BSSID goes in every packet.
   */
  private String bssid;
  /**
   * The extended basic service set identifier (ESSID).
   * <p>
   * An Extended basic Service Set (ESS) consists of all of the BSSs in the
   * network. For all practical purposes the ESSID identifies the same network
   * as the SSID does. The term SSID is used most often.
   * <p>
   * Unless explicitly configured this field always mirrors the SSID field.
   */
  private String essid;
  /**
   * The wireless access point advertised bit rates. (Mbps)
   * <p>
   * This is a list of channel data rates that the wireless access point
   * supports. This list consolidates the supported rates and extended supported
   * rates.
   * <p>
   * e.g. [1, 2, 5.5, 11, 6, 9, 12, 18, 24, 36, 48, 54] Mbit/sec
   */
  private Collection<Double> bitRates;
  /**
   * The operating mode of the (local) device, which depends on the network
   * topology. The mode can be
   * <ul>
   * <li>{@code Ad-Hoc} (network composed of only one cell and without Access
   * Point), </li>
   * <li>{@code Managed} (node connects to a network com‐ posed of many Access
   * Points, with roaming), </li>
   * <li>{@code Master} (the node is the synchronisation master or acts as an
   * Access Point), </li>
   * <li>{@code Repeater} (the node forwards packets between other wireless
   * nodes), </li>
   * <li>{@code Secondary} (the node acts as a backup master/repeater), </li>
   * <li>{@code Monitor} (the node is not associated with any cell and passively
   * monitor all packets on the frequency) or </li>
   * <li>{@code Auto}.</li></ul>
   */
  private String mode; // Master
  /**
   * The Timing Synchronization Function (TSF) value.
   * <p>
   * A Timing Synchronization Function (TSF) keeps the timers for all stations
   * in the same Basic Service Set (BSS) synchronized.
   * <p>
   * A value representing the time on the access point, which is the number of
   * microseconds the AP has been active. When timestamp reach its max (2^64
   * microsecond or ~580,000 years) it will reset to 0. This field is contained
   * in Beacon Frame & Probe Response frame.
   */
  private Long tsf;
  /**
   * The time in milliseconds) since a beacon was last seen from this network.
   */
  private Integer lastSeen;

  /**
   * Scan wireless networks on all available wireless ports on the local system.
   * <p>
   * This inspects the local system via the {@code /proc/net/dev} system file
   * and attempts to execute the {@code iw [interface] scan} system command on
   * each recognized wireless device.
   *
   * @return a (alpha-sorted) collection of WirelessNetworkInfo configurations
   * @throws IOException if the {@code /proc/net/dev} system file cannot be read
   *                     and parsed
   * @throws Exception   if the {@code iw [interface] scan} system command fails
   *                     to execute
   */
  public static Collection<WirelessNetworkInfo> scanIW() throws IOException, Exception {
    /**
     * Instantiate a new sorted Collection, then add all discovered networks.
     */
    Collection<WirelessNetworkInfo> networks = new TreeSet<>();
    for (String device : SIGUtility.readFileLines(Paths.get("/proc/net/dev"))) {
      if (device.trim().startsWith("wlan")) {
        Collection<String> output = SIGUtility.executeSudo("iw", device.split(":")[0].trim(), "scan");
        networks.addAll(parseIWScan(output));
      }
    }
    /**
     * Done.
     */
    return networks;
  }

  /**
   * Scan wireless networks on all available wireless ports on the local system.
   * This inspects the local system via the {@code /proc/net/dev} system file
   * and attempts to execute the {@code iwlist [interface] scanning} system
   * command on each recognized wireless device.
   * <p>
   * Some WiFI chip drivers are not supported or recognized by the current
   * {@code nl80211} interface and therefore do not respond to the
   * {@code iw [interface] scan} system command. These (legacy or less
   * functional) devices typically DO respond to the
   * {@code iwlist [interface] scanning} system command.
   *
   * @return a (alpha-sorted) collection of WirelessNetworkInfo configurations
   * @throws IOException if the {@code /proc/net/dev} system file cannot be read
   *                     and parsed
   * @throws Exception   if the {@code iwlist [interface] scanning} system
   *                     command fails to execute
   */
  public static Collection<WirelessNetworkInfo> scanIWList() throws IOException, Exception {
    /**
     * Instantiate a new sorted Collection, then add all discovered networks.
     */
    Collection<WirelessNetworkInfo> networks = new TreeSet<>();
    for (String device : SIGUtility.readFileLines(Paths.get("/proc/net/dev"))) {
      if (device.trim().startsWith("wlan")) {
        Collection<String> output = SIGUtility.executeSudo("iwlist", device.split(":")[0].trim(), "scanning");
        networks.addAll(parseIWList(output));
      }
    }
    /**
     * Done.
     */
    return networks;
  }

  /**
   * Parse the output from the {@code iw [interface] scan} system command.
   *
   * @param scanData text output from the {@code iwlist [interface] scanning}
   *                 system command.
   * @return a collection of WirelessNetworkInfo configurations
   */
  private static Collection<WirelessNetworkInfo> parseIWScan(Collection<String> scanData) {
    /**
     * Instantiate a new Collection. Use a HashSet since there is no particular
     * sorting preference at this stage.
     */
    Collection<WirelessNetworkInfo> networks = new HashSet<>();
    /**
     * Scan the data and populate the 'networks' HashSet.
     */
    WirelessNetworkInfo wireless = null;
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
    return networks;
  }

  /**
   * Parse the output from the {@code iwlist [interface] scanning} system
   * command. This is similar to {@link #parseIWScan(java.util.Collection)} but
   * processes slightly different output.
   * <p>
   * Developer note: prefer to use the {@code iw} system command if available.
   *
   * @param scanData text output from the {@code iwlist [interface] scanning}
   *                 system command.
   * @return a collection of WirelessNetworkInfo configurations
   */
  private static Collection<WirelessNetworkInfo> parseIWList(Collection<String> scanData) {
    /**
     * Instantiate a new Collection. Use a HashSet since there is no particular
     * sorting preference at this stage.
     */
    Collection<WirelessNetworkInfo> networks = new HashSet<>();
    /**
     * The physical detecting PORT is common to all detected networks.
     */
    String port = null;
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
      if (line.contains("Scan completed")) {
        // wlan0     Scan completed :
        port = line.split("\\s+")[0];
      } else if (line.startsWith("Cell")) {
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
        wireless.setPort(port);
        // Cell 01 - Address: F8:E4:FB:A0:FE:91
        wireless.setName(line.split("-")[0].trim());
        wireless.setMacAddress(line.split(":", 2)[1]);
        wireless.setBssid(line.split(":", 2)[1]);
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
      if (line.startsWith("Channel")) {
        // Channel:6
        wireless.setChannel(Integer.valueOf(line.split(":")[1]));
      } else if (line.startsWith("Frequency")) {
        // Frequency:2.437 GHz (Channel 6)
        wireless.setFrequency(Double.valueOf(line.split(":")[1].split(" ")[0]));
      } else if (line.startsWith("Quality")) {
        // Quality=57/70  Signal level=-53 dBm
        Matcher m = Pattern.compile("=(\\d+)/(\\d+) ").matcher(line);
        if (m.find()) {
          wireless.setQuality(Double.valueOf(m.group(1)) / Double.valueOf(m.group(2)));
        }
        wireless.setSignalLevel(Double.valueOf(line.split("=")[2].replaceAll("[a-zA-Z]", "").trim()));
      } else if (line.startsWith("Encryption key")) {
        // Encryption key:on
        wireless.setEncryption("on".equals(line.split(":")[1]));
        // Turn on WEP by default. WEP is turned off below if WPA is enabled.
      } else if (line.startsWith("ESSID")) {
        // ESSID:"WIFINET"
        wireless.setEssid(UTFCharacterUtility.substitute(line.split(":")[1].replaceAll("\"", "")));
        wireless.setSsid(UTFCharacterUtility.substitute(line.split(":")[1].replaceAll("\"", "")));
      } else if (line.startsWith("Mode")) {
        // Mode:Master
        wireless.setMode(line.split(":")[1]);
      } else if (line.contains("IEEE 802.11i/WPA2")) {
        // IE: IEEE 802.11i/WPA2 Version 1
        wireless.setWpa(Boolean.TRUE);
        wireless.setWep(Boolean.FALSE);
      } else if (line.startsWith("WPA Version 1")) {
        // IE: WPA Version 1
        wireless.setWpa(Boolean.TRUE);
        wireless.setWep(Boolean.FALSE);
      } else if (line.contains("Mb/s;")) {
        // Bit Rates:1 Mb/s; 2 Mb/s; 5.5 Mb/s; 11 Mb/s; 9 Mb/s
        //           18 Mb/s; 36 Mb/s; 54 Mb/s
        // Bit Rates:6 Mb/s; 12 Mb/s; 24 Mb/s; 48 Mb/s
        for (String rate : line.split(";")) {
          wireless.addBitRate(Double.valueOf(rate.replaceAll("[a-zA-Z:/ ]", "")));
        }
      } else if (line.startsWith("Extra")) {
        if (line.contains("tsf")) {
          // Extra:tsf=00000003856835fe
          // @TODO: convert TSF from string to long.
//          wireless.setTsf(line.split("=")[1]);
        } else if (line.contains("Last beacon")) {
          // Extra: Last beacon: 240ms ago
          wireless.setLastSeen(Integer.valueOf(line.split(":")[2].replaceAll("\\D", "")));
        }
      }
    }
    /**
     * Add the last wireless network in the list. This also captures the case
     * where there is only one entry.
     */
    networks.add(wireless);
    /**
     * Done.
     */
    return networks;
  }

  //<editor-fold defaultstate="collapsed" desc="Getter and Setter">
  public String getPort() {
    return port;
  }

  public void setPort(String port) {
    this.port = port;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getMacAddress() {
    return macAddress;
  }

  public void setMacAddress(String macAddress) {
    this.macAddress = macAddress;
  }

  public Integer getChannel() {
    return channel;
  }

  public void setChannel(Integer channel) {
    this.channel = channel;
  }

  public Double getFrequency() {
    return frequency;
  }

  public void setFrequency(Double frequency) {
    this.frequency = frequency;
  }

  public Double getQuality() {
    return quality;
  }

  public void setQuality(Double quality) {
    this.quality = quality;
  }

  public Double getSignalLevel() {
    return signalLevel;
  }

  public void setSignalLevel(Double signalLevel) {
    this.signalLevel = signalLevel;
  }

  public Boolean getEncryption() {
    return encryption;
  }

  public void setEncryption(Boolean encryption) {
    this.encryption = encryption;
  }

  public Boolean getWep() {
    return wep;
  }

  public void setWep(Boolean wep) {
    this.wep = wep;
  }

  public Boolean getWpa() {
    return wpa;
  }

  public void setWpa(Boolean wpa) {
    this.wpa = wpa;
  }

  public String getSsid() {
    return ssid;
  }

  public void setSsid(String ssid) {
    this.ssid = ssid;
  }

  public String getBssid() {
    return bssid;
  }

  public void setBssid(String bssid) {
    this.bssid = bssid;
  }

  public String getEssid() {
    // return the SSID if not explicitly set.
    return essid != null ? essid : ssid;
  }

  public void setEssid(String essid) {
    this.essid = essid;
  }

  public Collection<Double> getBitRates() {
    if (bitRates == null) {
      bitRates = new TreeSet<>();
    }
    return bitRates;
  }

  public void setBitRates(Collection<Double> bitRates) {
    this.bitRates = bitRates;
  }

  public void addBitRate(Double bitRate) {
    getBitRates().add(bitRate);
  }

  public String getMode() {
    return mode;
  }

  public void setMode(String mode) {
    this.mode = mode;
  }

  public Long getTSF() {
    return tsf;
  }

  public void setTSF(Long tsf) {
    this.tsf = tsf;
  }

  public Integer getLastSeen() {
    return lastSeen;
  }

  public void setLastSeen(Integer lastSeen) {
    this.lastSeen = lastSeen;
  }//</editor-fold>

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 97 * hash + Objects.hashCode(this.macAddress);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final WirelessNetworkInfo other = (WirelessNetworkInfo) obj;
    return Objects.equals(this.macAddress, other.macAddress);
  }

  @Override
  public String toString() {
    return "WirelessInterfaceInfo{" + "name=" + name
           + ", macAddress=" + macAddress
           + ", channel=" + channel
           + ", frequency=" + frequency
           + ", quality=" + quality
           + ", signalLevel=" + signalLevel
           + ", encryption=" + encryption
           + ", wep=" + wep
           + ", wpa=" + wpa
           + ", ssid=" + ssid
           + ", bssid=" + bssid
           + ", essid=" + essid
           + ", bitRates=" + bitRates
           + ", mode=" + mode
           + ", timeStamp=" + tsf
           + ", lastBeacon=" + lastSeen
           + '}';
  }

  /**
   * Comparison is alphabetic based on the SSID value.
   *
   * @param o the other network
   * @return the sort order.
   */
  @Override
  public int compareTo(WirelessNetworkInfo o) {
    return this.ssid.compareTo(o.getSsid());
  }

}
