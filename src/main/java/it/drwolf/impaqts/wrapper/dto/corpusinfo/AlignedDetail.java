package it.drwolf.impaqts.wrapper.dto.corpusinfo;

import java.util.ArrayList;
import java.util.List;

public class AlignedDetail {
	private String name;
	private String language_name;
	private List<PosItem> wposlist = new ArrayList<>();
	private List<PosItem> lopslist = new ArrayList<>();
	private boolean hasCase = false;
	private boolean hasLemma = false;
	private String tagsetdoc;

	public String getLanguage_name() {
		return language_name;
	}

	public List<PosItem> getLopslist() {
		return lopslist;
	}

	public String getName() {
		return name;
	}

	public String getTagsetdoc() {
		return tagsetdoc;
	}

	public List<PosItem> getWposlist() {
		return wposlist;
	}

	public boolean isHasCase() {
		return hasCase;
	}

	public boolean isHasLemma() {
		return hasLemma;
	}

	public void setHasCase(boolean hasCase) {
		this.hasCase = hasCase;
	}

	public void setHasLemma(boolean hasLemma) {
		this.hasLemma = hasLemma;
	}

	public void setLanguage_name(String language_name) {
		this.language_name = language_name;
	}

	public void setLopslist(List<PosItem> lopslist) {
		this.lopslist = lopslist;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setTagsetdoc(String tagsetdoc) {
		this.tagsetdoc = tagsetdoc;
	}

	public void setWposlist(List<PosItem> wposlist) {
		this.wposlist = wposlist;
	}
}

