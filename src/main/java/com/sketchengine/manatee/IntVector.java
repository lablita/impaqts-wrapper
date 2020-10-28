package com.sketchengine.manatee;/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.12
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

public class IntVector {
	protected transient boolean swigCMemOwn;
	private transient long swigCPtr;

	protected IntVector(long cPtr, boolean cMemoryOwn) {
		this.swigCMemOwn = cMemoryOwn;
		this.swigCPtr = cPtr;
	}

	public IntVector() {
		this(manateeJNI.new_IntVector__SWIG_0(), true);
	}

	public IntVector(long n) {
		this(manateeJNI.new_IntVector__SWIG_1(n), true);
	}

	protected static long getCPtr(IntVector obj) {
		return (obj == null) ? 0 : obj.swigCPtr;
	}

	public void add(int x) {
		manateeJNI.IntVector_add(this.swigCPtr, this, x);
	}

	public long capacity() {
		return manateeJNI.IntVector_capacity(this.swigCPtr, this);
	}

	public void clear() {
		manateeJNI.IntVector_clear(this.swigCPtr, this);
	}

	public synchronized void delete() {
		if (this.swigCPtr != 0) {
			if (this.swigCMemOwn) {
				this.swigCMemOwn = false;
				manateeJNI.delete_IntVector(this.swigCPtr);
			}
			this.swigCPtr = 0;
		}
	}

	@Override
	protected void finalize() {
		this.delete();
	}

	public int get(int i) {
		return manateeJNI.IntVector_get(this.swigCPtr, this, i);
	}

	public boolean isEmpty() {
		return manateeJNI.IntVector_isEmpty(this.swigCPtr, this);
	}

	public void reserve(long n) {
		manateeJNI.IntVector_reserve(this.swigCPtr, this, n);
	}

	public void set(int i, int val) {
		manateeJNI.IntVector_set(this.swigCPtr, this, i, val);
	}

	public long size() {
		return manateeJNI.IntVector_size(this.swigCPtr, this);
	}

}
