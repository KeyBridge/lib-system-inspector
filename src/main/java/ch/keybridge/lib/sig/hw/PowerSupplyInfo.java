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
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * Container for Linux power supply (i.e. battery status) information.
 * <p>
 * The Linux power supply class is used to represent battery, UPS, AC or DC
 * power supply properties to user-space.
 * <p>
 * It defines core set of attributes, which should be applicable to (almost)
 * every power supply out there. Attributes are available via sysfs and uevent
 * interfaces.
 * <p>
 * Each attribute has well defined meaning, up to unit of measure used. While
 * the attributes provided are believed to be universally applicable to any
 * power supply, specific monitoring hardware may not be able to provide them
 * all, so any of them may be skipped.
 * <p>
 * Units (Quoting include/linux/power_supply.h:) All voltages, currents,
 * charges, energies, time and temperatures in µV, µA, µAh, µWh, seconds and
 * tenths of degree Celsius unless otherwise stated.
 *
 * @see
 * <a href="https://www.kernel.org/doc/Documentation/power/power_supply_class.txt">Linux
 * power supply</a>
 * @author Key Bridge LLC
 * @since 1.0.0 (01/31/16) (01/31/16)
 */
public class PowerSupplyInfo {

  /**
   * The root directory for all power supply configuration files.
   */
  private static final String PATH_ROOT = "/sys/class/power_supply";

  /**
   * The power supply name. (e.g., InternalBattery-0). From
   * {@code POWER_SUPPLY_NAME}.
   */
  private String name;
  /**
   * The manufacturer. From {@code POWER_SUPPLY_MANUFACTURER}.
   */
  private String manufacturer;
  /**
   * The model number. From {@code POWER_SUPPLY_MODEL_NAME}.
   */
  private String model;
  /**
   * The serial number. From {@code POWER_SUPPLY_SERIAL_NUMBER}.
   */
  private String serial;
  /**
   * The (battery) technology. i.e. "Li-ion". From
   * {@code POWER_SUPPLY_TECHNOLOGY}.
   */
  private String technology;
  /**
   * The immediate status. i.e. "Discharging". From {@code POWER_SUPPLY_STATUS}.
   * <p>
   * This attribute represents operating status (charging, full, discharging
   * (i.e. powering a load), etc.). This corresponds to BATTERY_STATUS_* values,
   * as defined in battery.h.
   */
  private String status;
  /**
   * The battery capacity, total. From {@code POWER_SUPPLY_ENERGY_FULL} or
   * {@code POWER_SUPPLY_CHARGE_FULL}.
   * <p>
   * This may be energy (voltage * charge = Watt Hour) or just charge (Hour).
   */
  private Long capacityFull;
  /**
   * Remaining capacity. From {@code POWER_SUPPLY_ENERGY_NOW} or
   * {@code POWER_SUPPLY_CHARGE_NOW}.
   */
  private Long capacityNow;

  /**
   * The capacity discharge rate. From {@code POWER_SUPPLY_POWER_NOW} or
   * {@code POWER_SUPPLY_CURRENT_NOW}.
   */
  private Long capacityDischarge;

  //<editor-fold defaultstate="collapsed" desc="Getter and Setter">
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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

  public String getSerial() {
    return serial;
  }

  public void setSerial(String serial) {
    this.serial = serial;
  }

  public String getTechnology() {
    return technology;
  }

