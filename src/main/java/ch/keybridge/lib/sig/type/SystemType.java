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
package ch.keybridge.lib.sig.type;

import com.sun.jna.Platform;

/**
 * Enum of supported / recognized operating systems.
 * <p>
 * Provides name mapping from the osType value in {@code com.sun.jna.Platform}.
 *
 * @author Key Bridge LLC
 * @since 1.0.0 (01/31/16)
 */
public enum SystemType {

  UNSPECIFIED(-1),
  MAC(0),
  LINUX(1),
  WINDOWS(2),
  SOLARIS(3),
  FREEBSD(4),
  OPENBSD(5),
  WINDOWSCE(6),
  AIX(7),
  ANDROID(8),
  GNU(9),
  KFREEBSD(10),
  NETBSD(11);

  /**
   * the operating system type numebr.
   */
  private final int osType;

  private SystemType(int osType) {
    this.osType = osType;
  }

  /**
   * Get the OS Type value.
   *
   * @return the value
   */
  public int getOsType() {
    return osType;
  }

  /**
   * Get a SystemType from a operating system type.
   *
   * @param osType the operating system type
   * @return the enumerated platform type
   */
  public static SystemType fromOsType(int osType) {
    for (SystemType value : SystemType.values()) {
      if (value.getOsType() == osType) {
        return value;
      }
    }
    return UNSPECIFIED;
  }

  /**
   * Get the enumerated platform type for the current operating environment.
   *
   * @return the enumerated platform type
   */
  public static SystemType get() {
    return SystemType.fromOsType(Platform.getOSType());
  }

}
