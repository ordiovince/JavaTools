/**
 * 
 */
package vincent.ordioni.imdb.parser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.jdom.DataConversionException;
import org.jdom.JDOMException;

import vincent.ordioni.imdb.exception.AlreadyAddedException;
import vincent.ordioni.imdb.exception.MultiReslutsException;
import vincent.ordioni.imdb.exception.NoReslutsException;
import vincent.ordioni.imdb.parser.model.Episode;
import vincent.ordioni.imdb.parser.model.Saison;
import vincent.ordioni.imdb.parser.model.Serie;
import vincent.ordioni.imdb.parser.model.Videotheque;
import vincent.ordioni.test.JSONArray;
import vincent.ordioni.test.JSONException;
import vincent.ordioni.test.JSONObject;

/**
 * @author VIORDION
 */
public class IMDBParser {

    public static final String DATA_PATH = "data.xml";

    public static final String SOURCE_PATH = "source.xml";

    public static final int SOCKET_PORT = 8982;
    
    public static final String JOCKER = "%25";

    /**
     * @param search
     * @return
     * @throws IOException
     * @throws JSONException
     * @throws MultiReslutsException
     * @throws NoReslutsException
     */
    private static TreeMap<Integer, Saison> getEpisodesForSerie(String search) throws IOException, JSONException, MultiReslutsException, NoReslutsException {
        URL url = new URL("http://imdbapi.poromenos.org/js/?name=" + search);

        InputStream inputStream = url.openStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String source = "";
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            source += line;
        }

        if (source.equals("null")) {
            throw new NoReslutsException(search);
        }

        JSONObject jsonObject = new JSONObject(source);
        JSONObject jsonObject2 = null;
        try {
            jsonObject2 = jsonObject.getJSONObject(JSONObject.getNames(jsonObject)[0]);
        } catch (JSONException e) {
            JSONArray array = jsonObject.getJSONArray(JSONObject.getNames(jsonObject)[0]);
            MultiReslutsException imdbException = new MultiReslutsException(array.getJSONObject(0).getString("name"), array.length());
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                imdbException.addYear(object.getInt("year"));
            }
            throw imdbException;
        }
        JSONArray jsonArray = jsonObject2.getJSONArray("episodes");

        TreeMap<Integer, Saison> episodes = new TreeMap<Integer, Saison>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsObject = jsonArray.getJSONObject(i);
            Integer season = jsObject.getInt("season");
            Integer number = jsObject.getInt("number");
            String name = jsObject.getString("name");
            String date = "";

            if (!episodes.containsKey(season)) {
                episodes.put(season, new Saison(number));
            }
            episodes.get(season).addEpisode(new Episode(number, name, date));
        }

        return episodes;
    }

    /**
     * @return
     * @throws DataConversionException
     * @throws FileNotFoundException
     * @throws JDOMException
     * @throws IOException
     * @throws JSONException
     * @throws NoReslutsException
     * @throws MultiReslutsException
     */
    public static Videotheque getEpisodes() throws DataConversionException, FileNotFoundException, JDOMException, IOException, JSONException, NoReslutsException, MultiReslutsException {
        Videotheque series = Videotheque.loadFromXml(SOURCE_PATH, false);

        PrintWriter writer = null;
        try {
            Socket socket = new Socket("localhost", SOCKET_PORT);
            writer = new PrintWriter(socket.getOutputStream());
        } catch (Exception e) {
        }

        int i = 0;
        for (Serie serie : series.getSeries()) {
            i++;
            String search = serie.getName();
            search = search.replace(' ', '+');
            search += "&year=";
            search += serie.getYear().toString();
            serie.addAllSaisons(getEpisodesForSerie(search));

            if (writer != null) {
                writer.println(series.size() + "-" + i);
                writer.flush();
            }
        }
        if (writer != null) {
            writer.println("");
            writer.flush();
            writer.close();
        }

        return series;
    }

    /**
     * @param search
     * @return
     * @throws IOException
     * @throws JSONException
     */
    public static Set<Serie> search(String search) throws IOException, JSONException {
        Set<Serie> series = new TreeSet<Serie>();

        URL url = new URL("http://imdbapi.poromenos.org/js/?name=" + search);

        InputStream inputStream = url.openStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String source = "";
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            source += line;
        }

        if (source.equals("null")) {
            return null;
        }

        JSONObject jsonObject = new JSONObject(source);
        JSONObject jsonObject2 = null;
        String name = null;
        try {
            name = JSONObject.getNames(jsonObject)[0];
            jsonObject2 = jsonObject.getJSONObject(name);
        } catch (JSONException e) {
            JSONArray array = jsonObject.getJSONArray(JSONObject.getNames(jsonObject)[0]);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);

                series.add(new Serie(object.getString("name"), object.getInt("year")));
            }
            return series;
        }

        series.add(new Serie(name, jsonObject2.getInt("year")));

        return series;
    }

    /**
     * @param viewSerie
     * @throws DataConversionException
     * @throws FileNotFoundException
     * @throws JDOMException
     * @throws IOException
     * @throws JSONException
     * @throws NoReslutsException
     * @throws MultiReslutsException
     * @throws AlreadyAddedException
     */
    public static void addSerie(Serie serie) throws DataConversionException, FileNotFoundException, JDOMException, IOException, JSONException, NoReslutsException, MultiReslutsException,
            AlreadyAddedException {
        Videotheque source = Videotheque.loadFromXml(SOURCE_PATH, false);

        if (source.contains(serie.getId())) {
            throw new AlreadyAddedException(serie.getName());
        }

        String search = serie.getName();
        search = search.replace(' ', '+');
        search += "&year=";
        search += serie.getYear().toString();
        getEpisodesForSerie(search);

        source.addSerie(serie);

        Videotheque.storeToXml(SOURCE_PATH, source, false);
    }

    /**
     * @param serie
     * @throws DataConversionException
     * @throws FileNotFoundException
     * @throws JDOMException
     * @throws IOException
     * @throws NoReslutsException
     * @throws JSONException
     */
    public static void removeSerie(Serie serie) throws DataConversionException, FileNotFoundException, JDOMException, IOException, NoReslutsException, JSONException {
        Videotheque source = Videotheque.loadFromXml(SOURCE_PATH, false);

        source.removeSerie(serie.getId());

        Videotheque.storeToXml(SOURCE_PATH, source, false);
    }

    /**
     * @throws DataConversionException
     * @throws FileNotFoundException
     * @throws IOException
     * @throws NoReslutsException
     * @throws JSONException
     * @throws JDOMException
     * @throws MultiReslutsException
     */
    public static void loadData() throws DataConversionException, FileNotFoundException, IOException, NoReslutsException, JSONException, JDOMException, MultiReslutsException {
        Videotheque.storeToXml(DATA_PATH, getEpisodes(), true);
    }
}
