/**
 * 
 */
package vincent.ordioni.imdb.parser.model;

import java.util.Collection;
import java.util.TreeMap;

/**
 * @author VIORDION
 */
public class Saison {

    private Integer number;

    private Integer eps;

    private TreeMap<Integer, Episode> episodes;

    public Saison() {
    }

    /**
     * @param number
     */
    public Saison(Integer number) {
        this.number = number;
        this.episodes = new TreeMap<Integer, Episode>();
    }

    public void addEpisode(Episode episode) {
        episodes.put(episode.getNumber(), episode);
        eps = episodes.size();
    }

    /**
     * @return the number
     */
    public Integer getNumber() {
        return number;
    }

    /**
     * @param number the number to set
     */
    public void setNumber(Integer number) {
        this.number = number;
    }

    /**
     * @return the eps
     */
    public Integer getEps() {
        return eps;
    }

    /**
     * @param eps the eps to set
     */
    public void setEps(Integer eps) {
        this.eps = eps;
    }

    /**
     * @return the episodes
     */
    public TreeMap<Integer, Episode> getMapEpisodes() {
        return episodes;
    }

    /**
     * @param episodes the episodes to set
     */
    public void setMapEpisodes(TreeMap<Integer, Episode> episodes) {
        this.episodes = episodes;
        eps = episodes.size();
    }

    /**
     * @return the episodes
     */
    public Collection<Episode> getEpisodes() {
        return episodes.values();
    }

    @Override
    public String toString() {
        return "Season " + number;
    }
}
