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
import java.io.IOException;
import java.nio.file.Paths;

/**
 * A system memory container presenting memory status information from
 * {@code  /proc/meminfo}.
 * <p>
 * Provides information about distribution and utilization of memory. This
 * varies by architecture and compile options.
 * <p>
 * Memory refers to the state information of a computing system, as it is kept
 * active in some physical structure. The term "memory" is used for the
 * information in physical systems which are fast (ie. RAM), as a distinction
 * from physical systems which are slow to access (ie. data storage). By design,
 * the term "memory" refers to temporary state devices, whereas the term
 * "storage" is reserved for permanent data.
 *
 * @author Key Bridge LLC
 * @since 1.0.0 (01/31/16)
 * @see
 * <a href="https://git.kernel.org/cgit/linux/kernel/git/torvalds/linux.git/plain/Documentation/filesystems/proc.txt?id=34e431b0ae398fc54ea69ff85ec700722c9da773">The
 * /proc Filesystem</a>
 */
public class MemoryInfo {

  /**
   * Total usable ram (i.e. physical ram minus a few reserved bits and the
   * kernel binary code), in kByte.
   */
  private Long total;
  /**
   * An estimate of how much memory is available for starting new applications,
   * without swapping, in kByte.
   * <p>
   * Calculated from MemFree, SReclaimable, the size of the file LRU lists, and
   * the low watermarks in each zone. The estimate takes into account that the
   * system needs some page cache to function well, and that not all reclaimable
   * slab will be reclaimable, due to items being in use. The impact of those
   * factors will vary from system to system.
   */
  private Long available;

  /**
   * The total amount of swap space available, in kByte.
   */
  private Long swapTotal;
  /**
   * The total memory which has been evicted from RAM, and is temporarily on the
   * disk, in kByte.
   */
  private Long swapAvailable;

  /**
   * Get a instance of a system memory descriptor. This reads and parses the
   * {@code /prc/meminfo} and populates the internal configuration.
   *
   * @return a memory descriptor
   * @throws IOException if the file {@code /prc/meminfo} cannot be read
   */
  public static MemoryInfo getInstance() throws IOException {
    MemoryInfo memory = new MemoryInfo();
    /**
     * Scan the /proc/meminfo file for memory information.
     * <p>
     * From
     * https://git.kernel.org/cgit/linux/kernel/git/torvalds/linux.git/commit/?id=34e431b0ae398fc54ea69ff85ec700722c9da773
     * <p>
     * Currently, the amount of memory that is available for a new workload,
     * without pushing the system into swap, can be estimated from MemFree,
     * Active(file), Inactive(file), and SReclaimable, as well as the "low"
     * watermarks from /proc/zoneinfo.
     */
    long tempAvailable = 0;
    for (String entry : SIGUtility.readFileLines(Paths.get("/proc/meminfo"))) {
      /**
       * If MemAvailable is present then use that value.
       */
      if (entry.startsWith("MemAvailable")) {
        memory.setAvailable(Long.valueOf(entry.split(":")[1].replaceAll("\\D", "")));
      }
      /**
       * If MemAvailable is not present the calculate the sum of MemFree +
       * Active(file), Inactive(file), and Reclaimable.
       */
      if (entry.startsWith("MemFree")) {
        tempAvailable += Long.valueOf(entry.split(":")[1].replaceAll("\\D", ""));
      }
      if (entry.startsWith("Active(file)")) {
        tempAvailable += Long.valueOf(entry.split(":")[1].replaceAll("\\D", ""));
      }
      if (entry.startsWith("Inactive(file)")) {
        tempAvailable += Long.valueOf(entry.split(":")[1].replaceAll("\\D", ""));
      }
      if (entry.startsWith("SReclaimable")) {
        tempAvailable += Long.valueOf(entry.split(":")[1].replaceAll("\\D", ""));
      }
      /**
       * Set the total memory information.
       */
      if (entry.startsWith("MemTotal")) {
        memory.setTotal(Long.valueOf(entry.split(":")[1].replaceAll("\\D", "")));
      }
      /**
       * Set the SWAP values.
       */
      if (entry.startsWith("SwapFree")) {
        memory.setSwapAvailable(Long.valueOf(entry.split(":")[1].replaceAll("\\D", "")));
      }
      if (entry.startsWith("SwapTotal")) {
        memory.setSwapTotal(Long.valueOf(entry.split(":")[1].replaceAll("\\D", "")));
      }
    }
    /**
     * If MemAvailable is not present then use the assembled value.
     */
    if (memory.getAvailable() == null) {
      memory.setAvailable(tempAvailable);
    }

    return memory;
  }

  /**
   * Helper method to parse and convert a string value in KBytes to bytes.
   *
   * @param kilobyte the value, in KBytes
   * @return the value, in Bytes
   */
  public static Long asByteCount(Long kilobyte) {
    return kilobyte * 1024;
  }

  public Long getTotal() {
    return total;
  }

  public void setTotal(Long total) {
    this.total = total;
  }

  public Long getAvailable() {
    return available;
  }

  public void setAvailable(Long available) {
    this.available = available;
  }

  public Long getSwapTotal() {
    return swapTotal;
  }

  public void setSwapTotal(Long swapTotal) {
    this.swapTotal = swapTotal;
  }

  public Long getSwapAvailable() {
    return swapAvailable;
  }

  public void setSwapAvailable(Long swapAvailable) {
    this.swapAvailable = swapAvailable;
  }

  @Override
  public String toString() {
    return (total / 1024) + " MByte total, " + (available / 1024) + " MByte available";
  }

}
