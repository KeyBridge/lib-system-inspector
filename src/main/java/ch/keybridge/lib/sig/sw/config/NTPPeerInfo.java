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

import java.util.Objects;

/**
 * Line entry for the command {@code ntpq -pn} representing a NTP remote peer.
 * <p>
 * The Network Time Protocol (NTP) is a protocol and software implementation for
 * synchronizing the clocks of computer systems over packet-switched,
 * variable-latency data networks. Originally designed by David L. Mills of the
 * University of Delaware and still maintained by him and a team of volunteers,
 * it was first used before 1985 and is one of the oldest Internet protocols.
 *
 * @see <a href="http://www.ntp.org/ntpfaq/NTP-s-time.htm">The NTP FAQ, Time,
 * what Time?</a>
 * @see <a href="http://www.ntp.org/rfc.html">NTP RFCs</a>
 *
 */
public class NTPPeerInfo implements Comparable<NTPPeerInfo> {

  /**
   * The Select Field displays the current selection status. (The T Field
   * corresponds to the tally codes used in the ntpq peers display.)
   */
  private ESelectStatus selectStatus;
  /**
   * The remote peer or server being synced to. “LOCAL” is this local host
   * (included in case there are no remote peers or servers available).
   */
  private String remote;
  /**
   * Where or what the remote peer or server is itself synchronised to;
   */
  private String refid;
  /**
   * The remote peer or server Stratum.
   * <p>
   * NTP uses a hierarchical, semi-layered system of time sources. Each level of
   * this hierarchy is termed a "stratum" and is assigned a number starting with
   * zero at the top. A server synchronized to a stratum n server will be
   * running at stratum n + 1. The number represents the distance from the
   * reference clock and is used to prevent cyclical dependencies in the
   * hierarchy. Stratum is not always an indication of quality or reliability;
   * it is common to find stratum 3 time sources that are higher quality than
   * other stratum 2 time sources.
   *
   * @see
   * <a href="https://en.wikipedia.org/wiki/Network_Time_Protocol#Clock_strata">NTP
   * Stratum</a>
   */
  private Integer stratum;
  /**
   * Type (u: unicast or manycast client, b: broadcast or multicast client, l:
   * local reference clock, s: symmetric peer, A: manycast server, B: broadcast
   * server, M: multicast server, see “Automatic Server Discovery“);
   */
  private String type;
  /**
   * When last polled (seconds ago, “h” hours ago, or “d” days ago);
   */
  private Integer when;
  /**
   * poll – Polling frequency: rfc5905 suggests this ranges in NTPv4 from 4
   * (16s) to 17 (36h) (log2 seconds), however observation suggests the actual
   * displayed value is seconds for a much smaller range of 64 (26) to 1024
   * (210) seconds;
   *
   * @see <a href="http://www.ietf.org/rfc/rfc5905.txt">rfc5905</a>
   */
  private Integer poll;
  /**
   * An 8-bit left-shift shift register value recording polls (bit set =
   * successful, bit reset = fail) displayed in octal;
   */
  private String reach;
  /**
   * Round trip communication delay to the remote peer or server (milliseconds);
   */
  private Double delay;
  /**
   * Mean offset (phase) in the times reported between this local host and the
   * remote peer or server (RMS, milliseconds);
   */
  private Double offset;
  /**
   * Mean deviation (jitter) in the time reported for that remote peer or server
   * (RMS of difference of multiple time samples, milliseconds);
   */
  private Double jitter;

  /**
   * Construct a new NTP entry instance from a line of the command
   * {@code ntpq -pn}.
   * <p>
   * Developer note: This constructor throws exception for the first two
   * (header) lines and should be wrapped in a try/catch..
   *
   * @param row a ntpq output row
   * @return a new NTP Peer configuration
   */
  public static NTPPeerInfo parseNTPQEntry(String row) {
    String[] tokens = row.split("\\s+");
    if (tokens.length != 10) {
      throw new IllegalArgumentException("Invalid ntpq row definition. " + row);
    }
    NTPPeerInfo ntpPeer = new NTPPeerInfo();
    ntpPeer.setSelectStatus(ESelectStatus.fromValue(tokens[0].substring(0, 1)));
    ntpPeer.setRemote(tokens[0].substring(1, tokens[0].length()));
    ntpPeer.setRefid(tokens[1]);
    ntpPeer.setStratum(Integer.valueOf(tokens[2]));
    ntpPeer.setType(tokens[3]);
    ntpPeer.setWhen(Integer.valueOf(tokens[4]));
    ntpPeer.setPoll(Integer.valueOf(tokens[5]));
    ntpPeer.setReach(tokens[6]);
    ntpPeer.setDelay(Double.valueOf(tokens[7]));
    ntpPeer.setOffset(Double.valueOf(tokens[8]));
    ntpPeer.setJitter(Double.valueOf(tokens[9]));
    return ntpPeer;
  }

