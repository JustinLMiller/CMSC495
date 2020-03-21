package currency;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class GetCurrentRates {
    
    private static final String OPENEXCHANGERATEURL = "https://openexchangerates.org/api/latest.json?app_id=14d7276896a846b0a520443cfa2fae37&base=USD&prettyprint=false&show_alternative=false";
    
    //Method opens a connection to site Open Exchange Rate which supplies
    //the current currency exchange rates. I think around 170 are supplied.
    //They are then converted into a Map data structure, the time stamp and time (as a double)
    //Are added into the Map for use later.
    public Map getRate()
    {
        JSONParser parser = new JSONParser();
        Map<String, Double> rateMap;
        
        try{
            URL openURL = new URL(OPENEXCHANGERATEURL);
            URLConnection urlConnect = openURL.openConnection();
            BufferedReader inReader = new BufferedReader(new InputStreamReader(urlConnect.getInputStream()));
            JSONObject jo = (JSONObject)parser.parse(inReader);
            System.out.println(jo.toJSONString());
            Long timeStamp = (Long) jo.get("timestamp");
            System.out.println("Time stamp is: " + timeStamp);
            String baseCurrency = (String) jo.get("base");
            System.out.println("Base currency is: " + baseCurrency);
            rateMap = ((Map)jo.get("rates")); 
            System.out.println("success");
            rateMap.put("TIMESTAMP", (double) timeStamp);
        } catch (IOException | ParseException e) {
            return null;
        }
        return rateMap;
    }    
}