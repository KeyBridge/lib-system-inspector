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
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * A general socket descriptor.
 * <p>
 * A socket is one end-point of a two-way communication link between two
 * programs running on the network. Socket classes are used to represent the
 * connection between a client program and a server program. The java.net
 * package provides two classes--Socket and ServerSocket--that implement the
 * client side of the connection and the server side of the connection,
 * respectively.
 *
 * @author Key Bridge LLC
 * @since 1.0.0 (01/31/16) (01/30/16)
 * @see
 * <a href="https://www.kernel.org/doc/Documentation/networking/proc_net_tcp.txt">/proc/net/tcp</a>
 */
public class SocketInfo {

  /**
   * Specifies the address family (perhaps better described as low level
   * protocols) for the socket. Typically one of {@code udp, tcp, udp6, tcp6}
   */
  private ProtocolType protocol;
  /**
   * The local IP address.
   */
  private InetAddress localAddress;
  /**
   * The local socket port. Null for all (i.e. "*").
   */
  private Integer localPort;
  /**
   * The remote IP address.
   */
  private InetAddress remoteAddress;
  /**
   * The remote socket port. Null for all (i.e. "*").
   */
  private Integer remotePort;
  /**
   * The socket connection state.
   */
  private SocketState connectionState;
  /**
   * The transmission queue.
   */
  private Integer txQueue;
  /**
   * the receive queue.
   */
  private Integer rxQueue;

  /**
   * Read and parse TCP-IPv4 (but not IPv6) socket information from the
   * {@code netstat} system command.
   *
   * @return a TreeSet containing all open sockets.
   * @throws IOException if the {@code netstat} system command failed to execute
   */
  public static Collection<SocketInfo> getAllSockets() throws Exception {
    Collection<SocketInfo> sockets = new HashSet<>();
    /**
     * Add all IPv4 sockets: TCP and UDP.
     * <p>
     * `ss` is used to dump socket statistics. It allows showing information
     * similar to netstat. It can display more TCP and state information than
     * other tools.
     *
     * @TODO: If {@code ss} is not available then fall back to read and parse
     * socket information from the nd {@code /proc/net/tcp} and
     * {@code /proc/net/tcp6} run time files.
     */
    for (String entry : SIGUtility.execute("ss", "-an4")) {// throws Exception
      try {
        sockets.add(SocketInfo.parseSocketEntry(entry));
      } catch (Exception exception) {
        // ignore parse errors.
      }
    }
    return sockets;
  }

  /**
   * Parse a row from the output of the {@code ss} system command into a new
   * SocketInfo instance.
   *
   * @param entry a entry from the {@code netstat} system command
   * @return a new SocketInfo instance
   */
  public static SocketInfo parseSocketEntry(String entry) throws UnknownHostException, NullPointerException, IllegalArgumentException {
    String[] tokens = entry.trim().split("\\s+");
    if (tokens.length != 6 && tokens.length != 5) {
      throw new IllegalArgumentException("Invalid entry: " + entry + ". Length must be 5 or 6; is " + tokens.length);
    }
    /**
     * `ss` returns a 6 element, space separated column.
     */
    // 0 Netid
    // 1 State
    // 2 Recv-Q
    // 3 Send-Q
    // 4 Local Address:Port
    // 5 Peer Address:Port
    // 6 [Process] // often null
    // Example output  (whitespace removed for readability.
    //   Netid   State   Recv-Q   Send-Q   Local Address:Port   Peer Address:Port   Process
    //   udp     UNCONN  0        0        127.0.0.53%lo:53     0.0.0.0:*
    /**
     * Instantiate a new instance and assign fields.
     */
    SocketInfo socket = new SocketInfo();
    socket.setProtocol(ProtocolType.valueOf(tokens[0])); // netid
    socket.setConnectionState(SocketState.parse(tokens[1])); // state
    socket.setRxQueue(Integer.valueOf(tokens[2]));
    socket.setTxQueue(Integer.valueOf(tokens[3]));

    socket.setLocalAddress(InetAddress.getByName(tokens[4].split(":")[0]));
    socket.setLocalPort(parsePort(tokens[4].split(":")[1]));
    socket.setRemoteAddress(InetAddress.getByName(tokens[5].split(":")[0]));
    socket.setRemotePort(parsePort(tokens[5].split(":")[1]));
    /**
     * The rest of this data is informative only and not essential. Ignore any
     * subsequent parsing errors..
     */
    if (tokens.length == 6) {
      // capture the process
    }
    /**
     * Done
     */
    return socket;
  }

