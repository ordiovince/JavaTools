/**
 * 
 */
package vincent.ordioni.imdb.parser.model;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.TreeMap;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import vincent.ordioni.imdb.util.Utils;

/**
 * @author VIORDION
 */
public class Videotheque {

    private TreeMap<String, Serie> series;

    public Videotheque() {
        this.series = new TreeMap<String, Serie>();
    }

    public void addSerie(Serie serie) {
        series.put(serie.getId(), serie);
    }

    public void removeSerie(String id) {
        series.remove(id);
    }

    /**
     * @return the series
     */
    public TreeMap<String, Serie> getMapSeries() {
        return series;
    }

    /**
     * @param series the series to set
     */
    public void setMapSeries(TreeMap<String, Serie> series) {
        this.series = series;
    }

    /**
     * @return the series
     */
    public Collection<Serie> getSeries() {
        return series.values();
    }

    /**
     * @return
     */
    public int size() {
        return series.size();
    }

    public boolean contains(String id) {
        return series.containsKey(id);
    }

    /**
     * @param path
     * @param videotheque
     * @param withEpisodes
     * @throws IOException
     */
    public static void storeToXml(String path, Videotheque videotheque, boolean withEpisodes) throws IOException {
        Element racine = new Element("list");
        DateFormat dateFormat = new SimpleDateFormat();
        racine.setAttribute("date", dateFormat.format(new Date()));
        for (Serie serie : videotheque.getSeries()) {
            Element elementSerie = new Element("serie");
            elementSerie.setAttribute("id", serie.getId());
            elementSerie.setAttribute("name", serie.getName());
            elementSerie.setAttribute("dispName", serie.getDispName());
            elementSerie.setAttribute("year", serie.getYear().toString());

            if (withEpisodes) {
                for (Saison saison : serie.getSaisons()) {
                    Element elementSeason = new Element("saison");
                    elementSeason.setAttribute("number", saison.getNumber().toString());
                    elementSeason.setAttribute("eps", saison.getEps().toString());

                    for (Episode episode : saison.getEpisodes()) {
                        Element elementEpisode = new Element("episode");
                        elementEpisode.setAttribute("number", episode.getNumber().toString());
                        elementEpisode.setAttribute("name", episode.getName());
                        elementEpisode.setAttribute("date", episode.getDate());

                        elementSeason.addContent(elementEpisode);
                    }
                    elementSerie.addContent(elementSeason);
                }
            }

            racine.addContent(elementSerie);
        }

        Utils.storeXml(new Document(racine), path);
    }

    /**
     * @param path
     * @param withEpisodes
     * @return
     * @throws FileNotFoundException
     * @throws JDOMException
     * @throws IOException
     */
    public static Videotheque loadFromXml(String path, boolean withEpisodes) throws FileNotFoundException, JDOMException, IOException {
        Videotheque videotheque = new Videotheque();

        Document document = Utils.loadXml(path);
        Element root = document.getRootElement();
        for (Object serieObject : root.getChildren("serie")) {
            Element serieElement = (Element) serieObject;
            Serie serie = new Serie(serieElement.getAttribute("id").getValue(), serieElement.getAttribute("name").getValue(), serieElement.getAttribute("dispName").getValue(), serieElement
                    .getAttribute("year").getIntValue());

            if (withEpisodes) {

                for (Object saisonObject : serieElement.getChildren("saison")) {
                    Element saisonElement = (Element) saisonObject;
                    Saison saison = new Saison(saisonElement.getAttribute("number").getIntValue());

                    for (Object episodeObject : saisonElement.getChildren("episode")) {
                        Element episodeElement = (Element) episodeObject;
                        Episode episode = new Episode(episodeElement.getAttribute("number").getIntValue(), episodeElement.getAttribute("name").getValue(), episodeElement.getAttribute("date")
                                .getValue());

                        saison.addEpisode(episode);
                    }
                    serie.addSaison(saison);
                }
            }
            videotheque.addSerie(serie);
        }

        return videotheque;
    }
}
