
package com.android.settings.userbackup;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scifly.device.Device;
import scifly.permission.Permission;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

/**
 * This class is used to back up the user's configuration data.
 * 
 * @author melody.xu
 * @date 2014-6-20
 */
public class BackUpData extends BroadcastReceiver {
    private static final String TAG = "BackUpData";

    private static String DIRFILE = Environment.getExternalStorageDirectory().getAbsolutePath() + "/userdata";

    private static String XMLFILE = ".user_config_data.xml";

    private static String WIFICONFIG_OLD = "/data/misc/wifi/wpa_supplicant.conf";

    private final static String DEVICE_NAME_CHANGED = "com.eostek.scifly.intent.action.ACTION_DEVICE_INFO_CHANGED";

    /**
     * @param strType the first parameter is the type of data, such as "device".
     * @param strName the second parameter is the name of the data, such as
     *            "device_name".
     * @param value the third parameter is the value of the data,such as
     *            "Scifly5678".
     * @return When successfully backup user data, it returns true, otherwise it
     *         returns false.
     */
    public static boolean backupData(String strType, String strName, String value) {
        Log.d(TAG, "backupData:strType " + strType + "  strName:" + strName + "  value:" + value);
        File file = new File(DIRFILE + "/" + XMLFILE);
        if (file.length() <= 0)
            file.delete();
        if (strType.equals("wifi")) {
            if (copyFile())
                return true;
        } else if (updateData(strType, strName, value)) {
            return true;
        }
        return false;
    }

    /**
     * @param null
     * @return This method is used to identify whether a file exists, if the
     *         file does not exist, create a new file, if it exists, or is
     *         created successfully, it returns "true", otherwise it returns
     *         "false".
     */
    public static boolean isFileExist() {
        File file = new File(DIRFILE + "/" + XMLFILE);
        if (!file.exists()) {
            if (!createXML()) {
                Log.e(TAG, "create" + XMLFILE + " failed.");
                return false;
            } else {
                Log.d(TAG, "create" + XMLFILE + " succeed.");
                return true;
            }
        } else {
            Log.d(TAG, XMLFILE + " is exists .");
            return true;
        }
    }