  //<editor-fold defaultstate="collapsed" desc="Getter and Setter">
  public ProtocolType getProtocol() {
    return protocol;
  }

  public void setProtocol(ProtocolType protocol) {
    this.protocol = protocol;
  }

  public InetAddress getLocalAddress() {
    return localAddress;
  }

  public void setLocalAddress(InetAddress localAddress) {
    this.localAddress = localAddress;
  }

  public Integer getLocalPort() {
    return localPort;
  }

  public void setLocalPort(Integer localPort) {
    this.localPort = localPort;
  }

  public InetAddress getRemoteAddress() {
    return remoteAddress;
  }

  public void setRemoteAddress(InetAddress remoteAddress) {
    this.remoteAddress = remoteAddress;
  }

  public Integer getRemotePort() {
    return remotePort;
  }

  public void setRemotePort(Integer remotePort) {
    this.remotePort = remotePort;
  }

  public SocketState getConnectionState() {
    return connectionState;
  }

  public void setConnectionState(SocketState connectionState) {
    this.connectionState = connectionState;
  }

  public Integer getTxQueue() {
    return txQueue;
  }

  public void setTxQueue(Integer txQueue) {
    this.txQueue = txQueue;
  }

  public Integer getRxQueue() {
    return rxQueue;
  }

  public void setRxQueue(Integer rxQueue) {
    this.rxQueue = rxQueue;
  }//</editor-fold>

  /**
   * Determine if this represents an IPv4 or IPv6 socket.
   *
   * @return TRUE if IPv4, false if IPv6.
   */
  public boolean isIPv4() {
    return (localAddress instanceof Inet4Address);
  }

  /**
   * Determine if this represents an IPv4 or IPv6 socket.
   *
   * @return TRUE if IPv6, false if IPv4.
   */
  public boolean isIPv6() {
    return (localAddress instanceof Inet6Address);
  }

  /**
   * Parse a port value.
   *
   * @param port the port string value
   * @return the port, null if zero.
   */
  private static Integer parsePort(String port) {
    return ("*".equals(port) || "0".equals(port)) ? null : Integer.valueOf(port);
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 37 * hash + Objects.hashCode(this.localAddress);
    hash = 37 * hash + Objects.hashCode(this.localPort);
    hash = 37 * hash + Objects.hashCode(this.remoteAddress);
    hash = 37 * hash + Objects.hashCode(this.remotePort);
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
    final SocketInfo other = (SocketInfo) obj;
    if (!Objects.equals(this.localAddress, other.localAddress)) {
      return false;
    }
    if (!Objects.equals(this.localPort, other.localPort)) {
      return false;
    }
    if (!Objects.equals(this.remoteAddress, other.remoteAddress)) {
      return false;
    }
    return Objects.equals(this.remotePort, other.remotePort);
  }

  /**
   * Get a text representation of this socket; shows the protocol, local and
   * remote address plus port number.
   *
   * @return a text representation
   */
  @Override
  public String toString() {
    return protocol
      + String.format(" %-16s", connectionState)
      + String.format(" %16s:%-6s", localAddress.getHostAddress(), (localPort != null ? localPort : "*"))
      //      + "  " + localAddress.getHostAddress() + (localPort != null ? (":" + localPort) : "")
      + String.format(" %16s:%-6s", remoteAddress.getHostAddress(), (remotePort != null ? remotePort : "*"));
//      + " - " + remoteAddress.getHostAddress() + (remotePort != null ? (":" + remotePort) : "");
  }

  /**
   * Enumerated TCP/IP socket states. From {@code tcp_states.h} in the linux
   * kernel.
   */
  public static enum SocketState {
    ESTABLISHED,
    SYN_SENT,
    SYN_RECV,
    FIN_WAIT1,
    FIN_WAIT2,
    TIME_WAIT,
    CLOSE,
    CLOSE_WAIT,
    LAST_ACK,
    LISTEN,
    CLOSING,
    // UDP
    UNCONNECTED;

    /**
     * Get a SocketState instance corresponding to a particular code value.
     *
     * @param code the code
     * @return the instance
     */
    public static SocketState parse(String state) {
      for (SocketState ss : values()) {
        if (ss.name().startsWith(state)) {
          return ss;
        }
      }
      return null;
    }
  }

  /**
   * Recognized socket protocols reported by the {@code netstat} system command.
   */
  public static enum ProtocolType {
    raw, udp, tcp, udp6, tcp6, inet, ipx, ax25, netrom, ddp, unix;
  }
}
