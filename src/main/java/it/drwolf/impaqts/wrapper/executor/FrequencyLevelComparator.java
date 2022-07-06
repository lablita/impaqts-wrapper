package it.drwolf.impaqts.wrapper.executor;

import it.drwolf.impaqts.wrapper.dto.FrequencyResultLine;

import java.util.Comparator;

public class FrequencyLevelComparator implements Comparator<FrequencyResultLine> {

	private int level;

	@Override
	public int compare(FrequencyResultLine o1, FrequencyResultLine o2) {
		return o1.getWord().get(this.level).compareTo(o2.getWord().get(this.level));
	}

	public int getLevel() {
		return this.level;
	}

	@Override
	public Comparator<FrequencyResultLine> reversed() {
		return Comparator.super.reversed();
	}

	public void setLevel(int level) {
		this.level = level;
	}

}
