package main.java.com.minaev.crud;

import main.java.com.minaev.crud.view.PostView;

import java.util.Date;
import java.util.Formatter;


public class AppRunner {


    public static void main(String[] args) {
        //LabelView labelView = new LabelView();
        //labelView.choiceMenuLabel();
        PostView postView = new PostView();
        postView.choiceMenuPost();
    }

}