/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 4.0.1
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.sketchengine.manatee;

public class IntGenerator {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  protected IntGenerator(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(IntGenerator obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  @SuppressWarnings("deprecation")
  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        manateeJNI.delete_IntGenerator(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public int next() {
    return manateeJNI.IntGenerator_next(swigCPtr, this);
  }

  public boolean end() {
    return manateeJNI.IntGenerator_end(swigCPtr, this);
  }

}
