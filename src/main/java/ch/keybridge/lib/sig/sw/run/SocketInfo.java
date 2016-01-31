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
  private EProtocol protocol;
  /**
   * The local IP address.
   */
  private InetAddress localAddress;
  /**
   * The local socket port. Null for all.
   */
  private Integer localPort;
  /**
   * The remote IP address.
   */
  private InetAddress remoteAddress;
  /**
   * The remote socket port. Null for all.
   */
  private Integer remotePort;
  /**
   * The socket connection state.
   */
  private ESocketState connectionState;
  /**
   * The transmission queue.
   */
  private Integer txQueue;
  /**
   * the receive queue.
   */
  private Integer rxQueue;

  /**
   * Read and parseProcEntry socket information from the {@code /proc/net/tcp}
   * and {@code /proc/net/tcp6} files.
   *
   * @return a TreeSet containing all open sockets.
   * @throws IOException if the {@code /proc/net/tcp} and {@code /proc/net/tcp6}
   *                     files cannot be read.
   */
  public static Collection<SocketInfo> getAllSockets() throws Exception {
    Collection<SocketInfo> sockets = new HashSet<>();
    /**
     * Add all IPv4 sockets: TCP and UDP.
     */
    for (String entry : SIGUtility.execute("netstat", "-an4")) {// throws Exception
      try {
        sockets.add(SocketInfo.parseNetstatEntry(entry));
      } catch (UnknownHostException | NullPointerException | IllegalArgumentException unknownHostException) {
        // ignore parse errors.
      }
    }
    return sockets;
  }

  /**
   * Parse a row from the output of the {@code netstat} system command into a
   * new LinuxTCPSocket instance.
   *
   * @param entry a entry from the {@code netstat} system command
   * @return a new LinuxTCPSocket instance
   */
  public static SocketInfo parseNetstatEntry(String entry) throws UnknownHostException, NullPointerException, IllegalArgumentException {
    String[] tokens = entry.trim().split("\\s+");
    if (tokens.length != 6 && tokens.length != 5) {
      throw new IllegalArgumentException("Invalid entry: " + entry + ". Length must be 5 or 6; is " + tokens.length);
    }

    SocketInfo socket = new SocketInfo();
    socket.setProtocol(EProtocol.valueOf(tokens[0]));

    socket.setLocalAddress(InetAddress.getByName(tokens[3].split(":")[0]));
    socket.setLocalPort(parsePort(tokens[3].split(":")[1]));
    socket.setRemoteAddress(InetAddress.getByName(tokens[4].split(":")[0]));
    socket.setRemotePort(parsePort(tokens[4].split(":")[1]));
    if (tokens.length == 6) {
      socket.setConnectionState(ESocketState.valueOf(tokens[5]));
    }
    /**
     * The rest of this data is informative only and not essential. Ignore any
     * subsequent parsing errors..
     */
    try {
      socket.setRxQueue(Integer.valueOf(tokens[1]));
      socket.setTxQueue(Integer.valueOf(tokens[2]));
    } catch (Exception exception) {
    }

    return socket;
  }

  //<editor-fold defaultstate="collapsed" desc="Getter and Setter">
  public EProtocol getProtocol() {
    return protocol;
  }

  public void setProtocol(EProtocol protocol) {
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

  public ESocketState getConnectionState() {
    return connectionState;
  }

  public void setConnectionState(ESocketState connectionState) {
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
    if ("*".equals(port) || "0".equals(port)) {
      return null;
    }
    return Integer.parseInt(port, 16);
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

  @Override
  public String toString() {
    return protocol + ": " + localAddress.getHostAddress() + ":" + localPort + "  " + remoteAddress.getHostAddress() + ":" + remotePort;
  }

  /**
   * Enumerated TCP/IP socket states. From {@code tcp_states.h} in the linux
   * kernel.
   */
  public static enum ESocketState {
    ESTABLISHED("01"),
    SYN_SENT("02"),
    SYN_RECV("03"),
    FIN_WAIT1("04"),
    FIN_WAIT2("05"),
    TIME_WAIT("06"),
    CLOSE("07"),
    CLOSE_WAIT("08"),
    LAST_ACK("09"),
    LISTEN("0A"),
    CLOSING("0B");

    private final String code;

    private ESocketState(String code) {
      this.code = code;
    }

    public String getCode() {
      return code;
    }

    /**
     * Get a ESocketState instance corresponding to a particular code value.
     *
     * @param code the code
     * @return the instance
     */
    public static ESocketState fromCode(String code) {
      for (ESocketState state : ESocketState.values()) {
        if (state.getCode().equalsIgnoreCase(code)) {
          return state;
        }
      }
      return null;
    }
  }

  /**
   * Recognized socket protocols reported by the {@code netstat} system command.
   */
  public static enum EProtocol {
    raw, udp, tcp, udp6, tcp6, inet, ipx, ax25, netrom, ddp, unix;
  }
}
