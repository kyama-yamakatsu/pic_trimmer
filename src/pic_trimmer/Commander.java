// -*- Mode: java -*-
// $Author: kyama $
// Copyright(C) 2021 kyama yamakatsu
//
package pic_trimmer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import java.io.*;
import java.awt.*;
import javax.swing.*;


public class Commander implements ActionListener {

    private final String    FileExtension = "jpg";
    private final String    TrimedDirectory = "TRIMMED";

    private Main            main;
    private Detector        detector;
    private PropertyWindow  property;
    private MessageWindow   message;

    private String[]        pic_list = null;
    private int             process_pic;
    private boolean         loading = false;

    private BufferedImage   pic_image;
    private int             img_w, img_h;


    public Commander(Main jframe) {
	main = jframe;
	property = new PropertyWindow(main);
	message = new MessageWindow(main);
	detector = new Detector(property);

	process_pic = 0;
	pic_image = null;
	img_w = img_h = 0;
    }


    public BufferedImage getPicture() { return pic_image; }

    public void show(Point point) {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem menuItem;

 	menuItem = new JMenuItem("Property");
        menuItem.addActionListener(this);
        popupMenu.add(menuItem);

	menuItem = new JMenuItem("Set Picture directory and Detect trim size");
        menuItem.addActionListener(this);
        popupMenu.add(menuItem);

        menuItem = new JMenuItem("Save and Next");
	menuItem.addActionListener(this);
        popupMenu.add(menuItem);

 	menuItem = new JMenuItem("Oneshot test");
        menuItem.addActionListener(this);
        popupMenu.add(menuItem);

 	menuItem = new JMenuItem("Exit");
        menuItem.addActionListener(this);
        popupMenu.add(menuItem);

        popupMenu.show( (JFrame)main, point.x - 5, point.y - 5 );
    }

    // 多くの画像ファイルを開き、最小エリアを調べていく間は
    // 描画やメッセージが止まってしまうので、その部分は別スレッドにする
    // その間に操作を受け付けると対応が面倒なので操作受付を止める
    // ちょっとダサいが苦労してこだわることもあるまい
    private class setPicture_detectTrim_thread implements Runnable {
	setPicture_detectTrim_thread() {}
        public void run() {
            loading = true;        // loading scope
	    boolean result = setPicture_detectTrim();
	    if ( result ) {
		process_pic = 0;
		triming();
		message.println("トリミングして Save and Next を実行します");
	    }
            loading = false;
        }
    }

    public void actionPerformed(ActionEvent e) {
        if ( loading )
            return;

        String cmd = e.getActionCommand();

	if ( cmd.startsWith("Prop") ) {
	    property.set();
	    property.setVisible(true);

	} else if ( cmd.startsWith("Set Picture") ) {
            Thread thread =
		thread = new Thread( new setPicture_detectTrim_thread() );
	    thread.start();

	} else if ( cmd.startsWith("Save and") ) {
	    out_pic();

	    process_pic++;
	    triming();

	} else if ( cmd.startsWith("Oneshot") ) {
	    Oneshot();

	} else if ( cmd.startsWith("Exit") ) {
	    System.exit(0);
	}
    }

    private void Oneshot() {
	String currentDirectory = property.getDirPath();
	JFileChooser fc = new JFileChooser(currentDirectory);
	fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
	int selected = fc.showOpenDialog(main);
	if ( selected != JFileChooser.APPROVE_OPTION )
	    return;

	try {
	    File file = fc.getSelectedFile();
	    pic_image = ImageIO.read( file );
	    img_w = pic_image.getWidth();
	    img_h = pic_image.getHeight();
	    detector.detect_edge(img_w, img_h, pic_image);

	    int xl = detector.getEdge_l() + property.getLMargin();
	    int xr = detector.getEdge_r() - property.getRMargin();
	    int yu = detector.getEdge_u() + property.getUMargin();
	    int yb = detector.getEdge_b() - property.getBMargin();
	    int trim_w = xr - xl;
	    int trim_h = yb - yu;
	    main.setTrimSize( trim_w, trim_h, property.getAspect() );
	    main.setEdge(xl, xr, yu, yb);
	    main.setPictureSize(img_w, img_h);

	    message.println("one trim size = " + trim_w + " : " + trim_h);
	} catch(IOException iox) {
	    message.printErr("can't read picture.");
	}
    }

