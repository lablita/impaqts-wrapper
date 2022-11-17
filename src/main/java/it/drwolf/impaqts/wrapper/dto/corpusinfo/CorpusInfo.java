package it.drwolf.impaqts.wrapper.dto.corpusinfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CorpusInfo {
	private List<String> aligned = new ArrayList<>();
	private List<AlignedDetail> alignedDetails = new ArrayList<>();
	private List<List<String>> alsizes = new ArrayList<>();
	private List<CorpusAttribute> attributes = new ArrayList<>();
	private Date compiled;
	private String defaultattr;
	private boolean deffilterlink = false;
	private List<String> diachronic = new ArrayList<>();
	private String docStructure;
	private String encoding;
	private String errsetdoc;
	private List<String> freqttattrs = new ArrayList<>();

	// TODO
	// private List gramrels = new ArrayList();
	private String info;
	private String infohref;
	private boolean isErrorCorpus = false;
	private String lang;
	private List lposlist = new ArrayList();
	private String name;
	private String newversion;
	private boolean righttoleft = false;
	private String shortref;
	private Map<String, String> sizes = new HashMap<>();
	private String structctx;

	// TODO
	// private List structs;

	private List<CorpusInfoStructure> structures = new ArrayList<>();
	// TODO
	// private List subcorpattrs = new ArrayList();
	private String tagsetdoc;
	private String termdef;
	private boolean unicameral = false;
	private List<String> wposlist = new ArrayList<>();
	private String wsattr;
	private String wsdef;

	public List<String> getAligned() {
		return aligned;
	}

	public List<AlignedDetail> getAlignedDetails() {
		return alignedDetails;
	}

	public List<List<String>> getAlsizes() {
		return alsizes;
	}

	public List<CorpusAttribute> getAttributes() {
		return attributes;
	}

	public Date getCompiled() {
		return compiled;
	}

	public String getDefaultattr() {
		return defaultattr;
	}

	public List<String> getDiachronic() {
		return diachronic;
	}

	public String getDocStructure() {
		return docStructure;
	}

	public String getEncoding() {
		return encoding;
	}

	public String getErrsetdoc() {
		return errsetdoc;
	}

	public List<String> getFreqttattrs() {
		return freqttattrs;
	}

	public String getInfo() {
		return info;
	}

	public String getInfohref() {
		return infohref;
	}

	public String getLang() {
		return lang;
	}

	public List<String> getLposlist() {
		return lposlist;
	}

	public String getName() {
		return name;
	}

	public String getNewversion() {
		return newversion;
	}

	public String getShortref() {
		return shortref;
	}

	public Map<String, String> getSizes() {
		return sizes;
	}

	public String getStructctx() {
		return structctx;
	}

	public List<CorpusInfoStructure> getStructures() {
		return structures;
	}

	public String getTagsetdoc() {
		return tagsetdoc;
	}

	public String getTermdef() {
		return termdef;
	}

	public List<String> getWposlist() {
		return wposlist;
	}

	public String getWsattr() {
		return wsattr;
	}

	public String getWsdef() {
		return wsdef;
	}

	public boolean isDeffilterlink() {
		return deffilterlink;
	}

	public boolean isErrorCorpus() {
		return isErrorCorpus;
	}

	public boolean isRighttoleft() {
		return righttoleft;
	}

	public boolean isUnicameral() {
		return unicameral;
	}

	public void setAligned(List<String> aligned) {
		this.aligned = aligned;
	}

	public void setAlignedDetails(List<AlignedDetail> alignedDetails) {
		this.alignedDetails = alignedDetails;
	}

	public void setAlsizes(List<List<String>> alsizes) {
		this.alsizes = alsizes;
	}

	public void setAttributes(List<CorpusAttribute> attributes) {
		this.attributes = attributes;
	}

	public void setCompiled(Date compiled) {
		this.compiled = compiled;
	}

	public void setDefaultattr(String defaultattr) {
		this.defaultattr = defaultattr;
	}

	public void setDeffilterlink(boolean deffilterlink) {
		this.deffilterlink = deffilterlink;
	}

	public void setDiachronic(List<String> diachronic) {
		this.diachronic = diachronic;
	}

	public void setDocStructure(String docStructure) {
		this.docStructure = docStructure;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public void setErrorCorpus(boolean errorCorpus) {
		isErrorCorpus = errorCorpus;
	}

	public void setErrsetdoc(String errsetdoc) {
		this.errsetdoc = errsetdoc;
	}

	public void setFreqttattrs(List<String> freqttattrs) {
		this.freqttattrs = freqttattrs;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public void setInfohref(String infohref) {
		this.infohref = infohref;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public void setLposlist(List<String> lposlist) {
		this.lposlist = lposlist;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNewversion(String newversion) {
		this.newversion = newversion;
	}

	public void setRighttoleft(boolean righttoleft) {
		this.righttoleft = righttoleft;
	}

	public void setShortref(String shortref) {
		this.shortref = shortref;
	}

	public void setSizes(Map<String, String> sizes) {
		this.sizes = sizes;
	}

	public void setStructctx(String structctx) {
		this.structctx = structctx;
	}

	public void setStructures(List<CorpusInfoStructure> structures) {
		this.structures = structures;
	}

	public void setTagsetdoc(String tagsetdoc) {
		this.tagsetdoc = tagsetdoc;
	}

	public void setTermdef(String termdef) {
		this.termdef = termdef;
	}

	public void setUnicameral(boolean unicameral) {
		this.unicameral = unicameral;
	}

	public void setWposlist(List<String> wposlist) {
		this.wposlist = wposlist;
	}

	public void setWsattr(String wsattr) {
		this.wsattr = wsattr;
	}

	public void setWsdef(String wsdef) {
		this.wsdef = wsdef;
	}
}
