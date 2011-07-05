/**
 * 
 */
package vincent.ordioni.imdb.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * @author VIORDION
 */
public class Utils {

    /**
     * @param name
     * @return
     */
    public static String format(String name) {
        StringBuilder sb = new StringBuilder();

        StringTokenizer st = new StringTokenizer(name, ". ");
        if (st.countTokens() < 2) {
            return name;
        }
        while (st.hasMoreTokens()) {
            String word = st.nextToken();
            if (word.contains(":") || word.contains("-") || word.equals(word.toLowerCase()) || word.equals("It")) {
                sb.append(word);
            } else {
                int i = 0;
                for (; i < word.length() && word.charAt(i) == word.toUpperCase().charAt(i); i++) {
                    sb.append(word.charAt(i));
                }
                if (i < word.length() || word.length() == 1) {
                    sb.append('.');
                }
            }
            sb.append(' ');
        }

        return sb.toString();
    }

    /**
     * @param s
     * @return
     */
    public static String unAccent(String s) {
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("");
    }

    /**
     * @param search
     * @param separator
     * @return
     */
    public static Set<String> getSearchPermutations(String search, String separator) {
        Set<String> result = new TreeSet<String>();

        ArrayList<Object> list = new ArrayList<Object>();
        for (String str : search.split(separator)) {
            list.add(str);
        }

        for (ArrayList<Object> line : getPermutations(list)) {
            String lineStr = "";
            for (Object word : line) {
                lineStr += word + separator;
            }
            result.add(lineStr.substring(0, lineStr.lastIndexOf(separator)));
        }

        return result;
    }

    /**
     * @param list
     * @return
     */
    public static ArrayList<ArrayList<Object>> getPermutations(ArrayList<Object> list) {
        return getPermutations(list, list.size());
    }

    /**
     * @param list
     * @param n
     * @return
     */
    @SuppressWarnings("unchecked")
    private static ArrayList<ArrayList<Object>> getPermutations(ArrayList<Object> list, int n) {
        ArrayList<ArrayList<Object>> permutations = new ArrayList<ArrayList<Object>>();
        if (n == 1) {
            permutations.add((ArrayList<Object>) list.clone());
            return permutations;
        }
        for (int k = 0; k < n; k++) {
            Collections.swap(list, k, n - 1);
            permutations.addAll(getPermutations(list, n - 1));
            Collections.swap(list, k, n - 1);
        }
        return permutations;
    }

    /**
     * @param document
     * @param path
     * @throws IOException
     */
    public static void storeXml(Document document, String path) throws IOException {
        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        outputter.output(document, new FileOutputStream(path));
    }

    /**
     * @param path
     * @return
     * @throws FileNotFoundException
     * @throws JDOMException
     * @throws IOException
     */
    public static Document loadXml(String path) throws FileNotFoundException, JDOMException, IOException {
        SAXBuilder builder = new SAXBuilder();
        return builder.build(new FileInputStream(path));
    }
}
