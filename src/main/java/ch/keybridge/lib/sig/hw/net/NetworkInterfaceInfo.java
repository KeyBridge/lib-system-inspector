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
package ch.keybridge.lib.sig.hw.net;

import ch.keybridge.lib.sig.utility.SIGUtility;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Network interface information. Contains configuration and statistics for a
 * (physical) communications port.
 * <p>
 * Note that this class does NOT include IP address information, which may be
 * readily (and more easily) collected using the
 * {@code java.util.NetworkInterface} class utilities. Instead, this utility
 * class supplements the {@code java.util.NetworkInterface} with useful run-time
 * (hardware) configuration details and operating statistics.
 *
 *
 * @author Key Bridge LLC
 * @see
 * <a href="https://www.kernel.org/doc/Documentation/ABI/testing/sysfs-class-net">sysfs</a>
 */
public class NetworkInterfaceInfo {

  /**
   * The textual name of the interface as assigned by the CPE.
   */
  private String name;

  /**
   * The MAC address. A media access control address (MAC address), also called
   * physical address, is a unique identifier assigned to network interfaces for
   * communications on the physical network segment.
   */
  private String macAddress;

  /**
   * The configured speed (Mbps). Indicates the interface latest or current
   * speed value. Value is an integer representing the link speed in Mbits/sec.
   * <p>
   * Note: this attribute is only valid for interfaces that implement the
   * ethtool get_settings method (mostly Ethernet ).
   */
  private Integer speed;
  /**
   * The interface link state.
   */
  private ELinkState linkState;

  /**
   * The interface duplex mode.
   */
  private EDuplex duplex;

  /**
   * The total number of bytes of data transmitted by the interface.
   */
  private Long rxBytes;
  /**
   * The total number of packets of data received by the interface.
   */
  private Long rxPackets;
  /**
   * The total number of packets dropped by the device driver.
   */
  private Long rxDropped;
  /**
   * The total number of receive errors detected by the device driver.
   */
  private Long rxErrors;

  /**
   * The total number of bytes of data transmitted by the interface.
   */
  private Long txBytes;
  /**
   * The total number of packets of data transmitted by the interface.
   */
  private Long txPackets;
  /**
   * The total number of packets dropped by the device driver.
   */
  private Long txDropped;
  /**
   * The total number of transmit errors detected by the device driver.
   */
  private Long txErrors;

  /**
   * The number of carrier losses detected by the device driver.
   */
  private Long txCarrier;

  /**
   * colls The number of collisions detected on the interface.
   */
  private Long collisions;
  /**
   * The number of multicast frames received by the device driver.
   */
  private Long multicast;

  /**
   * IPV6 bytes received.
   */
  private Long ip6InOctets;
  /**
   * IPV6 bytes transmitted.
   */
  private Long ip6OutOctets;
  /**
   * IPV6 multicast bytes received.
   */
  private Long ip6InMcastOctets;
  /**
   * IPV6 multicast bytes sent.
   */
  private Long ip6OutMcastOctets;
  /**
   * IPV6 multicast packets received.
   */
  private Long ip6InMcastPkts;
  /**
   * IPV6 multicast bytes sent.
   */
  private Long ip6OutMcastPkts;

  /**
   * Scan the system and read statistics for all available interfaces.
   * <p>
   * This method parses the file {@code /proc/net/dev} and then builds a
   * NetworkInterfaceInfo instance for each discovered interface entry.
   *
   * @return a collection of NetworkInterfaceInfo configurations
   * @throws IOException if the file {@code /proc/net/dev} cannot be parsed
   */
  public static Collection<NetworkInterfaceInfo> getAllInterfaces() throws IOException {
    Collection<NetworkInterfaceInfo> networks = new HashSet<>();

    for (String line : SIGUtility.readFileLines(Paths.get("/proc/net/dev"))) {
      if (line.contains(":")) {
        networks.add(NetworkInterfaceInfo.getInstance(line.trim().split(":")[0].trim()));
      }
    }

    return networks;
  }

