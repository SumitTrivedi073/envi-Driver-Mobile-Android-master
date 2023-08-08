package sgs.env.ecabsdriver.callback;

/**
 * Created by Lenovo on 4/9/2018.
 */

public interface CountryPickerListener {
    void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID);
}
