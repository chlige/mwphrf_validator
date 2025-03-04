/**
 * 
 */
package com.walnutcs.mwphrf;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

import javax.help.CSH;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableRowSorter;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;

import com.walnutcs.mwphrf.phrf.PHRFBoatEntry;
import com.walnutcs.mwphrf.phrf.PHRFCertificate;
import com.walnutcs.mwphrf.phrf.PHRFCertificateValues.PHRFValue;
import com.walnutcs.mwphrf.phrf.PHRFCertificateValues.PHRFVariable;
import com.walnutcs.mwphrf.yachtscoring.LoadYachtScoringDialog;
import com.walnutcs.mwphrf.yachtscoring.YSEntry;
import com.walnutcs.mwphrf.yachtscoring.YSEvent;

/**
 * @author George Chlipala
 *
 */
public class MWPHRFValidator {

	private JFrame frame;
	
	private JTable entryTable;
	private TableRowSorter<BoatList> entrySorter;
	private JProgressBar progressBar;	
	private JLabel statusText;
	
	private JMenu mwphrfMenu;
	
	private Preferences prefs;
	
	private JComboBox<String> circleSelector;
	private JComboBox<String> divSelector;
	private JComboBox<String> classSelector;
	
	private static String HELP_HS = "help/helpset.hs";
	
	private static String[] EXPORT_COLUMN_NAMES = new String[] {
			"Sail Number", "Yacht Name", "Make Model", "Owner", 
			"Circle", "Division", "Class", "PHRF Match", 
			"Certificate Year", "Rating value", "Rating type" 
	};

	// Columns in a Railmeets entries spreadsheet
	private static final String RM_BOATNAME_COL = "Boat Name";
	private static final String RM_OWNERNAME_COL = "Name";
	private static final String RM_POSITION_COL = "Position";
	private static final String RM_SAILNUMBER_COL = "Sail Number";
	private static final String RM_BOATTYPE_COL = "Boat Type";
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		System.setProperty("line.separator", "\r\n");
//		System.setProperty("com.sun.security.enableAIAcaIssuers", "true");
		System.err.println(System.getProperty("javax.net.ssl.trustStore"));
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MWPHRFValidator window = new MWPHRFValidator();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * 
	 */
	public MWPHRFValidator() throws IOException {
		this.prefs = Preferences.userNodeForPackage(this.getClass());
		initialize();
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 800, 550);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("MWPHRF Validator");
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnApp = new JMenu("File");
		menuBar.add(mnApp);
		
		JMenuItem mntmLoad = new JMenuItem("Load entries...");
		mnApp.add(mntmLoad);
		mntmLoad.setAction(new LoadEntriesAction());
		
		mntmLoad = new JMenuItem("Load from YachtScoring...");
		mnApp.add(mntmLoad);
		mntmLoad.setAction(new LoadYachtScoringAction());
		
		JMenuItem mntmExport = new JMenuItem("Save displayed entries...");
		mnApp.add(mntmExport);
		mntmExport.setAction(new SaveEntriesAction(true));

		JMenuItem mntmExportAll = new JMenuItem("Save all entries...");
		mnApp.add(mntmExportAll);
		mntmExportAll.setAction(new SaveEntriesAction(false));