  /**
   * Get a single NetworkInterfaceInfo instance for the indicated interface
   * name. This reads and parses configuration details from the appropriate
   * {@code /sys/class/net/{name}/statistics} files.
   *
   * @param name the interface name. e.g. "eth0". This corresponds to the
   *             java.net.NetworkInterface {@code name} parameter (accessed via
   *             the getName() method.
   * @return a NetworkInterfaceInfo configuration
   * @throws IOException if the {@code /sys/class/net/{name}/statistics} files
   *                     cannot be read.
   */
  public static NetworkInterfaceInfo getInstance(String name) throws IOException {
    /**
     * Get the IP address config from the java.net.NetworkInterface class - it's
     * so much easier.
     */
    String path = "/sys/class/net/" + name;
    /**
     * Ensure that a valid interface name is received.
     */
    if (!Paths.get(path).toFile().exists()) {
      throw new FileNotFoundException(name + " configuration files not found on this system.");
    }
    NetworkInterfaceInfo interfaceInfo = new NetworkInterfaceInfo();
    interfaceInfo.setName(name);
    interfaceInfo.setMacAddress(SIGUtility.readFileString(Paths.get(path, "address")));
    interfaceInfo.setLinkState(ELinkState.valueOf(SIGUtility.readFileString(Paths.get(path, "operstate"))));
    /**
     * The speed and duplex files fail to read for loopback type interfaces.
     */
    try {
      interfaceInfo.setSpeed(SIGUtility.readFileInteger(Paths.get(path, "speed")));
      interfaceInfo.setDuplex(EDuplex.valueOf(SIGUtility.readFileString(Paths.get(path, "duplex"))));
    } catch (IOException | NumberFormatException iOException) {
    }
    /**
     * Interface statistics typically read OK, but are optional. Try but don't
     * fail on error.
     */
    try {
      interfaceInfo.setRxBytes(SIGUtility.readFileLong(Paths.get(path, "statistics", "rx_bytes")));
      interfaceInfo.setTxBytes(SIGUtility.readFileLong(Paths.get(path, "statistics", "tx_bytes")));

      interfaceInfo.setRxPackets(SIGUtility.readFileLong(Paths.get(path, "statistics", "rx_packets")));
      interfaceInfo.setTxPackets(SIGUtility.readFileLong(Paths.get(path, "statistics", "tx_packets")));

      interfaceInfo.setRxDropped(SIGUtility.readFileLong(Paths.get(path, "statistics", "rx_dropped")));
      interfaceInfo.setTxDropped(SIGUtility.readFileLong(Paths.get(path, "statistics", "tx_dropped")));

      interfaceInfo.setRxErrors(SIGUtility.readFileLong(Paths.get(path, "statistics", "rx_errors")));
      interfaceInfo.setTxErrors(SIGUtility.readFileLong(Paths.get(path, "statistics", "tx_errors")));

      interfaceInfo.setTxCarrier(SIGUtility.readFileLong(Paths.get(path, "statistics", "tx_carrier_errors")));

      interfaceInfo.setCollisions(SIGUtility.readFileLong(Paths.get(path, "statistics", "collisions")));
      interfaceInfo.setMulticast(SIGUtility.readFileLong(Paths.get(path, "statistics", "multicast")));
    } catch (IOException | NumberFormatException iOException) {
      Logger.getLogger(NetworkInterfaceInfo.class.getName()).log(Level.WARNING, "Error reading statistics for interface {0}", name);
    }
    /**
     * Build and populate a Stats container for the indicated IP address. This
     * method reads and parses the {@code /proc/net/dev_snmp} kernel run time
     * file.
     * <p>
     * Note: Packet and Byte statistics are reported against the physical (i.e.
     * Ethernet) interface and NOT against the IP address. ONLY IPv6 statistics
     * are reported at the IP layer.
     */
    try {
      Map<String, Long> x = new HashMap<>();
      for (String line : Files.readAllLines(Paths.get("/proc/net/dev_snmp6/", name))) {
        String[] tokens = line.split("\\s+");
        x.put(tokens[0], Long.valueOf(tokens[1]));
      }
      interfaceInfo.setIp6InOctets(x.get("Ip6InOctets"));
      interfaceInfo.setIp6OutOctets(x.get("Ip6OutOctets"));
      interfaceInfo.setIp6InMcastOctets(x.get("Ip6InMcastOctets"));
      interfaceInfo.setIp6OutMcastOctets(x.get("Ip6OutMcastOctets"));
      interfaceInfo.setIp6InMcastPkts(x.get("Ip6InMcastPkts"));
      interfaceInfo.setIp6OutMcastPkts(x.get("Ip6OutMcastPkts"));
    } catch (IOException | NumberFormatException iOException) {
    }

    return interfaceInfo;
  }
  //<editor-fold defaultstate="collapsed" desc="Getter and Setter">

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getSpeed() {
    return speed;
  }

  public void setSpeed(Integer speed) {
    this.speed = speed;
  }

