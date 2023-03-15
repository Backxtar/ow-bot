package de.backxtar.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class JSONReader {
    private final URL url;
    private BufferedReader reader;

    public JSONReader(final String urlRaw) throws IOException {
            this.url = new URL(urlRaw);
            this.reader = new BufferedReader(new InputStreamReader(url.openStream()));
    }

    public String getJSON() throws IOException {
        try {
            StringBuilder buffer = new StringBuilder();
            int read;
            char[] size = new char[1024];

            while ((read = this.reader.read(size)) != -1)
                buffer.append(size, 0, read);
            return buffer.toString();
        } finally {
            if (this.reader != null) this.reader.close();
        }
    }

    public URL getUrl() {
        return this.url;
    }

    public BufferedReader getReader() {
        return this.reader;
    }
}
