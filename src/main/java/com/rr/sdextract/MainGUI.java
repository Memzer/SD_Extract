package com.rr.sdextract;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileSystemView;

public class MainGUI {

	private static String url = "https://www.scribd.com/doc/295852051/Claude-Gordon-Systamatic-Approach-to-Daily-Practice";
	private static final String version = "v0.0.1";
	
	private static final int middleColumnBias = 5;
	
	private final JFileChooser fc = new JFileChooser();
	
	private JTextField outputDirField;
	private JTextField urlField;
	private JButton browseButton;
	private JButton startButton;
	private JProgressBar scanBar = new JProgressBar();
	private JProgressBar imgBar = new JProgressBar();
	private JProgressBar pdfBar = new JProgressBar();
	private JLabel scanText = new JLabel();
	private JLabel imgText = new JLabel();
	private JLabel pdfText = new JLabel();
	
	private ScribdPage scribd = new ScribdPage(this);
	
	public static void main(String[] args){
        MainGUI mainGui = new MainGUI();
        mainGui.init();
    }
	
	public void init() {
    	SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run() {
                createAndShowGUI();           
            }
        });
	}
	
	private void createAndShowGUI(){
		final JFrame frame = new JFrame("SCRIBD Downloader "+version);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setAlwaysOnTop(true);
        frame.setLayout(new GridLayout(1, 1));
        
        JPanel pane = new JPanel();
        pane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pane.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints(); 
        c.fill = GridBagConstraints.HORIZONTAL;
        frame.add(pane);        
        
        c.gridy = 0;
        c.gridx = 0;
        c.weightx = 1;
        pane.add(new JLabel("Output Directory:"),c);
        
        c.gridy = 0;
        c.gridx = 1;
        c.weightx = middleColumnBias;
        String path = FileSystemView.getFileSystemView().getDefaultDirectory().getPath();
        outputDirField = new JTextField(path);        
        pane.add(outputDirField,c);
        
        c.gridy = 0;
        c.gridx = 2;
        c.weightx = 1;
        browseButton = new JButton("Browse...");
        browseButton.addActionListener(new BrowseButtonAction());
        pane.add(browseButton,c);        
        
        c.gridy = 1;
        c.gridx = 0;
        c.weightx = 1;
        pane.add(new JLabel("URL:"),c);
        
        c.gridy = 1;
        c.gridx = 1;
        c.weightx = middleColumnBias;
        urlField = new JTextField();        
        pane.add(urlField,c);
        
        c.gridy = 1;
        c.gridx = 2;
        c.weightx = 1;
        startButton = new JButton("Start");
        startButton.addActionListener(new StartButtonAction());
        pane.add(startButton,c);
        
        c.gridy = 2;
        c.gridx = 0;
        c.weightx = 1;
        pane.add(new JLabel("Scan:"),c);
        
        c.gridy = 2;
        c.gridx = 1;
        c.weightx = middleColumnBias;
        pane.add(scanBar,c);
        
        c.gridy = 2;
        c.gridx = 2;
        c.weightx = 1;
        pane.add(scanText,c);
        
        c.gridy = 3;
        c.gridx = 0;
        c.weightx = 1;
        pane.add(new JLabel("Images:"),c);
        
        c.gridy = 3;
        c.gridx = 1;
        c.weightx = middleColumnBias;
        pane.add(imgBar,c);
        
        c.gridy = 3;
        c.gridx = 2;
        c.weightx = 1;
        pane.add(imgText,c);
        
        c.gridy = 4;
        c.gridx = 0;
        c.weightx = 1;
        pane.add(new JLabel("PDF:"),c);
        
        c.gridy = 4;
        c.gridx = 1;
        c.weightx = middleColumnBias;
        pane.add(pdfBar,c);
        
        c.gridy = 4;
        c.gridx = 2;
        c.weightx = 1;
        pane.add(pdfText,c);
        
        frame.pack();
        frame.setSize(600, 150);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    	setNativeLookAndFeel();
    	SwingUtilities.updateComponentTreeUI(frame);
	}
	
	private void setNativeLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException e) {
		} catch (ClassNotFoundException e) {
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		}
	}
	
	class BrowseButtonAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnVal = fc.showOpenDialog(browseButton);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File outputDirectory = fc.getSelectedFile();
                outputDirField.setText(outputDirectory.getPath());
            }
        }
    }
	
	class StartButtonAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {        	
        	scribd.setUrl(urlField.getText());
        	scribd.setPath(outputDirField.getText());
        	
        	Thread t = new Thread(){
                public void run(){
                	scribd.parse();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {}
                }
            };
            t.start();
        }
    }

	public JProgressBar getScanBar() {
		return scanBar;
	}

	public JProgressBar getImgBar() {
		return imgBar;
	}

	public JProgressBar getPdfBar() {
		return pdfBar;
	}

	public JLabel getScanText() {
		return scanText;
	}

	public JLabel getImgText() {
		return imgText;
	}

	public JLabel getPdfText() {
		return pdfText;
	}
}
