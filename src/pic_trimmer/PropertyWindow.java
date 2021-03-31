// -*- Mode: java -*-
// $Author: kyama $
// Copyright(C) 2021 kyama yamakatsu
//
package pic_trimmer;

import java.util.Properties;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import java.io.*;
import java.awt.*;
import javax.swing.*;


public class PropertyWindow extends JDialog implements ActionListener {

    private static final String  PROPERTIES_FILE  = "pt.properties";
    private JFrame               main;
    private Properties           properties;

    // 必要な Property
    // Load directory 文字列表示 - 設定ボタン
    // Aspect
    // Upper margin
    // Bottom margin
    // Left margin
    // Right margin
    // Noise mask bit １６進表記 ex) f0f0f0
    // detect length

    // properties 変数
    private String dirPath;
    private String aspect;
    private int u_margin, b_margin, l_margin, r_margin;
    private String noise_mask;
    private int detect_len;

    // properties User Interface
    JTextField dirPathTF;
    JButton dirPathB;
    JComboBox aspectCB;
    JTextField u_marginTF, b_marginTF, l_marginTF, r_marginTF;
    JButton okB, applyB, closeB;
    JTextField noise_maskTF;
    JTextField detect_lenTF;

    // アスペクト比プロパティ
    // 一眼レフカメラ 3：2
    // コンパクトデジカメ 4：3
    // iPhone 3:2
    // android 4:3, 16:9
    String[] aspect_items = { "3:2", "4:3", "16:9", "1:1" };


