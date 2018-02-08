package zombietechnologiesinc.com.barhop;

import java.util.Date;

/**
 * Created by emmaramos on 1/27/18.
 */

public class DailySpecial {

    String message;
    Date date;
    String dateAsString;
    int genreInt;

    public int getDayInt() {
        return dayInt;
    }

    public void setDayInt(int dayInt) {
        this.dayInt = dayInt;
    }

    int dayInt;

    public DailySpecial(){}

    public DailySpecial(String message, Date date, String dateAsString, int genreInt){

        this.message = message;
        this.date = date;
        this.dateAsString = dateAsString;
        this.genreInt = genreInt;

    }

    public DailySpecial(String message, String dateAsString, int genreInt, int dayInt){

        this.message = message;
        this.dateAsString = dateAsString;
        this.genreInt = genreInt;
        this.dayInt = dayInt;

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDateAsString() {
        return dateAsString;
    }

    public void setDateAsString(String dateAsString) {
        this.dateAsString = dateAsString;
    }

    public int getGenreInt() {
        return genreInt;
    }

    public void setGenreInt(int genreInt) {
        this.genreInt = genreInt;
    }
}
