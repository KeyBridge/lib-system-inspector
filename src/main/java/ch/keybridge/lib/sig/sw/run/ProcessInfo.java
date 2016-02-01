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
package ch.keybridge.lib.sig.sw.run;

import ch.keybridge.lib.sig.utility.SIGUtility;
import java.util.Calendar;
import java.util.Collection;
import java.util.Objects;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A process is an instance of a computer program that is being executed. It
 * contains the program code and its current activity. Depending on the
 * operating system (OS), a process may be made up of multiple threads of
 * execution that execute instructions concurrently.
 * <p>
 * OS-specific implementations may present additional fields than those
 * described in this interface.
 *
 * @author Key Bridge LLC
 * @since 1.0.0 (01/31/16)
 */
public class ProcessInfo implements Comparable<ProcessInfo> {

  /**
   * The process ID number of the process.
   */
  public Integer pid;
  /**
   * The executing command with all its arguments as a string.
   * <p>
   * Sometimes the process args will be unavailable; when this happens, ps will
   * instead print the executable name in brackets.
   */
  public String command;
  /**
   * The amount of time in milliseconds that the process has spent taking up CPU
   * time since the process was started.
   */
  public Integer cpuTime;
  /**
   * The priority of the process where 0 is highest.
   */
  public Integer priority;
  /**
   * The current state that the process is in.
   */
  public EState processState;

  /**
   * PROCESS FLAGS
   * <p>
   * The sum of these values is displayed in the "F" column, which is provided
   * by the flags output specifier:
   * <p>
   * 1 forked but didn't exec
   * <p>
   * 4 used super-user privileges
   */
  private Integer flag;
  /**
   * Effective user ID (alias uid).
   */
  private String uid;
  /**
   * Parent process ID.
   */
  private Integer ppid;
  /**
   * Processor utilization. Currently, this is the integer value of the percent
   * usage over the lifetime of the process. (see %cpu).
   */
  private Integer cpuUtilization;
  /**
   * Nice value. This ranges from 19 (nicest) to -20 (not nice to others), see
   * nice(1). (alias nice).
   */
  private Integer nice;
  /**
   * Address of the kernel function where the process is sleeping (use wchan if
   * you want the kernel function name). Running tasks will display a dash ('-')
   * in this column.
   */
  private Integer addr;
  /**
   * Approximate amount of swap space that would be required if the process were
   * to dirty all writable pages and then be swapped out. This number is very
   * rough!
   * <p>
   * (Byte)
   */
  private Integer size;
  /**
   * Name of the kernel function in which the process is sleeping, a "-" if the
   * process is running, or a "*" if the process is multi-threaded and ps is not
   * displaying threads.
   */
  private String wchan;
  /**
   * Starting time or date of the process. Only the year will be displayed if
   * the process was not started the same year ps was invoked, or "MmmDD" if it
   * was not started the same day, or "HH:MM" otherwise.
   */
  private String stime;
  /**
   * Controlling tty (terminal). (alias tname, tt).
   */
  private String tty;
  /**
   * Cumulative CPU time, "[DD-]HH:MM:SS" format. (alias cputime).
   */
  private String time;

  /**
   * A Collection of currently running processes.
   *
   * @return a non-null collection instance
   */
  public static Collection<ProcessInfo> getAllProcesses() throws Exception {
    Collection<ProcessInfo> processes = new TreeSet<>();
    for (String entry : SIGUtility.execute("ps", "-elf", "--no-headers")) {
      processes.add(ProcessInfo.parsePSEntry(entry));
    }
    return processes;
  }

  /**
   * Build an instance of a LinuxOSProcess from an entry of the command
   * {@code "ps -elf"}.
   *
   * @param processEntry the PS process line entry.
   * @return a new process instance.
   */
  public static ProcessInfo parsePSEntry(String processEntry) {
    String[] t = processEntry.split("\\s+", 15);
    if (t.length < 15) {
      throw new IllegalArgumentException("Invalid process definition: " + processEntry);
    }
    ProcessInfo p = new ProcessInfo();
    p.setFlag(intValue(t[0]));
    p.setProcessState(EState.fromValue(t[1]));
    p.setUid(t[2]);
    try {
      p.setPid(Integer.valueOf(t[3])); // require a valid PID
    } catch (NumberFormatException numberFormatException) {
      throw new IllegalArgumentException("Invalid process id: " + processEntry);
    }
    p.setPpid(intValue(t[4]));
    p.setCpuUtilization(intValue(t[5]));
    p.setPriority(intValue(t[6]));
    p.setNice(intValue(t[7]));
    p.setAddr(intValue(t[8]));
    p.setSize(intValue(t[9]));
    p.setWchan(t[10]);
    p.setStime(t[11]);
    p.setTty(t[12]);
    p.setTime(t[13]);
    p.setCommand(t[14]);

    return p;
  }

  /**
   * Convert to an integer or return null.
   *
   * @param string the input string
   * @return the string, converted to integer or null (on error)
   */
  private static Integer intValue(String string) {
    try {
      return Integer.valueOf(string);
    } catch (Exception exception) {
      return null;
    }
  }

  //<editor-fold defaultstate="collapsed" desc="Getter and Setter">
  public Integer getPid() {
    return pid;
  }

  public void setPid(Integer pid) {
    this.pid = pid;
  }

  public String getCommand() {
    return command;
  }

