package nesto.gankio.model;

import java.util.ArrayList;

/**
 * Created on 2016/5/10.
 * By nesto
 */
public class Results {
    private boolean error;
    private ArrayList<Data> results;

    public Results(boolean error, ArrayList<Data> results) {
        this.error = error;
        this.results = results;
    }

    public boolean isError() {
        return error;
    }

    public ArrayList<Data> getResults() {
        return results;
    }

    @Override
    public String toString() {
        return "Result{" +
                "error=" + error +
                ", results=" + results +
                '}';
    }
}
