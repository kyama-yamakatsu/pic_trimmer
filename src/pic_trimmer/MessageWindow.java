// -*- Mode: java -*-
// $Author: kyama $
// Copyright(C) 2021 kyama yamakatsu
//
package	pic_trimmer;

import	java.awt.*;
import	javax.swing.*;
import	javax.swing.text.*;


public class MessageWindow extends JDialog {

    private static final int            DEFAULT_LINE_MAX = 100;

    private JTextPane			jtext;
    private Document			contentModel;
    private int				lineCount = 0;
    private JViewport			viewport;

    private MutableAttributeSet		styleNormal;
    private MutableAttributeSet		styleWarning;
    private MutableAttributeSet		styleError;
    private MutableAttributeSet		styleDebug;

    private Color			nomalColor = Color.blue;
    private Color			warningColor = Color.pink;
    private Color			errorColor = Color.red;
    private Color			debugColor = new Color(10,100,10);


    public MessageWindow(JFrame main) {
	super(main,"Message");
	int width = main.getWidth();
	int height = main.getHeight();
	setBounds( main.getX(), main.getY() + height, width, 200 );

	jtext = new JTextPane();
	jtext.setEditable(false);
	JScrollPane scrollPane = new JScrollPane(jtext);
	getContentPane().setLayout(new BorderLayout());
	getContentPane().add(scrollPane,BorderLayout.CENTER);
	viewport = scrollPane.getViewport();
	contentModel = jtext.getDocument();
	setVisible(true);

	styleNormal = new SimpleAttributeSet();
	StyleConstants.setForeground(styleNormal,nomalColor);
	styleWarning = new SimpleAttributeSet();
	StyleConstants.setForeground(styleWarning,warningColor);
	styleError  = new SimpleAttributeSet();
	StyleConstants.setForeground(styleError,errorColor);
	styleDebug  = new SimpleAttributeSet();
	StyleConstants.setForeground(styleDebug,debugColor);

	int fontSize = 16;
	String fontName = "ＭＳ ゴシック";
	StyleConstants.setFontFamily(styleNormal,fontName);
	StyleConstants.setFontSize(styleNormal,fontSize);
	StyleConstants.setFontFamily(styleWarning,fontName);
	StyleConstants.setFontSize(styleWarning,fontSize);
	StyleConstants.setFontFamily(styleError,fontName);
	StyleConstants.setFontSize(styleError,fontSize);
	StyleConstants.setFontFamily(styleDebug,fontName);
	StyleConstants.setFontSize(styleDebug,fontSize);
    }

    public void println  (String s) { print( s, styleNormal ); }
    public void printWarn(String s) { print( s, styleWarning ); }
    public void printErr (String s) { print( s, styleError ); }
    private void print(String str,MutableAttributeSet style) {
	final String str_ = str;
	final MutableAttributeSet style_ = style;
	SwingUtilities.invokeLater(
	    new Runnable() {
		public void run() {
		    jtext.setCharacterAttributes( style_, false );
		    try {
			contentModel.insertString(
				contentModel.getLength(), str_ + "\n", style_);
		    } catch(BadLocationException e) {
			System.out.println("MessageDialog Exception 1.");
		    }
		    
		    if ( ++lineCount > DEFAULT_LINE_MAX ) {
			--lineCount;
			removeTopLine();
		    }
		}
	    });
    }
    // なぜか上の runnable を使わないと最新表示行が隠れてしまう
    private void _print(String str, MutableAttributeSet style) {
	jtext.setCharacterAttributes( style, false );
	try {
	    contentModel.insertString(
		contentModel.getLength(), str + "\n", style);
	} catch(BadLocationException e) {
	    System.out.println("MessageDialog Exception 1.");
	}
	if ( ++lineCount > DEFAULT_LINE_MAX ) {
	    --lineCount;
	    removeTopLine();
	}
    }

    private void removeTopLine() {
	try {
	    String contents = contentModel.getText(
		contentModel.getStartPosition().getOffset(),
		contentModel.getLength());
	    int newLineIndex = contents.indexOf("\n") + 1;
	    contentModel.remove(
		contentModel.getStartPosition().getOffset(),newLineIndex);
	} catch(BadLocationException e) {
	    System.out.println("MessageDialog Exception 2.");
	}
    }
}