    private boolean setPicture_detectTrim() {
	String currentDirectory = property.getDirPath();
	JFileChooser fc = new JFileChooser(currentDirectory);
	fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	int selected = fc.showOpenDialog(main);
	if ( selected != JFileChooser.APPROVE_OPTION )
	    return false;

	File dir = fc.getSelectedFile();
	File[] file_list = dir.listFiles();
	if ( file_list == null ) {
	    message.printErr("Error!");
	    return false;
	}

	int t = 0;
	for( int i=0; i<file_list.length; i++ )
	    if ( file_list[i].isFile() )
		if ( file_list[i].toString().endsWith(FileExtension) )
		    t++;
	if ( t == 0 ) {
	    message.printErr("No picture!");
	    return false;
	}

	pic_list = new String[t];
	t = 0;
	for( int i=0; i<file_list.length; i++ )
	    if ( file_list[i].isFile() )
		if ( file_list[i].toString().endsWith(FileExtension) )
		    pic_list[t++] = file_list[i].toString();

	int trim_w = 123456789, trim_h = 123456789;
	for( int i=0; i<pic_list.length; i++ ) {
	    try {
		File file = new File( pic_list[i] );
		pic_image = ImageIO.read( file );
		img_w = pic_image.getWidth();
		img_h = pic_image.getHeight();
		main.setPictureSize(img_w, img_h);

		detector.detect_edge(img_w, img_h, pic_image);
		int xl = detector.getEdge_l() + property.getLMargin();
		int xr = detector.getEdge_r() - property.getRMargin();
		int yu = detector.getEdge_u() + property.getUMargin();
		int yb = detector.getEdge_b() - property.getBMargin();
		message.println(
		    pic_list[i] + " - " + xl + " : " + yu + " : " +xr+" : "+yb);

		// 最も小さいトリムサイズを求める
		if ( trim_w > ( xr - xl ) )
		    trim_w = xr - xl;
		if ( trim_h > ( yb - yu ) )
		    trim_h = yb - yu;

	    } catch(IOException iox) {
		message.printErr("can't read picture.");
		return false;
	    }
	}
	property.setDirPath(dir.getPath());
	main.setTrimSize( trim_w, trim_h, property.getAspect() );
	return true;
    }

    private void triming() {
	if ( pic_list == null )
	    return;
	if ( process_pic >= pic_list.length ) {
	    message.println("No processing picture.");
	    return;
	}

	message.println("detect = " + pic_list[process_pic]);

	try {
	    pic_image = ImageIO.read( new File(pic_list[process_pic]) );
	} catch(IOException iox) {
	    message.printErr("can't read picture = " + pic_list[process_pic]);
	}

	img_w = pic_image.getWidth();
	img_h = pic_image.getHeight();
	main.setPictureSize(img_w, img_h);

	detector.detect_edge( img_w, img_h, pic_image );
	int xl = detector.getEdge_l() + property.getLMargin();
	int xr = detector.getEdge_r() - property.getRMargin();
	int yu = detector.getEdge_u() + property.getUMargin();
	int yb = detector.getEdge_b() - property.getBMargin();
	main.setEdge(xl, xr, yu, yb);
    }

    private void out_pic() {
	if ( pic_list == null )
	    return;
	if ( process_pic >= pic_list.length ) {
	    message.println("No processing picture.");
	    return;
	}

	// Exif
/***
https://drewnoakes.com/code/exif/
https://totomo.net/850.htm
https://qiita.com/sumeragizzz/items/739c435a7eca45d32bc8

https://qiita.com/izuki_y/items/e7e6d3f2d8880e81afac
https://ja.ojit.com/so/java/964827
**/

	String currentDirectory = property.getDirPath();
	try {
	    File dir = new File( currentDirectory + "/" + TrimedDirectory );
	    dir.mkdir();
	    String filepathname =
		currentDirectory + "/" + TrimedDirectory + "/" +
		pic_list[process_pic].substring(
		    pic_list[process_pic].lastIndexOf(File.separator)+1,
		    pic_list[process_pic].length());

	    File fo = new File( filepathname );
	    Rectangle trim = main.getTrim();
	    BufferedImage out_img =
		pic_image.getSubimage(
		    (int)trim.getX(), (int)trim.getY(),
		    (int)trim.getWidth(), (int)trim.getHeight());
	    ImageIO.write(out_img, "jpg", fo);

	    message.println("write  = " + filepathname);

	} catch(IOException iox) {
	    message.printErr("can't write picture.");
	}
    }
}
