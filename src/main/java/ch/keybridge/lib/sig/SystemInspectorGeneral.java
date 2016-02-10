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
package ch.keybridge.lib.sig;

import ch.keybridge.lib.sig.enumerated.ESystemType;
import ch.keybridge.lib.sig.hw.*;
import ch.keybridge.lib.sig.hw.net.NetworkInterfaceInfo;
import ch.keybridge.lib.sig.hw.net.WirelessNetworkInfo;
import ch.keybridge.lib.sig.hw.sensor.ThermalInfo;
import ch.keybridge.lib.sig.sw.OperatingSystemInfo;
import com.sun.jna.Platform;
import java.io.IOException;
import java.util.Collection;

/**
 * The main system inspector class. This is the {@code SIG} entry point.
 *
 * @author Key Bridge LLC
 * @since 1.0.0 (01/31/16)
 */
public class SystemInspectorGeneral {

  /**
   * The current platform type.
   */
  private final ESystemType currentPlatform;

  /**
   * Private constructor - use {@link #getInstance()}
   */
  private SystemInspectorGeneral() {
    if (Platform.isWindows()) {
      this.currentPlatform = ESystemType.WINDOWS;
    } else if (Platform.isLinux()) {
      this.currentPlatform = ESystemType.LINUX;
    } else if (Platform.isMac()) {
      this.currentPlatform = ESystemType.MAC;
    } else {
      this.currentPlatform = ESystemType.UNSPECIFIED;
    }
  }

  /**
   * Get a new SystemInspectorGeneral Instance.
   *
   * @return a new SystemInspectorGeneral Instance.
   */
  public static SystemInspectorGeneral getInstance() {
    return new SystemInspectorGeneral();
  }

  /**
   * Read and parse Operating system identifying information.
   * <p>
   * This reads and parses entries from the {@code /etc/os-release} and
   * {@code /proc/version_signature} system files.
   *
   * @return a OperatingSystemInfo container
   * @throws IOException if the required system files cannot be read and parsed.
   * @throws Exception   is the {@code uname} system command fails to execute.
   */
  public OperatingSystemInfo getOperatingSystemInfo() throws IOException, Exception {
    return OperatingSystemInfo.getInstance();
  }

  /**
   * Get an instance of a CPUInfo descriptor.
   *
   * @return a CPUInfo instance
   * @throws IOException if the file {@code /proc/cpuinfo} cannot be read.
   */
  public CPUInfo getCPUInfo() throws IOException {
    return CPUInfo.getInstance();
  }

  /**
   * Get a instance of a system memory descriptor. This reads and parses the
   * {@code /prc/meminfo} and populates the internal configuration.
   *
   * @return a memory descriptor
   * @throws IOException if the file {@code /prc/meminfo} cannot be read
   */
  public MemoryInfo getMemoryInfo() throws IOException {
    return MemoryInfo.getInstance();
  }

  /**
   * Read and parse all Files System instances on the current system. This
   * executes the {@code df -k} system command and parses the output.
   *
   * @return a collection of FileSystemInfo configurations
   * @throws Exception if the {@code df -k} system command fails to execute
   */
  public Collection<FileSystemInfo> getFileSystemInfo() throws Exception {
    return FileSystemInfo.getAllInstances();
  }

  /**
   * Scan the system and read statistics for all available interfaces.
   * <p>
   * This method parses the file {@code /proc/net/dev} and then builds a
   * NetworkInterfaceInfo instance for each discovered interface entry.
   *
   * @return a collection of NetworkInterfaceInfo configurations
   * @throws IOException if the file {@code /proc/net/dev} cannot be parsed
   */
  public Collection<NetworkInterfaceInfo> getNetworkInterfaceInfo() throws IOException {
    return NetworkInterfaceInfo.getAllInterfaces();
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
  public NetworkInterfaceInfo getNetworkInterfaceInfo(String name) throws IOException {
    return NetworkInterfaceInfo.getInstance(name);
  }

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
  public Collection<WirelessNetworkInfo> getWirelessNetworkInfo() throws IOException, Exception {
    return WirelessNetworkInfo.scanIW();
  }

  /**
   * Determine if this system is headless; that is - if it has NO display.
   *
   * @return TRUE if NO display is configured.
   */
  public boolean isHeadless() {
    try {
      return DisplayInfo.getAllDisplays().isEmpty();
    } catch (Exception exception) {
      return true;
    }
  }

  /**
   * Gets DisplayInfo Information. This method executes the
   * {@code xrandr --verbose} system command, then parses the output.
   *
   * @return An array of DisplayInfo objects representing monitors, etc.
   * @throws Exception if the {@code xrandr --verbose} system command fails to
   *                   execute.
   */
  public Collection<DisplayInfo> getDisplayInfo() throws Exception {
    return DisplayInfo.getAllDisplays();
  }

  /**
   * Determine if this system has a managed power supply, such as a battery or
   * UPS.
   *
   * @return TRUE if this system has a managed power supply, otherwise false.
   */
  public boolean hasPowerSupply() {
    try {
      return !PowerSupplyInfo.getAllInstances().isEmpty();
    } catch (IOException iOException) {
      return false;
    }
  }

  /**
   * Read and parse all PowerSupplyInfo instances on the current system.
   *
   * @return a collection of power supply instances.
   * @throws IOException if any power supply configuration cannot be read
   */
  public Collection<PowerSupplyInfo> getPowerSupplyInfo() throws IOException {
    return PowerSupplyInfo.getAllInstances();
  }

  /**
   * Scan, read and parse all available temperature sensor configurations on the
   * current system.
   * <p>
   * This method reads sensor information from the
   * {@code /sys/class/hwmon/hwmon[i]/device/temp[i]} kernel run time files.
   *
   * @return a collection of ThermalInfo configurations
   */
  public Collection<ThermalInfo> getThermalInfo() {
    return ThermalInfo.getAllInstances();
  }

  /**
   * Get the current platform.
   *
   * @return the current platform.
   */
  public ESystemType getPlatform() {
    return currentPlatform;
  }

  @Override
  public String toString() {
    return currentPlatform.name();
  }
}
