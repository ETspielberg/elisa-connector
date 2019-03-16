package unidue.ub.elisaconnector.model;

public class TitleData {

    private String isbn;

    private String notiz;

    private String notizIntern;

    public TitleData(String isbn) {
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
