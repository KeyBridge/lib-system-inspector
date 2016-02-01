/*
 * Copyright 2016 Caulfield IP Holdings (Caulfield) and affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * Software Code is protected by copyright. Caulfield hereby
 * reserves all rights and copyrights and no license is
 * granted under said copyrights in this Software License Agreement.
 * Caulfield generally licenses software for commercialization
 * pursuant to the terms of either a Standard Software Source Code
 * License Agreement or a Standard Product License Agreement.
 * A copy of these agreements may be obtained by sending a request
 * via email to info@caufield.org.
 */
package ch.keybridge.lib.sig.hw.sensor;

import ch.keybridge.lib.sig.utility.SIGUtility;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Computer system thermal sensor information.
 * <p>
 * This utility class contains information about the various THERMAL sensors on
 * a given computer system. It reads and parses thermal information from runtime
 * files in the {@code /sys/class/hwmon/hwmon{i}0/device} directory.
 * <p>
 * On a given system, there may be one or more hardware monitoring chips. Each
 * chip may have several features. For example, the LM78 monitors 7 voltage
 * inputs, 3 fans and one temperature. Feature names are standardized. Typical
 * fea‐ ture names are in0, in1, in2... for voltage inputs, fan1, fan2, fan3...
 * for fans and temp1, temp2, temp3... for tem‐ perature inputs.
 * <p>
 * Each feature may in turn have one or more sub-features, each representing an
 * attribute of the feature: input value, low limit, high limit, alarm, etc.
 * Sub-feature names are standardized as well. For example, the first voltage
 * input (in0) would typically have sub-features in0_input (measured value),
 * in0_min (low limit), in0_max (high limit) and in0_alarm (alarm flag). Which
 * sub-features are actually present depend on the exact chip type.
 *
 * @author Key Bridge LLC
 */
public class ThermalInfo {

  /**
   * The kernel module name. e.g. "coretemp".
   */
  public String moduleName;
  /**
   * The kernel module alias. e.g. "platform:coretemp".
   */
  private String moduleAlias;

  /**
   * The individual sensor label / name. e.g. "Core 0"
   */
  private String label;
  /**
   * The current sensor temperature reading. It is measured in degrees Celsius
   * with a resolution of 1/8th degree.
   */
  private Double currentTemperature;
  /**
   * The critical temperature value, above which a more cooling (if available)
   * is applied. This is generally used to control variable speed fans and other
   * adjustable cooling systems. (degrees Celsius)
   */
  private Double criticalTemperature;
  /**
   * The alarm temperature value (degrees Celsius), above which a
   * over-temperature alarm should be raised.
   * <p>
   * Damage to the monitored device may occur if temperature strays too far
   * above this temperature.
   */
  private Double alarmTemperature;
  /**
   * The maximum allowed / available temperature (degrees Celsius). This is the
   * maximum measurable temperature of the sensing apparatus.
   */
  private Double maxTemperature;

  /**
   * Supplemental information for main board (non CPU) type sensors. This is
   * extracted from the {@code tempX_type} file, which encodes as follows:
   * <pre>
   * 0 - disabled
   * 3 - thermal diode
   * 4 - thermistor
   * </pre>
   */
  private Double type;

  private static final String PATH_ROOT = "/sys/class/hwmon";

  /**
   * Scan, read and parse all available temperature sensor configurations on the
   * current system.
   * <p>
   * This method reads sensor information from the
   * {@code /sys/class/hwmon/hwmon[i]/device/temp[i]} kernel run time files.
   *
   * @return a collection of ThermalInfo configurations
   */
  public static Collection<ThermalInfo> getAllInstances() {
    Collection<ThermalInfo> sensors = new HashSet<>();
    /**
     * List the Hardware monitors.
     */
    Path root = Paths.get(PATH_ROOT);
    for (File directory : root.toFile().listFiles(new FilenameFilter() {
      @Override
      public boolean accept(File dir, String name) {
        return name.startsWith("hwmon");
      }
    })) {
      /**
       * Just iterate to TRY capturing the first ... say ... 20 sensors. This is
       * easier (but lazier) than scanning the kernel runtime files and
       * performing a regex on their file names.
       * <p>
       * 20 is chosen as an arbitrarily high number, likely to capture all
       * temperature sensors in a computer system.
       */
      for (int i = 0; i < 20; i++) {
        /**
         * Test that an _input file exists for this device. If yes, then try to
         * add a ThermalInfo container.
         */
        if (Paths.get(PATH_ROOT, directory.getName(), "device", "temp" + i + "_input").toFile().exists()) {
          try {
            sensors.add(ThermalInfo.getInstance(directory.getName(), "temp" + i));
          } catch (IOException ex) {
            Logger.getLogger(ThermalInfo.class.getName()).log(Level.SEVERE, null, ex);
          }
        }
      }
    }
    return sensors;
  }

