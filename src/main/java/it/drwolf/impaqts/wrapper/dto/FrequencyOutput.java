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

package it.drwolf.impaqts.wrapper.dto;

import java.util.ArrayList;
import java.util.List;

public class FrequencyOutput {
	private List<KWICLineDTO> lines = new ArrayList<>();
	private int concSize;
	private int fromP;

	public int getConcSize() {
		return this.concSize;
	}

	public int getFromP() {
		return this.fromP;
	}

	public List<KWICLineDTO> getLines() {
		return this.lines;
	}

	public void setConcSize(int concSize) {
		this.concSize = concSize;
	}

	public void setFromP(int fromP) {
		this.fromP = fromP;
	}

	public void setLines(List<KWICLineDTO> lines) {
		this.lines = lines;
	}
}
