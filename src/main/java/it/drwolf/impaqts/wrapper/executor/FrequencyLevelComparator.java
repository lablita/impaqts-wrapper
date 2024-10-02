/*
 * Copyright (C) 2024
 * EMMACorpus
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package it.drwolf.impaqts.wrapper.executor;

import it.drwolf.impaqts.wrapper.dto.FrequencyResultLine;

import java.util.Comparator;

public class FrequencyLevelComparator implements Comparator<FrequencyResultLine> {

	private int level;

	@Override
	public int compare(FrequencyResultLine o1, FrequencyResultLine o2) {
		if (this.level < 0) {
			return -1;
		}
		if (o1 == null || o1.getWord() == null || this.level >= o1.getWord().size()) {
			return -1;
		}
		if (o2 == null || o2.getWord() == null || this.level >= o2.getWord().size()) {
			return 1;
		}
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
