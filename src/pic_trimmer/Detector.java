// -*- Mode: java -*-
// $Author: kyama $
// Copyright(C) 2021 kyama yamakatsu
//
package pic_trimmer;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;


public class Detector {

    private PropertyWindow property;

    private int edge_u;
    private int edge_b;
    private int edge_l;
    private int edge_r;


    public Detector(PropertyWindow pw) {
	property = pw;
    }


    protected int getEdge_u() { return edge_u; }
    protected int getEdge_b() { return edge_b; }
    protected int getEdge_l() { return edge_l; }
    protected int getEdge_r() { return edge_r; }

    protected void detect_edge(int w, int h, BufferedImage img) {
	int detect_length = property.getDetectLen();

	int ssp = w/2 - detect_length/2; // 25% の位置 scan start point
	int sep = w/2 + detect_length/2; // 辺の半分を走査 scan end point
	int ssl = 0;                     // scan start line
	int sel = h/4;                   // 25% の位置 sacn end line
	edge_u = detect_line(ssp, sep, ssl, sel, true, img);

	ssp = w/2 - detect_length/2;
	sep = w/2 + detect_length/2;
	ssl = h-1;
	sel = h - h/4;
	edge_b = detect_line(ssp, sep, ssl, sel, true, img);

	ssp = h/2 - detect_length/2;
	sep = h/2 + detect_length/2;
	ssl = 0;
	sel = w/4;
	edge_l = detect_line(ssp, sep, ssl, sel, false, img);
	
	ssp = h/2 - detect_length/2;
	sep = h/2 + detect_length/2;
	ssl = w-1;
	sel = w - w/4;
	edge_r = detect_line(ssp, sep, ssl, sel, false, img);
    }

    protected int detect_line(
	int ssp, int sep, int ssl, int sel,
	boolean scan_horizontal, BufferedImage img) {

	int noise_mask_filter = property.getNoiseMask();
	Graphics g = img.getGraphics();
	int bpixel;   // 検査の base pixel 値
	if ( scan_horizontal )
	    bpixel = img.getRGB(ssp, ssl);
	else
	    bpixel = img.getRGB(ssl, ssp);
	bpixel &= noise_mask_filter;

	int detect_line = ssl;
	int line = ssl;
	while( true ) {
	    int point = ssp;
	    while( true ) {
		int x = point;
		int y = line;
		if ( !scan_horizontal ) {
		    x = line;
		    y = point;
		}
		int pixel = img.getRGB(x, y);
		pixel &= noise_mask_filter;
		if ( bpixel != pixel )
		    break;
		if ( point == sep ) {
		    detect_line = line;
		    g.setColor(Color.red);
		    if ( scan_horizontal )
			g.drawLine(ssp, detect_line, sep, detect_line);
		    else
			g.drawLine(detect_line, ssp, detect_line, sep);
		    break;
		}
		++point;
	    }
	    if ( ssl < sel ) {
		if ( line > sel )
		    break;
		++line;
	    } else {
		if ( line < sel )
		    break;
		--line;
	    }
	}
	return detect_line;
    }
}
