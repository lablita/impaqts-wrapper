package com.sketchengine.manatee;/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.12
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

public class AttrNotFound {
	protected transient boolean swigCMemOwn;
	private transient long swigCPtr;

	protected AttrNotFound(long cPtr, boolean cMemoryOwn) {
		this.swigCMemOwn = cMemoryOwn;
		this.swigCPtr = cPtr;
	}

	public AttrNotFound(String name) {
		this(manateeJNI.new_AttrNotFound(name), true);
	}

	protected static long getCPtr(AttrNotFound obj) {
		return (obj == null) ? 0 : obj.swigCPtr;
	}

	public String __str__() {
		return manateeJNI.AttrNotFound___str__(this.swigCPtr, this);
	}

	public synchronized void delete() {
		if (this.swigCPtr != 0) {
			if (this.swigCMemOwn) {
				this.swigCMemOwn = false;
				manateeJNI.delete_AttrNotFound(this.swigCPtr);
			}
			this.swigCPtr = 0;
		}
	}

	@Override
	protected void finalize() {
		this.delete();
	}

}
