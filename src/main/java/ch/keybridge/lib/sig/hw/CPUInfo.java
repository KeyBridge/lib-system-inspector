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
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Key Bridge LLC
 * @since 1.0.0 (01/31/16)
 */
public class CPUInfo {

  private static final OperatingSystemMXBean OS_MXBEAN = ManagementFactory.getOperatingSystemMXBean();

  // Logical and Physical Processor Counts
  /**
   * Physical CPUInfo count. This is the number physical CPUInfo chips on the
   * motherboard.
   */
  private int physicalCount = 0;
  /**
   * Logical CPUInfo count. This is the total number of physical CPUInfo (i.e.
   * sockets) times the number of cores per CPUInfo times the number of threads
   * per core. It essentially determines the number of parallel processes a
   * system can handle.
   */
  private int logicalCount = 0;

  /**
   * The CPUInfo vendor. e.g. "GenuineIntel"
   */
  private String vendor;
  /**
   * The processor model name. eg. Intel(R) Core(TM)2 Duo ICPU T7300 @ 2.00GHz
   */
  private String name;
  /**
   * The Identifier, eg. x86 Family 6 Model 15 Stepping 10.
   */
  private String identifier;
  /**
   * The term stepping level or revision level in the context of CPUInfo
   * architecture or integrated circuit is a version number. Stepping level
   * refers to the introduction or revision of the lithographic photomask or
   * masks within the set of plates that generate the pattern that produces an
   * integrated circuit.
   */
  private String stepping;
  /**
   * The CPUInfo model number.
   * <p>
   * Processor model numbers are used to distinguish between microprocessors
   * with different sets of features and different characteristics, such as
   * speed, level 1 and level 2 cache, etc. The model numbers can be used to
   * identify only basic features and most important characteristics, and may
   * not be used to identify all features/parameters of the CPUInfo. For
   * example, model numbers never identify CPUInfo ID, core stepping, core
   * voltage, maximum CPUInfo temperature and a few other parameters. If you
   * need to find out all details on specific processor please use S-spec
   * numbers for Intel processors or order product numbers for AMD
   * microprocessors.
   */
  private String model;
  /**
   * The CPUInfo family. The Family number is an 8-bit number derived from the
   * processor signature by adding the Extended Family number (bits 27:20) and
   * the Family number (bits 11:8). See section 5.1.2.2 of the "Intel Processor
   * Identification and the CPUID Instruction".
   */
  private String family;
  /**
   * The CPUInfo operating frequency. (MHz)
   */
  private Double frequency;
  /**
   * BogoMips (from "bogus" and MIPS) is an unscientific measurement of CPUInfo
   * speed made by the Linux kernel when it boots to calibrate an internal
   * busy-loop. An oft-quoted definition of the term is "the number of million
   * times per second a processor can do absolutely nothing".
   */
  private Double bogomips;

  /**
   * lm flag means Long mode cpu - 64 bit CPUInfo<br/>
   * Real mode 16 bit CPUInfo<br/>
   * Protected Mode is 32-bit CPUInfo<br/>
   */
  private Collection<String> flags;

  //<editor-fold defaultstate="collapsed" desc="Getter and Setter">
  public int getPhysicalCount() {
    return physicalCount;
  }

  public void setPhysicalCount(int physicalCount) {
    this.physicalCount = physicalCount;
  }

  public int getLogicalCount() {
    return logicalCount;
  }

  public void setLogicalCount(int logicalCount) {
    this.logicalCount = logicalCount;
  }

  public String getVendor() {
    return vendor;
  }