  //<editor-fold defaultstate="collapsed" desc="Getter and Setter">
  /**
   * Get the select status. Returns NO_STATE if not configured.
   *
   * @return the select status
   */
  public ESelectStatus getSelectStatus() {
    return selectStatus != null ? selectStatus : ESelectStatus.NO_STATE;
  }

  public void setSelectStatus(ESelectStatus selectStatus) {
    this.selectStatus = selectStatus;
  }

  public String getRemote() {
    return remote;
  }

  public void setRemote(String remote) {
    this.remote = remote;
  }

  public String getRefid() {
    return refid;
  }

  public void setRefid(String refid) {
    this.refid = refid;
  }

  public Integer getStratum() {
    return stratum;
  }

  public void setStratum(Integer stratum) {
    this.stratum = stratum;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Integer getWhen() {
    return when;
  }

  public void setWhen(Integer when) {
    this.when = when;
  }

  public Integer getPoll() {
    return poll;
  }

  public void setPoll(Integer poll) {
    this.poll = poll;
  }

  public String getReach() {
    return reach;
  }

  public void setReach(String reach) {
    this.reach = reach;
  }

  public Double getDelay() {
    return delay;
  }

  public void setDelay(Double delay) {
    this.delay = delay;
  }

  public Double getOffset() {
    return offset;
  }

  public void setOffset(Double offset) {
    this.offset = offset;
  }

  public Double getJitter() {
    return jitter;
  }

  public void setJitter(Double jitter) {
    this.jitter = jitter;
  }//</editor-fold>

  /**
   * Indicator that this NTP server is either a primary reference or active PPS
   * peer.
   *
   * @return TRUE if the select status is SYS_PEER or PPS_PEER.
   */
  public boolean isActive() {
    return ESelectStatus.SYS_PEER.equals(selectStatus) || ESelectStatus.PPS_PEER.equals(selectStatus);
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 13 * hash + Objects.hashCode(this.remote);
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
    final NTPPeerInfo other = (NTPPeerInfo) obj;
    return Objects.equals(this.remote, other.remote);
  }

  @Override
  public String toString() {
    return "NTPPeer " + "selectStatus=" + selectStatus + ", remote=" + remote + ", refid=" + refid + ", stratum=" + stratum + ", type=" + type + ", when=" + when + ", poll=" + poll + ", reach=" + reach + ", delay=" + delay + ", offset=" + offset + ", jitter=" + jitter;
  }

  /**
   * Sort order is by selected status, then stratum depth, then the time last
   * sync event.
   *
   * @param o the other entity
   * @return the sort order
   */
  @Override
  public int compareTo(NTPPeerInfo o) {
    if (selectStatus.equals(o.getSelectStatus())) {
      if (stratum.equals(o.getStratum())) {
        return when.compareTo(o.getWhen());
      }
      return stratum.compareTo(o.getStratum());
    }
    return getSelectStatus().compareTo(o.getSelectStatus());
  }

  /**
   * NTP Select Field Status values.
   * <p>
   * The Select Field displays the current selection status. (The T Field in the
   * following table gives the corresponding tally codes used in the ntpq peers
   * display.) The values are coded here.
   *
   * @see <a href="as follows">ntpd Reporting and Monitoring</a>
   */
  public static enum ESelectStatus {
    /**
     * The remote peer or server presently used as the primary reference.
     */
    SYS_PEER("*"),
    /**
     * PPS peer (when the prefer peer is valid). The actual system
     * synchronization is derived from a pulse-per-second (PPS) signal, either
     * indirectly via the PPS reference clock driver or directly via kernel
     * interface.
     */
    PPS_PEER("o"),
    /**
     * Good and a preferred remote peer or server (included by the combine
     * algorithm);
     */
    CANDIDATE("+"),
    /**
     * Good remote peer or server but not utilised (not among the first six
     * peers sorted by synchronization distance, ready as a backup source)
     */
    NOT_UTILIZED("#"),
    /**
     * No state indicated
     */
    NO_STATE(" "),
    /**
     * Out of tolerance, do not use (discarded by intersection algorithm)
     */
    OUTLIER_INTERSECTION("x"),
    /**
     * Out of tolerance, do not use (discarded by the cluster algorithm)
     */
    OUTLIER_CLUSTER("-");

    private final String token;

    private ESelectStatus(String code) {
      this.token = code;
    }

    public String getToken() {
      return token;
    }

    public static ESelectStatus fromValue(String token) {
      for (ESelectStatus value : ESelectStatus.values()) {
        if (value.getToken().equals(token)) {
          return value;
        }
      }
      return null;
    }
  }

}
