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

import java.util.HashMap;
import java.util.Map;

public class ReferencePositionResponse {
	private Long tokenNumber;
	private Long documentNumber;
	private Map<String, String> references = new HashMap<>();

	public Long getDocumentNumber() {
		return documentNumber;
	}

	public Map<String, String> getReferences() {
		return references;
	}

	public Long getTokenNumber() {
		return tokenNumber;
	}

	public void setDocumentNumber(Long documentNumber) {
		this.documentNumber = documentNumber;
	}

	public void setReferences(Map<String, String> references) {
		this.references = references;
	}

	public void setTokenNumber(Long tokenNumber) {
		this.tokenNumber = tokenNumber;
	}
}