    PropertyWindow(JFrame pa) {
	super(pa, "Property", true);
	main = pa;

        properties = new Properties();
	try {
	    BufferedInputStream in =
		new BufferedInputStream(
		    new FileInputStream( PROPERTIES_FILE ));
	    properties.load(in);
	    in.close();
	} catch(FileNotFoundException ffx) {
	} catch(IOException iox) {}
	dirPath = properties.getProperty("dirPath","/");	
	aspect  = properties.getProperty("aspect",aspect_items[0]);
	u_margin = Integer.valueOf(properties.getProperty("u_margin","20"));
	b_margin = Integer.valueOf(properties.getProperty("b_margin","20"));
	l_margin = Integer.valueOf(properties.getProperty("l_margin","20"));
	r_margin = Integer.valueOf(properties.getProperty("r_margin","20"));
	noise_mask =properties.getProperty("noise_mask","f0f0f0");
	detect_len =Integer.valueOf(properties.getProperty("detect_len","200"));

	JLabel lbl;
	GridBagLayout gridbag = new GridBagLayout();
	GridBagConstraints c = new GridBagConstraints();
	setLayout(gridbag);
	//
	lbl = new JLabel("Picture Directory");
	lbl.setPreferredSize(new Dimension(200,25));
	c.insets = new Insets(1, 2, 1, 1);
	c.gridwidth = 2;
	gridbag.setConstraints(lbl,c);
	add(lbl);

	dirPathB = new JButton("Dir Select");
	dirPathB.setPreferredSize(new Dimension(100,30));
	dirPathB.setActionCommand("Dir Select");
	dirPathB.addActionListener(this);
	c.insets = new Insets(1, 1, 1, 2);
	c.gridwidth = GridBagConstraints.REMAINDER;
	gridbag.setConstraints(dirPathB, c);
	add(dirPathB);

	dirPathTF = new JTextField();
	dirPathTF.setPreferredSize(new Dimension(300,25));
	c.insets = new Insets(1, 2, 1, 2);
	c.gridwidth = GridBagConstraints.REMAINDER;
	gridbag.setConstraints(dirPathTF, c);
	add(dirPathTF);
	//
	lbl = new JLabel("Aspect");
	lbl.setPreferredSize(new Dimension(100,25));
	c.insets = new Insets(1, 2, 1, 1);
	c.gridwidth = 2;
	gridbag.setConstraints(lbl,c);
	add(lbl);

	aspectCB = new JComboBox(aspect_items);
	aspectCB.setPreferredSize(new Dimension(100,30));
	c.insets = new Insets(1, 1, 1, 2);
	c.gridwidth = GridBagConstraints.REMAINDER;
	gridbag.setConstraints(aspectCB, c);
	add(aspectCB);
	//
	lbl = new JLabel("Upper margin");
	lbl.setPreferredSize(new Dimension(200,25));
	c.insets = new Insets(1, 2, 1, 1);
	c.gridwidth = 2;
	gridbag.setConstraints(lbl,c);
	add(lbl);

	u_marginTF = new JTextField();
	u_marginTF.setPreferredSize(new Dimension(100,25));
	c.insets = new Insets(1, 1, 1, 2);
	c.gridwidth = GridBagConstraints.REMAINDER;
	gridbag.setConstraints(u_marginTF, c);
	add(u_marginTF);

	lbl = new JLabel("Bottom margin");
	lbl.setPreferredSize(new Dimension(200,25));
	c.insets = new Insets(1, 2, 1, 1);
	c.gridwidth = 2;
	gridbag.setConstraints(lbl,c);
	add(lbl);

	b_marginTF = new JTextField();
	b_marginTF.setPreferredSize(new Dimension(100,25));
	c.insets = new Insets(1, 1, 1, 2);
	c.gridwidth = GridBagConstraints.REMAINDER;
	gridbag.setConstraints(b_marginTF, c);
	add(b_marginTF);

	lbl = new JLabel("Left margin");
	lbl.setPreferredSize(new Dimension(200,25));
	c.insets = new Insets(1, 2, 1, 1);
	c.gridwidth = 2;
	gridbag.setConstraints(lbl,c);
	add(lbl);

	l_marginTF = new JTextField();
	l_marginTF.setPreferredSize(new Dimension(100,25));
	c.insets = new Insets(1, 1, 1, 2);
	c.gridwidth = GridBagConstraints.REMAINDER;
	gridbag.setConstraints(l_marginTF, c);
	add(l_marginTF);

	lbl = new JLabel("Right margin");
	lbl.setPreferredSize(new Dimension(200,25));
	c.insets = new Insets(1, 2, 1, 1);
	c.gridwidth = 2;
	gridbag.setConstraints(lbl,c);
	add(lbl);

	r_marginTF = new JTextField();
	r_marginTF.setPreferredSize(new Dimension(100,25));
	c.insets = new Insets(1, 1, 1, 2);
	c.gridwidth = GridBagConstraints.REMAINDER;
	gridbag.setConstraints(r_marginTF, c);
	add(r_marginTF);
	//
	lbl = new JLabel("Noise Mask Bits. ex)f0f0f0");
	lbl.setPreferredSize(new Dimension(200,25));
	c.insets = new Insets(1, 2, 1, 1);
	c.gridwidth = 2;
	gridbag.setConstraints(lbl,c);
	add(lbl);

	noise_maskTF = new JTextField();
	noise_maskTF.setPreferredSize(new Dimension(100,25));
	c.insets = new Insets(1, 1, 1, 2);
	c.gridwidth = GridBagConstraints.REMAINDER;
	gridbag.setConstraints(noise_maskTF, c);
	add(noise_maskTF);
	//
	lbl = new JLabel("Detect length (deep)");
	lbl.setPreferredSize(new Dimension(200,25));
	c.insets = new Insets(1, 2, 1, 1);
	c.gridwidth = 2;
	gridbag.setConstraints(lbl,c);
	add(lbl);

	detect_lenTF = new JTextField();
	detect_lenTF.setPreferredSize(new Dimension(100,25));
	c.insets = new Insets(1, 1, 1, 2);
	c.gridwidth = GridBagConstraints.REMAINDER;
	gridbag.setConstraints(detect_lenTF, c);
	add(detect_lenTF);
	//
	okB = new JButton("OK");
	okB.setPreferredSize(new Dimension(100,30));
	okB.setActionCommand("ok");
	okB.addActionListener(this);
	c.insets = new Insets(1, 2, 1, 1);
	c.gridwidth = 1;
	gridbag.setConstraints(okB, c);
	add(okB);

	applyB = new JButton("Apply");
	applyB.setPreferredSize(new Dimension(100,30));
	applyB.setActionCommand("apply");
	applyB.addActionListener(this);
	c.insets = new Insets(1, 1, 2, 1);
	c.gridwidth = 1;
	gridbag.setConstraints(applyB, c);
	add(applyB);

	closeB = new JButton("Close");
	closeB.setPreferredSize(new Dimension(100,30));
	closeB.setActionCommand("close");
	closeB.addActionListener(this);
	c.insets = new Insets(1, 1, 2, 2);
	c.gridwidth = GridBagConstraints.REMAINDER;
	gridbag.setConstraints(closeB, c);
	add(closeB);

	pack();
	Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
	int mw = getWidth();
	int mh = getHeight();
	setBounds(new Rectangle(d.width/2-mw/2, d.height/2-mh/2, mw, mh));
    }


