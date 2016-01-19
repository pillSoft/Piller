/*
This file is part of Piller.
Piller is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.
Piller is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.
You should have received a copy of the GNU General Public License
along with Piller. If not, see <http://www.gnu.org/licenses/>.
Copyright 2015, Giulio Fagioli, Lorenzo Salani
*/
package com.pillsoft.piller.xml;

import android.content.res.AssetManager;
import android.util.Log;

import com.pillsoft.piller.Changelog;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class XmlChangelogParser {

    private Document doc;
    private List<Changelog> changelogs = new ArrayList<>();

    public List<Changelog> parseChangelogString(AssetManager am){
        changelogs.clear();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try{

            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(am.open("Changelogs.xml"));
        }catch (Exception e){
            return null;
        }
        Log.d("XmlChangelogs", "Inizio lettura");
        Element root = doc.getDocumentElement();
        NodeList nodeList = root.getElementsByTagName("Change");
        if (nodeList!= null && nodeList.getLength()>0){
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element element = (Element)nodeList.item(i);
                String versionName = getString(element,"VersionName");
                int versionCode = Integer.parseInt(getString(element,"VersionCode"));
                String description = getString(element,"Description");
                String date = getString(element,"Date");
                Changelog t = new Changelog(versionName, versionCode, description, date);
                changelogs.add(t);
            }
        }
        return changelogs;
    }

    private static String getString(Element e, String tagName) {
        NodeList nodes = e.getElementsByTagName(tagName);
        Element tagElement = (Element) nodes.item(0);
        return tagElement.getFirstChild().getNodeValue();
    }

    public List<Changelog> getThemes() {
        return changelogs;
    }


}
