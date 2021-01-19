package it.drwolf.impaqts.wrapper.query;

// rappresenta la coppia <nome, valore> che è il filtro di ricerca (es. word="casa" oppure pos="VER.*")
public class QueryTag {
    String name; // nome del tag (es. pos, lemma, ...)
    String value; // valore del tag ricercato
    String structure = "token"; // struttura a cui appartiene il metadato (a livello di token, di sentence, di documento,...)
    boolean startsWithValue = false; // il tag deve iniziare per value (es. value = "sa", word = "sa.*")
    boolean endsWithValue = false; // il tag deve finire per value
    boolean containsValue = false; // il tag deve contenere value
    boolean matchCase = true; // il valore non ignora maiuscole/minuscole
    boolean negation = false; // il token è != e non =

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

    public String getCql() {
        String val = value;
        if (containsValue) {
            val = ".*" + val + ".*";
        } else if (startsWithValue) {
            val = val + ".*";
        } else if (endsWithValue) {
            val = ".*" + val;
        }
        if (!matchCase) {
            val = "(?i)" + val;
        }
        String op = "=";
        if (this.negation) {
            op = "!=";
        }
        if (!structure.equals("token")) {
            return structure + " " + name + op + "\"" + val + "\"";
        }
        return name + op + "\"" + val + "\"";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return this.value;
    }

    public boolean getEndsWithValue() {
        return this.endsWithValue;
    }

    public boolean getStartsWithValue() {
        return this.startsWithValue;
    }

    public boolean getContainsValue() {
        return this.containsValue;
    }

    public boolean getMatchCase() {
        return this.matchCase;
    }

    public String getStructure() { return this.structure; }

    public boolean getNegation() {
        return this.negation;
    }

    public void setValue(String x) {
        this.value = x;
    }

    public void setEndsWithValue(boolean x) {
        this.endsWithValue = x;
    }

    public void setStartsWithValue(boolean x) {
        this.startsWithValue = x;
    }

    public void setContainsValue(boolean x) {
        this.containsValue = x;
    }

    public void setMatchCase(boolean x) {
        this.matchCase = x;
    }

    public void setStructure(String x) { this.structure = x; }

    public void setNegation(boolean negation) {
        this.negation = negation;
    }
}
