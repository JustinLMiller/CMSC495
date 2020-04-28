package group.currencyconverter;


public class CurrencyConverter {
    
    public static void main(String[] args) {
        try {
            CurrencyDB currDB = new CurrencyDB();
            ConversionModule convMod = new ConversionModule(currDB);
            GUI g = new GUI(); // Launch the GUI
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
        
    }
}
