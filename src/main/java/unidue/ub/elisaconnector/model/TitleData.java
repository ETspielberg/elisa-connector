package unidue.ub.elisaconnector.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TitleData {

    private String isbn;

    private String notiz;

    @JsonProperty("notiz_intern")
    private String notizIntern;

    public TitleData() {
    }

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