  public void setTechnology(String technology) {
    this.technology = technology;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Long getCapacityFull() {
    return capacityFull;
  }

  public void setCapacityFull(Long capacityFull) {
    this.capacityFull = capacityFull;
  }

  public Long getCapacityNow() {
    return capacityNow;
  }

  public void setCapacityNow(Long capacityNow) {
    this.capacityNow = capacityNow;
  }

  public Long getCapacityDischarge() {
    return capacityDischarge;
  }

  public void setCapacityDischarge(Long capacityDischarge) {
    this.capacityDischarge = capacityDischarge;
  }

  /**
   * Indicator that the power supply is presently charging.
   *
   * @return TRUE if status is "Charging".
   */
  public boolean isCharging() {
    return "Charging".equalsIgnoreCase(status);
  }

  /**
   * Estimated remaining capacity in percent, ranging from 0.0 to 1.0.
   *
   * @return the estimated remaining capacity (in percent).
   */
  public double getPercentCharged() {
    return capacityNow / capacityFull;
  }

  /**
   * Estimated time remaining on the power source, in seconds. If positive,
   * seconds remaining. If negative, -1.0 (calculating) or -2.0 (unlimited).
   *
   * @return the estimated time remaining (in seconds).
   */
  public long getTimeRemaining() {
    return isCharging() ? -2 : 3600 * capacityNow / capacityDischarge;
  }//</editor-fold>

  /**
   * Read and parse all PowerSupplyInfo instances on the current system.
   *
   * @return a collection of power supply instances.
   * @throws IOException if any power supply configuration cannot be read
   */
  public static Collection<PowerSupplyInfo> getAllInstances() throws IOException {
    Collection<PowerSupplyInfo> powerSupplies = new HashSet<>();

    Path root = Paths.get(PATH_ROOT);
    for (File file : root.toFile().listFiles(new FileFilter() {
      @Override
      public boolean accept(File pathname) {
        return pathname.isDirectory();
      }
    })) {
      powerSupplies.add(PowerSupplyInfo.getInstance(file.getName()));
    }
    return powerSupplies;
  }

  /**
   * Get an instance of the PowerSupplyInfo described by the indicated name.
   *
   * @param powerSupplyName the power supply name. This is a subdirectory name
   *                        under {@code /sys/class/power_supply}
   * @return a PowerSupplyInfo configuration
   * @throws IOException if the {@code uevent} file cannot be read.
   */
  public static PowerSupplyInfo getInstance(String powerSupplyName) throws IOException {
    /**
     * Instantiate and populate a new PowerSupply instance.
     */
    PowerSupplyInfo powerSupply = new PowerSupplyInfo();
    for (String line : SIGUtility.readFileLines(Paths.get(PATH_ROOT, powerSupplyName, "uevent"))) {
      /**
       * The device type information.
       */
      if (line.startsWith("POWER_SUPPLY_NAME")) {
        powerSupply.setName(line.split("=")[1]);
      }
      if (line.startsWith("POWER_SUPPLY_MANUFACTURER")) {
        powerSupply.setManufacturer(line.split("=")[1]);
      }
      if (line.startsWith("POWER_SUPPLY_MODEL_NAME")) {
        powerSupply.setModel(line.split("=")[1]);
      }
      if (line.startsWith("POWER_SUPPLY_SERIAL_NUMBER")) {
        powerSupply.setManufacturer(line.split("=")[1]);
      }
      if (line.startsWith("POWER_SUPPLY_TECHNOLOGY")) {
        powerSupply.setManufacturer(line.split("=")[1]);
      }
      /**
       * The status.
       */
      if (line.startsWith("POWER_SUPPLY_STATUS")) {
        powerSupply.setStatus(line.split("=")[1]);
      }
      /**
       * The energy capacity and discharge rate.
       */
      if (line.startsWith("POWER_SUPPLY_ENERGY_NOW") || line.startsWith("POWER_SUPPLY_CHARGE_NOW")) {
        powerSupply.setCapacityNow(Long.valueOf(line.split("=")[1]));
      }
      if (line.startsWith("POWER_SUPPLY_ENERGY_FULL") || line.startsWith("POWER_SUPPLY_CHARGE_FULL")) {
        powerSupply.setCapacityFull(Long.valueOf(line.split("=")[1]));
      }
      if (line.startsWith("POWER_SUPPLY_POWER_NOW") || line.startsWith("POWER_SUPPLY_CURRENT_NOW")) {
        powerSupply.setCapacityDischarge(Long.valueOf(line.split("=")[1]));
      }
    }
    return powerSupply;
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 97 * hash + Objects.hashCode(this.name);
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
    final PowerSupplyInfo other = (PowerSupplyInfo) obj;
    return Objects.equals(this.name, other.name);
  }

  /**
   * Get the power supply name plus percent charged and, if not charging, the
   * estimated time remaining.
   *
   * @return the power supply status.
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder()
            .append(name).append(" ")
            .append(getPercentCharged()).append("%");
    if (isCharging()) {
      sb.append(" charging");
    } else {
      sb.append(", ").append(getTimeRemaining()).append("s.");
    }
    return sb.toString();
  }

}
