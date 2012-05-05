/*
 * Copyright 2000-2012 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jetbrains.android.util;

import org.jetbrains.annotations.NotNull;

/**
* @author Eugene.Kudelevsky
*/
public class ResourceEntry {
  private final String myType;
  private final String myName;

  public ResourceEntry(@NotNull String type, @NotNull String name) {
    myType = type;
    myName = name;
  }

  @NotNull
  public String getType() {
    return myType;
  }

  @NotNull
  public String getName() {
    return myName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ResourceEntry entry = (ResourceEntry)o;

    if (!myName.equals(entry.myName)) return false;
    if (!myType.equals(entry.myType)) return false;

    return true;
  }

  @Override
  public String toString() {
    return "[" + myType + ":" + myName + "]";
  }

  @Override
  public int hashCode() {
    int result = myType.hashCode();
    result = 31 * result + myName.hashCode();
    return result;
  }
}