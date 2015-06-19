package Parser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by dell on 13/06/2015.
 */
public class XMLParser {

    public static List<List<String>> parseFeed(String classe, String[] param, String content) {

        boolean inDataItemTag = false;
        String currentTagName = "";
        List<String> generic = null;
        List<List<String>> genericList = new ArrayList<>();
        try {


            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(content));

            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        currentTagName = parser.getName();
                        if (currentTagName.equals(classe)) {
                            inDataItemTag = true;
                            generic = new ArrayList<>();
                            genericList.add(generic);
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals(classe)) {
                            inDataItemTag = false;
                        }
                        currentTagName = "";
                        break;

                    case XmlPullParser.TEXT:
                        if (inDataItemTag && generic != null) {
                            if(Arrays.asList(param).contains(currentTagName)){
                                generic.add(parser.getText());
                            }
                        }
                        break;
                }

                eventType = parser.next();

            }

            return genericList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
