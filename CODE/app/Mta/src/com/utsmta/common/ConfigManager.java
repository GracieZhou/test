package com.utsmta.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.utsmta.utils.LogUtil;
import com.utsmta.utils.MtaUtils;

import android.content.Context;

public class ConfigManager {
	private static final String TAG = "ConfigManager";
	
	private static final String TEMP_CONFIG_FILE_PATH = "temp_config.xml";
	
	private static final String CONFIG_FILE_NAME = "config.xml";
	
	private static final String AMTA_CONFIG_FILE_NAME = "amta_config.xml";
	
	private Context context = null;
	
	private String deviceName = null;
	
	private String deviceBranch = null;
	
	private ArrayList<FactoryGroup> groups = new ArrayList<FactoryGroup>();
	
	private HashMap<String, FactoryFragment> fragmentsMap = new HashMap<String, FactoryFragment>();

	protected void fetchDeviceInfo(Context context){
		InputStream is 	= null;	
		try {
			DocumentBuilder docBuilder 	= DocumentBuilderFactory.newInstance().newDocumentBuilder();					
			try {
				is = context.getAssets().open(CONFIG_FILE_NAME);
				try {
					Document doc = docBuilder.parse(is);
					deviceName = doc.getDocumentElement().getAttribute("device_name");
					deviceBranch = doc.getDocumentElement().getAttribute("device_branch");
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			if(is != null){
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	protected File getTempConfigFile(){
		File file = null;
		
		for(String usbDir : MtaUtils.getMountedUsbDevices("/mnt/usb/")){
			file = new File(usbDir+"/"+TEMP_CONFIG_FILE_PATH);
			if(file.exists()) return file;
		}
		
		return null;
	}
	
	public void parseConfig(FactoryDevice device, InputStream is){
		try {
			DocumentBuilder docBuilder 	= DocumentBuilderFactory.newInstance().newDocumentBuilder();					
			try {				
				try {
					Document doc = docBuilder.parse(is);						
					Element root = doc.getDocumentElement();
					NodeList groupNodes = root.getElementsByTagName("Group");
					
					groups.clear();
					fragmentsMap.clear();
					
					int index = 0;
					
					for(int i = 0; i < groupNodes.getLength(); ++i){
						Element groupNode = (Element) groupNodes.item(i);
						
						//parse FactoryGroup
						String groupName  = groupNode.getAttribute("name");
						FactoryGroup group = new FactoryGroup(groupName);

						if("true".equalsIgnoreCase(groupNode.getAttribute("enable"))){
							group.setActive(true);
						}else{
							group.setActive(false);
						}
						
						NodeList propertyList = groupNode.getElementsByTagName("property");
						for(int j = 0; j < propertyList.getLength(); ++j){
							Element property = (Element) propertyList.item(j);
							String key 		= property.getAttribute("name");
							String value	= property.getFirstChild().getNodeValue();
							group.setProperty(key, value);
						}
						
						NodeList itemNodes = groupNode.getElementsByTagName("Item");
						for(int m = 0; m < itemNodes.getLength(); ++m){
							
							Element itemNode = (Element) itemNodes.item(m);
							
							//parse FactoryItem
							String itemName 	= itemNode.getAttribute("name");
							FactoryItem item = new FactoryItem(itemName);
							
							if("true".equalsIgnoreCase(itemNode.getAttribute("enable"))){
								item.setActive(true);
							}else{
								item.setActive(false);
							}
							
							NodeList properties = itemNode.getElementsByTagName("property");
							for(int n = 0; n < properties.getLength(); ++n){
								Element property = (Element) properties.item(n);
								String key 		= property.getAttribute("name");
								String value	= property.getFirstChild().getNodeValue();
								item.setProperty(key, value);
							}
							
							if(device.onFactoryItemAdd(item, group)){
								item.setIndex(index);
								group.addItem(item);
								++index;
							}														
						}
						
						if(device.onFactoryGroupAdd(group)){
							groups.add(group);
							FactoryFragment factoryFragment = device.createFactoryFragment(group);
							fragmentsMap.put(groupName, factoryFragment);						
						}
					}
					
					for(FactoryGroup group : groups){
						LogUtil.d(TAG, "item name : "+group.getName());	
						group.printAllItems();
					}
					
//					Iterator it = fragmentsMap.entrySet().iterator();
//					while(it.hasNext()){
//						Map.Entry<String, FactoryFragment> entry = (Entry<String, FactoryFragment>) it.next();
//						String key = entry.getKey();
//						FactoryFragment fragment = entry.getValue();
//						LogUtil.d(TAG, "fragmentsMap key:"+key+" , value:"+FactoryFragment.class.toString());
//					}
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			if(is != null){
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}		
	}
	
	public void parseConfig(FactoryDevice device){
		if(null == device) return;
		
		InputStream is 	= null;
		
		File tmpFile = getTempConfigFile();
		
		if(tmpFile != null){
			try {
				is = new FileInputStream(tmpFile);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			try {
				is = this.context.getAssets().open(CONFIG_FILE_NAME);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(is != null){
			parseConfig(device, is);
		}
	}
	
	public void parseAmtaConfig(FactoryDevice device){
		InputStream is = null;
		
		try {
			is = this.context.getAssets().open(AMTA_CONFIG_FILE_NAME);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(is != null){
			parseConfig(device, is);
		}
	}
	
	public String deviceName(){
		return deviceName;
	}
	
	public String deviceBranch(){
		return deviceBranch;
	}
	
	public ConfigManager(Context context){
		this.context = context;
		this.fetchDeviceInfo(this.context);
	}
	
	public int getFactoryGroupIndex(FactoryGroup group){
		return getFactoryGroupIndex(group.getName());
	}
	
	public int getFactoryGroupIndex(String name){
		for(int i = 0; i < groups.size(); ++i){
			if(groups.get(i).getName().equalsIgnoreCase(name)){
				return i;
			}
		}
		
		return -1;
	}
	
	public FactoryGroup getFactoryGroup(int index){
		FactoryGroup group = null;
		if(index >=0 && index < groups.size()){
			group = groups.get(index);
		}
		
		return group;
	}
	
	public FactoryGroup getFactoryGroup(String name){
		for(FactoryGroup group : groups){
			if (group.getName().equals(name)) {
				return group;
			}
		}
		
		return null;
	}
	
	public FactoryFragment getFactoryFragment(int index){
		FactoryFragment fragment = null;
		
		FactoryGroup group = getFactoryGroup(index);
		if(group != null){
			fragment = fragmentsMap.get(group.getName());
		}
		return fragment;
	}
	
	public FactoryFragment getFactoryFragment(FactoryItem factoryItem){
		return fragmentsMap.get(factoryItem.getName());
	}
	
	public FactoryFragment getFactoryFragment(String name){
		return fragmentsMap.get(name);
	}
	
	public FactoryFragment getFirstNotEmptyFragment(){
		FactoryFragment fragment = null;
		
		for (int i = 0; i < groups.size(); i++) {
			fragment = getFactoryFragment(i);
			if(fragment != null) break;
		}
		
		return fragment;
	}
	
	public ArrayList<FactoryGroup> getAllGroups(){
		return groups;
	}
	
	public HashMap<String, FactoryFragment> getAllFragmentsMap(){
		return fragmentsMap;
	}
	
	public FactoryItem getFactoryItem(String name){
		for(FactoryGroup group : getAllGroups()){
			for(FactoryItem item : group.getAllItems()){
				if(item.getName().equals(name))
					return item;
			}
		}
		
		return null;
	}
}