  public ELinkState getLinkState() {
    return linkState;
  }

  public void setLinkState(ELinkState linkState) {
    this.linkState = linkState;
  }

  public String getMacAddress() {
    return macAddress;
  }

  public void setMacAddress(String macAddress) {
    this.macAddress = macAddress;
  }

  public EDuplex getDuplex() {
    return duplex;
  }

  public void setDuplex(EDuplex duplex) {
    this.duplex = duplex;
  }

  public Long getRxBytes() {
    return rxBytes;
  }

  public void setRxBytes(Long rxBytes) {
    this.rxBytes = rxBytes;
  }

  public Long getRxPackets() {
    return rxPackets;
  }

  public void setRxPackets(Long rxPackets) {
    this.rxPackets = rxPackets;
  }

  public Long getRxDropped() {
    return rxDropped;
  }

  public void setRxDropped(Long rxDropped) {
    this.rxDropped = rxDropped;
  }

  public Long getRxErrors() {
    return rxErrors;
  }

  public void setRxErrors(Long rxErrors) {
    this.rxErrors = rxErrors;
  }

  public Long getTxBytes() {
    return txBytes;
  }

  public void setTxBytes(Long txBytes) {
    this.txBytes = txBytes;
  }

  public Long getTxPackets() {
    return txPackets;
  }

  public void setTxPackets(Long txPackets) {
    this.txPackets = txPackets;
  }

  public Long getTxDropped() {
    return txDropped;
  }

  public void setTxDropped(Long txDropped) {
    this.txDropped = txDropped;
  }

  public Long getTxErrors() {
    return txErrors;
  }

  public void setTxErrors(Long txErrors) {
    this.txErrors = txErrors;
  }

  public Long getTxCarrier() {
    return txCarrier;
  }

  public void setTxCarrier(Long txCarrier) {
    this.txCarrier = txCarrier;
  }

  public Long getCollisions() {
    return collisions;
  }

  public void setCollisions(Long collisions) {
    this.collisions = collisions;
  }

  public Long getMulticast() {
    return multicast;
  }

  public void setMulticast(Long multicast) {
    this.multicast = multicast;
  }

  public Long getIp6InOctets() {
    return ip6InOctets;
  }

  public void setIp6InOctets(Long ip6InOctets) {
    this.ip6InOctets = ip6InOctets;
  }

  public Long getIp6OutOctets() {
    return ip6OutOctets;
  }

  public void setIp6OutOctets(Long ip6OutOctets) {
    this.ip6OutOctets = ip6OutOctets;
  }

  public Long getIp6InMcastOctets() {
    return ip6InMcastOctets;
  }

  public void setIp6InMcastOctets(Long ip6InMcastOctets) {
    this.ip6InMcastOctets = ip6InMcastOctets;
  }

  public Long getIp6OutMcastOctets() {
    return ip6OutMcastOctets;
  }

  public void setIp6OutMcastOctets(Long ip6OutMcastOctets) {
    this.ip6OutMcastOctets = ip6OutMcastOctets;
  }

  public Long getIp6InMcastPkts() {
    return ip6InMcastPkts;
  }

  public void setIp6InMcastPkts(Long ip6InMcastPkts) {
    this.ip6InMcastPkts = ip6InMcastPkts;
  }

  public Long getIp6OutMcastPkts() {
    return ip6OutMcastPkts;
  }

  public void setIp6OutMcastPkts(Long ip6OutMcastPkts) {
    this.ip6OutMcastPkts = ip6OutMcastPkts;
  }//</editor-fold>

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 43 * hash + Objects.hashCode(this.name);
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
    final NetworkInterfaceInfo other = (NetworkInterfaceInfo) obj;
    return Objects.equals(this.name, other.name);
  }

  @Override
  public String toString() {
    return name + (macAddress != null ? " HWaddr " + macAddress : "");
  }

  /**
   * Recognized operating states. This is read from the
   * {@code /sys/class/net/{name}/operstate} file.
   * <p>
   * Indicates the interface RFC2863 operational state as a string. Possible
   * values are: "unknown", "notpresent", "down", "lowerlayerdown", "testing",
   * "dormant", "up".
   */
  public static enum ELinkState {
    up, down, unknown, notpresent, lowerlayerdown, testing, dormant;
  }

  /**
   * Recognized duplex modes. This is read from the
   * {@code /sys/class/net/{name}/duplex} file.
   */
  public static enum EDuplex {
    auto, full, half;
  }

}