    /**
     * The method for updating the user profile data.
     * 
     * @param strType the first parameter is the type of data, such as "device".
     * @param strName the second parameter is the name of the data, such as
     *            "device_name".
     * @param value the third parameter is the value of the data,such as
     *            "Scifly5678".
     * @return When the user configuration data is updated successfully, it
     *         returns true, otherwise it returns false.
     */
    public static boolean updateData(String strType, String strName, String value) {
        Log.d(TAG, "UpdateData():strType " + strType + "  strName:" + strName + "  value:" + value);
        if (value == null || value == "") {
            Log.e(TAG, "the value is null");
            return false;
        }
        Log.d(TAG, "UpdateData()" + DIRFILE + "/" + XMLFILE);
        if (!isFileExist()) {
            Log.d(TAG, ".user_config_data.xml is not exist , maybe it is create failed...");
            return false;
        }
        Document document = load(DIRFILE + "/" + XMLFILE);
        if (document != null)
            docPrint(document);
        Node root = document.getDocumentElement();
        int mtypecount = 0;
        /** 如果root有子元素 */
        if (root.hasChildNodes()) {
            /** typelist */
            NodeList typelist = root.getChildNodes();
            /** 循环取得所有类型的节点 ，当所得节点为传入类型时，再更新该类型下满足条件的名字的值 */
            for (int i = 0; i < typelist.getLength(); i++) {
                Node type = typelist.item(i);
                if (type.getNodeType() == Node.ELEMENT_NODE && type.getNodeName().equals(strType))
                    mtypecount = i;
            }
            Log.d(TAG, "type.getNodeName():" + typelist.item(mtypecount).getNodeName());
            NodeList namelist = typelist.item(mtypecount).getChildNodes();
            for (int k = 0; k < namelist.getLength(); k++) {
                Node name = namelist.item(k);

                if (name.getNodeType() == Node.ELEMENT_NODE && name.getNodeName().equals(strName)) {
                    /** 删除符合条件的节点 */
                    // typelist.item(mtypecount).removeChild(name);
                    /** 修改符合条件的名字的值,如 device_name的值 */
                    name.getFirstChild().setNodeValue(value);
                    Log.d(TAG, "name.getFirstChild():" + name.getFirstChild().getNodeValue());
                    /** 修改完后重新保存 */
                    // Node2XmlFile(root, DIRFILE + "/" + XMLFILE);
                    doc2XmlFile(document, DIRFILE + "/" + XMLFILE);
                    Log.d(TAG, "UPDATE OK");
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * This method is used to create a new xml file. Note: This method has been
     * locked, ie, while allowing one thread to access the method.
     * 
     * @param null
     * @return Create a file successfully, returns "true", otherwise it returns
     *         "false".
     */
    public static synchronized boolean createXML() {
        File dir = new File(DIRFILE);
        boolean isDirExist = true;
        if (!dir.exists())
            isDirExist = dir.mkdirs();
        if (isDirExist) {
            try {
                Log.d(TAG, "create xml file " + XMLFILE + " starting!");
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.newDocument();
                Element root = document.createElement("userbackup");
                document.appendChild(root);
                Comment cmt1 = document.createComment("the data of user is used for backing up system config");
                root.appendChild(cmt1);

                Comment cmt2 = document.createComment("the Device Info.");
                root.appendChild(cmt2);

                Element device = document.createElement("device");
                root.appendChild(device);
                device.setAttribute("name", "DeviceConfig");

                Element device_name = document.createElement("device_name");
                device_name.appendChild(document.createTextNode("none"));
                device.appendChild(device_name);

                Comment cmt3 = document.createComment("the TimeZone Info.");
                root.appendChild(cmt3);
                Element timezone = document.createElement("timezone");
                root.appendChild(timezone);
                timezone.setAttribute("name", "TimeZoneConfig");
                Element time_zone = document.createElement("time_zone");
                time_zone.appendChild(document.createTextNode("none"));
                timezone.appendChild(time_zone);

                Comment cmt4 = document.createComment("the City Info.");
                root.appendChild(cmt4);
                Element city = document.createElement("city");
                root.appendChild(city);
                city.setAttribute("name", "CityConfig");
                Element my_city = document.createElement("my_city");
                my_city.appendChild(document.createTextNode("none"));
                city.appendChild(my_city);

                Comment cmt5 = document.createComment("the Locale Info.");
                root.appendChild(cmt5);
                Element locale = document.createElement("locale");
                root.appendChild(locale);
                locale.setAttribute("name", "LocaleConfig");
                Element my_locale = document.createElement("my_locale");
                my_locale.appendChild(document.createTextNode("none"));
                locale.appendChild(my_locale);
                // Node2XmlFile(root, DIRFILE + "/" + XMLFILE);
                doc2XmlFile(document, DIRFILE + "/" + XMLFILE);
                Log.d(TAG, "create xml file " + XMLFILE + " End !");
            } catch (Exception ex) {
                ex.printStackTrace();
                Log.d(TAG, "create xml file " + XMLFILE + " Failed !");
                return false;
            }
            return true;
        } else {
            Log.d(TAG, DIRFILE + " is not exist , maybe create failed ...");
            return false;
        }
    }

    /**
     * This method is used to print out a document file contains three nodes
     * (including the root node).This method is generally used for debugging.
     */
    public static void docPrint(Document doc) {
        Log.d(TAG, "docPrint()");
        Node root = doc.getDocumentElement();
        /** 如果root有子元素 */
        try {
            if (root.hasChildNodes()) {
                /** typelist */
                NodeList typelist = root.getChildNodes();
                /** 循环取得所有类型的节点 ，当所得节点为传入类型时，再更新该类型下满足条件的名字的值 */
                Log.d(TAG, "typelist lenght:" + typelist.getLength());
                for (int i = 0; i < typelist.getLength(); i++) {
                    Node type = typelist.item(i);
                    if (type.hasChildNodes()) {
                        if (type.getNodeType() == Node.ELEMENT_NODE) {
                            NodeList namelist = typelist.item(i).getChildNodes();
                            for (int k = 0; k < namelist.getLength(); k++) {
                                Node name = namelist.item(k);
                                if (name.getNodeType() == Node.ELEMENT_NODE) {
                                    Log.d(TAG, "Name:" + name.getFirstChild().getNodeName() + "   Value:"
                                            + name.getFirstChild().getNodeValue());
                                }
                            }
                        }
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Save the Document object to an xml file to a specified path.
     */
    public static boolean doc2XmlFile(Document document, String filename) {
        boolean flag = true;
        File file = new File(filename);
        Log.d(TAG, "doc2XmlFile file.exists():" + file.exists());
        if (file.exists())
            file.delete();
        try {
            /** 将document中的内容写入文件中 */
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();
            /** 编码 */
            // transformer.setOutputProperty(OutputKeys.ENCODING, "GB2312");
            docPrint(document);
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new File(filename));
            transformer.transform(source, result);
            Log.d(TAG, "load(filename)=" + load(filename));
            if (load(filename) != null)
                docPrint(load(filename));
        } catch (Exception ex) {
            flag = false;
            ex.printStackTrace();
        }
        Log.d(TAG, "doc2XmlFile flag:" + flag);
        return flag;
    }

    /**
     * Loading an xml document.
     */
    public static Document load(String filename) {
        Log.d(TAG, "load()");
        File file = new File(filename);
        if (file.exists() && file.length() > 0) {
            Document document = null;
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                document = builder.parse(new File(filename));
                document.normalize();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return document;
        }
        return null;
    }

    /**
     * Copy wifi config_file to SDcard.
     */
    public static boolean copyFile() {
        Log.d(TAG, "copyFile");
        File file = new File(DIRFILE + "/wpa_supplicant.conf");
        if (file.exists())
            file.delete();
        try {
            Log.d(TAG, "cp " + WIFICONFIG_OLD + " " + DIRFILE + "/wpa_supplicant.conf");
            Permission eshell = new Permission("JCheb2lkLnNldHRpbmdzanJt");
            boolean b_shell = eshell.exec("cp " + WIFICONFIG_OLD + " " + DIRFILE + "/wpa_supplicant.conf");
            return b_shell;
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }
        return false;

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (DEVICE_NAME_CHANGED.equals(intent.getAction())) {
            Log.d(TAG, "Action : " + DEVICE_NAME_CHANGED + "  Backup DeviceName");
            backupData("device", "device_name", Device.getDeviceName(context));
        }
    }
}