  public void setCommand(String command) {
    this.command = command;
  }

  public Integer getCpuTime() {
    return cpuTime;
  }

  public void setCpuTime(Integer cpuTime) {
    this.cpuTime = cpuTime;
  }

  public Integer getPriority() {
    return priority;
  }

  public void setPriority(Integer priority) {
    this.priority = priority;
  }

  public EState getProcessState() {
    return processState;
  }

  public void setProcessState(EState processState) {
    this.processState = processState;
  }

  public Integer getFlag() {
    return flag;
  }

  public void setFlag(Integer flag) {
    this.flag = flag;
  }

  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  public Integer getPpid() {
    return ppid;
  }

  public void setPpid(Integer ppid) {
    this.ppid = ppid;
  }

  public Integer getCpuUtilization() {
    return cpuUtilization;
  }

  public void setCpuUtilization(Integer cpuUtilization) {
    this.cpuUtilization = cpuUtilization;
  }

  public Integer getNice() {
    return nice;
  }

  public void setNice(Integer nice) {
    this.nice = nice;
  }

  public Integer getAddr() {
    return addr;
  }

  public void setAddr(Integer addr) {
    this.addr = addr;
  }

  public Integer getSize() {
    return size;
  }

  public void setSize(Integer size) {
    this.size = size;
  }

  public String getWchan() {
    return wchan;
  }

  public void setWchan(String wchan) {
    this.wchan = wchan;
  }

  public String getStime() {
    return stime;
  }

  public void setStime(String stime) {
    this.stime = stime;
  }

  public String getTty() {
    return tty;
  }

  public void setTty(String tty) {
    this.tty = tty;
  }

  public String getTime() {
    return time;
  }

  public void setTime(String time) {
    this.time = time;
  }//</editor-fold>

  /**
   * Get the amount of time in milliseconds that the process has spent taking up
   * CPU time since the process was started.
   *
   * @return the process total CPU time (milliseconds); "-1" on error.
   */
  public Integer getCPUTime() {
    /**
     * Cumulative CPU time, "[DD-]HH:MM:SS" format. (alias cputime).
     */
    if (time != null && !time.isEmpty()) {
      Matcher m = Pattern.compile(time.contains("-")
                                  ? "(\\d+)-(\\d+):(\\d\\d):(\\d\\d)"
                                  : "(\\d+):(\\d\\d):(\\d\\d)").matcher(time);
      if (m.find()) {
        Calendar cal = Calendar.getInstance();
        if (time.contains("-")) {
          cal.add(Calendar.DAY_OF_YEAR, -Integer.valueOf(m.group(1)));
          cal.add(Calendar.HOUR, -Integer.valueOf(m.group(2)));
          cal.add(Calendar.MINUTE, -Integer.valueOf(m.group(3)));
          cal.add(Calendar.SECOND, -Integer.valueOf(m.group(4)));
        } else {
          cal.add(Calendar.HOUR, -Integer.valueOf(m.group(1)));
          cal.add(Calendar.MINUTE, -Integer.valueOf(m.group(2)));
          cal.add(Calendar.SECOND, -Integer.valueOf(m.group(3)));
        }
        return (int) (System.currentTimeMillis() - cal.getTimeInMillis());
      }
    }
    return -1;
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 53 * hash + Objects.hashCode(this.pid);
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
    final ProcessInfo other = (ProcessInfo) obj;
    return Objects.equals(this.pid, other.pid);
  }

  @Override
  public int compareTo(ProcessInfo o) {
    return this.pid.compareTo(o.getPid());
  }

  @Override
  public String toString() {
    return pid + " (" + command + ")";
  }

  //<editor-fold defaultstate="collapsed" desc="EState Enum Class">
  /**
   * The current state that the process is in.
   */
  public static enum EState {
    /**
     * Idle
     */
    IDLE("Idle"),
    /**
     * Running
     */
    RUNNING("Running"),
    /**
     * Sleeping
     */
    SLEEPING("Sleeping"),
    /**
     * Stopped
     */
    STOPPED("Stopped"),
    /**
     * Uninterruptible
     */
    UNINTERRUPTIBLE("Uninterruptible"),
    /**
     * Zombie
     */
    ZOMBIE("Zombie");

    private final String value;

    private EState(String value) {
      this.value = value;
    }

    /**
     * The value.
     *
     * @return the value.
     */
    public String getValue() {
      return value;
    }

    /**
     * Get a process state from a string value. This accepts process state
     * values from all operating systems (linux, max, windows).
     *
     * @param state the OS-reported process state
     * @return the enumerated equivalent, IDLE if not declared.
     */
    public static EState fromValue(String state) {
      if (state == null) {
        return IDLE;
      }

      switch (state.length() == 2 ? state.charAt(0) + "" : state) {
        case "D":
          return UNINTERRUPTIBLE; // linux
        case "I":
          return IDLE;
        case "R":
          return RUNNING;
        case "S":
          return SLEEPING;
        case "T":
          return STOPPED;
//   case "W": return   ;
        case "X":
          return ZOMBIE;
        case "U":
          return UNINTERRUPTIBLE; // mac
        case "Z":
          return ZOMBIE;
        case "Unknown":
          return IDLE; // windows
        case "Running":
          return RUNNING; // windows
        default:
          return IDLE;

      }

    }
  }//</editor-fold>

}
