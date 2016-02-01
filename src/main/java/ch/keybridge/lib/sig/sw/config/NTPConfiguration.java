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
package ch.keybridge.lib.sig.sw.config;

import ch.keybridge.lib.sig.utility.SIGUtility;
import java.util.Collection;
import java.util.HashSet;

/**
 * Container for Network Time Protocol status on a Linux system.
 * <p>
 * This bean queries the Linux system {@code ntpq} utility to discover
 * information about the curent NTP status. The ntpq utility program is used to
 * query NTP servers which implement the recommended NTP mode 6 control message
 * format about current state and to request changes in that state.
 *
 * @author Key Bridge LLC
 * @since 1.0.0 (01/31/16) 01/26/2016
 */
public class NTPConfiguration {

  /**
   * Indicator that NTP is configured on this system.
   */
  private boolean configured;
  /**
   * Indicator that this system time is synchronized with at least one NTP
   * server.
   */
  private boolean inSync;
  /**
   * A collection of NTP servers.
   */
  private Collection<NTPPeerInfo> peers;

  /**
   * Construct a new LinuxNTPStatus and initialize the peers collection. This
   * executes and reads the response from the system command {@code ntpq -pn}.
   *
   * @throws Exception if the system command {@code ntpq -pn} fails to execute.
   */
  public static NTPConfiguration getInstance() throws Exception {
    NTPConfiguration ntpConfig = new NTPConfiguration();
    for (String line : SIGUtility.execute("ntpq", "-pn")) {
      try {
        NTPPeerInfo peer = NTPPeerInfo.parseNTPQEntry(line);
        /**
         * Activate if is in sync.
         */
        if (peer.isActive()) {
          ntpConfig.setInSync(true);
        }
        ntpConfig.getPeers().add(peer);
      } catch (Exception e) {
        // ignore parse errors.
      }
    }
    return ntpConfig;
  }

  //<editor-fold defaultstate="collapsed" desc="Getter and Setter">
  /**
   * NTP is configured if there is at least one peer.
   *
   * @return TRUE if any peer is configured.
   */
  public boolean isConfigured() {
    return !getPeers().isEmpty();
  }

  public boolean isInSync() {
    return inSync;
  }

  public void setInSync(boolean inSync) {
    this.inSync = inSync;
  }

  public Collection<NTPPeerInfo> getPeers() {
    if (peers == null) {
      peers = new HashSet<>();
    }
    return peers;
  }

  public void setPeers(Collection<NTPPeerInfo> peers) {
    this.peers = peers;
  }//</editor-fold>

  @Override
  public String toString() {
    return "NTP Status: configured = " + configured + ", inSync = " + inSync;
  }

}
