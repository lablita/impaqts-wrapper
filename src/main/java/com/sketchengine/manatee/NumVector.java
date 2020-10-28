package com.sketchengine.manatee;/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.12
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

public class NumVector {
	protected transient boolean swigCMemOwn;
	private transient long swigCPtr;

	protected NumVector(long cPtr, boolean cMemoryOwn) {
		this.swigCMemOwn = cMemoryOwn;
		this.swigCPtr = cPtr;
	}

	public NumVector() {
		this(manateeJNI.new_NumVector__SWIG_0(), true);
	}

	public NumVector(long n) {
		this(manateeJNI.new_NumVector__SWIG_1(n), true);
	}

	protected static long getCPtr(NumVector obj) {
		return (obj == null) ? 0 : obj.swigCPtr;
	}

	public void add(long x) {
		manateeJNI.NumVector_add(this.swigCPtr, this, x);
	}

	public long capacity() {
		return manateeJNI.NumVector_capacity(this.swigCPtr, this);
	}

	public void clear() {
		manateeJNI.NumVector_clear(this.swigCPtr, this);
	}

	public synchronized void delete() {
		if (this.swigCPtr != 0) {
			if (this.swigCMemOwn) {
				this.swigCMemOwn = false;
				manateeJNI.delete_NumVector(this.swigCPtr);
			}
			this.swigCPtr = 0;
		}
	}

	@Override
	protected void finalize() {
		this.delete();
	}

	public long get(int i) {
		return manateeJNI.NumVector_get(this.swigCPtr, this, i);
	}

	public boolean isEmpty() {
		return manateeJNI.NumVector_isEmpty(this.swigCPtr, this);
	}

	public void reserve(long n) {
		manateeJNI.NumVector_reserve(this.swigCPtr, this, n);
	}

	public void set(int i, long val) {
		manateeJNI.NumVector_set(this.swigCPtr, this, i, val);
	}

	public long size() {
		return manateeJNI.NumVector_size(this.swigCPtr, this);
	}

}
