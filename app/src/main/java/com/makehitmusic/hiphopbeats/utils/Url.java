package com.makehitmusic.hiphopbeats.utils;

/**
 * Created by Sushant on 06.02.19.
 */

public class Url {

    // global server
    //public static String base_url = "https://mhm-beats.herokuapp.com";

    // local server - Shivani's System (WiFi - Algorithm)
    //public static String BASE_URL = "http://10.0.0.241/Dante/MHMBeats/";

    // local server - Shivani's System (WiFi - Airtel@Zerotouch)
    public static String BASE_URL = "http://192.168.1.8/Dante/MHMBeats/";

     // List of APIs

    /**
     * Producer's List
     * METHOD - GET
     * PARAMS - nil
     * RESPONSE - JSON
     */
    public static String PRODUCERS_LIST = BASE_URL + "/producers.php";

    /**
     * Category/Genre List
     * METHOD - GET
     * PARAMS - nil
     * RESPONSE - JSON
     */
    public static String CATEGORIES_LIST = BASE_URL + "/categories.php";

    /**
     * Purchased Beats/Product List
     * METHOD - GET
     * PARAMS - nil
     * RESPONSE - JSON
     */
    public static String PURCHASE_LIST = BASE_URL + "/products.php?purchase=true";

    /**
     * Beats List according to producers
     * METHOD - GET
     * PARAMS - nil
     * NOTE - latest: true will show the latest 25 beats and false will show all beats.
     * RESPONSE - JSON response for producerID = 56462, user_id = 114909
     */
    public static String BEATS_LIST_PRODUCERS = BASE_URL +
            "/products.php?producer_id=\\(producerID)&userid=\\(user_id)&latest=\\(latest)";

    /**
     * Beats List according to categories
     * METHOD - GET
     * PARAMS - nil
     * NOTE - latest: true will show the latest 25 beats and false will show all beats.
     * RESPONSE - JSON response for categoryID = 140, userid = 114909
     */
    public static String BEATS_LIST_CATEGORIES = BASE_URL +
            "/products.php?category_id=\\(categoryID)&userid=\\(user_id)&latest=\\(latest)";

    /**
     * Favorites Beats List
     * METHOD - GET
     * PARAMS - nil
     * NOTE - latest: true will show the latest 25 beats and false will show all beats.
     * RESPONSE - JSON response for userid = 114909
     */
    public static String BEATS_LIST_FAVORITES = BASE_URL +
            "/products.php?user_id=\\(userID)&latest=\\(latest)";

    /**
     * Social Register or Login User
     * METHOD - POST
     * PARAMS -
     * [1] If login with FB : {“email”: “”, “username”: “”, “firstname”: “”, “lastname”: “”,
     * “userID”: “”, “idToken”: “”, “loginType” : “Facebook”,
     * “photo”: imagedata in base64Encoding}
     * [2] If login with Google : {“email”: “”, “username”: “”, “firstname”: “”,
     * “lastname”: “”, “userID”: “”, “idToken”: “”, “loginType” : “Google”,
     * “photo”: imagedata in base64Encoding}
     * RESPONSE - JSON
     */
    public static String SOCIAL_LOGIN = BASE_URL + "/social_login.php";

    /**
     * Add or Remove Favorites
     * METHOD - POST
     * PARAMS - {“product_id”: “”, “user_id”: “”, “status”: true/false}
     * [1] True if add to favorites
     * [2] False if remove from favorites
     * RESPONSE - JSON
     */
    public static String ADD_REMOVE_FAVORITES = BASE_URL + "favorites.php";

    /**
     * Verify Receipt (not used)
     * METHOD - POST
     * PARAMS - nil
     * RESPONSE - JSON
     */
    public static String VALIDATE_RECEIPT = BASE_URL + "validate_receipt.php";

}