  public void setVendor(String vendor) {
    this.vendor = vendor;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  public String getStepping() {
    return stepping;
  }

  public void setStepping(String stepping) {
    this.stepping = stepping;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public String getFamily() {
    return family;
  }

  public void setFamily(String family) {
    this.family = family;
  }

  public Double getFrequency() {
    return frequency;
  }

  public void setFrequency(Double frequency) {
    this.frequency = frequency;
  }

  public Double getBogomips() {
    return bogomips;
  }

  public void setBogomips(Double bogomips) {
    this.bogomips = bogomips;
  }

  public Collection<String> getFlags() {
    if (flags == null) {
      flags = new HashSet<>();
    }
    return flags;
  }

  public void setFlags(Collection<String> flags) {
    this.flags = flags;
  }//</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Calculated Getter Methods">
  /**
   * Find If Processor (CPUInfo) is 64 bit / 32 bit [long mode ~ lm].
   * <p>
   * lm flag means Long mode (64 bit CPUInfo. Real mode 16 bit CPUInfo.
   * Protected Mode is 32-bit CPUInfo.
   *
   * @return
   */
  public boolean is64Bit() {
    return getFlags().contains("lm");
  }

  /**
   * Get the System uptime in seconds since the last restart.
   * <p>
   * This reads from {@code /proc/uptime}.
   *
   * @return Number of seconds since boot.
   */
  public Long getSystemUptime() throws IOException {
    /**
     * The first number is the total number of seconds the system has been up.
     * The second number is how much of that time the machine has spent idle, in
     * seconds.
     */
    String seconds = SIGUtility.readFileString(Paths.get("/proc/uptime")).split("\\s+")[0];
    return Double.valueOf(seconds).longValue();
  }

  /**
   * Get the System/ICPU Serial Number, if available. On Linux, this requires
   * either root permissions, or installation of the (deprecated) HAL library
   * (lshal command).
   *
   * @return the System/ICPU Serial Number, if available, otherwise returns
   *         "unknown"
   */
  public String getSystemSerialNumber() {
    /**
     * If root privilege, or if otherwise authorized via appArmor.
     */
    try {
      for (String string : SIGUtility.execute("dmidecode", "-t system")) {
        if (string.contains("UUID")) {
          return string.split(":")[1].trim();
        }
      }
    } catch (Exception ex) {
      Logger.getLogger(CPUInfo.class.getName()).log(Level.SEVERE, null, ex);
    }
    /**
     * Cannot determine.
     *
     * @TODO: Try to return a MAC addr as serial number.
     */
    return "unknown";
  }

  /**
   * Returns the "recent cpu usage" in percent for the whole system from
   * {@code /proc/stat}.
   *
   * @return the "recent cpu usage" for the whole system; a negative value if
   *         not available.
   */
  public Double getSystemUsage() {
    /**
     * {@link com.sun.management.OperatingSystemMXBean#getSystemCpuLoad()} if a
     * user is running the Oracle JVM. This value is a double in the [0.0,1.0]
     * interval. A value of 0.0 means that all CPUs were idle during the recent
     * period of time observed, while a value of 1.0 means that all CPUs were
     * actively running 100% of the time during the recent period being
     * observed. All values between 0.0 and 1.0 are possible depending of the
     * activities going on in the system. If the system recent cpu usage is not
     * available, the method returns a negative value. Calling this method
     * immediately upon instantiating the {@link ICPU} may give unreliable
     * results. If a user is not running the Oracle JVM, this method will
     * default to the behavior and return value of
     * {@link #getSystemCpuLoadBetweenTicks()}.
     */
    /**
     * Read /proc/stat
     * <p>
     * cpu — Measures the number of jiffies (1/100 of a second for x86 systems)
     * that the system has been in user mode, user mode with low priority
     * (nice), system mode, idle task, I/O wait, IRQ (hardirq), and softirq
     * respectively. The IRQ (hardirq) is the direct response to a hardware
     * event. The IRQ takes minimal work for queuing the "heavy" work up for the
     * softirq to execute. The softirq runs at a lower priority than the IRQ and
     * therefore may be interrupted more frequently. The total for all CPUs is
     * given at the top, while each individual CPU is listed below with its own
     * statistics.
     * <p>
     * The very first "cpu" line aggregates the numbers in all of the other
     * "cpuN" lines.
     * <p>
     * These numbers identify the amount of time the CPU has spent performing
     * different kinds of work. Time units are in USER_HZ or Jiffies (typically
     * hundredths of a second).
     * <p>
     * The meanings of the columns are as follows, from left to right:
     * <pre>
     * user: normal processes executing in user mode
     * nice: niced processes executing in user mode
     * system: processes executing in kernel mode
     * idle: twiddling thumbs
     * iowait: waiting for I/O to complete
     * irq: servicing interrupts
     * softirq: servicing softirqs</pre>
     * <p>
     * The "intr" line gives counts of interrupts serviced since boot time, for
     * each of the possible system interrupts. The first column is the total of
     * all interrupts serviced; each subsequent column is the total for that
     * particular interrupt.
     * <p>
     * The "ctxt" line gives the total number of context switches across all
     * CPUs.
     * <p>
     * The "btime" line gives the time at which the system booted, in seconds
     * since the Unix epoch.
     * <p>
     * The "processes" line gives the number of processes and threads created,
     * which includes (but is not limited to) those created by calls to the
     * fork() and clone() system calls.
     * <p>
     * The "procs_running" line gives the number of processes currently running
     * on CPUs.
     * <p>
     * The "procs_blocked" line gives the number of processes currently blocked,
     * waiting for I/O to complete.
     * <p>
     * Copied from the kernel documentation of the /proc filesystem
     * <p>
     * The 8th column is called steal_time. It counts the ticks spent executing
     * other virtual hosts (in virtualised environments like Xen)
     */
    try {
      for (String string : SIGUtility.readFileLines(Paths.get("/proc/stat"))) {
        String[] tokens = string.trim().split("\\s+");
        if (tokens[0].equals("cpu")) { // read only the aggregate number.
          return (Double.valueOf(tokens[1]) + Double.valueOf(tokens[2]) + Double.valueOf(tokens[3])) / Double.valueOf(tokens[4]);
//          return (d(tokens[1]) + d(tokens[3])) / (d(tokens[1]) + d(tokens[3]) + d(tokens[5]));
        }
      }
    } catch (IOException ex) {
      Logger.getLogger(CPUInfo.class.getName()).log(Level.SEVERE, null, ex);
    }
    return -1.0;
  }

  /**
   * Get a map detailing the usage (percent) of all processors on this system.
   * <p>
   * For a multiprocessor / multi-core system the returned map will include
   * calculated usage for each processor core.
   *
   * @return a sorted map containing entries of
   *         {@code [processor name, processor usage (%)]}
   */
  public Map<String, Double> getSystemUsageDetail() {
    Map<String, Double> systemUsage = new TreeMap<>();
    try {
      for (String string : SIGUtility.readFileLines(Paths.get("/proc/stat"))) {
        String[] tokens = string.trim().split("\\s+");
        if (tokens[0].startsWith("cpu") && !tokens[0].equals("cpu")) {
          systemUsage.put(tokens[0], (Double.valueOf(tokens[1]) + Double.valueOf(tokens[2]) + Double.valueOf(tokens[3])) / Double.valueOf(tokens[4]));
        }
      }
    } catch (IOException ex) {
      Logger.getLogger(CPUInfo.class.getName()).log(Level.SEVERE, null, ex);
    }
    return systemUsage;
  }

  private double d(String s) {
    return Double.valueOf(s);
  }

  /**
   * Returns the system load average for the last minute from
   * {@code /proc/loadavg}.
   * <p>
   * The system load average is the sum of the number of runnable entities
   * queued to the available processors and the number of runnable entities
   * running on the available processors averaged over a period of time. The way
   * in which the load average is calculated is operating system specific but is
   * typically a damped time-dependent average. If the load average is not
   * available, a negative value is returned. This method is designed to provide
   * a hint about the system load and may be queried frequently. The load
   * average may be unavailable on some platforms (e.g., Windows) where it is
   * expensive to implement this method.
   *
   * @return the system load average; or a negative value if not available.
   */
  public Double getSystemLoadAverage() throws IOException {
    /**
     * /proc/loadavg
     * <p>
     * The first three fields in this file are load average figures giving the
     * number of jobs in the run queue (state R) or waiting for disk I/O (state
     * D) averaged over 1, 5, and 15 minutes. They are the same as the load
     * average numbers given by uptime(1) and other programs.
     * <p>
     * The fourth field consists of two numbers separated by a slash (/). The
     * first of these is the number of currently executing kernel scheduling
     * entities (processes, threads); this will be less than or equal to the
     * number of CPUs. The value after the slash is the number of kernel
     * scheduling entities that currently exist on the system.
     * <p>
     * The fifth field is the PID of the process that was most recently created
     * on the system.
     * <p>
     */
    return Double.valueOf(SIGUtility.readFileString(Paths.get("/proc/loadavg")).split("\\s+")[0]);
  }//</editor-fold>

  /**
   * Get an instance of a CPUInfo descriptor.
   *
   * @return a CPUInfo instance
   * @throws IOException if the file {@code /proc/cpuinfo} cannot be read.
   */
  public static CPUInfo getInstance() throws IOException {
    /**
     * Put the common stuff into a map for processing and count up the physical
     * and logical processor units.
     */
    Map<String, String> cpuinfo = new HashMap<>();
    Set<String> physical = new HashSet<>();
    Set<String> processor = new HashSet<>();
    for (String entry : Files.readAllLines(Paths.get("/proc/cpuinfo"))) {
      /**
       * Split fails if the line has no value.
       */
      try {
        cpuinfo.put(entry.split(":")[0].trim(), entry.split(":")[1].trim());
      } catch (Exception e) {
        continue;
      }
      /**
       * Count up the physical and logical processor units.
       */
      if (entry.startsWith("procesor")) {
        processor.add(entry.split(":")[1].trim());
      }
      if (entry.startsWith("physical id")) {
        physical.add(entry.split(":")[1].trim());
      }

    }
    /**
     * Instantiate and populate a new CPU instance. Set the physical and logical
     * processor count.
     */
    CPUInfo cpu = new CPUInfo();
    cpu.setLogicalCount(processor.size());
    cpu.setPhysicalCount(physical.size());
    /**
     * Set the other parameters.
     */
    cpu.setVendor(cpuinfo.get("vendor_id"));
    cpu.setName(cpuinfo.get("model name"));
//    cpu.setIdentifier(cpuinfo.get("model name"));
    cpu.setStepping(cpuinfo.get("stepping"));
    cpu.setFamily(cpuinfo.get("cpu family"));
    cpu.setFrequency(Double.valueOf(cpuinfo.get("cpu MHz")));
    cpu.setBogomips(Double.valueOf(cpuinfo.get("bogomips")));
    cpu.setFlags(Arrays.asList(cpuinfo.get("flags").split("\\s+")));

    return cpu;
  }

  /**
   * Get the name.
   *
   * @return the name.
   */
  @Override
  public String toString() {
    return name;
  }

}
