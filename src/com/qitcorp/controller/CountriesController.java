package com.qitcorp.controller;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.qitcorp.dao.CountriesDAO;
import com.qitcorp.model.CountriesModel;

@SuppressWarnings("rawtypes")
public class CountriesController extends GenericForwardComposer{
	private static final long serialVersionUID = 1L;
	
	private Map<Integer, String> countriesMap;
	private Textbox txtName;
	private Listbox lstCountries;
	private Button btnAdd;
	private Button btnEdit;
	private Button btnDelete;
	private Button btnExportExcel;
	private Button btnExportTxt;
	
	private int selectedID;
	private String selectedCountry;
	
	@SuppressWarnings("unchecked")
	public void doAfterCompose(Component comp) {
		try{
			super.doAfterCompose(comp);
			this.fillPaisesList();
			this.txtName.setFocus(true);
			this.btnEdit.setDisabled(true);
			this.btnDelete.setDisabled(true);
			this.selectedID = 0;
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void cleanListbox(Listbox component) {
		if (component != null) {
			component.getItems().removeAll(component.getItems());
		}
	}
	
	private void fillPaisesList() throws SQLException {
		CountriesDAO objeDAO = new CountriesDAO();
		this.cleanListbox(this.lstCountries);
		this.countriesMap = objeDAO.findAll();
		for (Map.Entry<Integer, String> iterator : this.countriesMap.entrySet())
	    {
			Listitem row = new Listitem();
		    row.setParent(this.lstCountries);
	        Listcell c1 = new Listcell(String.valueOf(iterator.getKey()));
	        Listcell c2 = new Listcell(iterator.getValue());
	        c1.setParent(row);
	        c2.setParent(row);
	        row.setAttribute("countryID", String.valueOf(iterator.getKey()));
	        row.setAttribute("countryName", iterator.getValue());
	        row.addEventListener(Events.ON_CLICK, evtSelectedRow);
	    }
		if(this.lstCountries.getItemCount() == 0){
			Listitem row = new Listitem();
			row.setParent(this.lstCountries);
			Listcell c1 = new Listcell("No results found.");
			c1.setSpan(2);
			c1.setParent(row);
			this.btnExportExcel.setDisabled(true);
			this.btnExportTxt.setDisabled(true);
		} else {
			this.btnExportExcel.setDisabled(false);
			this.btnExportTxt.setDisabled(false);
		}
	}
	
	public EventListener<Event> evtSelectedRow = new EventListener<Event>(){
		  public void onEvent(Event event) throws Exception {
			  btnAdd.setDisabled(true);
			  btnEdit.setDisabled(false);
			  btnDelete.setDisabled(false);
			  selectedID = Integer.parseInt(event.getTarget().getAttribute("countryID").toString());
			  selectedCountry = event.getTarget().getAttribute("countryName").toString();
			  txtName.setText(event.getTarget().getAttribute("countryName").toString());
		  }
	};
	
	public void onClick$btnClean(){
		this.btnAdd.setDisabled(false);
		this.btnEdit.setDisabled(true);
		this.btnDelete.setDisabled(true);
		this.txtName.setText("");
		this.txtName.setFocus(true);
	}
	
	public void onClick$btnAdd() {
		try {
			if (!this.txtName.getText().trim().equals("")) {
				if (!this.countriesMap.containsValue(this.txtName.getText().trim())) {
					CountriesDAO objeDAO = new CountriesDAO();
					CountriesModel obje = new CountriesModel(this.selectedID, this.txtName.getText().trim());
					if (objeDAO.add(obje)) {
						Messagebox.show("Successful operation", "ATTENTION", Messagebox.OK, Messagebox.INFORMATION);
						int activePage = this.lstCountries.getActivePage();
						this.fillPaisesList();
						this.lstCountries.setActivePage(activePage);
						this.onClick$btnClean();
					} else {
						Messagebox.show("Could not complete operation", "ATTENTION", Messagebox.OK, Messagebox.EXCLAMATION);
					}
				} else {
					Messagebox.show("The entered country is already registered!", "ATTENTION", Messagebox.OK, Messagebox.EXCLAMATION);
				}
			} else {
				Messagebox.show("Empty fields!", "ATTENTION", Messagebox.OK, Messagebox.EXCLAMATION);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Messagebox.show("An error has occurred", "ATTENTION", Messagebox.OK, Messagebox.ERROR);
		}
	}
	
	public void onClick$btnEdit() {
		try {
			if (this.selectedID != 0) {
				if (!this.txtName.getText().trim().equals("")) {
					boolean sameRegister = (this.selectedCountry.equals(this.txtName.getText().trim()));
					if (sameRegister || !this.countriesMap.containsValue(this.txtName.getText().trim())) {
						CountriesDAO objeDAO = new CountriesDAO();
						CountriesModel obje = new CountriesModel(this.selectedID, this.txtName.getText().trim());
						if (objeDAO.edit(obje)) {
							Messagebox.show("Successful operation", "ATTENTION", Messagebox.OK, Messagebox.INFORMATION);
							int activePage = this.lstCountries.getActivePage();
							this.fillPaisesList();
							this.lstCountries.setActivePage(activePage);
							this.onClick$btnClean();
						} else {
							Messagebox.show("Could not complete operation", "ATTENTION", Messagebox.OK, Messagebox.EXCLAMATION);
						}
					} else {
						Messagebox.show("The entered country is already registered!", "ATTENTION", Messagebox.OK, Messagebox.EXCLAMATION);
					}
				} else {
					Messagebox.show("Empty fields!", "ATTENTION", Messagebox.OK, Messagebox.EXCLAMATION);
				}
			
			} else {
				Messagebox.show("You must select a register to perform the operation!", "ATTENTION", Messagebox.OK, Messagebox.EXCLAMATION);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Messagebox.show("An error has occurred", "ATTENTION", Messagebox.OK, Messagebox.ERROR);
		}
	}

	
	@SuppressWarnings("unchecked")
	public void onClick$btnDelete() {
		try {
			if (this.selectedID != 0) {
				Messagebox.show("Are you sure to delete the selected register?", "CONFIRMATION",
						new Messagebox.Button[] { Messagebox.Button.YES, Messagebox.Button.NO },
						new String[] { "Confirm", "Cancel" }, Messagebox.QUESTION, null, new EventListener() {
							public void onEvent(Event event) throws Exception {
								if (((Messagebox.Button) event.getData()) == Messagebox.Button.YES) {
									CountriesDAO objeDAO = new CountriesDAO();
									CountriesModel obje = new CountriesModel();
									obje.setCountry_id(selectedID);
									if (objeDAO.delete(obje)) {
										Messagebox.show("Successful operation", "ATTENTION", Messagebox.OK, Messagebox.INFORMATION);
										int activePage = lstCountries.getActivePage();
										fillPaisesList();
										lstCountries.setActivePage(activePage);
										onClick$btnClean();
									} else {
										Messagebox.show("Could not complete operation", "ATTENTION", Messagebox.OK, Messagebox.EXCLAMATION);
									}
								}
							}
						});
			} else {
				Messagebox.show("You must select a register to perform the operation!", "ATTENTION", Messagebox.OK, Messagebox.EXCLAMATION);
			}
		} catch (Exception e) {
			Messagebox.show("An error has occurred", "ATTENTION", Messagebox.OK, Messagebox.ERROR);
			e.printStackTrace();
		}
	}

	/**
	* Exportacion de archivo de texto con framework ZK de Java
	*/
	public void onClick$btnExportTxt() {
		File txtFile = new File("Countries.txt");
		BufferedWriter bw = null;
		String header = "ID|COUNTRY NAME";
		try {
			bw = new BufferedWriter(new FileWriter(txtFile));
			bw.write(header);
			bw.newLine();
			for (Listitem iterator : this.lstCountries.getItems()) {
				bw.write(iterator.getAttribute("countryID").toString() + "|" + iterator.getAttribute("countryName").toString());
				bw.newLine();
			}
			bw.close();
			Filedownload.save(txtFile, null);
			Messagebox.show("Operacion exitosa.", "ATENCION", Messagebox.OK, Messagebox.INFORMATION);
			Clients.clearBusy();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Messagebox.show("Ha ocurrido un error", "ATENCION", Messagebox.OK, Messagebox.ERROR);
		} catch (IOException e) {
			e.printStackTrace();
			Messagebox.show("Ha ocurrido un error", "ATENCION", Messagebox.OK, Messagebox.ERROR);
		}
	}
	
	/*
	 * Files with xlsx format are for Excel 2010 and above
	 * Files with xls format are for Excel 97-2003
	 * */
	public void onUpload$btnReadFile(UploadEvent evt) throws InvalidFormatException, IOException, SQLException{
		Media media = evt.getMedia();
		List<CountriesModel> countriesExcelList = new ArrayList<CountriesModel>();
		CountriesDAO objeDAO = new CountriesDAO();
		if(media.getFormat().equals("xlsx") || media.getFormat().equals("xls")){
			Workbook workbook = null;
			if(media.getFormat().equals("xlsx")){
				workbook = new XSSFWorkbook(media.getStreamData());
			}
			else if(media.getFormat().equals("xls")){
				workbook = new HSSFWorkbook(media.getStreamData());
			}
			Sheet firstSheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = firstSheet.iterator();
			CountriesModel obje;
			int duplicates = 0;
			while (iterator.hasNext()) {
				obje = new CountriesModel();
				Row row = iterator.next();
				//You can compare the row number starting from 0 ...
				//You can specify the cell you want with row.getCell(x) and x start from 0 ...
				if(row.getRowNum() > 0) {
					String currentCell = row.getCell(1).getStringCellValue().trim();
					if(!countriesMap.containsValue(currentCell)){
						obje.setCountry_name(currentCell);
						countriesExcelList.add(obje);
					} else {
						duplicates++;
					}
				}
			}
			workbook.close();
			if(countriesExcelList.size() != 0){
				if(objeDAO.addFromExcel(countriesExcelList)){
					if(duplicates == 0){
						Messagebox.show("Successful operation.","ATTENTION", Messagebox.OK, Messagebox.INFORMATION);
					} else if(duplicates > 0){
						Messagebox.show("Successful operation. There are " + duplicates + " registers that were ignored.","ATTENTION", Messagebox.OK, Messagebox.INFORMATION);
					}
				} else{
					Messagebox.show("Could not complete operation","ATTENTION", Messagebox.OK, Messagebox.EXCLAMATION);
				}
			} else{
				Messagebox.show("All the registers from the Excel file are duplicated!","ATTENTION", Messagebox.OK, Messagebox.EXCLAMATION);
			}
			this.fillPaisesList();
			this.onClick$btnClean();
		} else{
			Messagebox.show("You must upload an Excel-formatted file","ATTENTION", Messagebox.OK, Messagebox.EXCLAMATION);
		}
	}
	
	public void onClick$btnExportExcel(){
		File excelFile = new File("Countries.xlsx");
		XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Countries");
        int rowNum = 0;
        int fieldsNum = 2; //Number of fields
        Row row = sheet.createRow(rowNum++);
        Cell cell = row.createCell(0);
        cell.setCellValue("Country ID");
        cell = row.createCell(1);
        cell.setCellValue("Country name");
        for(Listitem iterator : this.lstCountries.getItems()) {
        	row = sheet.createRow(rowNum++);
            for (int i = 0; i < fieldsNum; i++) {
            	cell = row.createCell(i);
            	switch(i){
            		case 0:
            			cell.setCellValue(Integer.parseInt(iterator.getAttribute("countryID").toString()));
            			break;
            		case 1:
            			cell.setCellValue(iterator.getAttribute("countryName").toString());
            			break;
            	}
            }
        }
        try {
            FileOutputStream outputStream = new FileOutputStream(excelFile);
            workbook.write(outputStream);
            workbook.close();
            Filedownload.save(excelFile, null);
            Messagebox.show("Successful operation.","ATTENTION", Messagebox.OK, Messagebox.INFORMATION);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Messagebox.show("An error has occurred","ATTENTION", Messagebox.OK, Messagebox.ERROR);
        } catch (IOException e) {
            e.printStackTrace();
            Messagebox.show("An error has occurred","ATTENTION", Messagebox.OK, Messagebox.ERROR);
        }
	}
	
	public void onChanging$txtSearch(InputEvent evt){
		this.cleanListbox(this.lstCountries);
		String search = evt.getValue().trim(); 
		for (Map.Entry<Integer, String> iterator : this.countriesMap.entrySet()){
			if (String.valueOf(iterator.getKey()).contains(search) || iterator.getValue().toUpperCase().contains(search.toUpperCase())) {
				Listitem row = new Listitem();
				row.setParent(this.lstCountries);
				Listcell c1 = new Listcell(String.valueOf(iterator.getKey()));
				Listcell c2 = new Listcell(iterator.getValue());
				c1.setParent(row);
				c2.setParent(row);
				row.setAttribute("countryID", String.valueOf(iterator.getKey()));
		        row.setAttribute("countryName", iterator.getValue());
		        row.addEventListener(Events.ON_CLICK, evtSelectedRow);
			} 
		}
		if(this.lstCountries.getItemCount() == 0){
			Listitem row = new Listitem();
			row.setParent(this.lstCountries);
			Listcell c1 = new Listcell("No results found.");
			c1.setSpan(2);
			c1.setParent(row);
			this.btnExportExcel.setDisabled(true);
			this.btnExportTxt.setDisabled(true);
		}
		else {
			this.btnExportExcel.setDisabled(false);
			this.btnExportTxt.setDisabled(false);
		}
	}
}