    public void actionPerformed(ActionEvent e) {
	String cmd = e.getActionCommand();

	if ( cmd.equals("Dir Select") ) {
	    JFileChooser fc = new JFileChooser(dirPath);
	    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    int selected = fc.showOpenDialog(main);
	    if ( selected == JFileChooser.APPROVE_OPTION ) {
		File dir = fc.getSelectedFile();
		if ( dir.isDirectory() ) {
		    dirPathTF.setText(dir.getPath());
		}
	    }

	} else if ( cmd.equals( "ok") ) {
	    get();
	    setVisible(false);
	} else if ( cmd.equals( "apply") ) {
	    get();
	} else if ( cmd.equals( "close") ) {
	    setVisible(false);
	} else {
	    if ( e.getSource() instanceof JTextField )
		get();
	}

	main.repaint();
    }

    // 変数値をＵＩに設定する
    void set() {
	dirPathTF.setText(dirPath);
	aspectCB.setSelectedItem(aspect);
	u_marginTF.setText(String.valueOf(u_margin));
	b_marginTF.setText(String.valueOf(b_margin));
	l_marginTF.setText(String.valueOf(l_margin));
	r_marginTF.setText(String.valueOf(r_margin));
	noise_maskTF.setText(noise_mask);
	detect_lenTF.setText(String.valueOf(detect_len));
    }

    // ＵＩ設定データを変数に戻す
    void get() {
	dirPath = dirPathTF.getText();
	aspect = (String)aspectCB.getSelectedItem();
	u_margin = Integer.valueOf(u_marginTF.getText());
	b_margin = Integer.valueOf(b_marginTF.getText());
	l_margin = Integer.valueOf(l_marginTF.getText());
	r_margin = Integer.valueOf(r_marginTF.getText());
	noise_mask = noise_maskTF.getText();
	detect_len = Integer.valueOf(detect_lenTF.getText());

	// ここで設定を Property に保存しセーブしておく
	properties.setProperty("dirPath",dirPath);
	properties.setProperty("aspect",aspect);
	properties.setProperty("u_margin",String.valueOf(u_margin));
	properties.setProperty("b_margin",String.valueOf(b_margin));
	properties.setProperty("l_margin",String.valueOf(l_margin));
	properties.setProperty("r_margin",String.valueOf(r_margin));
	properties.setProperty("noise_mask",noise_mask);
	properties.setProperty("detect_len",String.valueOf(detect_len));
        try {
            BufferedOutputStream out =
                new BufferedOutputStream(
                    new FileOutputStream(PROPERTIES_FILE));
            properties.store(out,"Picture Trimmer Properties File");
            out.close();
        } catch(IOException iox){}	
    }

    // getter
    protected String getDirPath() { return dirPath; }
    protected String getAspect() { return aspect; }
    protected int getUMargin() { return u_margin; }
    protected int getBMargin() { return b_margin; }
    protected int getLMargin() { return l_margin; }
    protected int getRMargin() { return r_margin; }
    protected int getDetectLen() { return detect_len; }
    protected int getNoiseMask() {
	int noise_mask_bit;
	noise_mask_bit = Integer.parseInt(noise_mask, 16);
	return noise_mask_bit;
    }

    // setter
    protected void setDirPath(String path) { dirPath = path; }
}
