/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.event;

/**
 * This enum contains the available types of a change.
 *
 * @since 1.0.0
 */
public enum ChangeType {

  /**
   * A change of this type indicates, that something new has been added. E.g. one or multiple item(s) have been inserted
   * into a collection.
   */
  ADD,

  /**
   * A change of this type indicates, that something that already exists has been updated. E.g. an existing item has
   * been updated.
   */
  UPDATE,

  /**
   * A change of this type indicates, that something has been removed. E.g. one or multiple existing item(s) have been
   * removed from a collection.
   */
  REMOVE
}
