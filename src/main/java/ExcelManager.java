import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ExcelManager {

	public ExcelManager() {

	}

	/* carrega o arquivo escolhido */
	private FileInputStream loadExcel() {
		try {
			FileInputStream arquivo = new FileInputStream(new File(chooseFile()));

			return arquivo;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return null;

	}

	// Criar um seletor de arquivo

	private String chooseFile() {

		JFileChooser fileChooser = new JFileChooser();

		// Definir o título do seletor de arquivo
		fileChooser.setDialogTitle("Selecione um arquivo");

		// Filtrar para mostrar apenas arquivos Excel
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Arquivos Excel (*.xlsx, *.xls)", "xlsx", "xls");
		fileChooser.setFileFilter(filter);

		// Mostrar o seletor de arquivo
		int result = fileChooser.showOpenDialog(null);

		// Verificar se o usuário selecionou um arquivo
		if (result == JFileChooser.APPROVE_OPTION) {
			// Obter o arquivo selecionado
			java.io.File selectedFile = fileChooser.getSelectedFile();

			return selectedFile.getAbsolutePath();
		} else {
			System.out.println("Nenhum arquivo selecionado.");

		}

		return "";

	}

	/* metodo para fechar o arquivo */
	private void closeFile(FileInputStream arquivo) {

		try {
			arquivo.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * Este método é responsável por criar um objeto XSSFWorkbook partir de um
	 * FileInputStream fornecido como parametro.
	 */
	private XSSFWorkbook createWorkbook(FileInputStream arquivo) {
		try {
			XSSFWorkbook workbook = new XSSFWorkbook(arquivo);
//			Workbook workbook = WorkbookFactory.create(arquivo);
			return workbook;
		} catch (EncryptedDocumentException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();
		}

		return null;
	}

	/*
	 * Passa o arquivo escolhido por parametro para o workbook(que lida com arquivos
	 * excel) e retorna
	 */
	public XSSFWorkbook getFile() {

		FileInputStream arquivo = loadExcel();
		XSSFWorkbook workbook = createWorkbook(arquivo);
		closeFile(arquivo);
		return workbook;
	}

	/* lendo planilha do excel */
	/* passa por parametro o objeto que representa o arquivo excel */
	public ArrayList<Map<String, Object>> readExcelData(XSSFWorkbook workbook) {

		/* pega a primeira planilha do arquivo excel */
		Sheet sheet = workbook.getSheetAt(0);

		ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

		for (Row row : sheet) {
			/* Loop para percorrer as linhas */
			if (row.getRowNum() != 0) {

				Map<String, Object> excelData = new HashMap<String, Object>();
				for (Cell cell : row) {
					/* Caso a coluna tenha o index 1 o conteudo da celula dele vai ser String */

					if (row.getRowNum() != 0 && cell.getColumnIndex() == 1 && cell.getCellType() == CellType.STRING) {
						/* Adicionando na Lista o valor da celula */
						excelData.put("placa", cell.getStringCellValue());
					}

					/*
					 * pulando a primeira linha e pega a coluna zero formata o valor da Data da
					 * celula e passa o valor numerico
					 */
					if (row.getRowNum() != 0 && cell.getColumnIndex() == 0 && DateUtil.isCellDateFormatted(cell)
							&& cell.getCellType() == CellType.NUMERIC) {
						/* atribuindo a variavel date o valor primitivo da data */
						Date date = cell.getDateCellValue();
						/* formatando para o padrão SP */
						TimeZone timezone = TimeZone.getTimeZone("America/Sao_Paulo");
						/* formatando a representação da data */
						SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy");
						/* formato recebe o padrao SP */
						format.setTimeZone(timezone);
						/* Nova data já nos padrões , formatando a data primitiva */
						String strDate = format.format(date);
						/* adiciona a lista de datas */
						excelData.put("data_vistoria", strDate);

					}

					if (row.getRowNum() != 0 && cell.getColumnIndex() == 2 && cell.getCellType() == CellType.STRING) {
						/* Adicionando na Lista o valor da celula */
						excelData.put("baixado", cell.getStringCellValue());
					}

					if (row.getRowNum() != 0 && cell.getColumnIndex() == 3 && cell.getCellType() == CellType.STRING) {
						/* Adicionando na Lista o valor da celula */
						excelData.put("cadastro", cell.getStringCellValue());
					}
				}

				data.add(excelData);
			}
		}

		return data;
	}
}
