package com.example.username.forecast;

/**
 * Created by username on 10/5/2016.
 */
public class PhotoQuery {
    private PhotoResults results;

    private String count;

    private String created;

    private String lang;

    public PhotoResults getResults ()
    {
        return results;
    }

    public void setResults (PhotoResults results)
    {
        this.results = results;
    }

    public String getCount ()
    {
        return count;
    }

    public void setCount (String count)
    {
        this.count = count;
    }

    public String getCreated ()
    {
        return created;
    }

    public void setCreated (String created)
    {
        this.created = created;
    }

    public String getLang ()
    {
        return lang;
    }

    public void setLang (String lang)
    {
        this.lang = lang;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [results = "+results+", count = "+count+", created = "+created+", lang = "+lang+"]";
    }


}
