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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Objects;
import java.util.TreeSet;

/**
 * Parsed container of content from the {@code /etc/passwd} file.
 * <p>
 * {@code /etc/passwd} file stores essential information, which is required
 * during login i.e. user account information. {@code /etc/passwd} is a text
 * file, that contains a list of the system's accounts, giving for each account
 * some useful information like user ID, group ID, home directory, shell, etc.
 * <p>
 * {@code /etc/passwd} has general read permission but write access only for the
 * superuser/root account.
 *
 * @author Key Bridge LLC
 * @since 1.0.0 (01/31/16) 01/30/16
 */
public class SystemUserInfo implements Comparable<SystemUserInfo> {

  /**
   * It is used when user logs in. It should be between 1 and 32 characters in
   * length.
   */
  private String username;
  /**
   * An x character indicates that encrypted password is stored in /etc/shadow
   * file.
   */
  private String password;
  /**
   * Each user must be assigned a user ID (UID). UID 0 (zero) is reserved for
   * root and UIDs 1-99 are reserved for other predefined accounts. Further UID
   * 100-999 are reserved by system for administrative and system
   * accounts/groups.
   */
  private String userID;
  /**
   * The primary group ID (stored in /etc/group file)
   */
  private String groupID;
  /**
   * The comment field. It allow you to add extra information about the users
   * such as user's full name, phone number etc. This field use by finger
   * command.
   */
  private String userIDInfo;
  /**
   * The absolute path to the directory the user will be in when they log in. If
   * this directory does not exists then users directory becomes /
   */
  private String homeDirectory;
  /**
   * The absolute path of a command or shell (/bin/bash). Typically, this is a
   * shell. Please note that it does not have to be a shell.
   */
  private String commandShell;

  /**
   * Read and parse the local {@code /etc/passwd} configuration file into a
   * collection of SystemUserInfo configurations.
   *
   * @return a HashSet containing all OS users.
   * @throws IOException if the {@code /etc/passwd} file cannot be read.
   */
  public static Collection<SystemUserInfo> getAllUsers() throws IOException {
    Collection<SystemUserInfo> users = new TreeSet<>();
    for (String entry : Files.readAllLines(Paths.get("/etc/passwd"))) {
      try {
        users.add(SystemUserInfo.parseEntry(entry));
      } catch (Exception e) {
        System.err.println(" error: " + entry + "   -  " + e.getMessage());
      }
    }
    return users;
  }

  /**
   * Parse an entry from the {@code /etc/passwd} file into a LinuxOSUser
   * instance.
   *
   * @param entry a line entry from the {@code /etc/passwd} file
   * @return a LinuxOSUser instance
   */
  public static SystemUserInfo parseEntry(String entry) {
    String[] t = entry.trim().split(":");
    if (t.length != 7) {
      throw new IllegalArgumentException("Invalid passwd entry: " + entry + "  " + t.length);
    }
//    for (int i = 0; i < t.length; i++) {      System.out.println(i + "  " + t[i]);    }
    SystemUserInfo user = new SystemUserInfo();
    user.setUsername(t[0]);
    user.setPassword(t[1]);
    user.setUserID(t[2]);
    user.setGroupID(t[3]);
    user.setUserIDInfo(t[4]);
    user.setHomeDirectory(t[5]);
    user.setCommandShell(t[6]);
    return user;
  }

  //<editor-fold defaultstate="collapsed" desc="Getter and Setter">
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getUserID() {
    return userID;
  }

  public void setUserID(String userID) {
    this.userID = userID;
  }

  public String getGroupID() {
    return groupID;
  }

  public void setGroupID(String groupID) {
    this.groupID = groupID;
  }

  public String getUserIDInfo() {
    return userIDInfo;
  }

  public void setUserIDInfo(String userIDInfo) {
    this.userIDInfo = userIDInfo;
  }

  public String getHomeDirectory() {
    return homeDirectory;
  }

  public void setHomeDirectory(String homeDirectory) {
    this.homeDirectory = homeDirectory;
  }

  public String getCommandShell() {
    return commandShell;
  }

  public void setCommandShell(String commandShell) {
    this.commandShell = commandShell;
  }//</editor-fold>

  /**
   * Determine if this user has remote access to the current system.
   * <p>
   * This method ONLY inspects for remote SSH access. It inspects the user home
   * directory, looking for a {@code ~/.ssh/id_rsa} or {@code ~/.ssh/id_dsa}
   * file.
   *
   * @return TRUE if this user has a {@code ~/.ssh/id_rsa} file in their home
   *         directory.
   */
  public Boolean isRemoteAccessCapable() {
    return Paths.get(homeDirectory, ".ssh", "id_rsa").toFile().exists()
           || Paths.get(homeDirectory, ".ssh", "id_dsa").toFile().exists();
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 17 * hash + Objects.hashCode(this.username);
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
    final SystemUserInfo other = (SystemUserInfo) obj;
    return Objects.equals(this.username, other.username);
  }

  @Override
  public int compareTo(SystemUserInfo o) {
    return this.username.compareTo(o.username);
  }

  @Override
  public String toString() {
    return username + ", " + homeDirectory + ", " + commandShell;
  }

}
