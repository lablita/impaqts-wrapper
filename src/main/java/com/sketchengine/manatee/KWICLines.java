package com.sketchengine.manatee;/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.12
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

public class KWICLines {
	protected transient boolean swigCMemOwn;
	private transient long swigCPtr;

	protected KWICLines(long cPtr, boolean cMemoryOwn) {
		this.swigCMemOwn = cMemoryOwn;
		this.swigCPtr = cPtr;
	}

	public KWICLines(Corpus corp, RangeStream r, String left, String right, String kwica, String ctxa, String struca,
			String refa, int maxctx) {
		this(manateeJNI.new_KWICLines__SWIG_0(Corpus.getCPtr(corp), corp, RangeStream.getCPtr(r), r, left, right, kwica,
				ctxa, struca, refa, maxctx), true);
	}

	public KWICLines(Corpus corp, RangeStream r, String left, String right, String kwica, String ctxa, String struca,
			String refa) {
		this(manateeJNI.new_KWICLines__SWIG_1(Corpus.getCPtr(corp), corp, RangeStream.getCPtr(r), r, left, right, kwica,
				ctxa, struca, refa), true);
	}

	protected static long getCPtr(KWICLines obj) {
		return (obj == null) ? 0 : obj.swigCPtr;
	}

	public synchronized void delete() {
		if (this.swigCPtr != 0) {
			if (this.swigCMemOwn) {
				this.swigCMemOwn = false;
				manateeJNI.delete_KWICLines(this.swigCPtr);
			}
			this.swigCPtr = 0;
		}
	}

	@Override
	protected void finalize() {
		this.delete();
	}

	public long get_ctxbeg() {
		return manateeJNI.KWICLines_get_ctxbeg(this.swigCPtr, this);
	}

	public long get_ctxend() {
		return manateeJNI.KWICLines_get_ctxend(this.swigCPtr, this);
	}

	public StrVector get_kwic() {
		return new StrVector(manateeJNI.KWICLines_get_kwic(this.swigCPtr, this), true);
	}

	public int get_kwiclen() {
		return manateeJNI.KWICLines_get_kwiclen(this.swigCPtr, this);
	}

	public StrVector get_left() {
		return new StrVector(manateeJNI.KWICLines_get_left(this.swigCPtr, this), true);
	}

	public int get_linegroup() {
		return manateeJNI.KWICLines_get_linegroup(this.swigCPtr, this);
	}

	public long get_pos() {
		return manateeJNI.KWICLines_get_pos(this.swigCPtr, this);
	}

	public StrVector get_ref_list() {
		return new StrVector(manateeJNI.KWICLines_get_ref_list(this.swigCPtr, this), true);
	}

	public String get_refs() {
		return manateeJNI.KWICLines_get_refs(this.swigCPtr, this);
	}

	public StrVector get_right() {
		return new StrVector(manateeJNI.KWICLines_get_right(this.swigCPtr, this), true);
	}

	public boolean nextcontext() {
		return manateeJNI.KWICLines_nextcontext(this.swigCPtr, this);
	}

	public boolean nextline() {
		return manateeJNI.KWICLines_nextline(this.swigCPtr, this);
	}

	public boolean skip(int count) {
		return manateeJNI.KWICLines_skip(this.swigCPtr, this, count);
	}

}
