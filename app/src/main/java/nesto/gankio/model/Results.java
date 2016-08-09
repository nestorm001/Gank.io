package nesto.gankio.model;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import java.util.ArrayList;

/**
 * Created on 2016/5/10.
 * By nesto
 */
@AutoValue
public abstract class Results {
    public abstract boolean isError();

    public abstract ArrayList<Data> getResults();

    public static Results create(boolean newError, ArrayList<Data> newResults) {
        return builder()
                .setError(newError)
                .setResults(newResults)
                .build();
    }

    public static Builder builder() {return new AutoValue_Results.Builder();}

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder setError(boolean newError);

        public abstract Builder setResults(ArrayList<Data> newResults);

        public abstract Results build();
    }

    public static TypeAdapter<Results> typeAdapter(Gson gson) {
        return new AutoValue_Results.GsonTypeAdapter(gson);
    }
}
