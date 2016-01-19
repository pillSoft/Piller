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

public class MainViewCard {
    private String title;
    private String body;
    private String actionButton;
    private String backgroundColor = "#ffffff";
    private String backgroundImage = null;

    public MainViewCard(String title, String body, String actionButton) {
        this.title = title;
        this.body = body;
        this.actionButton = actionButton;
    }

    public MainViewCard(String title, String body, String actionButton, String backgroundColor, String backgroundImage) {
        this.title = title;
        this.body = body;
        this.actionButton = actionButton;
        this.backgroundColor = backgroundColor;
        this.backgroundImage = backgroundImage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getActionButton() {
        return actionButton;
    }

    public void setActionButton(String actionButton) {
        this.actionButton = actionButton;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }
}
