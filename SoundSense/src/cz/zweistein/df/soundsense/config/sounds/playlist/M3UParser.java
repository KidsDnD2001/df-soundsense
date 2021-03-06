package cz.zweistein.df.soundsense.config.sounds.playlist;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import cz.zweistein.df.soundsense.util.log.LoggerSource;

public class M3UParser implements IPlayListParser {
	private static Logger logger = LoggerSource.LOGGER;

	@Override
	public List<String> parse(String parentFilename, InputStream is) throws IOException {

		List<String> list = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));

		String line = reader.readLine();

		if ("#EXTM3U".equals(line)) {

			while (line != null) {

				if (!line.startsWith("#") && !line.isEmpty()) {

					String soundFileName = new File(parentFilename).getParent() + "/" + line;
					// validate that wave file exists.
					if (!new File(soundFileName).exists()) {
						// we did not find file on relative path respecting
						// location of
						// parent xml, lets see if it is on absolute path
						if (new File(line).exists()) {
							soundFileName = line;
						} else {
							logger.warning("Did not find " + soundFileName + ", ignoring.");
							soundFileName = null;
						}
					}

					if (soundFileName != null) {
						list.add(soundFileName);
					}

				}

				line = reader.readLine();
			}

		} else {
			throw new IOException("Wrong header for playlist");
		}

		return list;
	}

}
