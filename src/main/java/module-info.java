module tn.esprit.pidev {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires java.sql;
    //requires mysql.connector.java;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires jbcrypt;
    requires java.mail;
    requires java.desktop;
    requires spring.security.crypto;
    requires twilio;
    requires aerogear.otp.java;
    requires com.google.zxing;
    requires com.google.zxing.javase;
    requires java.net.http;
    requires com.google.gson;
    requires itextpdf;
    requires stripe.java;
    requires jdk.jsobject;
    requires org.json;
    requires java.base;
    requires java.prefs;
    requires java.xml;
    requires java.xml.bind;
    requires java.management;
    requires java.naming;
    requires java.activation;
    requires java.rmi;
    requires java.instrument;
    requires javafx.media;
    requires okhttp3;
    requires nylas;
    requires com.fasterxml.jackson.core;
    requires com.google.api.client;
    requires google.http.client.jackson2;
    requires google.api.services.calendar.v3.rev411;
    requires google.api.client;
    requires google.oauth.client.java6;
    requires google.oauth.client.jetty;
    requires com.google.api.client.auth;
    requires mysql.connector.j;

    opens tn.esprit.pidev to javafx.fxml;
    opens tn.esprit.pidev.gestion_activite.gui to javafx.fxml;
    opens tn.esprit.pidev.gestion_commande.controllers to javafx.fxml;
    opens tn.esprit.pidev.gestion_ressource.Controllers to javafx.fxml;
    opens tn.esprit.pidev.gestion_produit.gui to javafx.fxml;
    opens tn.esprit.pidev.gestion_rdv to javafx.fxml;
    opens tn.esprit.pidev.gestion_rdv.enteties to javafx.fxml;
    opens tn.esprit.pidev.gestion_rdv.services to javafx.fxml;
    opens tn.esprit.pidev.gestion_rdv.Controller to javafx.fxml;



    exports tn.esprit.pidev;
    exports tn.esprit.pidev.gestion_activite.gui;
    exports tn.esprit.pidev.gestion_commande.controllers;
    exports tn.esprit.pidev.gestion_commande.entities;
    exports tn.esprit.pidev.gestion_ressource.Controllers;
    exports tn.esprit.pidev.gestion_ressource.Entities;
    exports tn.esprit.pidev.gestion_produit.entities;
    exports tn.esprit.pidev.gestion_produit.gui;
    exports tn.esprit.pidev.gestion_rdv.enteties;
    exports tn.esprit.pidev.gestion_rdv;
    exports tn.esprit.pidev.gestion_rdv.services;
    exports tn.esprit.pidev.gestion_rdv.dao;
    exports tn.esprit.pidev.gestion_rdv.Controller;

}
