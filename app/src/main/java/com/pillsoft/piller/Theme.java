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
package com.pillsoft.piller;

public class Theme {

    private String theme_name;
    private String theme_package;
    private String type;
    private String theme_color;
    private String theme_darkcolor;
    private String theme_accentcolor;
    private String theme_highlightedcolor;
    private String theme_motto;

    public Theme(String theme_name, String theme_package, String type, String theme_color, String theme_darkcolor, String theme_accentcolor, String theme_highlightedcolor, String theme_motto) {
        this.theme_name = theme_name.replaceAll("\\s","");
        this.theme_package = theme_package;
        this.type = type;
        this.theme_color = theme_color;
        this.theme_darkcolor = theme_darkcolor;
        this.theme_accentcolor = theme_accentcolor;
        this.theme_highlightedcolor = theme_highlightedcolor;
        this.theme_motto = theme_motto;
    }

    public String getTheme_name() {
        return theme_name;
    }

    public void setTheme_name(String theme_name) {
        this.theme_name = theme_name;
    }

    public String getTheme_package() {
        return theme_package;
    }

    public void setTheme_package(String theme_package) {
        this.theme_package = theme_package;
    }

    public String getTheme_type() {
        return type;
    }

    public void setTheme_type(String type) {
        this.type = type;
    }

    public String getTheme_color() {
        return theme_color;
    }

    public void setTheme_color(String theme_color) {
        this.theme_color = theme_color;
    }

    public String getTheme_darkcolor() {
        return theme_darkcolor;
    }

    public void setTheme_darkcolor(String theme_darkcolor) {
        this.theme_darkcolor = theme_darkcolor;
    }

    public String getTheme_highlightedcolor() {
        return theme_highlightedcolor;
    }

    public void setTheme_highlightedcolor(String theme_highlightedcolor) {
        this.theme_highlightedcolor = theme_highlightedcolor;
    }

    public String getTheme_accentcolor() {
        return theme_accentcolor;
    }

    public void setTheme_accentcolor(String theme_accentcolor) {
        this.theme_accentcolor = theme_accentcolor;
    }

    public String getTheme_motto() {
        return theme_motto;
    }

    public void setTheme_motto(String theme_motto) {
        this.theme_motto = theme_motto;
    }



}
