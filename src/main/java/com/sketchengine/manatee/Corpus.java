/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 4.0.1
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.sketchengine.manatee;

public class Corpus {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  protected Corpus(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(Corpus obj) {
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
        manateeJNI.delete_Corpus(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public Corpus(String corp_name) {
    this(manateeJNI.new_Corpus(corp_name), true);
  }

  public long size() {
    return manateeJNI.Corpus_size(swigCPtr, this);
  }

  public long search_size() {
    return manateeJNI.Corpus_search_size(swigCPtr, this);
  }

  public PosAttr get_attr(String name, boolean struct_attr) {
    long cPtr = manateeJNI.Corpus_get_attr__SWIG_0(swigCPtr, this, name, struct_attr);
    return (cPtr == 0) ? null : new PosAttr(cPtr, false);
  }

  public PosAttr get_attr(String name) {
    long cPtr = manateeJNI.Corpus_get_attr__SWIG_1(swigCPtr, this, name);
    return (cPtr == 0) ? null : new PosAttr(cPtr, false);
  }

  public Structure get_struct(String name) {
    long cPtr = manateeJNI.Corpus_get_struct(swigCPtr, this, name);
    return (cPtr == 0) ? null : new Structure(cPtr, false);
  }

  public String get_info() {
    return manateeJNI.Corpus_get_info(swigCPtr, this);
  }

  public String get_conf(String item) {
    return manateeJNI.Corpus_get_conf(swigCPtr, this, item);
  }

  public String get_conffile() {
    return manateeJNI.Corpus_get_conffile(swigCPtr, this);
  }

  public String get_confpath() {
    return manateeJNI.Corpus_get_confpath(swigCPtr, this);
  }

  public void set_default_attr(String attname) {
    manateeJNI.Corpus_set_default_attr(swigCPtr, this, attname);
  }

  public void set_reference_corpus(String refcorp) {
    manateeJNI.Corpus_set_reference_corpus(swigCPtr, this, refcorp);
  }

  public RangeStream filter_query(RangeStream s) {
    long cPtr = manateeJNI.Corpus_filter_query(swigCPtr, this, RangeStream.getCPtr(s), s);
    return (cPtr == 0) ? null : new RangeStream(cPtr, true);
  }

  public String get_sizes() {
    return manateeJNI.Corpus_get_sizes(swigCPtr, this);
  }

  public void compile_frq(String attr) {
    manateeJNI.Corpus_compile_frq(swigCPtr, this, attr);
  }

  public void compile_arf(String attr) {
    manateeJNI.Corpus_compile_arf(swigCPtr, this, attr);
  }

  public void compile_aldf(String attr) {
    manateeJNI.Corpus_compile_aldf(swigCPtr, this, attr);
  }

  public void compile_docf(String attr, String docstruc) {
    manateeJNI.Corpus_compile_docf(swigCPtr, this, attr, docstruc);
  }

  public void freq_dist(RangeStream r, String crit, int limit, StrVector words, NumVector freqs, NumVector norms) {
    manateeJNI.Corpus_freq_dist__SWIG_0(swigCPtr, this, RangeStream.getCPtr(r), r, crit, limit, StrVector.getCPtr(words), words, NumVector.getCPtr(freqs), freqs, NumVector.getCPtr(norms), norms);
  }

  public PosAttr get_struct_attr(String name) {
    long cPtr = manateeJNI.Corpus_get_struct_attr(swigCPtr, this, name);
    return (cPtr == 0) ? null : new PosAttr(cPtr, false);
  }

  public void freq_dist(FastStream r, String crit, int limit, StrVector words, NumVector freqs, NumVector norms) {
    manateeJNI.Corpus_freq_dist__SWIG_1(swigCPtr, this, FastStream.getCPtr(r), r, crit, limit, StrVector.getCPtr(words), words, NumVector.getCPtr(freqs), freqs, NumVector.getCPtr(norms), norms);
  }

  public void freq_dist_from_fs(FastStream r, String crit, int limit, StrVector words, NumVector freqs, NumVector norms) {
    manateeJNI.Corpus_freq_dist_from_fs(swigCPtr, this, FastStream.getCPtr(r), r, crit, limit, StrVector.getCPtr(words), words, NumVector.getCPtr(freqs), freqs, NumVector.getCPtr(norms), norms);
  }

  public FastStream Range2Pos(RangeStream r) {
    long cPtr = manateeJNI.Corpus_Range2Pos(swigCPtr, this, RangeStream.getCPtr(r), r);
    return (cPtr == 0) ? null : new FastStream(cPtr, false);
  }

  public RangeStream eval_query(String query) {
    long cPtr = manateeJNI.Corpus_eval_query(swigCPtr, this, query);
    return (cPtr == 0) ? null : new RangeStream(cPtr, true);
  }

  public FastStream filter_fstream(FastStream s) {
    long cPtr = manateeJNI.Corpus_filter_fstream(swigCPtr, this, FastStream.getCPtr(s), s);
    return (cPtr == 0) ? null : new FastStream(cPtr, true);
  }

  public long count_rest(FastStream s) {
    return manateeJNI.Corpus_count_rest(swigCPtr, this, FastStream.getCPtr(s), s);
  }

  public void count_structattr_vals(String structname, String saname, boolean docsizes, NumVector freqs) {
    manateeJNI.Corpus_count_structattr_vals(swigCPtr, this, structname, saname, docsizes, NumVector.getCPtr(freqs), freqs);
  }

}
