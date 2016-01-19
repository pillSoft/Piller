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

import com.pillsoft.piller.Theme;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class XmlParser {

    private Document doc;
    private List<Theme> themes = new ArrayList<>();

    public List<Theme> parseThemes(AssetManager am){
        themes.clear();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try{
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(am.open("Themes.xml"));
        }catch (Exception e){
            e.printStackTrace();
        }
        Element root = doc.getDocumentElement();
        NodeList nodeList = root.getElementsByTagName("Theme");
        if (nodeList!= null && nodeList.getLength()>0){
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element element = (Element)nodeList.item(i);
                String theme_name = getString(element,"Name");
                String theme_package = getString(element,"Package");
                String type = getString(element,"Type");
                String theme_color = getString(element,"Color");
                String theme_darkcolor = getString(element,"DarkColor");
                String theme_accentcolor = getString(element,"AccentColor");
                String theme_highlightedcolor = getString(element,"HighlightedColor");
                String theme_motto = getString(element,"Motto");
                Theme t = new Theme(theme_name, theme_package.toLowerCase(), type.toLowerCase(), theme_color, theme_darkcolor, theme_accentcolor, theme_highlightedcolor, theme_motto);
                themes.add(t);
            }
        }
        return themes;
    }

    public List<Theme> parseThemesString(String xml){
        themes.clear();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try{
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(new InputSource(new StringReader(xml)));
        }catch (Exception e){
            return null;
        }
        Element root = doc.getDocumentElement();
        NodeList nodeList = root.getElementsByTagName("Theme");
        if (nodeList!= null && nodeList.getLength()>0){
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element element = (Element)nodeList.item(i);
                String theme_name = getString(element,"Name");
                String theme_package = getString(element,"Package");
                String type = getString(element,"Type");
                String theme_color = getString(element,"Color");
                String theme_darkcolor = getString(element,"DarkColor");
                String theme_accentcolor = getString(element,"AccentColor");
                String theme_highlightedcolor = getString(element,"HighlightedColor");
                String theme_motto = getString(element,"Motto");
                Theme t = new Theme(theme_name, theme_package, type, theme_color, theme_darkcolor, theme_accentcolor, theme_highlightedcolor, theme_motto);
                themes.add(t);
            }
        }
        return themes;
    }

    private static String getString(Element e, String tagName) {
        NodeList nodes = e.getElementsByTagName(tagName);
        Element tagElement = (Element) nodes.item(0);
        return tagElement.getFirstChild().getNodeValue();
    }

    public List<Theme> getThemes() {
        return themes;
    }


}
