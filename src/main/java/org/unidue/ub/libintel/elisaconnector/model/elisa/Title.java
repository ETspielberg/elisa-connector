package org.unidue.ub.libintel.elisaconnector.model.elisa;

/**
 * a title object holding the title data for a single book
 */
public class Title {

    private TitleData title;

    public Title() {
    }

    public Title(TitleData title) {
        this.title = title;
    }

    public TitleData getTitle() {
        return title;
    }

    public void setTitle(TitleData title) {
        this.title = title;
    }
}
