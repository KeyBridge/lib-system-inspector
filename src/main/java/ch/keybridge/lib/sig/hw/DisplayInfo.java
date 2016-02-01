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
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;

/**
 * DisplayInfo refers to the information regarding a video source and monitor
 * identified by the EDID standard.
 * <p>
 * This utility class parses the EDID value reported by the command
 * {@code xrandr}. xrandr is an official configuration utility to the RandR X
 * Window System extension. It can be used to set the size, orientation or
 * reflection of the outputs for a screen.
 * <p>
 * Extended display identification data (EDID) is a data structure provided by a
 * digital display to describe its capabilities to a video source (e.g. graphics
 * card or set-top box). It is what enables a modern personal computer to know
 * what kinds of monitors are connected to it.
 *
 * @author Key Bridge LLC
 * @since 1.0.0 (01/31/16)
 */
public class DisplayInfo {

  /**
   * The original unparsed EDID byte array.
   * <p>
   * Extended display identification data (EDID) is a data structure provided by
   * a digital display to describe its capabilities to a video source (e.g.
   * graphics card or set-top box). It is what enables a modern personal
   * computer to know what kinds of monitors are connected to it.
   */
  private byte[] edid;

  private String edidVersion;

  private String manufacturer;
  private String model;
  private Boolean digital;
  private Date dateOfManufacture;

  private Integer screenWidth;
  private Integer screenHeight;

  private String serialNumber;
  private String comment;
  private String name;

  /**
   * Gets DisplayInfo Information. This method executes the
   * {@code xrandr --verbose} system command, then parses the output.
   *
   * @return An array of DisplayInfo objects representing monitors, etc.
   * @throws Exception if the {@code xrandr --verbose} system command fails to
   *                   execute.
   */
  public static Collection<DisplayInfo> getAllDisplays() throws Exception {
    /**
     * Looking for EDID information formatted as:
     * <pre>
     * 	EDID:
     *        00ffffffffffff0022f0142600000000
     *        210e01030e321f78eacfb5a355499925
     *        105054a56f806159814081808199a940
     *        a94fd1c0d100744b80a072b02a4080d0
     *        1300ef361100001c000000fd0030551e
     *        5e15000a202020202020000000fc0068
     *        70204c323333350a20202020000000ff
     *        00434e503433335a3054540a20200098
     * </pre>
     * <p>
     * Need to concatenate and then parse the byte array.
     */

    Set<DisplayInfo> displays = new HashSet<>();
    boolean foundEdid = false;
    StringBuilder sb = new StringBuilder();
    for (String s : SIGUtility.execute("xrandr", "--verbose")) {
      if (s.contains("EDID")) {
        foundEdid = true;
        sb = new StringBuilder();
        continue;
      }
      if (foundEdid) {
        sb.append(s.trim());
        if (sb.length() >= 256) {
          String edidString = sb.toString();
          displays.add(DisplayInfo.getInstance(hexStringToByteArray(edidString)));
          foundEdid = false;
        }
      }
    }
    return displays;
  }

  /**
   * Construct a new display instance and parse the provided EDID information.
   *
   * @param edid the EDID information
   */
  public static DisplayInfo getInstance(byte[] edid) {
    DisplayInfo displayInfo = new DisplayInfo();
    displayInfo.setEdid(edid);
    return displayInfo;
  }

  //<editor-fold defaultstate="collapsed" desc="Getter and Setter">
  /**
   * The EDID byte array.
   * <p>
   * Extended display identification data (EDID) is a data structure provided by
   * a digital display to describe its capabilities to a video source (e.g.
   * graphics card or set-top box). It is what enables a modern personal
   * computer to know what kinds of monitors are connected to it.
   *
   * @return The original unparsed EDID byte array.
   */
  public byte[] getEdid() {
    return Arrays.copyOf(edid, edid.length);
  }

  /**
   * Set the EDID.
   *
   * @param edid
   */
  public void setEdid(byte[] edid) {
    this.edid = edid;
    parseEdid(edid);
  }

  public String getEdidVersion() {
    return edidVersion;
  }

  public void setEdidVersion(String edidVersion) {
    this.edidVersion = edidVersion;
  }

  public String getManufacturer() {
    return manufacturer;
  }

