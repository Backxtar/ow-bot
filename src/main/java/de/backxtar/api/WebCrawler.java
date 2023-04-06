package de.backxtar.api;

import java.io.IOException;
import java.util.Objects;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class WebCrawler {
    private final String url;
    private Connection con;
    private Document doc;
    private Element patch, section;
    private String date, patchTitle;

    public WebCrawler(final String url) {
        this.url = url;
    }

    public boolean crawlWeb() {
        try {
            this.con = Jsoup.connect(this.url);
            this.doc = con.get();
            return true;
        } catch (IOException io) {
            return false;
        }
    }

    public String getDate() {
        this.date = this.doc.getElementsByClass("PatchNotes-date").first().text();
        return this.date;
    }

    public Element getCurrentPatch() {
        if (this.patch == null) this.patch = this.doc.getElementsByClass("PatchNotes-patch PatchNotes-live").first();
        return this.patch;
    }

    public Element getSection() {
        if (this.section == null) this.section = this.patch.getElementsByClass("PatchNotes-section").first();
        return this.section;
    }

    public String getPatchTitle() {
        getCurrentPatch();
        this.patchTitle = this.patch.getElementsByClass("PatchNotes-patchTitle").text();
        return this.patchTitle;
    }

    public String getSectionTitle() {
        getCurrentPatch();
        getSection();
        return this.section.getElementsByClass("PatchNotes-sectionTitle").text();
    }

    public String getSectionDesc() {
        getCurrentPatch();
        getSection();
        return this.section.getElementsByClass("PatchNotes-sectionDescription").text();
    }

    public String getTitle() {
        return this.doc.title();
    }

    public String getUrl() {
        return this.url;
    }

    public String getFavicon() {
        Element element = this.doc.head().select("link[href~=.*\\.ico]").first();
        return element.attr("href");
    }

    public boolean compBody(final String body) {
        return Objects.equals(this.doc.body().html(), body);
    }
}
