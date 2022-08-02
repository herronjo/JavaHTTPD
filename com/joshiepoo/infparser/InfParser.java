package com.joshiepoo.infparser;

import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class InfParser {
	public static Map<String, Map<String, String>> parse(String filename) throws FileNotFoundException {
		Map<String, Map<String, String>> inf = new HashMap<String, Map<String, String>>();
		File file = new File(filename);
		if (file.exists()) {
			Scanner scnr = new Scanner(file);
			while (scnr.hasNextLine()) {
				String line = scnr.nextLine();
				if (line.length() > 0 && line.startsWith("[") && line.endsWith("]")) {
					String section = line.substring(1, line.length()-1);
					Map<String, String> kv = new HashMap<String, String>();
					boolean finished = false;
					while (!finished && scnr.hasNextLine()) {
						String infline = scnr.nextLine();
						if (infline.length() != 0) {
							if (!infline.startsWith(";")) {
								String[] split = infline.split("=");
								String name = split[0];
								StringBuilder str = new StringBuilder();
								for (int i = 1; i < split.length; i++) {
									str.append(split[i]).append("=");
								}
								str.deleteCharAt(str.length() - 1);
								kv.put(name, str.toString());
							}
						} else {
							finished = true;
						}
					}
					inf.put(section, kv);
				}
			}
			scnr.close();
		} else {
			throw new FileNotFoundException(filename);
		}
		return inf;
	}
}
