package tk.thelocky.eazyarch.res;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.TreeMap;

class DataNode {
    public String value = "";
    public final TreeMap<String, DataNode> childs = new TreeMap<>();
}

public final class R {
    public static final TreeMap<String, DataNode> childs = new TreeMap<>();

    private R() {}

    public static void initialize() {
        initialize("ru");
    }

    public static void initialize(String lang) {
        try {
            File r = new File(R.class.getResource("resource_" + lang + ".xml").toURI());
            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            f.setValidating(false);
            DocumentBuilder builder = f.newDocumentBuilder();
            Document doc = builder.parse(r);
            NodeList nRoot = doc.getElementsByTagName("resources");
            if (nRoot.getLength() > 0) {
                loadData(nRoot.item(0), childs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadData(Node from, TreeMap<String, DataNode> to) {
        NodeList list = from.getChildNodes();
        to.clear();
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            if (node instanceof Element) {
                Element e = (Element) node;
                String name = e.getTagName();
                String val = trimEndOfLine(getFirstLevelContent(node));
                DataNode dataNode = new DataNode();
                dataNode.value = val;
                loadData(node, dataNode.childs);
                to.put(name, dataNode);
            }
        }
    }

    private static String trimEndOfLine(String src) {
        String[] vals = src.split("\n");
        String val = "";
        for (String val1 : vals) {
            String trimmed = val1.trim();
            if (!trimmed.isEmpty()) {
                if (!val.isEmpty())
                    val += "\n";
                val += trimmed;
            }
        }
        return val;
    }

    private static String getFirstLevelContent(Node from) {
        String res = "";
        NodeList list = from.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node item = list.item(i);
            if (item.getNodeType() == Node.TEXT_NODE) {
                res += item.getNodeValue();
            }
        }
        return res;
    }

    public static String get(String resPath) {
        String[] lev = resPath.split(":");
        TreeMap<String, DataNode> curChilds = childs;
        for (int i = 0; i < lev.length; i++) {
            if (curChilds.containsKey(lev[i])) {
                DataNode node = curChilds.get(lev[i]);
                if (i == lev.length - 1) {
                    return node.value;
                } else {
                    curChilds = node.childs;
                }
            }
        }
        return "";
    }
}
