# System Inspector General *(SIG)*

![Gadget](docs/gadget.png)

*SIG* is a set of utilities to inspect and collect information about the (native)
compute platform and operating system.

*SIG* data collection methods are written with the **Linux** operating system in
mind but with a little care and feeding may be readily adopted to also support
OSX (trivial) and Windows (less easy, but not difficult).

## Approach

*SIG* does not make or use JNA calls. Instead *SIG* reads files from the **/proc**
and **/sys** file systems and executes and parses the output of various
common system commands such as **ps**, **df** and **netstat**.

*SIG* does not collect network IP configurations, which are already available within
Java. Instead SIG collects interface status and statistics to supplement the
existing information available in java.util.NetworkInterface.

## Feature Request

Click the **Issues** button above to make a feature request or report a bug.

## Release Info
v1.0.0 (Jan 2016) Initial Release

SIG collects the following system information:
* Hardware: CPU, memory, file system, network interface, display, power supply
* Software: operating system, processes, sockets, users, NTP configuration

v1.0.1 (Feb 2016) Feature add
* Collect thermal sensor data

v1.0.2 (Feb 2016) Feature add, Bug fixes
* Add IPv6 stats, system timezone, remote access user flag
* Fix socket number parsing
* Rewrite OS to support debian flavors
* System uptime in seconds (not millis)
* Sorted NTP peers

v1.0.3 (Feb 2016) Feature add
* Add wireless network scanning

## Alternatives
If you are looking for a ready multiplatform SI library consider
[oshi](https://github.com/dblock/oshi) or [sigar](https://support.hyperic.com/display/SIGAR/Home)



# License

[Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)
