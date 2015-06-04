package org.cobbzilla.wizardtest;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.Random;

import static org.cobbzilla.util.string.StringUtil.safeFunctionName;

public class TestNames {

    public static final String[] FRUITS = {
        "Apple", "Apricot", "Bilberry", "Blackberry", "Blueberry", "Boysenberry", "Cantaloupe", "Cherry",
        "Coconut", "Cranberry", "Date", "Dragonfruit", "Elderberry", "Fig", "Gooseberry", "Grape",
        "Grapefruit", "Guava", "Huckleberry", "Lemon", "Lime", "Lychee", "Mango", "Melon", "Cantaloupe",
        "Honeydew", "Watermelon", "Mulberry", "Nectarine", "Olive", "Orange", "Clementine", "Tangerine",
        "Papaya", "Passionfruit", "Peach", "Pear", "Persimmon", "Plum", "Pineapple", "Pomegranate",
        "Pomelo", "Raspberry", "Strawberry",
    };

    public static final String[] NATIONALITIES = {
        "Afghan", "Albanian", "Algerian", "Andorran", "Angolan", "Argentinian", "Armenian", "Australian",
        "Austrian", "Azerbaijani", "Bahamian", "Bahraini", "Bangladeshi", "Barbadian", "Belarusian", "Belgian",
        "Belizean", "Beninese", "Bhutanese", "Bolivian", "Bosnian", "Botswanan", "Brazilian", "British", "Bruneian",
        "Bulgarian", "Burkinese", "Burmese", "Burundian", "Cambodian", "Cameroonian", "Canadian", "Cape", "Verdean",
        "Chadian", "Chilean", "Chinese", "Colombian", "Congolese", "Costa", "Rican", "Croatian", "Cuban", "Cypriot",
        "Czech", "Danish", "Djiboutian", "Dominican", "Dominican", "Ecuadorean", "Egyptian", "Salvadorean", "English",
        "Eritrean", "Estonian", "Ethiopian", "Fijian", "Finnish", "French", "Gabonese", "Gambian", "Georgian", "German",
        "Ghanaian", "Greek", "Grenadian", "Guatemalan", "Guinean", "Guyanese", "Haitian", "Dutch", "Honduran", "Hungarian",
        "Icelandic", "Indian", "Indonesian", "Iranian", "Iraqi", "Irish", "Italian", "Jamaican", "Japanese", "Jordanian",
        "Kazakh", "Kenyan", "Kuwaiti", "Laotian", "Latvian", "Lebanese", "Liberian", "Libyan", "Lithuanian", "Macedonian",
        "Madagascan", "Malawian", "Malaysian", "Maldivian", "Malian", "Maltese", "Mauritanian", "Mauritian", "Mexican",
        "Moldovan", "Monacan", "Mongolian", "Montenegrin", "Moroccan", "Mozambican", "Namibian", "Nepalese", "Dutch",
        "Nicaraguan", "Nigerien", "Nigerian", "North", "Korean", "Norwegian", "Omani", "Pakistani", "Panamanian", "Guinean",
        "Paraguayan", "Peruvian", "Philippine", "Polish", "Portuguese", "Qatari", "Romanian", "Russian", "Rwandan", "Saudi",
        "Scottish", "Senegalese", "Serbian", "Seychellois", "Sierra", "Leonian", "Singaporean", "Slovak", "Slovenian",
        "Somali", "South", "African", "South", "Korean", "Spanish", "Sri", "Lankan", "Sudanese", "Surinamese", "Swazi",
        "Swedish", "Swiss", "Syrian", "Taiwanese", "Tadjik", "Tanzanian", "Thai", "Togolese", "Trinidadian<br>", "Tunisian",
        "Turkish", "Turkmen", "Tuvaluan", "Ugandan", "Ukrainian", "British", "American", "Uruguayan", "Uzbek", "Vanuatuan",
        "Venezuelan", "Vietnamese", "Welsh", "Western", "Samoan", "Yemeni", "Yugoslav", "Zaïrean", "Zambian", "Zimbabwean"
    };

    private static final Random rand = new Random();

    public static String safeName () {
        return safeFunctionName(nationality())+"-"+safeFunctionName(fruit())+"-"+RandomStringUtils.randomAlphanumeric(10);
    }

    public static String name() { return nationality() + " " + fruit(); }

    public static String fruit() { return FRUITS[rand.nextInt(FRUITS.length)]; }

    public static String nationality() { return NATIONALITIES[rand.nextInt(NATIONALITIES.length)]; }

}