  public void setManufacturer(String manufacturer) {
    this.manufacturer = manufacturer;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public Boolean getDigital() {
    return digital;
  }

  public void setDigital(Boolean digital) {
    this.digital = digital;
  }

  public Date getDateOfManufacture() {
    return dateOfManufacture;
  }

  public void setDateOfManufacture(Date dateOfManufacture) {
    this.dateOfManufacture = dateOfManufacture;
  }

  public Integer getScreenWidth() {
    return screenWidth;
  }

  public void setScreenWidth(Integer screenWidth) {
    this.screenWidth = screenWidth;
  }

  public Integer getScreenHeight() {
    return screenHeight;
  }

  public void setScreenHeight(Integer screenHeight) {
    this.screenHeight = screenHeight;
  }

  public String getSerialNumber() {
    return serialNumber;
  }

  public void setSerialNumber(String serialNumber) {
    this.serialNumber = serialNumber;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }//</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="EDID Parsing Methods">
  /**
   * Internal method to parse a EDID into its components.
   *
   * @param edid the edid byte array
   */
  private void parseEdid(byte[] edid) {
    /**
     * Manufacturer
     */
    String manufacturerTemp = String
            .format("%8s%8s", Integer.toBinaryString(edid[8] & 0xFF), Integer.toBinaryString(edid[9] & 0xFF))
            .replace(' ', '0');
    this.manufacturer = String.format("%s%s%s", (char) (64 + Integer.parseInt(manufacturerTemp.substring(1, 6), 2)),
                                      (char) (64 + Integer.parseInt(manufacturerTemp.substring(7, 11), 2)),
                                      (char) (64 + Integer.parseInt(manufacturerTemp.substring(12, 16), 2))).replace("@", "");
    // Product ID, bytes 10 and 11. Bytes 10-11 are product ID expressed in hex characters
    this.model = Integer.toHexString(ByteBuffer.wrap(Arrays.copyOfRange(edid, 10, 12)).order(ByteOrder.LITTLE_ENDIAN).getShort() & 0xffff);
// Serial number, bytes 12-15. Bytes 12-15 are Serial number (last 4 characters)
    this.serialNumber = String.format("%s%s%s%s", getAlphaNumericOrHex(edid[15]), getAlphaNumericOrHex(edid[14]),
                                      getAlphaNumericOrHex(edid[13]), getAlphaNumericOrHex(edid[12]));
// Byte 20 is Video input params
    this.digital = (1 == edid[20] >> 7);
// dateOfManufacture
    // Byte 16 is manufacture week
    // Byte 17 is manufacture year after 1990
    Calendar date = Calendar.getInstance();
    date.set(Calendar.WEEK_OF_YEAR, edid[16]);
    date.set(Calendar.YEAR, edid[17] + 1990);
    this.dateOfManufacture = date.getTime();
    // Bytes 18-19 are EDID version
    this.edidVersion = edid[18] + "." + edid[19];
    // Byte 21 is horizontal size in cm
    this.screenWidth = Integer.valueOf(edid[21]);
    // Byte 22 is vertical size in cm
    this.screenHeight = Integer.valueOf(edid[21]);

    byte[][] descriptor = getDescriptors(edid);
    for (int d = 0; d < descriptor.length; d++) {
      switch (getDescriptorType(descriptor[d])) {
        case 0xff:
          this.serialNumber = getDescriptorText(descriptor[d]);
          break;
        case 0xfe:
          this.comment = getDescriptorText(descriptor[d]);
          break;
        case 0xfd: // Range Limits
          break;
        case 0xfc:
          this.name = getDescriptorText(descriptor[d]);
          break;
        case 0xfb: // White Point Data
          break;
        case 0xfa: // Standard Timing ID
          break;
        default: // Manufacturer Data or Preferred Timing
          break;
      }
    }

  }

  /**
   * Parse a string representation of a hex array into a byte array.
   *
   * @param string The string to be parsed
   * @return a byte array with each pair of characters converted to a byte
   */
  private static byte[] hexStringToByteArray(String string) {
    int len = string.length();
    byte[] data = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
      data[i / 2] = (byte) ((Character.digit(string.charAt(i), 16) << 4) + Character.digit(string.charAt(i + 1), 16));
    }
    return data;
  }

  private static String getAlphaNumericOrHex(byte b) {
    return Character.isLetterOrDigit((char) b) ? String.format("%s", (char) b) : String.format("%02X", b);
  }

  /**
   * Get the VESA descriptors
   *
   * @param edid The EDID byte array
   * @return A 2D array with four 18-byte elements representing VESA descriptors
   */
  private static byte[][] getDescriptors(byte[] edid) {
    byte[][] desc = new byte[4][18];
    for (int i = 0; i < desc.length; i++) {
      System.arraycopy(edid, 54 + 18 * i, desc[i], 0, 18);
    }
    return desc;
  }

  /**
   * Get the VESA descriptor type
   *
   * @param desc An 18-byte VESA descriptor
   * @return An integer representing the first four bytes of the VESA descriptor
   */
  private static int getDescriptorType(byte[] desc) {
    return ByteBuffer.wrap(Arrays.copyOfRange(desc, 0, 4)).getInt();
  }

  /**
   * Parse descriptor text
   *
   * @param desc An 18-byte VESA descriptor
   * @return Plain text starting at the 4th byte
   */
  private static String getDescriptorText(byte[] desc) {
    return new String(Arrays.copyOfRange(desc, 4, 18)).trim();
  }//</editor-fold>

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 79 * hash + Arrays.hashCode(this.edid);
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
    final DisplayInfo other = (DisplayInfo) obj;
    return Arrays.equals(this.edid, other.edid);
  }

  @Override
  public String toString() {
    return name;
  }

}
