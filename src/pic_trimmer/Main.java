// -*- Mode: java -*-
// $Author: kyama $
// Copyright(C) 2021 kyama yamakatsu
//
package pic_trimmer;

import java.awt.Point;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import java.awt.event.*;
import javax.swing.*;


public final class Main extends JFrame
    implements MouseListener, MouseMotionListener, ComponentListener {

    private Commander      commander;

    // 全て実際の絵のピクセルサイズ
    private Rectangle      pic_size = null;  // 原点はツール
    private Rectangle      edge_size = null; // 原点は pic
    private Rectangle      trim_size = null; // 原点は pic
    private double         scaleFactor = (double)1.0;

    private boolean        isMousePressLNow = false;
    private int            drag_offset_x, drag_offset_y;

    // content pane には飾り領域も含まれてるの？ 
    private final int      F_EDGE = 5;
    private final int      F_TOP  = 20;

    private final int      WIDTH = 800;
    private final int      HEIGHT = 600;


    Main() {
        super("Pic Trimmer ver. 1.2");
	setPreferredSize( new Dimension(WIDTH, HEIGHT) );
	Dimension dim = getToolkit().getScreenSize();
	setBounds(
	    (int)(dim.getWidth()-WIDTH)/2, (int)(dim.getHeight()-HEIGHT)/2,
	    WIDTH, HEIGHT);

	commander = new Commander(this);

        addMouseListener(this);
        addMouseMotionListener(this);
	addComponentListener(this);
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	setVisible(true);
    }

    protected void setTrimSize( int w, int h, String aspect) {
	int trim_w, trim_h;

	// aspect - { "3:2", "4:3", "16:9", "1:1" }
	if ( aspect.startsWith("3") ) {
	    trim_w = h*3/2; // h/2*3
	    trim_h = w*2/3; // w/3*2
	} else if ( aspect.startsWith("4") ) {
	    trim_w = h*4/3;
	    trim_h = w*3/4;
	} else if ( aspect.startsWith("16") ) {
	    trim_w = h*16/9;
	    trim_h = w*9/16;
	} else {
	    trim_w = h*1/1;
	    trim_h = w*1/1;
	}

	if ( trim_w > w ) trim_w = w;
	if ( trim_h > h ) trim_h = h;

	trim_size = new Rectangle();
	trim_size.setSize( trim_w, trim_h );
    }

    protected void setPictureSize(int w, int h) {
	pic_size = new Rectangle(w, h);
	changeZoomFactor();
	repaint();
    }

    protected void setEdge(int xl, int xr, int yu, int yb) {
	edge_size = new Rectangle( xl, yu, xr-xl, yb-yu );
	// edge のセンターに trim を置く
	double x = edge_size.getX();
	x += (edge_size.getWidth() - trim_size.getWidth())/2;
	double y = edge_size.getY();
	y += (edge_size.getHeight() - trim_size.getHeight())/2;
	trim_size.setLocation( (int)x, (int)y );
	repaint();
    }

    protected Rectangle getTrim() {
	return trim_size;
    }

    // 最外径等が変わると scaleFactor と pic_size の原点が変わる
    private void changeZoomFactor() {
	double c_w = (double)(getContentPane().getWidth() - F_EDGE * 2);
	double c_h = (double)(getContentPane().getHeight() - F_TOP - F_EDGE );

	if ( pic_size == null )
	    return;

	double scale_w, scale_h;
	scale_w = c_w / pic_size.getWidth();
	scale_h = c_h / pic_size.getHeight();

	scaleFactor = Math.min( scale_w, scale_h );

	double x = ((double)getContentPane().getWidth()) / scaleFactor;
	x -= pic_size.getWidth();
	x /= 2.0;
	double y = ((double)getContentPane().getHeight()) / scaleFactor;
	y -= pic_size.getHeight();
	y /= 2.0;

	pic_size.setLocation( (int)x, (int)y );
    }

    public void paint(Graphics g) {
        if ( !isMousePressLNow )
	    super.paint(g);

	g.translate( F_EDGE, F_TOP );
	((Graphics2D)g).scale(scaleFactor, scaleFactor);

	if ( commander.getPicture() != null )
	    g.drawImage(
		commander.getPicture(),
		(int)pic_size.getX(), (int)pic_size.getY(),
		(int)pic_size.getWidth(), (int)pic_size.getHeight(), this);

	int x=0, y=0, w=0, h=0;
	// 元絵の最外径
	if ( pic_size != null ) {
	    g.setColor(Color.red);
	    x = (int)pic_size.getX();
	    y = (int)pic_size.getY();
	    w = (int)pic_size.getWidth();
	    h = (int)pic_size.getHeight();
	    g.drawRect(x,y,w-1,h-1);
	}

	// edge
	if ( edge_size != null ) {
	    g.setColor(Color.red);
	    int xx = x + (int)edge_size.getX();
	    int yy = y + (int)edge_size.getY();
	    w = (int)edge_size.getWidth();
	    h = (int)edge_size.getHeight();
	    g.drawRect(xx,yy,w-1,h-1);
	}

	// トリム枠
	if ( trim_size != null ) {
	    g.setColor(Color.blue);
	    int xx = x + (int)trim_size.getX();
	    int yy = y + (int)trim_size.getY();
	    w = (int)trim_size.getWidth();
	    h = (int)trim_size.getHeight();
	    g.drawRect(xx,yy,w-1,h-1);
	}
/****
	((Graphics2D)g).scale( 1.0, 1.0 );
	// 元絵の最外径
	g.setColor(Color.red);
	int x = (int)(pic_size.getX() * scaleFactor);
	int y = (int)(pic_size.getY() * scaleFactor);
	int w = (int)(pic_size.getWidth() * scaleFactor);
	int h = (int)(pic_size.getHeight() * scaleFactor);
	g.drawRect(x,y,w-1,h-1);
	g.drawRect(x+1,y+1,w-1-2,h-1-2);

	// edge
	if ( edge_size != null ) {
	    g.setColor(Color.red);
	    int xx = x + (int)(edge_size.getX() * scaleFactor);
	    int yy = y + (int)(edge_size.getY() * scaleFactor);
	    w = (int)(edge_size.getWidth() * scaleFactor);
	    h = (int)(edge_size.getHeight() * scaleFactor);
	    g.drawRect(xx,yy,w-1,h-1);
	    g.drawRect(xx+1,yy+1,w-1-2,h-1-2);
	}

	// トリム枠
	if ( trim_size != null ) {
	    g.setColor(Color.blue);
	    int xx = x + (int)(trim_size.getX() * scaleFactor);
	    int yy = y + (int)(trim_size.getY() * scaleFactor);
	    w = (int)(trim_size.getWidth() * scaleFactor);
	    h = (int)(trim_size.getHeight() * scaleFactor);
	    g.drawRect(xx,yy,w-1,h-1);
	    g.drawRect(xx+1,yy+1,w-1-2,h-1-2);
	}
****/
    }


    public static void main(String args[]) {
	new Main();
    }


    public void componentHidden(ComponentEvent e) {}
    public void componentShown(ComponentEvent e) {}
    public void componentMoved(ComponentEvent e) {}
    public void componentResized(ComponentEvent e) {
	changeZoomFactor();
    }


    // MouseListener interface.
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e)  {}
    public void mouseClicked(MouseEvent e) {}
    // 絵の実サイズで処理
    public void mousePressed(MouseEvent e) {
        if ( (e.getModifiers() & InputEvent.BUTTON1_MASK) != 0 ) {
	    if ( trim_size == null )
		return;

	    Point p = e.getPoint();
	    int px = (int)(((double)p.x-F_EDGE) / scaleFactor);
	    int py = (int)(((double)p.y-F_TOP) / scaleFactor);

	    int x = (int)(pic_size.getX() + trim_size.getX());
	    int y = (int)(pic_size.getY() + trim_size.getY());
	    int w = (int)trim_size.getWidth();
	    int h = (int)trim_size.getHeight();
	    if ( px < x || (x+w) <= px )
		return;
	    if ( py < y || (y+h) <= py )
		return;

	    drag_offset_x = px - x;
	    drag_offset_y = py - y;
	    isMousePressLNow = true;

	} else if ( (e.getModifiers() & InputEvent.BUTTON3_MASK) != 0 ) {
            commander.show(e.getPoint());
	}
    }

    public void mouseReleased(MouseEvent e) {
	isMousePressLNow = false;
        /// repaint();
    }

    
    // MouseMotionListener interface.
    public void mouseMoved(MouseEvent e) {}
    // 絵の実サイズで処理
    // XOR 描画がどうもうまく行かないが最近のPCは早いので、、m(_ _)m
    public void mouseDragged(MouseEvent e) {    	
        if ( !isMousePressLNow || edge_size == null || trim_size == null )
            return;

	Point p = e.getPoint();
	int point_x = (int)(((double)p.x-F_EDGE) / scaleFactor);
	int point_y = (int)(((double)p.y-F_TOP) / scaleFactor);
	int pic_x = (int)pic_size.getX();
	int pic_y = (int)pic_size.getY();
	int t_new_x =  point_x - pic_x - drag_offset_x;
	int t_new_y =  point_y - pic_y - drag_offset_y;
	int t_w = (int)trim_size.getWidth();
	int t_h = (int)trim_size.getHeight();

	int edge_x = (int)edge_size.getX();
	int edge_y = (int)edge_size.getY();
	int edge_w = (int)edge_size.getWidth();
	int edge_h = (int)edge_size.getHeight();

	if ( t_new_x < edge_x ) {
	    t_new_x = edge_x;
	} else if ( (edge_x+edge_w) <= (t_new_x+t_w) ) {
	    t_new_x = edge_x + (edge_w - t_w);
	}

	if ( t_new_y < edge_y ) {
	    t_new_y = edge_y;
	} else if ( (edge_y+edge_h) <= (t_new_y+t_h) ) {
	    t_new_y = edge_y + (edge_h - t_h);
	}

	trim_size.setLocation( t_new_x, t_new_y );
        repaint();
    }
}
