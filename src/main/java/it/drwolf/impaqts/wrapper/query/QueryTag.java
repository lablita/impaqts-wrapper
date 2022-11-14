package it.drwolf.impaqts.wrapper.query;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// rappresenta la coppia <nome, valore> che è il filtro di ricerca (es. word="casa" oppure pos="VER.*")
public class QueryTag {
	public final static String LEMMA = "lemma";
	public final static String PHRASE = "phrase";
	public final static String WORD = "word";
	public final static String CHARACTER = "character";

	public final static String CQL = "cql";
	// menu a tendina sopra il testo
	String name; // nome del tag (es. pos, lemma, ...)
	// il testo inserito da cercare
	String value; // valore del tag ricercato

	String structure = "token"; // struttura a cui appartiene il metadato (a livello di token, di sentence, di documento,...)
	boolean startsWithValue = false; // il tag deve iniziare per value (es. value = "sa", word = "sa.*")
	boolean endsWithValue = false; // il tag deve finire per value
	boolean containsValue = false; // il tag deve contenere value
	boolean matchCase = true; // il valore non ignora maiuscole/minuscole
	boolean negation = false; // il token è != e non =

	String defaultAttributeCQL;

	public QueryTag() {

	}

	public QueryTag(String name) {
		this.name = name;
	}

	public QueryTag(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public QueryTag(String name, String value, String structure) {
		this.name = name;
		this.value = value;
		this.structure = structure;
	}

	public String getCQLCharacter() {
		String cql = "[" + QueryTag.WORD + "=\".*" + this.value + ".*\"]";
		return cql;
	}

	public String getCQLPhrase() {
		String[] valueArray = this.value.split(" ");
		List<String> cqlList = new ArrayList<>();
		for (String value : valueArray) {
			//come su bonito
			//cqlList.add("[" + QueryTag.WORD + "=\"(?i)" + value + "\"]");

			//come su http://corpora.dipartimentidieccellenza-dilef.unifi.it/
			cqlList.add("[" + QueryTag.WORD + "=\"" + value + "\"]");
		}
		return cqlList.stream().collect(Collectors.joining(" "));
	}

	public boolean getContainsValue() {
		return this.containsValue;
	}

	public String getCql() {

		String val = this.value;
		if (this.containsValue) {
			val = ".*" + val + ".*";
		} else if (this.startsWithValue) {
			val = val + ".*";
		} else if (this.endsWithValue) {
			val = ".*" + val;
		}
		if (!this.matchCase) {
			val = "(?i)" + val;
		}
		String op = "=";
		if (this.negation) {
			op = "!=";
		}
		if (!this.structure.equals("token")) {
			// strip "doc." string from name
			final String n = this.name.replaceAll("doc\\.", "");
			return this.structure + " " + n + op + "\"" + val + "\"";
		}
		return this.name + op + "\"" + val + "\"";

	}

	public String getDefaultAttributeCQL() {
		return this.defaultAttributeCQL;
	}

	public boolean getEndsWithValue() {
		return this.endsWithValue;
	}

	public boolean getMatchCase() {
		return this.matchCase;
	}

	public String getName() {
		return this.name;
	}

	public boolean getNegation() {
		return this.negation;
	}

	public boolean getStartsWithValue() {
		return this.startsWithValue;
	}

	public String getStructure() {
		return this.structure;
	}

	public String getValue() {
		return this.value;
	}

	public void setContainsValue(boolean x) {
		this.containsValue = x;
	}

	public void setDefaultAttributeCQL(String defaultAttributeCQL) {
		this.defaultAttributeCQL = defaultAttributeCQL;
	}

	public void setEndsWithValue(boolean x) {
		this.endsWithValue = x;
	}

	public void setMatchCase(boolean x) {
		this.matchCase = x;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNegation(boolean negation) {
		this.negation = negation;
	}

	public void setStartsWithValue(boolean x) {
		this.startsWithValue = x;
	}

	public void setStructure(String x) {
		this.structure = x;
	}

	public void setValue(String x) {
		this.value = x;
	}
}
