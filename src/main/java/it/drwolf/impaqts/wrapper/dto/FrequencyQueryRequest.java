package it.drwolf.impaqts.wrapper.dto;

import java.util.ArrayList;
import java.util.List;

public class FrequencyQueryRequest {
	private Integer frequencyLimit;
	private Boolean includeCategoriesWithNoHits;
	private Integer frequencyColSort;
	private String frequencyTypeSort;
	private String frequencyType;
	private List<String> categories = new ArrayList<>();
	private List<FrequencyOption> freqOptList = new ArrayList<>();
	private String category;
	private boolean positive;

	public List<String> getCategories() {
		return this.categories;
	}

	public String getCategory() {
		return this.category;
	}

	public List<FrequencyOption> getFreqOptList() {
		return this.freqOptList;
	}

	public Integer getFrequencyColSort() {
		return this.frequencyColSort;
	}

	public Integer getFrequencyLimit() {
		return this.frequencyLimit;
	}

	public String getFrequencyType() {
		return this.frequencyType;
	}

	public String getFrequencyTypeSort() {
		return this.frequencyTypeSort;
	}

	public Boolean getIncludeCategoriesWithNoHits() {
		return this.includeCategoriesWithNoHits;
	}

	public boolean isPositive() {
		return this.positive;
	}

	public void setCategories(List<String> categories) {
		this.categories = categories;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setFreqOptList(List<FrequencyOption> freqOptList) {
		this.freqOptList = freqOptList;
	}

	public void setFrequencyColSort(Integer frequencyColSort) {
		this.frequencyColSort = frequencyColSort;
	}

	public void setFrequencyLimit(Integer frequencyLimit) {
		this.frequencyLimit = frequencyLimit;
	}

	public void setFrequencyType(String frequencyType) {
		this.frequencyType = frequencyType;
	}

	public void setFrequencyTypeSort(String frequencyTypeSort) {
		this.frequencyTypeSort = frequencyTypeSort;
	}

	public void setIncludeCategoriesWithNoHits(Boolean includeCategoriesWithNoHits) {
		this.includeCategoriesWithNoHits = includeCategoriesWithNoHits;
	}

	public void setPositive(boolean positive) {
		this.positive = positive;
	}
}

