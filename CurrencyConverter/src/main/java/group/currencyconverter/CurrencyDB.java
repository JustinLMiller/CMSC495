package group.currencyconverter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class CurrencyDB {
    
    private HashMap codes, rates; 
    String JSONRates;
    long lastUpdatedDate;
    
    // This is the URL for the API.
    private static final String OPENEXCHANGERATEURL = "https://openexchangerates.org/api/latest.json?app_id=14d7276896a846b0a520443cfa2fae37&base=USD&prettyprint=false&show_alternative=false";
    private static final String JSONPath = "latestRates.JSON";
    private static final String codesPath = "Currencies.csv";
    
    
    // Constructor for the DB object.
    public CurrencyDB() throws Exception {
        // Initialize the two hashmaps.
        codes = new HashMap();
        rates = new HashMap();
        JSONRates = "";
        lastUpdatedDate = 0;
        
        // Now let's load in the rates.
        updateRates();
        // Now load all the currency codes/descriptions.
        loadCurrencyCodesFromFile();
        // Clean up the two hash tables, make sure entries in one are in the other.
        cleanupCurrencyCodes();
    }
    // This is the main function called when the object is created that will update the rates.
    private void updateRates() throws ParseException, Exception {
        // Let's try to load the rates from the API
        // If the API fails, try the file.
        // If the file fails, toss an exception and exit.
        

        try {
            updateRatesFromAPI();
        } catch (Exception eAPI) {
            try {
                updateRatesFromFile();
            } catch (Exception eFile) {
                throw eFile;
            } 
        }

        
        // If we survived this far, we now have a JSON in the String, time to parse it.
        parseJSON(JSONRates);
        
    }
    
    // This is the call to the API, where it will suck in the rates from the Internet.
    private void updateRatesFromAPI() throws Exception {
        // First we open a connection to the API.
        try{
            // Set the URL
            URL openURL = new URL(OPENEXCHANGERATEURL);
            // Open the connection.
            URLConnection urlConnect = openURL.openConnection();
            // Read the return string from the URL
            BufferedReader inReader = new BufferedReader(new InputStreamReader(urlConnect.getInputStream()));
            // Shove the data in the BufferedREader into a simple String.
            JSONRates = inReader.lines().collect(Collectors.joining());
            
            // Now let's save the JSON to the file.   We only do this in the API call since
            // if we get to the loadfromfile, it makes no sense to resave the data we already have.
            saveJSONToFile();
            
        } catch (Exception e) {
            throw e;
        }
        

        
    }
    
    // This is the call to the API, where it will suck in the rates from the last successful load.
    // This should only be called if updateRatesFromAPI() fails.    
    private void updateRatesFromFile() throws Exception {
        try {
            // First we load the JSON into a string.
            JSONRates = new String(Files.readAllBytes(Paths.get(JSONPath)));
            
        } catch (Exception e) {
            throw e;
        }
        
    }
    
    // This is method that saves the current JSON into a file.
    private void saveJSONToFile() throws IOException {
        try {
            File file = new File(JSONPath);
            OutputStream outputStr = new FileOutputStream(file);
            
            // If we don't already have the file there, create it, but
            // in reality this doesn't really get hit due to FileOutputStream 
            // creating a new file each time.
            if (!file.exists())
                file.createNewFile();
            
            // This all just writes the data to the file using the OutputStream
            // and then cleans up all the objects.
            byte[] contentInBytes = JSONRates.getBytes();
            outputStr.write(contentInBytes);
            outputStr.flush();
            outputStr.close();
        } catch (IOException e) {
            throw e;
        }
    }
    
    // This is the big proc that parses the JSON from the API call into the hashtable.
    private void parseJSON(String JSON) throws ParseException {
        JSONParser parser = new JSONParser();
        
        try {
            // Shove the JSON string into the parser/object.
            JSONObject jo = (JSONObject)parser.parse(JSON);
            
            //Set the lastupdated field.
            lastUpdatedDate = (Long) jo.get("timestamp");
            
            //Shove all the rates into the rates HashMap.
            rates = ((HashMap)jo.get("rates"));
            
            
        } catch (ParseException e) {
            throw e;
        }
    }
    
    // This loads all the 3-letter ISO currency codes plus their long description into
    // the hashtable.   currFile is the full path + filename to be opened.
    private void loadCurrencyCodesFromFile() throws IOException {
        try {
            File file = new File(codesPath);
            InputStream inputStr = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStr));
            
            // For each line, we need to split on the first comma, then
            // shove the key/value fields into the codes table.
            String line;
            while((line=br.readLine()) != null) {
               String splitLine[] = line.split(",",2);
               // The replaceall strips any non alphanumerics to prevent wierd data issues
               codes.put(splitLine[0].replaceAll("[^a-zA-Z0-9]", ""),splitLine[1]);
            }
            
        } catch (IOException e) {
            throw e;
        }
        
        
    }
    
    // This is a cleanup proc called after the JSON and the currency codes are loaded.
    // It cleans up entries in both hashtables that do not exist in the other. 
    // For instance, if the API passes in a code that is not in the currency codes file, 
    // it needs to be removed.   Likewise any entries in the currency codes need to be removed
    // if there is not a corresponding entry in the conversion codes.
    private void cleanupCurrencyCodes() {
        // This is based on two loops.   First we loop through the rates to ensure matching entries
        // in the codes.   If any aren't found in the codes, delete them from the rates.
        
        // Now we do the same in reverse, checking for matching entries between codes and rates, 
        // removing any mismatches from the codes.
        
        Set<String> rateskeys = rates.keySet();
        for(String key: rateskeys) {
            if (codes.containsKey(key) == false) {
                codes.remove(key);
            }
        }

    
        Set<String> codeskeys = codes.keySet();
        for(String key: codeskeys) {
            if (rates.containsKey(key) == false) {
                rates.remove(key);
            }
        }
    }

    
    
    // This is to get the code+desc hashmap for any GUI population needs.
    public HashMap getCurrencyCodesHash() {
        return codes;
    }
    
    // This will pass back the conversion rate (normalized on USDs) for a given currency code.
    // input must be a 3-letter ISO code that exists in both codes and rates HashMaps.
    public double getConversionRate(String currency) {
         // This weirdness is because the Hashmap sometimes stores the string values from the JSON as a
         // Long (integer) instead of a Double (decimal).   So we convert it to a string first, then force
         // It into a double.
         String tempStr = rates.get(currency).toString();
         Double tempDbl = new Double(tempStr);
         
         return tempDbl;
     
    }
    
    // This will pass back all the keys for the 3-digit currency codes that exist in the
    // codes HashMap.
    public String[] getAllCurrencyCodes() {
        
        // Converts the Set of Objects to an array of Strings
        String[] keys = (String[]) codes.keySet().toArray(new String[codes.size()]);
        return keys;
    }
    
    // Returns the long description from the codes HashMap for the given currency
    // code.   
    public String getCurrencyCodeDescription(String currencyCode) {
        return (String) codes.get(currencyCode);
    }
    
    // returns the timecode (seconds since 1/1/1970 00:00:00.000) for when the
    // rates were last updated.   This date comes out of the JSON.
    public long getLastUpdatedDate() {
        return lastUpdatedDate;
    }

}
