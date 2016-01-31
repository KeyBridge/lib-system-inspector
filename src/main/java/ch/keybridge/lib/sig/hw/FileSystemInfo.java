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
package ch.keybridge.lib.sig.hw;

import ch.keybridge.lib.sig.utility.SIGUtility;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * The File System is a storage pool, device, partition, volume, concrete file
 * system or other implementation specific means of file storage. See subclasses
 * for definitions as they apply to specific platforms.
 * <p>
 * Shows information about the file systems.
 *
 * @author Key Bridge LLC
 * @since 1.0.0 (01/31/16) (01/31/16)
 */
public class FileSystemInfo {

  /**
   * The name of the storage (e.g. /dev/sda1)
   */
  private String name;
  /**
   * Total Size of the file system. (Kbyte)
   */
  private Long size;
  /**
   * The used capacity of the the file system. (Kbyte)
   */
  private Long used;
  /**
   * The free (available) capacity of the the file system. (Kbyte)
   */
  private Long available;
  /**
   * The path where the file system is mounted.
   */
  private String mountPoint;

  /**
   * Read and parse all Files System instances on the current system. This
   * executes the {@code df -k} system command and parses the output.
   *
   * @return a collection of FileSystemInfo configurations
   * @throws Exception if the {@code df -k} system command fails to execute
   */
  public static Collection<FileSystemInfo> getAllInstances() throws Exception {
    Collection<FileSystemInfo> fsInfo = new HashSet<>();
    for (String dfEntry : SIGUtility.execute("df", "-k")) {
      try {
        fsInfo.add(FileSystemInfo.parseDFEntry(dfEntry));
      } catch (Exception e) {
        /**
         * The df output (first line) header fails to parse. Ignore this
         * expected error.
         */
      }
    }
    return fsInfo;
  }

  /**
   * Parse a line from the output of the {@code df -k} system command into a
   * FileSystemInfo configuration.
   *
   * @param entry one line from the {@code df -k} system command
   * @return a FileSystemInfo instance
   */
  private static FileSystemInfo parseDFEntry(String entry) {
    FileSystemInfo fs = new FileSystemInfo();
    String[] tokens = entry.split("\\s+");
    if (tokens.length != 6) {
      throw new IllegalArgumentException("Invalid entry: " + entry);
    }

    fs.setName(tokens[0]);
    fs.setSize(Long.valueOf(tokens[1]));
    fs.setUsed(Long.valueOf(tokens[2]));
    fs.setAvailable(Long.valueOf(tokens[3]));
    fs.setMountPoint(tokens[5]);

    return fs;
  }

  //<editor-fold defaultstate="collapsed" desc="Getter and Setter">
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Long getSize() {
    return size;
  }

  public void setSize(Long size) {
    this.size = size;
  }

  public Long getUsed() {
    return used;
  }

  public void setUsed(Long used) {
    this.used = used;
  }

  public Long getAvailable() {
    return available;
  }

  public void setAvailable(Long available) {
    this.available = available;
  }

  public String getMountPoint() {
    return mountPoint;
  }

  public void setMountPoint(String mountPoint) {
    this.mountPoint = mountPoint;
  }

  /**
   * The used capacity of the the file system. (Percent).
   *
   * @return The used capacity of the the file system.
   */
  public Double getUsedPercent() {
    return used.doubleValue() / size.doubleValue();
  }//</editor-fold>

  /**
   * Convert a kilobyte (kB) value to a Mbyte (MB) value.
   * <p>
   * A kilobyte (kB) is a decimal multiple of the unit byte for digital
   * information or computer storage. The prefix kilo (symbol k) is defined in
   * the International System of Units (SI) as a multiplier of 10<sup>3</sup>,
   * therefore, 1 kilobyte = 10<sup>3</sup> bytes = 1000 bytes.
   * <p>
   * In the immediate configuration however, and traditionally, this metric
   * prefix is used to designate binary multiplier 2<sup>10</sup> = 1024, so 1
   * Kbyte = 1024 bytes (note the capital K). The correct prefix for
   * 2<sup>10</sup>, kibibyte (KiB), introduced by the International
   * Electrotechnical Commission (IEC) in 1999, was not adopted by the computer
   * and telecommunication industry.
   *
   * @param kByte a value in kilobyte (kB)
   * @return the value, converted to Mbyte (MB) (i.e. divided by 1024).
   */
  public static Double toMByte(Long kByte) {
    return kByte.doubleValue() / 1024d;
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 97 * hash + Objects.hashCode(this.name);
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
    final FileSystemInfo other = (FileSystemInfo) obj;
    return Objects.equals(this.name, other.name);
  }

  @Override
  public String toString() {
    return name + " mounted at " + mountPoint;
  }

}
