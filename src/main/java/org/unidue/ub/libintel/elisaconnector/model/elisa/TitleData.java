package org.unidue.ub.libintel.elisaconnector.model.elisa;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * metadata for a single entry in the memory list in elisa. It holds the isbn of the book to be added to the memory
 * list, the inter note as well as the library note.
 */
public class TitleData {

    private String isbn;

    private String notiz;

    @JsonProperty("notiz_intern")
    private String notizIntern;

    public TitleData() {
    }

    /**
     * @param isbn the isbn of the book to be added to the memory list
     */
    public TitleData(String isbn) {
        this.notizIntern = "";
        this.notiz = "";
        this.isbn = isbn;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getNotiz() {
        return notiz;
    }

    public void setNotiz(String notiz) {
        this.notiz = notiz;
    }

    public String getNotizIntern() {
        return notizIntern;
    }

    public void setNotizIntern(String notizIntern) {
        this.notizIntern = notizIntern;
    }
}
