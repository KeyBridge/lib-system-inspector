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

import ch.keybridge.lib.sig.hw.sensor.ThermalInfo;
import org.junit.Test;

/**
 *
 * @author Key Bridge LLC
 */
public class ThermalSensorTest {

  @Test
  public void testThermal() {
    System.out.println("Thermal Sensor Test");
    for (ThermalInfo instance : ThermalInfo.getAllInstances()) {
      System.out.println(instance);
    }
  }

}
