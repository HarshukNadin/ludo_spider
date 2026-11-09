package model;

import java.util.ArrayList;
import java.util.List;

/** Minimal CSV helpers (quoted fields with commas and "" escapes). */
public final class CsvUtil {
    private CsvUtil() {}

    public static List<String> parseLine(String line) {
        List<String> out = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean quoted = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (quoted) {
                if (c == '"' && i + 1 < line.length() && line.charAt(i + 1) == '"') { cur.append('"'); i++; }
                else if (c == '"') quoted = false;
                else cur.append(c);
            } else if (c == '"') quoted = true;
            else if (c == ',') { out.add(cur.toString()); cur.setLength(0); }
            else cur.append(c);
        }
        out.add(cur.toString());
        return out;
    }

    public static String escape(String s) {
        if (s == null) return "";
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) return '"' + s.replace("\"", "\"\"") + '"';
        return s;
    }

    public static String join(List<String> fields) {
        List<String> esc = new ArrayList<>();
        for (String f : fields) esc.add(escape(f));
        return String.join(",", esc);
    }
}
