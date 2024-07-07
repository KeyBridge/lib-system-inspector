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

import ch.keybridge.lib.sig.hw.*;
import ch.keybridge.lib.sig.hw.net.NetworkInterfaceInfo;
import ch.keybridge.lib.sig.sw.OperatingSystemInfo;
import ch.keybridge.lib.sig.sw.config.NTPConfiguration;
import ch.keybridge.lib.sig.sw.config.SystemUserInfo;
import ch.keybridge.lib.sig.sw.run.ProcessInfo;
import ch.keybridge.lib.sig.sw.run.SocketInfo;
import ch.keybridge.lib.sig.utility.SIGUtility;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import org.junit.Test;

/**
 *
 * @author Key Bridge LLC
 * @since 1.0.0 (01/31/16)
 */
public class SystemInspectorTest {

  public SystemInspectorTest() {
  }

  @Test
  public void testSIG() throws IOException, Exception {

    SystemInspectorGeneral sig = SystemInspectorGeneral.getInstance();
    System.out.println("SIG " + sig);

    CPUInfo cpu = sig.getCPUInfo();
    System.out.println("cpu     : " + cpu);
    System.out.println("uptime  : " + cpu.getSystemUptime());
    System.out.println("  cpu usage  : " + cpu.getSystemUsage() + "%");
    for (Map.Entry<String, Double> entry : cpu.getSystemUsageDetail().entrySet()) {
      System.out.println("  " + entry.getKey() + " " + entry.getValue() + "%");

    }

    MemoryInfo memory = sig.getMemoryInfo();
    System.out.println("memory  : " + memory);

    Collection<FileSystemInfo> fsInfo = sig.getFileSystemInfo();
    System.out.println("file systems");
    for (FileSystemInfo fileSystemInfo : fsInfo) {
      System.out.println("   fs  : " + fileSystemInfo);
    }

    System.out.println("networks");
    for (NetworkInterfaceInfo network : NetworkInterfaceInfo.getAllInterfaces()) {
      System.out.println("  net    : " + network);
    }

    Collection<DisplayInfo> displays = sig.getDisplayInfo();
    System.out.println("display(s)");
    for (DisplayInfo dis : displays) {
      System.out.println(" display: " + dis);
    }

    Collection<PowerSupplyInfo> powerSupplyInfo = sig.getPowerSupplyInfo();
    System.out.println("power supplies");
    for (PowerSupplyInfo p : powerSupplyInfo) {
      System.out.println(" ps     : " + p);
    }

    OperatingSystemInfo os = sig.getOperatingSystemInfo();
    System.out.println("os      : " + os);

    Collection<ProcessInfo> processInfo = os.getProcesses();
    System.out.println("processes");
    for (ProcessInfo proces : processInfo) {
      System.out.println("  ps    : " + proces);
    }

    Collection<SocketInfo> sockets = os.getSockets();
    System.out.println("sockets");
    for (SocketInfo socket : sockets) {
      System.out.println("  so    : " + socket);
    }

    Collection<SystemUserInfo> users = os.getUsers();
    System.out.println("users");
    for (SystemUserInfo user : users) {
      System.out.println(" user    : " + user);
    }

    if (SIGUtility.canExecute("ntpd")) {
      NTPConfiguration ntp = os.getNTPConfiguration();
      System.out.println("ntp   : " + ntp);
    }
  }

}