		JMenuItem mntmQuit = new JMenuItem("Quit");
		mnApp.add(mntmQuit);
		mntmQuit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}	
		});	
		
		mnApp = new JMenu("Ratings");
		menuBar.add(mnApp);
		
		this.mwphrfMenu = new JMenu("MWPRHF");

		mwphrfMenu.add(new JMenuItem(new LoadRatingsAction()));
		mwphrfMenu.add(new JMenuItem(new ValueSelectionAction()));

		JMenuItem mntmAnalyze = new JMenuItem("Analyze MWPHRF ToT");
		mwphrfMenu.add(mntmAnalyze);
		mntmAnalyze.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ev) {
				try {
					MWPHRFAnalyzer analyzer = new MWPHRFAnalyzer();
					analyzer.show();
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, e.getLocalizedMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		mnApp.add(mwphrfMenu);
		
		/*
		JMenuItem mntmORC = new JMenuItem("Load ORC Ratings");
		mnApp.add(mntmORC);
		mntmORC.setAction(null);
		*/
			
		mnApp = new JMenu("Help");
		menuBar.add(mnApp);
		
		ClassLoader cl = MWPHRFValidator.class.getClassLoader();  
		HelpSet hs = null;
		URL hsURL = null;
		try {
			hsURL = HelpSet.findHelpSet(cl, HELP_HS);
			if ( hsURL != null ) 
				hs = new HelpSet(null, hsURL);
		} catch (Exception ee) {
			JOptionPane.showMessageDialog(frame, 
					ee.getMessage(),
					"Error loading HelpSet",
					JOptionPane.ERROR_MESSAGE);
			System.err.print(hsURL.toExternalForm());
			ee.printStackTrace();
		}
		
		JMenuItem mntm = new JMenuItem("Help Contents");
		mnApp.add(mntm);
		if ( hs != null ) {
			HelpBroker hb = hs.createHelpBroker();
			mntm.addActionListener(new CSH.DisplayHelpFromSource( hb ));
		}
		
		mntm = new JMenuItem("About");
		mnApp.add(mntm);
		mntm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(frame, 
						"<html><h1>MWPHRF Validator</h1><p>Tool to retrive/validate MWPHRF (https://www.mwphrf.org) ratings for a set of boats</p><br><p>&copy; 2021 George Chlipala</p></html>",
						"About MWPRHF Validator",
						JOptionPane.PLAIN_MESSAGE);
			}
		});
		
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		topPanel.add(panel, BorderLayout.NORTH);

		Box verticalBox = Box.createVerticalBox();
		panel.add(verticalBox);
		
		Box horizontalBox = Box.createHorizontalBox();		
		verticalBox.add(horizontalBox);		
		
		// Circle selector
		horizontalBox.add(new JLabel("Select circle:"));		
		circleSelector = new JComboBox<String>();
		circleSelector.setAction(new SelectCircleAction());
		horizontalBox.add(circleSelector);

		// Division selector
		horizontalBox.add(new JLabel("Select division:"));		
		divSelector = new JComboBox<String>();
		divSelector.setAction(new SelectDivAction());
		horizontalBox.add(divSelector);
		
		// Class selector
		horizontalBox.add(new JLabel("Select class:"));		
		classSelector = new JComboBox<String>();
		classSelector.setAction(new SelectClassAction());
		horizontalBox.add(classSelector);
		
		/*
		// Create a second line for the PHRF functions.
		horizontalBox = Box.createHorizontalBox();		
		verticalBox.add(horizontalBox);		
		
		// Create button to trigger load of ratings from MWPHRF
		JButton loadButton = new JButton("Load ratings from MWPHRF");
		loadButton.setAction(loadRatingsAction);
		horizontalBox.add(loadButton);
				
		horizontalBox.add(new JLabel("Select ratings variable:"));
		JButton hcpButton = new JButton("HCP");
		hcpButton.setAction(new ValueSelectionAction(PHRFVariable.HCP));
		horizontalBox.add(hcpButton);

		JButton dhcpButton = new JButton("DHCP");
		dhcpButton.setAction(new ValueSelectionAction(PHRFVariable.DHCP));
		horizontalBox.add(dhcpButton);
		
		JButton nshcpButton = new JButton("NSHCP");
		nshcpButton.setAction(new ValueSelectionAction(PHRFVariable.NSHCP));
		horizontalBox.add(nshcpButton);
		*/

		// Create the YS entry table
		entryTable = new JTable(BoatList.getInstance());
		entryTable.setShowGrid(true);
		entryTable.setFillsViewportHeight(true);
		entrySorter = new TableRowSorter<BoatList>(BoatList.getInstance());
		entryTable.setRowSorter(entrySorter);
		entryTable.getColumnModel().getColumn(7).setCellEditor(new BoatSelectorCellEditor());
		entryTable.getColumnModel().getColumn(8).setCellEditor(new CertificateSelectorCellEditor());
		entryTable.getColumnModel().getColumn(9).setCellEditor(new CertificateValueSelectorCellEditor());
		
		entryTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent me) {
				if ( me.getClickCount() == 2 ) {
					int row = entryTable.getSelectedRow();
					BoatList boatList = BoatList.getInstance();
					BoatEntry boat = boatList.getBoat(entryTable.convertRowIndexToModel(row));
					PHRFBoatEntry selBoat = boat.getMatchList().getSelectedBoat();
					if ( selBoat != null ) {
						PHRFCertificate selCert = selBoat.getCertList().getSelectedCertificate();
						if ( selCert != null ) {
							try {
								URL pdfURL = new URL("https://mwphrf.org/" + selCert.getURL());
								CertificateView.displayCertificate(pdfURL, 
										selBoat.toString().concat(String.format(" %d", selCert.getYear())));
							} catch ( IOException e ) {
								JOptionPane.showMessageDialog(frame, "Unable to retrieve certificate PDF!\n" + e.getLocalizedMessage(), 
										"ERROR", JOptionPane.ERROR_MESSAGE);
								e.printStackTrace();
							}
							
						}
					}
				}
			}
		});
		
		// Create scroll pane for table
		JScrollPane scrollPane = new JScrollPane(entryTable);
		topPanel.add(scrollPane, BorderLayout.CENTER);

		// Add pane for status bar
		panel = new JPanel();
		panel.setMinimumSize(new Dimension(10,25));
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
		panel.add(Box.createHorizontalGlue());
		topPanel.add(panel, BorderLayout.SOUTH);
		
		// Add status text area
		statusText = new JLabel();
		panel.add(statusText);

		// Add progress bar
		progressBar = new JProgressBar(0,1);
		progressBar.setPreferredSize(new Dimension(50, progressBar.getHeight()));
