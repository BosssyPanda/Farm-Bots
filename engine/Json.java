/**
 * Tiny hand-rolled JSON string escaper. The engine emits JSON by hand to stay
 * dependency-free and portable inside Vercel Sandbox. Engine-internal.
 */
public final class Json {
    private Json() {}

    /** Returns a JSON string literal (with surrounding quotes) for {@code s}. */
    public static String str(String s) {
        if (s == null) return "null";
        StringBuilder sb = new StringBuilder("\"");
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"':  sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 0x20) sb.append(String.format("\\u%04x", (int) c));
                    else sb.append(c);
            }
        }
        sb.append("\"");
        return sb.toString();
    }

    public static String unquote(String jsonString) {
        if (jsonString == null || jsonString.length() < 2) return "";
        int start = jsonString.charAt(0) == '"' ? 1 : 0;
        int end = jsonString.charAt(jsonString.length() - 1) == '"' ? jsonString.length() - 1 : jsonString.length();
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < end; i++) {
            char c = jsonString.charAt(i);
            if (c == '\\' && i + 1 < end) {
                char n = jsonString.charAt(++i);
                switch (n) {
                    case '"': sb.append('"'); break;
                    case '\\': sb.append('\\'); break;
                    case 'n': sb.append('\n'); break;
                    case 'r': sb.append('\r'); break;
                    case 't': sb.append('\t'); break;
                    default: sb.append(n);
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String intArray(int[] values) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < values.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(values[i]);
        }
        sb.append("]");
        return sb.toString();
    }

    public static String stringArray(String[] values) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < values.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(str(values[i]));
        }
        sb.append("]");
        return sb.toString();
    }
}
