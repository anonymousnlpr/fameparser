package de.joint.frames.parsing.data;

import java.net.*;
import java.io.*;
import java.util.*;
import net.sf.json.JSONArray;

/**
 *
 * @author sfaralli
 */
public class BabelFy {

    private final static String userkey = "yourkeyhere";
    private final static String USER_AGENT = "Mozilla/5.0";

    public static String babelfy(String text) throws UnsupportedEncodingException, Exception {
        return sendGet("https://babelfy.io/v1/disambiguate?text=" + URLEncoder.encode(text, "UTF-8") + "&lang=EN&key=" + userkey);
    }

    // HTTP GET request

    private static String sendGet(String url) throws Exception {

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        return response.toString();

    }

    public static List<BFyAnnotation> parse(String json) {

        List<BFyAnnotation> result = new ArrayList<>();
        JSONArray arr = JSONArray.fromObject(json);
        for (int i = 0; i < arr.size(); i++) {

            String tokenFragment_s = arr.getJSONObject(i).getJSONObject("tokenFragment").getString("start");
            String tokenFragment_e = arr.getJSONObject(i).getJSONObject("tokenFragment").getString("end");
            String charSegment_s = arr.getJSONObject(i).getJSONObject("charFragment").getString("start");
            String charSegment_e = arr.getJSONObject(i).getJSONObject("charFragment").getString("end");
            String babelSynsetID = arr.getJSONObject(i).getString("babelSynsetID");
            String DBpediaURL = arr.getJSONObject(i).getString("DBpediaURL");
            String BabelNetURL = arr.getJSONObject(i).getString("BabelNetURL");
            String score = arr.getJSONObject(i).getString("score");
            String coherenceScore = arr.getJSONObject(i).getString("coherenceScore");
            String globalScore = arr.getJSONObject(i).getString("globalScore");
            String source = arr.getJSONObject(i).getString("source");

            result.add(
                    new BFyAnnotation(new Integer(tokenFragment_s),
                            new Integer(tokenFragment_e),
                            new Integer(charSegment_s),
                            new Integer(charSegment_e),
                            babelSynsetID,
                            DBpediaURL,
                            BabelNetURL,
                            new Double(score),
                            new Double(coherenceScore),
                            new Double(globalScore),
                            source
                    )
            );
        }

        return result;
    }

    
}