//		progressBar.setMaximumSize(progressBar.getPreferredSize());
		progressBar.setValue(0);
		panel.add(progressBar);
		
		// Create bottom pane
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BorderLayout(0, 0));
				
			
		// Add the ratings TreeTable
//		ratingsTable = new JTreeTable(PHRFList.getInstance());
//		ratingsTable = new JTable(PHRFList.getInstance());
//		ratingsTable.setShowGrid(true);
//		ratingsTable.setFillsViewportHeight(true);
//		ratingsTable.getTree().setRootVisible(false);
		
//		scrollPane = new JScrollPane(ratingsTable);
//		bottomPanel.add(scrollPane, BorderLayout.CENTER);
/*		
		// Create split pane and the top and bottom panels.
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setTopComponent(topPanel);
		splitPane.setBottomComponent(bottomPanel);
		splitPane.setDividerLocation(0.6);
*/
		frame.getContentPane().add(topPanel, BorderLayout.CENTER);

	}

	/**
	 * Load entries from a YachtScoring spreadsheet
	 * 
	 * @param entryFile File object for YachtScoring spreadsheet
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void loadEntries(File entryFile) throws FileNotFoundException, IOException {
		HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(entryFile));
		HSSFSheet sheet = wb.getSheetAt(0);
		
		statusText.setText("Loading YachtScoring entries");
		
		// Setup variables for columns in the spreadsheet.
		int YS_YACHTNAME_COL = -1;
		int YS_SAILNUMBER_COL_A = -1;
		int YS_SAILNUMBER_COL_B = -1;
		int YS_OWNER_FIRST_COL = -1;
		int YS_OWNER_LAST_COL = -1;
		int YS_MAKE_MODEL_COL = -1;
		int YS_CIRCLE_COL = -1;
		int YS_DIV_COL = -1;
		int YS_CLASS_COL = -1;

		// Get the first row (header row) and find the colums for each of the headers
		HSSFRow headerRow = sheet.getRow(0);
		int cellCount = headerRow.getPhysicalNumberOfCells();

		for ( int c = 0; c < cellCount; c++ ) {
			String value = headerRow.getCell(c).getStringCellValue();
			switch (value) {
			case "YACHT_SAIL_1": YS_SAILNUMBER_COL_A = c; break;
			case "YACHT_SAIL_2": YS_SAILNUMBER_COL_B = c; break;
			case "YACHT_NAME" : YS_YACHTNAME_COL = c; break;
			case "OWNER_FIRST": YS_OWNER_FIRST_COL = c; break;
			case "OWNER_LAST": YS_OWNER_LAST_COL = c; break;
			case "YACHT_MAKE": YS_MAKE_MODEL_COL = c; break;
			case "RACING_CIRCLE" : YS_CIRCLE_COL = c; break;
			case "DIVISION" : YS_DIV_COL = c; break;
			case "CLASS_ALT_NAME": YS_CLASS_COL = c; break;
			}
		}
		
		int rowCount = sheet.getPhysicalNumberOfRows();	
		
		BoatList entryList = BoatList.getInstance();
		entryList.clear();
		
		progressBar.setMaximum(rowCount - 1);
		
		for ( int r = 1; r < rowCount; r++ ) {
			HSSFRow row = sheet.getRow(r);
			
			HSSFCell sailNumberCell = row.getCell(YS_SAILNUMBER_COL_B);
			
			String sailNumber = sailNumberCell.getCellType() == CellType.NUMERIC ?						
					String.format("%.0f", sailNumberCell.getNumericCellValue()) :
					sailNumberCell.getStringCellValue();
					
			String yachtName = row.getCell(YS_YACHTNAME_COL).getStringCellValue();
			String makeModel = row.getCell(YS_MAKE_MODEL_COL).getStringCellValue();
			String ownerName = row.getCell(YS_OWNER_FIRST_COL).getStringCellValue().concat(" ").concat(row.getCell(YS_OWNER_LAST_COL).getStringCellValue());
			String racingCircle = row.getCell(YS_CIRCLE_COL).getStringCellValue();
			String racingDivision = row.getCell(YS_DIV_COL).getStringCellValue();
			String racingClass = row.getCell(YS_CLASS_COL).getStringCellValue();
			
			BoatEntry entry = new BoatEntry(yachtName, sailNumber, makeModel, ownerName, racingCircle, racingDivision, racingClass);
			entryList.addBoat(entry);
			progressBar.setValue(r - 1);
		}
		
		// Setup the selectors for filtering the table.
		circleSelector.removeAllItems();
		circleSelector.addItem("Select...");
		for ( String circle : entryList.getCircles() ) {
			circleSelector.addItem(circle);
		}
		divSelector.removeAllItems();
		classSelector.removeAllItems();

		// Set the final status
		statusText.setText("YachtScoring entries loaded");
		progressBar.setValue(0);
	}
	
	/**
	 * Load entries from a YachtScoring event (online)
	 * 
	 * @param event YSEvent object from YachtScoring
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws URISyntaxException 
	 */
	private void loadYachtScoringEntries(YSEvent event) throws IOException, URISyntaxException {
		statusText.setText("Loading YachtScoring entries");
		
		BoatList entryList = BoatList.getInstance();
		entryList.clear();

		progressBar.setIndeterminate(true);

		List<YSEntry> entries = event.getEntries();
		
		for ( YSEntry entry : entries ) {
			entryList.addBoat(entry);
		}
		
		// Setup the selectors for filtering the table.
		circleSelector.removeAllItems();
		circleSelector.addItem("Select...");
		for ( String circle : entryList.getCircles() ) {
			circleSelector.addItem(circle);
		}
		divSelector.removeAllItems();
		classSelector.removeAllItems();

		// Set the final status
		statusText.setText("YachtScoring entries loaded");
		progressBar.setIndeterminate(false);
		progressBar.setValue(0);
	}
	
	/**
	 * Load entries from a Railmeets entry spreadsheet
	 * 
	 * @param entryFile File object for Railmeets spreadsheet
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void loadRailmeetEntries(File aFile) throws FileNotFoundException, IOException {
		Reader fileIn = new FileReader(aFile);
		List<CSVRecord> records = CSVFormat.DEFAULT.builder()
				.setHeader()
				.setSkipHeaderRecord(true)
				.build()
				.parse(fileIn)
				.getRecords();
		
		statusText.setText("Loading Railmeets entries");
		
		BoatList entryList = BoatList.getInstance();
		entryList.clear();
		
		progressBar.setMaximum(records.size() - 1);
		
		for ( int r = 0; r < records.size(); r++  ) {
			CSVRecord row = records.get(r);
			if ( row.get(RM_POSITION_COL).equalsIgnoreCase("skipper") ) {
				BoatEntry entry = new BoatEntry(row.get(RM_BOATNAME_COL), 
						row.get(RM_SAILNUMBER_COL), 
						row.get(RM_BOATTYPE_COL), 
						row.get(RM_OWNERNAME_COL), "NONE", "NONE", "NONE");
				entryList.addBoat(entry);				
			}
			progressBar.setValue(r - 1);
		}
		
	}
	
	private void saveEntries(File outputFile, FileFilter filter, boolean saveVisible) throws IOException {
		String fileExt = "tsv";
		
		int i = outputFile.getName().lastIndexOf('.');
		if (i > 0) {
		    fileExt = outputFile.getName().substring(i+1).toLowerCase();
		} else {
			if ( filter instanceof FileNameExtensionFilter ) {
				FileNameExtensionFilter fnFilter = (FileNameExtensionFilter) filter;
				fileExt = fnFilter.getExtensions()[0];
				outputFile = new File(outputFile.getAbsolutePath().concat(".").concat(fileExt));
			}			
		}

		switch ( fileExt ) {
		case "xls":
			this.saveEntriesXLS(outputFile, saveVisible); break;
		case "csv":
			this.saveEntriesText(outputFile, saveVisible, ","); break;
		case "tsv":
		case "txt":
			this.saveEntriesText(outputFile, saveVisible, "\t"); break;
		default:
			// Need to throw error log.
			JOptionPane.showMessageDialog(this.frame, "File type not recognized for file ".
						concat(outputFile.getName()).
						concat("\nRecognized file types are tsv, txt, csv, and xls"), 
					"Invalid file type", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void saveEntriesText(File outputFile, boolean saveVisible, String sep) throws IOException {	
		BoatList boatList = BoatList.getInstance();	
		PrintWriter writer = new PrintWriter(outputFile);

		writer.println(Arrays.asList(EXPORT_COLUMN_NAMES).stream().collect(Collectors.joining(sep)));
		
		boolean escapeValues = ! sep.equalsIgnoreCase("\t");
		
		int maxCount = saveVisible ? entryTable.getRowCount() : boatList.getRowCount();
		
		progressBar.setMaximum(maxCount);

		for ( int row = 0; row < maxCount ; row++ ) {
			progressBar.setValue(row);

			BoatEntry entry = boatList.getBoat(saveVisible ? entryTable.convertRowIndexToModel(row) : row);
			ArrayList<String> rowData = new ArrayList<String>(EXPORT_COLUMN_NAMES.length);
			
			rowData.add(entry.getSailNumber());
			rowData.add(entry.getYachtName());
			rowData.add(entry.getMakeModel());
			rowData.add(entry.getOwnerName());
			rowData.add(entry.getRacingCircle());
			rowData.add(entry.getRacingDivision());
			rowData.add(entry.getRacingClass());
			
			PHRFBoatEntry selBoat = entry.getMatchList().getSelectedBoat();
			if ( selBoat != null ) {
				rowData.add(selBoat.toString());
				PHRFCertificate selCert = selBoat.getCertList().getSelectedCertificate();
				if ( selCert != null ) {
					rowData.add(selCert.toString());
					PHRFValue value = selCert.getValues().getSelectedValue();
					if ( value != null ) {
						rowData.add(String.format("%d", value.getValue().intValue()));
						rowData.add(value.getVariable().toString());
					} else {
						rowData.add("");
						rowData.add("");
					}
				} else {
					rowData.add("");
					rowData.add("");
					rowData.add("");
				}
			} else {
				rowData.add("");
				rowData.add("");
				rowData.add("");
				rowData.add("");
			}
			
			if ( escapeValues )
				writer.println(rowData.stream().map(temp ->{
					return escapeSpecialCharacters(temp, sep);
				}).collect(Collectors.joining(sep)));
			else 
				writer.println(rowData.stream().collect(Collectors.joining(sep)));
		}
		writer.flush();
		writer.close();
	}
	
	public String escapeSpecialCharacters(String data, String sep) {
	    String escapedData = data.replaceAll("\\R", " ");
	    if (data.contains(sep) || data.contains("\"") || data.contains("'")) {
	        data = data.replace("\"", "\"\"");
	        escapedData = "\"" + data + "\"";
	    }
	    return escapedData;
	}

	private void saveEntriesXLS(File outputFile, boolean saveVisible) throws IOException {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("Entries");
		
		HSSFRow sheetRow = sheet.createRow(0);
		
		for ( int c = 0 ; c < EXPORT_COLUMN_NAMES.length; c++ ) {
			HSSFCell cell = sheetRow.createCell(c, CellType.STRING);
			cell.setCellValue(EXPORT_COLUMN_NAMES[c]);
		}
		
		BoatList boatList = BoatList.getInstance();		
		int maxCount = saveVisible ? entryTable.getRowCount() : boatList.getRowCount();
		
		progressBar.setMaximum(maxCount);

		for ( int row = 0; row < maxCount ; row++ ) {
			progressBar.setValue(row);
			sheetRow = sheet.createRow(row + 1);
			
			BoatEntry entry = boatList.getBoat(saveVisible ? entryTable.convertRowIndexToModel(row) : row);
			
			sheetRow.createCell(0, CellType.STRING).setCellValue(entry.getSailNumber());
			sheetRow.createCell(1, CellType.STRING).setCellValue(entry.getYachtName());
			sheetRow.createCell(2, CellType.STRING).setCellValue(entry.getMakeModel());
			sheetRow.createCell(3, CellType.STRING).setCellValue(entry.getOwnerName());
			sheetRow.createCell(4, CellType.STRING).setCellValue(entry.getRacingCircle());
			sheetRow.createCell(5, CellType.STRING).setCellValue(entry.getRacingDivision());
			sheetRow.createCell(6, CellType.STRING).setCellValue(entry.getRacingClass());
						
			PHRFBoatEntry selBoat = entry.getMatchList().getSelectedBoat();
			if ( selBoat != null ) {
				sheetRow.createCell(7, CellType.STRING).setCellValue(selBoat.toString());
				PHRFCertificate selCert = selBoat.getCertList().getSelectedCertificate();
				if ( selCert != null ) {
					sheetRow.createCell(8, CellType.NUMERIC).setCellValue(selCert.getYear());
					PHRFValue value = selCert.getValues().getSelectedValue();
					if ( value != null ) {
						sheetRow.createCell(9, CellType.NUMERIC).setCellValue(value.getValue().intValue());
						sheetRow.createCell(10, CellType.STRING).setCellValue(value.getVariable().toString());
					}
				}
			}
		}
		for ( int c = 0 ; c < 11; c++ ) {
			sheet.autoSizeColumn(c);
		}
		wb.write(outputFile);
		wb.close();
	}
	
	
	/**
	 * Action to to load Entries from a YachtScoring file.
	 * 
	 * @author George Chlipala
	 */
	private class LoadEntriesAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public LoadEntriesAction() {
			putValue(NAME, "Load entries...");
			putValue(SHORT_DESCRIPTION, "Load YachtScoring entries");
		}
		public void actionPerformed(ActionEvent e) {
			JFileChooser fileChooser = new JFileChooser();
			String wd = prefs.get("last_path", System.getProperty("user.home"));
			fileChooser.setCurrentDirectory(new File(wd));
			FileNameExtensionFilter ysFilter = new FileNameExtensionFilter("Excel file (YachtScoring)", "xls");
			fileChooser.setFileFilter(ysFilter);
			FileNameExtensionFilter rmFilter = new FileNameExtensionFilter("CSV file (Railmeets)", "csv");
			fileChooser.addChoosableFileFilter(rmFilter);
			int retVal = fileChooser.showOpenDialog(frame);
			if ( retVal == JFileChooser.APPROVE_OPTION ) {
				try {
					File selFile = fileChooser.getSelectedFile();
					prefs.put("last_path", selFile.getParent());
					FileFilter selFilter = fileChooser.getFileFilter();
					if ( selFilter.getDescription().equalsIgnoreCase("CSV file (Railmeets)") ) {
						loadRailmeetEntries(selFile);
					} else {
						loadEntries(selFile);						
					}
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(frame, e1.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Action to to load Entries from a YachtScoring, directly.
	 * 
	 * @author George Chlipala
	 */
	private class LoadYachtScoringAction extends AbstractAction {
		/**
		 * 
		 */
		
		private static final long serialVersionUID = 1L;
		public LoadYachtScoringAction() {
			putValue(NAME, "Load from YachtScoring...");
			putValue(SHORT_DESCRIPTION, "Load from YachtScoring");		
			
		}
		
		public void actionPerformed(ActionEvent e) {
			YSEvent selectedEvent = LoadYachtScoringDialog.showSelectionDialog(frame, "Select Event");
			if ( selectedEvent != null ) {
				try {
					loadYachtScoringEntries(selectedEvent);
				} catch (IOException | URISyntaxException e1) {
					statusText.setText("");
					JOptionPane.showMessageDialog(frame, e1.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Action to Save Entries to a XLS, CSV or TSV file. 
	 */
	private class SaveEntriesAction extends AbstractAction {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private boolean saveVisible = false;
		
		public SaveEntriesAction(boolean saveVisible) {
			putValue(NAME, saveVisible ? "Export visible entries..." : "Save all entries...");
			putValue(SHORT_DESCRIPTION, "Export entries with matched ratings");
			this.saveVisible = saveVisible;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser fileChooser = new JFileChooser();
			String wd = prefs.get("last_path", System.getProperty("user.home"));
			fileChooser.setCurrentDirectory(new File(wd));
			
			// Add xls file filter
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel file", "xls");
			fileChooser.addChoosableFileFilter(filter);		
			
			// Add tsv file filter
			filter = new FileNameExtensionFilter("Tab-delimited file", "tsv", "txt");
			fileChooser.addChoosableFileFilter(filter);		

			// Add csv file filter
			filter = new FileNameExtensionFilter("CSV file", "csv");
			fileChooser.addChoosableFileFilter(filter);		
			
			int retVal = fileChooser.showSaveDialog(frame);
			if ( retVal == JFileChooser.APPROVE_OPTION ) {
				File selFile = fileChooser.getSelectedFile();
				prefs.put("last_path", selFile.getParent());
				SwingWorker saveEntries = new SwingWorker() {
					@Override
					protected Object doInBackground() throws Exception {
						try {
							statusText.setText("Saving entries to file.");
							saveEntries(selFile, fileChooser.getFileFilter(), saveVisible);
							statusText.setText("Entries saved to file.");
						} catch (IOException e1) {
							statusText.setText("");
							JOptionPane.showMessageDialog(frame, e1.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
							e1.printStackTrace();
						}
						progressBar.setValue(0);
						return selFile;
					}
					
				};
				saveEntries.execute();
			}
		}	
	}

	/**
	 * Action called by JComboBox for the circle.
	 */
	private class SelectCircleAction extends AbstractAction {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public SelectCircleAction() { 
			putValue(NAME, "Select racing circle...");
			putValue(SHORT_DESCRIPTION, "Select a racing circle.");
		}
		
		public void actionPerformed(ActionEvent e) {
			divSelector.removeAllItems();
			classSelector.removeAllItems();
			
			BoatList boatList = BoatList.getInstance();
			
			if ( circleSelector.getSelectedIndex() > 0 ) {
				String selCircle = (String) circleSelector.getSelectedItem();
				CircleRowFilter<BoatList, Integer> rowFilter = new CircleRowFilter<BoatList, Integer>(selCircle);
				entrySorter.setRowFilter(rowFilter);	
				divSelector.addItem("Select...");
				for ( String div : boatList.getDivisions(selCircle) )  {
					divSelector.addItem(div);
				}
			} else {
				entrySorter.setRowFilter(null);
			}
		}
	}
	
	/**
	 * Row filter for the starting circle.
	 * 
	 * @param <M>
	 * @param <I>
	 */
	private class CircleRowFilter<M extends BoatList, I extends Integer> extends RowFilter<M,I> {

		private String selCircle = null;
		
		public CircleRowFilter(String selCircle) {
			this.selCircle = selCircle;
		}
		
		@Override
		public boolean include(Entry<? extends M, ? extends I> entry) {
			BoatList myList = entry.getModel();
			BoatEntry thisBoat = myList.getBoat(entry.getIdentifier());
			return thisBoat.getRacingCircle().equalsIgnoreCase(this.selCircle);
		}
		
	}
	
	/**
	 * Action called by JComboBox for divisions
	 */
	private class SelectDivAction extends AbstractAction {
		
		private static final long serialVersionUID = 1L;

		public SelectDivAction() { 
			putValue(NAME, "Select racing division...");
			putValue(SHORT_DESCRIPTION, "Select a racing division.");
		}
		
		public void actionPerformed(ActionEvent e) {
			classSelector.removeAllItems();
			String selCircle = (String) circleSelector.getSelectedItem();
			
			BoatList boatList = BoatList.getInstance();
			
			if ( divSelector.getSelectedIndex() > 0 ) {
				String selDiv = (String) divSelector.getSelectedItem();
				DivRowFilter<BoatList, Integer> rowFilter = new DivRowFilter<BoatList, Integer>(selCircle, selDiv);
				entrySorter.setRowFilter(rowFilter);	
				classSelector.addItem("Select...");
				for ( String racingClass : boatList.getClasses(selCircle, selDiv) )  {
					classSelector.addItem(racingClass);
				}
			} else {
				CircleRowFilter<BoatList, Integer> rowFilter = new CircleRowFilter<BoatList, Integer>(selCircle);
				entrySorter.setRowFilter(rowFilter);	
			}
		}
	}
	
	/**
	 * Row filter for the division
	 * 
	 * @param <M>
	 * @param <I>
	 */
	private class DivRowFilter<M extends BoatList, I extends Integer> extends RowFilter<M,I> {

		private String selCircle = null;
		private String selDiv = null;
		
		public DivRowFilter(String selCircle, String selDiv) {
			this.selCircle = selCircle;
			this.selDiv = selDiv;
		}
		
		@Override
		public boolean include(Entry<? extends M, ? extends I> entry) {
			BoatList myList = entry.getModel();
			BoatEntry thisBoat = myList.getBoat(entry.getIdentifier());
			return thisBoat.getRacingCircle().equalsIgnoreCase(this.selCircle) &&
					thisBoat.getRacingDivision().equalsIgnoreCase(this.selDiv);
		}
		
	}

	/**
	 * Action for JComboxBox for the class
	 */
	private class SelectClassAction extends AbstractAction {
		
		private static final long serialVersionUID = 1L;

		public SelectClassAction() { 
			putValue(NAME, "Select racing class...");
			putValue(SHORT_DESCRIPTION, "Select a racing class.");
		}
		
		public void actionPerformed(ActionEvent e) {
			String selCircle = (String) circleSelector.getSelectedItem();
			String selDiv = (String) divSelector.getSelectedItem();
			
			if ( classSelector.getSelectedIndex() > 0 ) {
				String selClass = (String) classSelector.getSelectedItem();
				ClassRowFilter<BoatList, Integer> rowFilter = new ClassRowFilter<BoatList, Integer>(selCircle, selDiv, selClass);
				entrySorter.setRowFilter(rowFilter);	
			} else {
				DivRowFilter<BoatList, Integer> rowFilter = new DivRowFilter<BoatList, Integer>(selCircle, selDiv);
				entrySorter.setRowFilter(rowFilter);	
			}
		}
	}
	
	/**
	 * Row filter for the class
	 * 
	 * @param <M>
	 * @param <I>
	 */
	private class ClassRowFilter<M extends BoatList, I extends Integer> extends RowFilter<M,I> {

		private String selCircle = null;
		private String selDiv = null;
		private String selClass = null;
		
		public ClassRowFilter(String selCircle, String selDiv, String selClass) {
			this.selCircle = selCircle;
			this.selDiv = selDiv;
			this.selClass = selClass;
		}
		
		@Override
		public boolean include(Entry<? extends M, ? extends I> entry) {
			BoatList myList = entry.getModel();
			BoatEntry thisBoat = myList.getBoat(entry.getIdentifier());
			return thisBoat.getRacingCircle().equalsIgnoreCase(this.selCircle) &&
					thisBoat.getRacingDivision().equalsIgnoreCase(this.selDiv) &&
					thisBoat.getRacingClass().equalsIgnoreCase(this.selClass);
		}
		
	}
	
	/**
	 * Action called by "Find Ratings..." menu item for MWPHRF ratings
	 */
	private class LoadRatingsAction extends AbstractAction {

		private static final long serialVersionUID = 1L;
		
		JCheckBox yachtName = new JCheckBox("Yacht Name");
		JCheckBox sailNumber = new JCheckBox("Sail #");
		JCheckBox makeModel = new JCheckBox("Make-Model");
		
		Box dialogBox = Box.createVerticalBox();

		public LoadRatingsAction() { 
			putValue(NAME, "Find Ratings...");
			putValue(SHORT_DESCRIPTION, "Find MWPHRF Ratings");
			
			// Create the layout for the Confirmation Dialog.
			dialogBox.add(new JLabel("Search by:"));
			dialogBox.add(yachtName);
			dialogBox.add(sailNumber);
			dialogBox.add(makeModel);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			
			if ( entryTable.getRowCount() == 0 ) {
				// If there are not any entries in the entryTable, display and error and return.
				JOptionPane.showMessageDialog(null, "You must load entries before trying to find MWPHRF ratings!", "No entries", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			// Show the confirmation dialog
			int response = JOptionPane.showConfirmDialog(null, 
						dialogBox,
						"Load ratings from MWPHRF",
						JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.PLAIN_MESSAGE);
			
			// If it was closed or cancelled, then skip.
			if ( response == JOptionPane.CLOSED_OPTION || response == JOptionPane.CANCEL_OPTION ) {
				return;
			}
	
			progressBar.setMaximum(entryTable.getRowCount());

			Object source = e.getSource();
			
			if ( source instanceof JButton ) {
				JButton thisButton = (JButton)source;
				thisButton.setEnabled(false);				
			}
			
			mwphrfMenu.setEnabled(false);
			
			boolean searchSailNumber = sailNumber.isSelected();
			boolean searchYachtName = yachtName.isSelected();
			boolean searchMakeModel = makeModel.isSelected();
			/* 
			 * Create a SwingWorker to perform the search.  
			 * The search make take a while so, it is best to run this asynchronously.
			 */
			SwingWorker<BoatList, Object> phrfSearch = new SwingWorker<BoatList, Object> () {
				@Override
				protected BoatList doInBackground() throws Exception {
					BoatList boatList = BoatList.getInstance();
					
					statusText.setText("Loading MWPHRF ratings");
					entryTable.setEnabled(false);
					
					for ( int row = 0; row < entryTable.getRowCount() ; row++ ) {
						boatList.getBoat(entryTable.convertRowIndexToModel(row)).findMatches(searchSailNumber,
								 															 searchYachtName,
								 															 searchMakeModel);
						progressBar.setValue(row + 1);
						entryTable.repaint();
					}
					return boatList;
				}
				
				@Override
				protected void done() {
					entryTable.setEnabled(true);
					progressBar.setValue(0);
					if ( source instanceof JButton ) {
						JButton thisButton = (JButton)source;
						thisButton.setEnabled(true);
					}
					mwphrfMenu.setEnabled(true);
					statusText.setText("MWPHRF Ratings loaded.");
				}
			};
			phrfSearch.execute();
		}
	}

	private class ValueSelectionAction extends AbstractAction {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		public ValueSelectionAction() {
			putValue(NAME, "Select variable...");
			putValue(SHORT_DESCRIPTION, "Set all MPHRF variables.");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			PHRFVariable[] options = { PHRFVariable.HCP, PHRFVariable.DHCP, PHRFVariable.NSHCP };
			
			Object varOption = JOptionPane.showInputDialog(null,
					"Rating variable to assign...",
					"Select MPWHRF variable",
					JOptionPane.PLAIN_MESSAGE,
					null,
					options,
					options[0]);
			
			if ( varOption == null ) {
				return;
			}
			
			PHRFVariable variable = (PHRFVariable) varOption;
			
			BoatList boatList = BoatList.getInstance();
			statusText.setText("Selecting MWPHRF rating");
			progressBar.setMaximum(entryTable.getRowCount());
			progressBar.setValue(0);
			
			for ( int row = 0; row < entryTable.getRowCount() ; row++ ) {
				BoatEntry entry = boatList.getBoat(entryTable.convertRowIndexToModel(row));
				PHRFBoatEntry phrfBoat = entry.getMatchList().getSelectedBoat();
				if ( phrfBoat != null ) {
					PHRFCertificate cert = phrfBoat.getCertList().getSelectedCertificate();
					if ( cert != null ) {
						cert.getValues().setSelectedVariable(variable);
					}
				}
				progressBar.setValue(row + 1);
				entryTable.repaint();
			}
			statusText.setText("");
			progressBar.setValue(0);
		}
	}
	
	private class InitialORC extends AbstractAction {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			
		}
	}
	
}
