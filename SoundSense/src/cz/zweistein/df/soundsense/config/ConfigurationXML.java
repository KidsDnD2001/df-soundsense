package cz.zweistein.df.soundsense.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;

import cz.zweistein.df.soundsense.gui.control.Threshold;
import cz.zweistein.df.soundsense.util.log.LoggerSource;

public class ConfigurationXML {
	private static Logger logger = LoggerSource.logger;
	
	private String fileName;
	private Document doc;
	
	private String gamelogPath;
	private String gamelogEncoding;
	private String soundpacksPath;
	private float volume;
	private long playbackTheshhold;

	private List<String> autoUpdateURLs;
	private boolean replaceFiles;
	private boolean deleteFiles;

	private boolean gui;

	public ConfigurationXML(String configurationFile) throws SAXException, IOException {
		
		this.fileName = configurationFile;
		
		InputSource source = new InputSource(new FileInputStream(this.fileName));
		DOMParser parser = new DOMParser();
		parser.parse(source);
		this.doc = parser.getDocument();
		
		Element configNodes = (Element) doc.getElementsByTagName("configuration").item(0);
		
		Node gamelogNode = configNodes.getElementsByTagName("gamelog").item(0);
		this.gamelogPath = gamelogNode.getAttributes().getNamedItem("path").getNodeValue();
		this.gamelogEncoding = gamelogNode.getAttributes().getNamedItem("encoding").getNodeValue();
		
		Node soundpacksNode = configNodes.getElementsByTagName("soundpacks").item(0);
		this.soundpacksPath = soundpacksNode.getAttributes().getNamedItem("path").getNodeValue();
		
		Node volumeNode = configNodes.getElementsByTagName("volume").item(0);
		String volumeAdjustmentText = volumeNode.getAttributes().getNamedItem("value").getNodeValue();
		try {
			this.volume = Float.parseFloat(volumeAdjustmentText);
		} catch (NumberFormatException e) {
			logger.info("Volume value '"+volumeAdjustmentText+" is not recognized as a number, using default "+this.volume+".");
		}
		
		Node playbackTheshholdNode = configNodes.getElementsByTagName("playbackTheshhold").item(0);
		String playbackTheshholdText = playbackTheshholdNode.getAttributes().getNamedItem("value").getNodeValue();
		this.playbackTheshhold = Threshold.EVERYTHING.getValue();
		try {
			this.playbackTheshhold = Long.parseLong(playbackTheshholdText);
		} catch (NumberFormatException e) {
			logger.info("Volume value '"+playbackTheshholdText+" is not recognized as a number, using default "+this.volume+".");
		}
		
		Node autoUpdateURLNode = configNodes.getElementsByTagName("autoUpdateURLs").item(0);
		this.autoUpdateURLs = new ArrayList<String>();
		
		NodeList urls = autoUpdateURLNode.getChildNodes();
		for (int j = 0; j < urls.getLength(); j++) {
			Node configNode = urls.item(j);
			String name = configNode.getLocalName();
			
			if ("item".equals(name)) {
				autoUpdateURLs.add(configNode.getAttributes().getNamedItem("path").getNodeValue());
			}
		}
		
		this.setDeleteFiles(parseBoolean("autoUpdateDeleteFiles", configNodes));
		this.setReplaceFiles(parseBoolean("autoUpdateReplaceFiles", configNodes));
		
		this.gui = parseBoolean("gui", configNodes); 
		
	}
	
	private boolean parseBoolean(String noneName, Element nodes) {
		Node node = nodes.getElementsByTagName(noneName).item(0);
		String guiText = node.getAttributes().getNamedItem("value").getNodeValue();
		return Boolean.parseBoolean(guiText);
	}
	
	public void saveConfiguration() {
		logger.info("Saving configuration.");
		
		Element configNodes = (Element) doc.getElementsByTagName("configuration").item(0);
		
		Node gamelogNode = configNodes.getElementsByTagName("gamelog").item(0);
		gamelogNode.getAttributes().getNamedItem("path").setNodeValue(this.getGamelogPath());
		
		Node volumeNode = configNodes.getElementsByTagName("volume").item(0);
		volumeNode.getAttributes().getNamedItem("value").setNodeValue(Float.toString(this.getVolume()));
		
		Node playbackTheshholdNode = configNodes.getElementsByTagName("playbackTheshhold").item(0);
		playbackTheshholdNode.getAttributes().getNamedItem("value").setNodeValue(Long.toString(this.getPlaybackTheshhold()));
		
		Node autoUpdateReplaceFilesNode = configNodes.getElementsByTagName("autoUpdateReplaceFiles").item(0);
		autoUpdateReplaceFilesNode.getAttributes().getNamedItem("value").setNodeValue(Boolean.toString(this.getReplaceFiles()));
		
		Node autoUpdateDeleteFilesNode = configNodes.getElementsByTagName("autoUpdateDeleteFiles").item(0);
		autoUpdateDeleteFilesNode.getAttributes().getNamedItem("value").setNodeValue(Boolean.toString(this.getDeleteFiles()));
		
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.transform(new DOMSource(this.doc), new StreamResult(new File(this.fileName)));
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		
	}
	
	public String getGamelogPath() {
		return this.gamelogPath;
	}
	
	public void setGamelogPath(String gamelogPath) {
		this.gamelogPath = gamelogPath;
	}
	
	public String getSoundpacksPath() {
		return this.soundpacksPath;
	}

	public float getVolume() {
		return this.volume;
	}
	
	public void setVolume(float volume) {
		this.volume = volume;
	}
	
	public boolean getGui() {
		return this.gui;
	}

	public List<String> getAutoUpdateURLs() {
		return autoUpdateURLs;
	}

	public void setPlaybackTheshhold(long playbackTheshhold) {
		this.playbackTheshhold = playbackTheshhold;
	}

	public long getPlaybackTheshhold() {
		return playbackTheshhold;
	}

	public void setReplaceFiles(boolean replaceFiles) {
		this.replaceFiles = replaceFiles;
	}

	public boolean getReplaceFiles() {
		return replaceFiles;
	}

	public void setDeleteFiles(boolean deleteFiles) {
		this.deleteFiles = deleteFiles;
	}

	public boolean getDeleteFiles() {
		return deleteFiles;
	}

	public String getGamelogEncoding() {
		return gamelogEncoding;
	}

	public void setGamelogEncoding(String gamelogEncoding) {
		this.gamelogEncoding = gamelogEncoding;
	}
	
}
