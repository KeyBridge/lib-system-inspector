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
package ch.keybridge.lib.sig.sw;

import ch.keybridge.lib.sig.sw.config.NTPConfiguration;
import ch.keybridge.lib.sig.sw.config.SystemUserInfo;
import ch.keybridge.lib.sig.sw.run.ProcessInfo;
import ch.keybridge.lib.sig.sw.run.SocketInfo;
import ch.keybridge.lib.sig.utility.SIGUtility;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collection;

/**
 * Computer operating system information.
 * <p>
 * An operating system (OS) is the software on a computer that manages the way
 * different programs use its hardware, and regulates the ways that a user
 * controls the computer.
 * <p>
 * This implementation is written to read LINUX configurations.
 *
 * @author Key Bridge LLC
 * @since 1.0.0 (01/31/16) (01/31/16)
 */
public class OperatingSystemInfo {

  /**
   * The operating system family. e.g. "GNU/Linux"
   */
  public String family;

  /**
   * The operating system name. This is typically the publisher (i.e. "Ubuntu",
   * "Debian", "Red Hat", etc.)
   */
  public String name;
  /**
   * The operating system version. e.g. "12.04.3 LTS, Precise Pangolin"
   */
  public String version;
  /**
   * The Operating system version ID. This is typically a (terse) version
   * number. e.g. "12.04".
   */
  public String versionId;
  /**
   * The Operating system release. This is typically the compiled kernel
   * version. e.g. "3.2.0-55-generic".
   */
  public String release;

  /**
   * The machine hardware name. e.g. "x86_64"
   */
  public String machine;

  /**
   * Read and parse Operating system identifying information.
   * <p>
   * This reads and parses entries from the {@code /etc/os-release} and output
   * from the {@code uname} system command.
   *
   * @return a OperatingSystemInfo container
   * @throws IOException if the {@code /etc/os-release} system file cannot be
   *                     read and parsed.
   * @throws Exception   is the {@code uname} system command fails to execute.
   */
  public static OperatingSystemInfo getInstance() throws IOException, Exception {
    OperatingSystemInfo osInfo = new OperatingSystemInfo();

    /**
     * Read and parse the configuration file.
     */
    for (String line : SIGUtility.readFileLines(Paths.get("/etc/os-release"))) {
      switch (line.split("=")[0]) {
        case "NAME":
          osInfo.setName(stripQuotes(line.split("=")[1]));
          break;
        case "VERSION":
          osInfo.setVersion(stripQuotes(line.split("=")[1]));
          break;
        case "VERSION_ID":
          osInfo.setVersionId(stripQuotes(line.split("=")[1]));
          break;
      }
    }
    /**
     * Parse the kernel and machine information from the {uname} system command.
     */
    osInfo.setFamily(SIGUtility.executeSimple("uname", "-i"));
    osInfo.setRelease(SIGUtility.executeSimple("uname", "-r"));
    osInfo.setMachine(SIGUtility.executeSimple("uname", "-m"));
    return osInfo;
  }

  /**
   * Strip the first and last quote from the input string.
   *
   * @param string a quoted string. e.g. "this and that"
   * @return the unquoted string. e.g. this and that
   */
  private static String stripQuotes(String string) {
    return string.replaceAll("^\"|\"$", "");
  }

  //<editor-fold defaultstate="collapsed" desc="Getter and Setter">
  public String getFamily() {
    return family;
  }

  public void setFamily(String family) {
    this.family = family;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getVersionId() {
    return versionId;
  }

  public void setVersionId(String versionId) {
    this.versionId = versionId;
  }

  public String getRelease() {
    return release;
  }

  public void setRelease(String release) {
    this.release = release;
  }

  public String getMachine() {
    return machine;
  }

  public void setMachine(String machine) {
    this.machine = machine;
  }//</editor-fold>

  /**
   * A Collection of currently running processes.
   *
   * @return a non-null collection instance
   */
  public Collection<ProcessInfo> getProcesses() throws Exception {
    return ProcessInfo.getAllProcesses();
  }

  /**
   * Read and parse socket information from the {@code /proc/net/tcp} and
   * {@code /proc/net/tcp6} files.
   *
   * @return a TreeSet containing all open sockets.
   * @throws IOException if the {@code /proc/net/tcp} and {@code /proc/net/tcp6}
   *                     files cannot be read.
   */
  public Collection<SocketInfo> getSockets() throws Exception {
    return SocketInfo.getAllSockets();
  }

  /**
   * Read and parse the local {@code /etc/passwd} file into a collection of
   * LinuxOSUser instances.
   *
   * @return a HashSet containing all OS users.
   * @throws IOException if the {@code /etc/passwd} file cannot be read.
   */
  public Collection<SystemUserInfo> getUsers() throws IOException {
    return SystemUserInfo.getAllUsers();
  }

  /**
   * Construct a new LinuxNTPStatus and initialize the peers collection. This
   * executes and reads the response from the system command {@code ntpq -pn}.
   *
   * @throws Exception if the system command {@code ntpq -pn} fails to execute.
   */
  public NTPConfiguration getNTPConfiguration() throws Exception {
    return NTPConfiguration.getInstance();
  }

  @Override
  public String toString() {
    return name != null ? name : release;
  }

}