  /**
   * Internal builder method to read sensor information from the
   * {@code /sys/class/hwmon/hwmon[i]/device/temp[i]} kernel run time files.
   *
   * @param hwmon the hardware monitor subdirectory name, located in the
   *              {@code /sys/class/hwmon/} directory.
   * @param name  the temperature sensor name. This is the data file prefix,
   *              typically formatted as "temp[d]. i.e. "temp1".
   * @return a new ThermalInfo configuration
   * @throws IOException if the run time data files cannot be read.
   */
  private static ThermalInfo getInstance(String hwmon, String name) throws IOException {
    ThermalInfo thermalInfo = new ThermalInfo();
    thermalInfo.setModuleName(SIGUtility.readFileString(Paths.get(PATH_ROOT, hwmon, "device", "name")));
    thermalInfo.setModuleAlias(SIGUtility.readFileString(Paths.get(PATH_ROOT, hwmon, "device", "modalias")));
    /**
     * System (main board) sensors do not declare a label.
     */
    Path label = Paths.get(PATH_ROOT, hwmon, "device", name + "_label");
    thermalInfo.setLabel(label.toFile().exists()
                         ? SIGUtility.readFileString(label)
                         : name);

    thermalInfo.setCurrentTemperature(SIGUtility.readFileDouble(Paths.get(PATH_ROOT, hwmon, "device", name + "_input")) / 1000);
    thermalInfo.setCriticalTemperature(SIGUtility.readFileDouble(Paths.get(PATH_ROOT, hwmon, "device", name + "_crit")) / 1000);
    thermalInfo.setAlarmTemperature(SIGUtility.readFileDouble(Paths.get(PATH_ROOT, hwmon, "device", name + "_crit_alarm")) / 10000);
    thermalInfo.setMaxTemperature(SIGUtility.readFileDouble(Paths.get(PATH_ROOT, hwmon, "device", name + "_max")));
    /**
     * Core (CPU) sensors do not declare the type.
     */
    Path type = Paths.get(PATH_ROOT, hwmon, "device", name + "_type");
    thermalInfo.setType(type.toFile().exists()
                        ? SIGUtility.readFileDouble(type)
                        : null);

    return thermalInfo;
  }

  //<editor-fold defaultstate="collapsed" desc="Getter and Setter">
  public String getModuleName() {
    return moduleName;
  }

  public void setModuleName(String moduleName) {
    this.moduleName = moduleName;
  }

  public String getModuleAlias() {
    return moduleAlias;
  }

  public void setModuleAlias(String moduleAlias) {
    this.moduleAlias = moduleAlias;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public Double getCurrentTemperature() {
    return currentTemperature;
  }

  public void setCurrentTemperature(Double currentTemperature) {
    this.currentTemperature = currentTemperature;
  }

  public Double getCriticalTemperature() {
    return criticalTemperature;
  }

  public void setCriticalTemperature(Double criticalTemperature) {
    this.criticalTemperature = criticalTemperature;
  }

  public Double getAlarmTemperature() {
    return alarmTemperature;
  }

  public void setAlarmTemperature(Double alarmTemperature) {
    this.alarmTemperature = alarmTemperature;
  }

  public Double getMaxTemperature() {
    return maxTemperature;
  }

  public void setMaxTemperature(Double maxTemperature) {
    this.maxTemperature = maxTemperature;
  }

  public Double getType() {
    return type;
  }

  public void setType(Double type) {
    this.type = type;
  }//</editor-fold>

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 53 * hash + Objects.hashCode(this.label);
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
    final ThermalInfo other = (ThermalInfo) obj;
    return Objects.equals(this.label, other.label);
  }

  @Override
  public String toString() {
    return label + ": " + new DecimalFormat("#0.0").format(currentTemperature) + "°C";
  }

}
