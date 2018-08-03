package com.rr.sdextract;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.filechooser.FileSystemView;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.property.AreaBreakType;

public class PdfGenerator {
	
	private File output;
	private List<String> imgUrls;
	private MainGUI mainGui;
	
	public PdfGenerator(MainGUI mainGui, File output, List<String> imgUrls) {
		super();
		this.mainGui = mainGui;
		this.output = output;
		this.imgUrls = imgUrls;
	}

	public void create() {
		PdfWriter writer;
		try {
			writer = new PdfWriter(output);		
			PdfDocument pdf = new PdfDocument(writer);
			Document document = new Document(pdf);
			document.setMargins(10, 10, 10, 10);
			Iterator<String> urlsIter = imgUrls.iterator();
			int n=1;
			DecimalFormat df = new DecimalFormat("0.00");
			while(urlsIter.hasNext()) {
				String url = urlsIter.next();
				try {
					Image image = new Image(ImageDataFactory.create(new URL(url)));
					document.add(image);
					if(urlsIter.hasNext()) {
						document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
				mainGui.getPdfBar().setValue(n);
				mainGui.getPdfText().setText(df.format(100*n/mainGui.getPdfBar().getMaximum())+"%");
				n++;
			}
			mainGui.getPdfText().setText("Done!");
			document.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
}
