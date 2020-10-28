package com.sketchengine.manatee;/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.12
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

public class CorpRegion {
	protected transient boolean swigCMemOwn;
	private transient long swigCPtr;

	protected CorpRegion(long cPtr, boolean cMemoryOwn) {
		this.swigCMemOwn = cMemoryOwn;
		this.swigCPtr = cPtr;
	}

	public CorpRegion(Corpus corp, String attra, String struca, boolean ignore_nondef) {
		this(manateeJNI.new_CorpRegion__SWIG_0(Corpus.getCPtr(corp), corp, attra, struca, ignore_nondef), true);
	}

	public CorpRegion(Corpus corp, String attra, String struca) {
		this(manateeJNI.new_CorpRegion__SWIG_1(Corpus.getCPtr(corp), corp, attra, struca), true);
	}

	protected static long getCPtr(CorpRegion obj) {
		return (obj == null) ? 0 : obj.swigCPtr;
	}

	public synchronized void delete() {
		if (this.swigCPtr != 0) {
			if (this.swigCMemOwn) {
				this.swigCMemOwn = false;
				manateeJNI.delete_CorpRegion(this.swigCPtr);
			}
			this.swigCPtr = 0;
		}
	}

	@Override
	protected void finalize() {
		this.delete();
	}

	public StrVector region(long frompos, long topos, char posdelim, char attrdelim) {
		return new StrVector(
				manateeJNI.CorpRegion_region__SWIG_0(this.swigCPtr, this, frompos, topos, posdelim, attrdelim), true);
	}

	public StrVector region(long frompos, long topos, char posdelim) {
		return new StrVector(manateeJNI.CorpRegion_region__SWIG_1(this.swigCPtr, this, frompos, topos, posdelim), true);
	}

	public StrVector region(long frompos, long topos) {
		return new StrVector(manateeJNI.CorpRegion_region__SWIG_2(this.swigCPtr, this, frompos, topos), true);
	}

}
