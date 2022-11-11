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
	private List<FrequencyOption> multilevelFrequency = new ArrayList<>();
	private String category;
	private boolean positive;

	public List<String> getCategories() {
		return this.categories;
	}

	public String getCategory() {
		return this.category;
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

	public List<FrequencyOption> getMultilevelFrequency() {
		return this.multilevelFrequency;
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

	public void setMultilevelFrequency(List<FrequencyOption> multilevelFrequency) {
		this.multilevelFrequency = multilevelFrequency;
	}

	public void setPositive(boolean positive) {
		this.positive = positive;
	}
}

